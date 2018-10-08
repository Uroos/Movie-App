package com.example.home.mymovieapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import okhttp3.HttpUrl;

import static android.content.ContentValues.TAG;
import static com.example.home.mymovieapp.utils.Constants.BASE_VIDEO_URL;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.videoViewHolder> {

    Context context;
    ArrayList<String> videos;
    static int i = 1;

    public VideoAdapter(Context context, ArrayList<String> videos) {
        this.context = context;
        this.videos = videos;
        i = 1;
    }

    @NonNull
    @Override
    public VideoAdapter.videoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);
        return new VideoAdapter.videoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.videoViewHolder holder, int position) {
        holder.setData();
    }

    @Override
    public int getItemCount() {
        if (videos == null || videos.size() == 0)
            return 0;
        return videos.size();
    }

    public class videoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView videoText;

        public videoViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            videoText = itemView.findViewById(R.id.tv_video);
        }

        public void setData() {
            videoText.setText("Trailer " + i);
            i += 1;
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_VIDEO_URL).newBuilder();
            urlBuilder.addQueryParameter("v", videos.get(position));
            String url = urlBuilder.build().toString();
            Uri uri = Uri.parse(url);
            Log.v(TAG, "url for clicked in video list is " + url);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        }
    }

    public void setVideoData(ArrayList<String> videos) {
        this.videos = videos;
    }
}
