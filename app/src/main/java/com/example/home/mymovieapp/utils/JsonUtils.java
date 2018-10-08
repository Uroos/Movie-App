package com.example.home.mymovieapp.utils;

import android.content.Context;
import android.util.Log;

import com.example.home.mymovieapp.R;
import com.example.home.mymovieapp.model.MovieInfo;
import com.example.home.mymovieapp.model.Reviews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonUtils{
    private static final String TAG = JsonUtils.class.getSimpleName();

    //private final Context mcontext;
    static String title;
    static long id;
    static int voteCount;
    static String posterPath;
    static String overView;
    static String releaseDate;

    public static MovieInfo parseSingleMovie (Context context, String myResponse){
        MovieInfo movie = new MovieInfo(null,0,0,null,null,null);
        if(myResponse!=null){
            try{
                JSONObject json = new JSONObject(myResponse);
                id = json.optLong(context.getString(R.string.json_id),0);
                title = json.optString(context.getString(R.string.json_title),context.getString(R.string.opt_title));
                voteCount = json.optInt(context.getString(R.string.json_votecount),0);
                posterPath = json.optString(context.getString(R.string.json_posterpath),"");
                overView = json.optString(context.getString(R.string.json_overview),context.getString(R.string.opt_available));
                releaseDate = json.optString(context.getString(R.string.json_date),context.getString(R.string.opt_date));
                movie = new MovieInfo(title, id, voteCount, posterPath, overView, releaseDate);
            }catch(JSONException e){
                e.printStackTrace();
            }
            return movie;
        }else
            return null;
    }
    public static ArrayList<MovieInfo> parseMovieJson(Context context, String myResponse) {
        ArrayList<MovieInfo> movies= new ArrayList<>();
        if (myResponse != null) {
            try {
                JSONObject json = new JSONObject(myResponse);
                JSONArray result = json.getJSONArray(context.getString(R.string.json_results));
                for (int i = 0; i < result.length(); i++) {
                    JSONObject objectOfResult = result.getJSONObject(i);

                    title = objectOfResult.optString(context.getString(R.string.json_title),context.getString(R.string.opt_title));
                    id = objectOfResult.optLong(context.getString(R.string.json_id),0);
                    voteCount = objectOfResult.optInt(context.getString(R.string.json_votecount),0);
                    posterPath = objectOfResult.optString(context.getString(R.string.json_posterpath),"");
                    overView = objectOfResult.optString(context.getString(R.string.json_overview),context.getString(R.string.opt_available));
                    releaseDate = objectOfResult.optString(context.getString(R.string.json_date),context.getString(R.string.opt_date));
                    movies.add(new MovieInfo(title, id, voteCount, posterPath, overView, releaseDate));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return movies;
        } else
            return null;
    }

    public static ArrayList<Reviews> parseReviewJson(Context context, String myResponse) {
        ArrayList<Reviews> reviews = new ArrayList<>();

        if (myResponse != null) {
            try {
                JSONObject json = new JSONObject(myResponse);
                JSONArray result = json.getJSONArray(context.getString(R.string.json_results));
                for (int i = 0; i < result.length(); i++) {
                    JSONObject objectOfResult = result.getJSONObject(i);
                    String author = objectOfResult.optString(context.getString(R.string.json_author),context.getString(R.string.fallback_author));
                    String content = objectOfResult.optString(context.getString(R.string.json_content),context.getString(R.string.fallback_content));
                    reviews.add(new Reviews(author, content));
                    //Log.v(TAG, "author: " + author + "- content: " + content);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return reviews;
        } else
            return null;
    }

    public static ArrayList<String> parseTrailerJson(Context context, String myResponse) {
        ArrayList<String> trailers = new ArrayList<>();

        if (myResponse != null) {
            try {
                JSONObject json = new JSONObject(myResponse);
                JSONArray result = json.getJSONArray(context.getString(R.string.json_results));
                for (int i = 0; i < result.length(); i++) {
                    JSONObject objectOfResult = result.getJSONObject(i);
                    String trailerKey = objectOfResult.optString(context.getString(R.string.json_key),context.getString(R.string.fallback_key));
                    trailers.add(new String(trailerKey));
                    //Log.v(TAG, "key: " + trailerKey);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return trailers;
        } else
            return null;
    }
}
