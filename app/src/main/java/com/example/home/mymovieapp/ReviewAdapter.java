package com.example.home.mymovieapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.home.mymovieapp.model.Reviews;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.reviewViewHolder> {
    Context context;
    ArrayList<Reviews> reviews;

    public ReviewAdapter(Context context, ArrayList<Reviews> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewAdapter.reviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
        return new ReviewAdapter.reviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.reviewViewHolder holder, int position) {
        holder.setData(reviews.get(position));
    }

    @Override
    public int getItemCount() {
        if (reviews == null || reviews.size() == 0)
            return 0;
        return reviews.size();
    }

    public class reviewViewHolder extends RecyclerView.ViewHolder {

        public TextView reviewAuthor;
        public TextView reviewContent;

        public reviewViewHolder(View itemView) {
            super(itemView);
            reviewAuthor = itemView.findViewById(R.id.tv_review_author);
            reviewContent = itemView.findViewById(R.id.tv_review_content);
        }

        public void setData(Reviews review) {
            reviewAuthor.setText(review.getAuthor().trim());
            reviewContent.setText(review.getContent().trim());
        }
    }

    public void setReviewData(ArrayList<Reviews> reviews) {
        this.reviews = reviews;
    }
}
