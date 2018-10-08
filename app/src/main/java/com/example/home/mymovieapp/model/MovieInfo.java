package com.example.home.mymovieapp.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "movies")
public class MovieInfo implements Serializable{

    @PrimaryKey
    private long id;
    private String title;

    @Ignore
    private int vote_count;
    @Ignore
    private String poster_path;
    @Ignore
    private String overview;
    @Ignore
    private String release_date;

    //@Ignore
    public MovieInfo(long id, String title) {
        this.id = id;
        this.title = title;
    }

    @Ignore
    public MovieInfo(String title,
                     long id,
                     int vote_count,
                     String poster_path,
                     String overview,
                     String release_date) {
        this.title = title;
        this.id = id;
        this.vote_count = vote_count;
        this.poster_path = poster_path;
        this.overview = overview;
        this.release_date = release_date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getVote_count() {
        return vote_count;
    }

    public void setVote_count(int vote) {
        this.vote_count = vote;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }
}
