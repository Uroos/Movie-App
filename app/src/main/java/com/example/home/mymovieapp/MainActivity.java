package com.example.home.mymovieapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.home.mymovieapp.model.MovieInfo;
import com.example.home.mymovieapp.utils.Constants;
import com.example.home.mymovieapp.utils.JsonUtils;
import com.example.home.mymovieapp.utils.OkHttpHandler;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.home.mymovieapp.utils.Constants.BASE_URL;
import static com.example.home.mymovieapp.utils.Constants.BUNDLE_RECYCLER_LAYOUT;
import static com.example.home.mymovieapp.utils.Constants.POPULAR;
import static com.example.home.mymovieapp.utils.Constants.TOP_RATED;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler,
        OkHttpHandler.OnUpdateListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = true;

    //public static int top = -1;
    @BindView(R.id.rv_movies)
    RecyclerView recyclerView;
    @BindView(R.id.tv_error_message_display)
    TextView errorMessageDisplay;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar loadingIndicator;

    MainViewModel viewModel;
    private MovieAdapter movieAdapter;
    ArrayList<MovieInfo> movies;
    GridLayoutManager layoutManager;

    String[] params = new String[4];
    OkHttpHandler task;

    Parcelable savedRecyclerLayoutState;
    Bundle bundleRecyclerViewState;

    int positionIndex;
    int topView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        bundleRecyclerViewState = new Bundle();
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        loadingIndicator.setVisibility(View.VISIBLE);
        task = new OkHttpHandler();
        task.setUpdateListener(this);

        movies = new ArrayList<>();
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        movieAdapter = new MovieAdapter(this, movies, this);
        recyclerView.setAdapter(movieAdapter);
        if (savedInstanceState != null) {
            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
        Log.v(TAG, "on create");
        setupData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.BUNDLE_RECYCLER_LAYOUT, recyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            loadingIndicator.setVisibility(View.VISIBLE);
            setupData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings_action) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(MovieInfo movie) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("movie", movie);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onUpdate(String response) {
        if (response != null) {
            movies = JsonUtils.parseMovieJson(this, response);
            movieAdapter.setMovieData(movies);
            movieAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(movieAdapter);
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
            showMovieData();
        } else
            showErrorMessage();

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Set this flag to true so that when control returns to MainActivity, it can refresh the data.
        PREFERENCES_HAVE_BEEN_UPDATED = true;
        //This call removes all observers attached to MainActivity.
        viewModel.getMovies().removeObservers(this);
    }

    private void setupData() {
        loadingIndicator.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        if (isOnline() && !getString(R.string.pref_value_favorite).equals(findChoice())) {
            params[0] = BASE_URL;
            if (getString(R.string.pref_value_popular).equals(findChoice())) {
                params[1] = POPULAR;
                runQuery(params);
            } else if (getString(R.string.pref_value_toprated).equals(findChoice())) {
                params[1] = TOP_RATED;
                runQuery(params);
            }
        } else if (!isOnline() && !(getString(R.string.pref_value_favorite).equals(findChoice()))) {
            showErrorMessage();
        } else if (isOnline() && getString(R.string.pref_value_favorite).equals(findChoice())) {
            loadFavoriteMovies();
        } else {
            loadFavoriteMovies();
        }
    }

    private void loadFavoriteMovies() {
        showMovieData();
        PREFERENCES_HAVE_BEEN_UPDATED = false;

        //final MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<MovieInfo>>() {
            @Override
            public void onChanged(@Nullable List<MovieInfo> movieInfos) {
                Log.d(TAG, "updating list of movies from live data in view model size is: " + movieInfos.size());
                MovieAdapter mvadap = new MovieAdapter(MainActivity.this, (ArrayList) movieInfos, MainActivity.this);
                recyclerView.setAdapter(mvadap);
                movieAdapter.notifyDataSetChanged();
            }
        });
    }

    private void runQuery(String[] params) {
        OkHttpHandler currentTask = new OkHttpHandler();
        currentTask.setUpdateListener(this);
        currentTask.execute(params);
        PREFERENCES_HAVE_BEEN_UPDATED = false;

    }

    private void showMovieData() {
        loadingIndicator.setVisibility(View.INVISIBLE);
        errorMessageDisplay.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        loadingIndicator.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.GONE);
        errorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private String findChoice() {
        // Returns true if the user's preference for sorting is popular, false otherwise
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        String choice = prefs.getString(getString(R.string.pref_key), getString(R.string.pref_value_popular));
        return choice;
    }
}
