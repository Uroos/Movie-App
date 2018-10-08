package com.example.home.mymovieapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.home.mymovieapp.model.AppDatabase;
import com.example.home.mymovieapp.model.MovieInfo;
import com.example.home.mymovieapp.model.Reviews;
import com.example.home.mymovieapp.utils.JsonUtils;
import com.example.home.mymovieapp.utils.OkHttpHandler;
import com.example.home.mymovieapp.utils.SeparatorDecoration;
import com.example.home.mymovieapp.utils.SingleMovieOkHttpHandler;
import com.example.home.mymovieapp.utils.VideoOkHttpHandler;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.HttpUrl;

import static android.support.v7.widget.RecyclerView.VERTICAL;
import static com.example.home.mymovieapp.utils.Constants.BASE_IMAGE_URL;
import static com.example.home.mymovieapp.utils.Constants.BASE_URL;
import static com.example.home.mymovieapp.utils.Constants.BASE_VIDEO_URL;
import static com.example.home.mymovieapp.utils.Constants.REVIEWS;
import static com.example.home.mymovieapp.utils.Constants.VIDEOS;

public class DetailActivity extends AppCompatActivity implements OkHttpHandler.OnUpdateListener,
        VideoOkHttpHandler.OnVideoUpdateListener,
        SingleMovieOkHttpHandler.OnMovieUpdateListener {

    private final static String TAG = DetailActivity.class.getSimpleName();

    MovieInfo movieInfo;
    static long id;
    String title;
    String releaseDate;
    String overview;
    int voteCount;
    static String url;

    @BindView(R.id.iv_detail)
    ImageView posterImage;
    @BindView(R.id.tv_title_detail)
    TextView titleText;
    @BindView(R.id.btn_star)
    ToggleButton imgButton;
    @BindView(R.id.tv_description_title)
    TextView descriptionTitleText;
    @BindView(R.id.tv_description_detail)
    TextView descriptionText;
    @BindView(R.id.tv_vote_title)
    TextView voteTitleText;
    @BindView(R.id.tv_vote_detail)
    TextView voteText;
    @BindView(R.id.tv_date_title)
    TextView dateTitleText;
    @BindView(R.id.tv_date_detail)
    TextView dateText;
    @BindView(R.id.detail_title_review)
    TextView reviewText;
    @BindView(R.id.detail_title_trailer)
    TextView trailerText;
    @BindView(R.id.rv_review)
    RecyclerView recyclerViewReview;
    @BindView(R.id.rv_trailers)
    RecyclerView recyclerViewTrailer;

    LinearLayoutManager layoutManagerReview;
    LinearLayoutManager layoutManagerTrailer;
    private ReviewAdapter reviewAdapter;
    private VideoAdapter videoAdapter;

    private AppDatabase db;

    MovieInfo movie;
    ArrayList<Reviews> reviews;
    ArrayList<String> trailers;

    OkHttpHandler reviewTask;
    VideoOkHttpHandler videoTask;
    SingleMovieOkHttpHandler movieTask;

    String[] movieparams = new String[3];
    String[] reviewparams = new String[4];
    String[] videoparams = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = AppDatabase.getsInstance(getApplicationContext());
        ButterKnife.bind(this);

        reviewTask = new OkHttpHandler();
        reviewTask.setUpdateListener(this);
        videoTask = new VideoOkHttpHandler();
        videoTask.setVideoUpdateListener(this);

        reviews = new ArrayList<>();
        trailers = new ArrayList<>();

        //Setting up the reviews recycler view.
        layoutManagerReview = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewReview.setLayoutManager(layoutManagerReview);
        recyclerViewReview.setHasFixedSize(true);
        SeparatorDecoration decoration = new SeparatorDecoration(this, getResources().getColor(R.color.colorPrimaryDark), 1.5f);
        recyclerViewReview.addItemDecoration(decoration);
        reviewAdapter = new ReviewAdapter(this, reviews);
        recyclerViewReview.setAdapter(reviewAdapter);

        layoutManagerTrailer = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        DividerItemDecoration decorationvertical = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        recyclerViewTrailer.addItemDecoration(decorationvertical);
        recyclerViewTrailer.setLayoutManager(layoutManagerTrailer);
        recyclerViewTrailer.setHasFixedSize(true);
        videoAdapter = new VideoAdapter(this, trailers);
        recyclerViewReview.setAdapter(videoAdapter);

        if (isOnline()) {
            if (savedInstanceState == null) {
                Log.v(TAG, "savedInstance is null");
                Intent intent = getIntent();
                if (intent != null) {
                    movieInfo = (MovieInfo) intent.getSerializableExtra("movie");

                    id = movieInfo.getId();
                    title = movieInfo.getTitle();

                    releaseDate = movieInfo.getRelease_date();
                    if (releaseDate == null || TextUtils.isEmpty(releaseDate)) {
                        getMovieResponse(String.valueOf(id));
                    } else {
                        overview = movieInfo.getOverview();
                        voteCount = movieInfo.getVote_count();
                        String path = movieInfo.getPoster_path();
                        url = buildUrl(path);
                        populateUI(id, title, overview, releaseDate, voteCount, url);
                    }
                } else
                    finish();

            } else {
                Log.v(TAG, "savedInstance is not null");
                populateUI(Long.parseLong(savedInstanceState.getString("id")),
                        savedInstanceState.getString("title"),
                        savedInstanceState.getString("overview"),
                        savedInstanceState.getString("date"),
                        savedInstanceState.getInt("vote"),
                        savedInstanceState.getString("url"));
            }
        } else {
            posterImage.setVisibility(View.GONE);
            titleText.setVisibility(View.GONE);
            descriptionTitleText.setVisibility(View.GONE);
            descriptionText.setVisibility(View.GONE);
            imgButton.setVisibility(View.GONE);
            voteTitleText.setVisibility(View.GONE);
            voteText.setVisibility(View.GONE);
            dateTitleText.setVisibility(View.GONE);
            dateText.setVisibility(View.GONE);
            reviewText.setVisibility(View.GONE);
            trailerText.setVisibility(View.GONE);
            recyclerViewReview.setVisibility(View.GONE);
            recyclerViewTrailer.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_star)
    public void toggle(final ToggleButton button) {
        final String movietitle = titleText.getText().toString();

        final LiveData<MovieInfo> movie = db.taskDao().getMovie(id);
        movie.observe(this, new Observer<MovieInfo>() {
            @Override
            public void onChanged(@Nullable final MovieInfo movieInfo) {
                movie.removeObserver(this);
                if (movieInfo == null) {
                    final MovieInfo m = new MovieInfo(id, movietitle);
                    AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            db.taskDao().insertMovie(m);
                        }
                    });
                    button.setChecked(true);
                } else {
                    //delete from database
                    AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            db.taskDao().deleteMovie(movieInfo);
                        }
                    });
                    button.setChecked(false);
                    //finish();
                }

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(TAG, "savedInstance is being saved.");
        String title = titleText.getText().toString();
        String overview = descriptionText.getText().toString();
        String date = dateText.getText().toString();
        String vote = voteText.getText().toString();
        String urlSaved = url;

        outState.putString("id", String.valueOf(id));
        outState.putString("title", title);
        outState.putString("overview", overview);
        outState.putString("date", date);
        outState.putString("vote", vote);
        outState.putString("url", urlSaved);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idOfItem = item.getItemId();
        if (idOfItem == R.id.settings_share) {
            if (isOnline()) {
                HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_VIDEO_URL).newBuilder();
                urlBuilder.addQueryParameter("v", trailers.get(0));
                String url = urlBuilder.build().toString();
                Uri uri = Uri.parse(url);
                Log.v(TAG, "url for clicked in video list is " + url);
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body) + url);
                if (intent.resolveActivity(this.getPackageManager()) != null) {
                    this.startActivity(intent);
                }
            } else {
                Toast.makeText(this, "Can't share!", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void populateUI(long id, String title, String overview, String releaseDate, int voteCount, String url) {
        titleText.setText(title);
        descriptionText.setText(overview);
        dateText.setText(releaseDate);
        voteText.setText(String.valueOf(voteCount));
        Picasso.with(DetailActivity.this).load(url).error(R.mipmap.ic_launcher).into(posterImage);
        getReviewResponse(String.valueOf(id));
        getVideoResponse(String.valueOf(id));
        setupFactory(id);
    }

    private void setupFactory(long id) {
        AddMovieViewModelFactory factory = new AddMovieViewModelFactory(db, id);
        final AddMovieViewModel viewModel
                = ViewModelProviders.of(this, factory).get(AddMovieViewModel.class);
        viewModel.getMovie().observe(this, new Observer<MovieInfo>() {
            @Override
            public void onChanged(@Nullable MovieInfo movieInfo) {
                viewModel.getMovie().removeObserver(this);
                if (movieInfo == null) {
                    imgButton.setChecked(false);
                } else {
                    imgButton.setChecked(true);
                }
            }
        });
    }

    private String buildUrl(String path) {
        if (path != null) {
            HttpUrl.Builder myUrl = HttpUrl.parse(BASE_IMAGE_URL).newBuilder();
            myUrl.addPathSegment(path).build();
            String url = myUrl.toString();
            // The url will contain an extra %2F added in the path which doesn't let the image load.
            String newUrl = url.replace("%2F", "/");
            return newUrl;
        } else
            return null;
    }

    private void getMovieResponse(String id) {
        movieparams[0] = BASE_URL;
        movieparams[1] = id;
        movieTask = new SingleMovieOkHttpHandler();
        movieTask.setMovieUpdateListener(this);
        movieTask.execute(movieparams);
    }

    private void getReviewResponse(String id) {
        reviewparams[0] = BASE_URL;
        reviewparams[1] = id;
        reviewparams[2] = REVIEWS;
        reviewTask = new OkHttpHandler();
        reviewTask.setUpdateListener(this);
        reviewTask.execute(reviewparams);
    }

    private void getVideoResponse(String id) {
        videoparams[0] = BASE_URL;
        videoparams[1] = id;
        videoparams[2] = VIDEOS;
        videoTask = new VideoOkHttpHandler();
        videoTask.setVideoUpdateListener(this);
        videoTask.execute(videoparams);

    }

    @Override
    public void onUpdate(String response) {
        if (response != null) {
            reviews = JsonUtils.parseReviewJson(this, response);
            if (reviews != null && reviews.size() > 0) {
                //Log.v(TAG, "reviews array is " + reviews.size());
                reviewAdapter.setReviewData(reviews);
                recyclerViewReview.setAdapter(reviewAdapter);
                recyclerViewReview.setVisibility(View.VISIBLE);
            } else {
                reviewText.setVisibility(View.GONE);
                recyclerViewReview.setVisibility(View.GONE);
            }
        } else {
            reviewText.setVisibility(View.GONE);
            recyclerViewReview.setVisibility(View.GONE);
        }
    }

    @Override
    public void onVideoUpdate(String response) {
        //Here we get response json for videos
        //Log.v(TAG, "video response is:" + response);
        if (response != null) {
            trailers = JsonUtils.parseTrailerJson(this, response);
            if (trailers != null && trailers.size() > 0) {
                Log.v(TAG, "trailers array is " + trailers.size());
                videoAdapter.setVideoData(trailers);
                recyclerViewTrailer.setAdapter(videoAdapter);
                recyclerViewTrailer.setVisibility(View.VISIBLE);
            } else {
                trailerText.setVisibility(View.GONE);
                recyclerViewTrailer.setVisibility(View.VISIBLE);
            }

        } else {
            trailerText.setVisibility(View.GONE);
            recyclerViewTrailer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMovieUpdate(String response) {
        Log.v(TAG, "movie response is:" + response);
        if (response != null) {
            movie = JsonUtils.parseSingleMovie(this, response);
            populateUI(movie.getId(),
                    movie.getTitle(),
                    movie.getOverview(),
                    movie.getRelease_date(),
                    movie.getVote_count(),
                    buildUrl(movie.getPoster_path()));

        } else {

        }
    }
}
