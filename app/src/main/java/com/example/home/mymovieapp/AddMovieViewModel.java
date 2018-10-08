package com.example.home.mymovieapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.home.mymovieapp.model.AppDatabase;
import com.example.home.mymovieapp.model.MovieInfo;

public class AddMovieViewModel extends ViewModel {

    private LiveData<MovieInfo> movie;

    public AddMovieViewModel(AppDatabase db, long movieId) {
        movie = db.taskDao().getMovie(movieId);
    }

    public LiveData<MovieInfo> getMovie() {
        return movie;
    }
}
