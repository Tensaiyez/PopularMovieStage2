package com.example.tensaiye.popularmovie.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tensaiye.popularmovie.Database.FavoriteEntry;
import com.example.tensaiye.popularmovie.R;
import com.example.tensaiye.popularmovie.Review;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>{
    private List<FavoriteEntry> FavoriteList;
    private Context context;
    private AdapterView.OnItemClickListener mItemClickListener;
    public class FavoriteViewHolder extends RecyclerView.ViewHolder{

        public TextView mTitle;
        public ImageView mPoster;


            public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle=itemView.findViewById(R.id.Favorite_Title);
            mPoster=itemView.findViewById(R.id.favorite_image);


        }
    }
    public FavoriteAdapter(List<FavoriteEntry> FavoriteList, Context context) {
        this.context = context;

        this.FavoriteList = FavoriteList;
    }
        @NonNull
    @Override
    public FavoriteAdapter.FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context=viewGroup.getContext();
        View view= LayoutInflater.from(context).inflate(R.layout.favorite,viewGroup,false);

        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.FavoriteViewHolder favoriteViewHolder, int i) {
    FavoriteEntry favoriteEntry=FavoriteList.get(i);
    favoriteViewHolder.mTitle.setText(favoriteEntry.getTitle());
        Picasso.with(context)
                .load(FavoriteList.get(i).getPoster())
                .into(favoriteViewHolder.mPoster);


    }

    @Override
    public int getItemCount() {
        return FavoriteList.size();
    }

    public void setFavoriteList(List<FavoriteEntry> favoriteList){
        FavoriteList=favoriteList;
        notifyDataSetChanged();
    }
    public List<FavoriteEntry> getFavoriteList(){
        return FavoriteList;
    }
}
