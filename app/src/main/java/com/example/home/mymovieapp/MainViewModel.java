package com.example.home.mymovieapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.home.mymovieapp.model.AppDatabase;
import com.example.home.mymovieapp.model.MovieInfo;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<MovieInfo>> movies;

    public MainViewModel(@NonNull Application application) {
        super(application);
        movies = AppDatabase.getsInstance(this.getApplication()).taskDao().loadAllMovies();
        Log.d(TAG, "Actively retrieving tasks from teh database");
    }

    public LiveData<List<MovieInfo>> getMovies() {
        return movies;
    }
}
