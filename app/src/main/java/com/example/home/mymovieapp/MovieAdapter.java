package com.example.home.mymovieapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.home.mymovieapp.model.MovieInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import okhttp3.HttpUrl;

import static com.example.home.mymovieapp.utils.Constants.BASE_IMAGE_URL;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.movieViewHolder> {

    Context context;
    ArrayList<MovieInfo> movies;
    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final MovieAdapterOnClickHandler clickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieAdapterOnClickHandler {
        void onClick(MovieInfo movie);
    }

    /**
     * Creates a MovieAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public MovieAdapter(Context context, ArrayList<MovieInfo> movies, MovieAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.movies = movies;
        this.clickHandler = clickHandler;
    }

    @Override
    public movieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false);
        return new movieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(movieViewHolder holder, int position) {
        holder.setData(movies.get(position));

    }

    @Override
    public int getItemCount() {
        if (movies == null || movies.size() == 0)
            return 0;
        return movies.size();
    }


    public class movieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView movieImage;
        public TextView titleText;

        public movieViewHolder(View itemView) {
            super(itemView);
            movieImage = itemView.findViewById(R.id.iv_poster);
            titleText = itemView.findViewById(R.id.tv_movie_name);
            itemView.setOnClickListener(this);
        }

        public void setData(MovieInfo movie) {

            if (movie.getPoster_path() != null && !TextUtils.isEmpty(movie.getPoster_path())) {
                titleText.setVisibility(View.GONE);
                movieImage.setVisibility(View.VISIBLE);
                HttpUrl.Builder myUrl = HttpUrl.parse(BASE_IMAGE_URL).newBuilder();
                myUrl.addPathSegment(movie.getPoster_path())
                        .build();
                String url = myUrl.toString();
                // The url will contain an extra %2F added in the path which doesn't let the image load.
                String newUrl = url.replace("%2F", "/");
                Picasso.with(context).load(newUrl).error(R.mipmap.ic_launcher).into(movieImage);
            } else {
                movieImage.setVisibility(View.GONE);
                titleText.setVisibility(View.VISIBLE);
                titleText.setText(movie.getTitle());
            }
        }

        @Override
        public void onClick(View view) {
            if (clickHandler != null) {
                int position = getAdapterPosition();
                clickHandler.onClick(movies.get(position));
            }
        }
    }

    public void setMovieData(ArrayList<MovieInfo> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public ArrayList<MovieInfo> getMovies() {
        return movies;
    }
}
