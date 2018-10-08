package com.example.home.mymovieapp.utils;

import android.os.AsyncTask;
import android.util.Log;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.home.mymovieapp.utils.Constants.API_KEY;
import static com.example.home.mymovieapp.utils.Constants.POPULAR;
import static com.example.home.mymovieapp.utils.Constants.TOP_RATED;

public class OkHttpHandler extends AsyncTask<String, Void, String> {
    private static final String TAG = OkHttpHandler.class.getSimpleName();
    OnUpdateListener listener;

    public interface OnUpdateListener {
        void onUpdate(String s);
    }

    public void setUpdateListener(OnUpdateListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... s) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(s[0]).newBuilder();
        urlBuilder.addQueryParameter("api_key", API_KEY);
        // If we are just querying popular or top-rated movies we don't need to add id to the url.
        if(s[1].equals(POPULAR)||s[1].equals(TOP_RATED)) {
            urlBuilder.addPathSegment(s[1]);
        }else{
            urlBuilder.addPathSegment(s[1]);// movie id
            urlBuilder.addPathSegment(s[2]);// reviews
        }

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
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (listener != null) {
            listener.onUpdate(s);
        }
    }
}
