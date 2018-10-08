package com.example.home.mymovieapp.utils;

import android.os.AsyncTask;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.home.mymovieapp.utils.Constants.API_KEY;

public class SingleMovieOkHttpHandler extends AsyncTask<String, Void, String> {

    SingleMovieOkHttpHandler.OnMovieUpdateListener listener;

    public interface OnMovieUpdateListener {
        void onMovieUpdate(String s);
    }
    public void setMovieUpdateListener(OnMovieUpdateListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... s) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(s[0]).newBuilder();
        urlBuilder.addQueryParameter("api_key", API_KEY);
        urlBuilder.addPathSegment(s[1]);// movie id
        String url = urlBuilder.build().toString();
        Response response = null;
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (listener != null) {
            listener.onMovieUpdate(s);
        }
    }
}
