package com.example.tensaiye.popularmovie.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tensaiye.popularmovie.R;
import com.example.tensaiye.popularmovie.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<Review> reviewList;
    private Context context;



    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        public TextView mReviewAuthor;
        public TextView mReviewContent;


        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            mReviewAuthor=itemView.findViewById(R.id.author_tv);
            mReviewContent=itemView.findViewById(R.id.Content_tv);


        }
    }

    public ReviewAdapter(List<Review> reviewList, Context context) {
        this.context = context;

        this.reviewList = reviewList;

    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.review, viewGroup, false);


        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder reviewViewHolder, int i) {
        Review review=reviewList.get(i);
        reviewViewHolder.mReviewContent.setText(review.getContent());
        reviewViewHolder.mReviewAuthor.setText(review.getAuthor());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }


}
