package com.example.home.mymovieapp;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.home.mymovieapp.model.AppDatabase;

public class AddMovieViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase db;
    private final long movieId;

    public AddMovieViewModelFactory(AppDatabase database, long movieId) {
        db = database;
        this.movieId = movieId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddMovieViewModel(db, movieId);
    }
}
