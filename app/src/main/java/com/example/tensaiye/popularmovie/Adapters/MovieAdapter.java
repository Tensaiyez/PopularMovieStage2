package com.example.tensaiye.popularmovie.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tensaiye.popularmovie.DetailActivity;
import com.example.tensaiye.popularmovie.Movie;
import com.example.tensaiye.popularmovie.R;
import com.squareup.picasso.Picasso;
import java.util.List;



public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
private List<Movie>movies;
private Context mContext;
private  int layoutID;


    public class MovieViewHolder extends RecyclerView.ViewHolder  {
    GridLayout moviesLayout;
    TextView movieTitle;
    ImageView imageView;


    public MovieViewHolder(View V){
        super(V);
        moviesLayout=(GridLayout) V.findViewById(R.id.movie_layout);
        movieTitle=(TextView)V.findViewById(R.id.Original_tv);
        imageView=(ImageView)V.findViewById(R.id.list_movie_image);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                Intent intent = new Intent(mContext, DetailActivity.class);

                intent.putExtra("id",movies.get(position).getId());
                intent.putExtra("original_name", movies.get(position).getOriginalName());
                intent.putExtra("release_date", movies.get(position).getReleaseDate());
                intent.putExtra("poster_image", movies.get(position).getPosterImage());
                intent.putExtra("overview", movies.get(position).getOverView());
                intent.putExtra("user_rating", movies.get(position).getUserRating());
                intent.putExtra("backdrop_path",movies.get(position).getBackdrop());
               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

            }
        });
    }
    }
public MovieAdapter(List<Movie>movies,int layoutID,Context context){
    this.movies=movies;
    this.layoutID =layoutID;
    this.mContext=context;
}
    @NonNull
    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    View view=LayoutInflater.from(viewGroup.getContext()).inflate(layoutID,viewGroup,false);
    return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.MovieViewHolder movieViewHolder, int i) {



        Picasso.with(mContext)
                .load(movies.get(i).getPosterImage())
                .into(movieViewHolder.imageView);

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

}
