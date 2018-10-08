package com.example.home.mymovieapp.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM movies")
    LiveData<List<MovieInfo>> loadAllMovies();

    @Query("SELECT * from movies WHERE id = :id")
    LiveData<MovieInfo> getMovie(long id);

    @Insert
    void insertMovie(MovieInfo movie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(MovieInfo movie);

    @Delete
    void deleteMovie(MovieInfo movie);
}
