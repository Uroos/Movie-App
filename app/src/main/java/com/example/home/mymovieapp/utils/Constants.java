package com.example.home.mymovieapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.home.mymovieapp.BuildConfig;
import com.example.home.mymovieapp.MainActivity;

public class Constants {

    // Used in MainActivity
    public static final String BUNDLE_RECYCLER_LAYOUT = "classname.recycler.layout";
    public static final String API_KEY = BuildConfig.API_KEY;
    public static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";

    // Used in DetailActivity
    public static final String REVIEWS = "reviews";
    public static final String VIDEOS = "videos";

    // Used in MovieAdapter
    public static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w185/";

    // Used in VideoAdapter
    public static final String BASE_VIDEO_URL = "https://www.youtube.com/watch";

}
