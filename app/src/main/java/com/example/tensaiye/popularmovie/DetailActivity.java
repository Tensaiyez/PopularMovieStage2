package com.example.tensaiye.popularmovie;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Delete;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import com.android.support:design:28.0.0;

import com.example.tensaiye.popularmovie.API.RetrofitService;
import com.example.tensaiye.popularmovie.API.ServiceInterface;
import com.example.tensaiye.popularmovie.Adapters.FavoriteAdapter;
import com.example.tensaiye.popularmovie.Adapters.ReviewAdapter;
import com.example.tensaiye.popularmovie.Adapters.TrailerAdapter;
import com.example.tensaiye.popularmovie.Database.FavoriteEntry;
import com.example.tensaiye.popularmovie.Database.MovieDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailActivity extends AppCompatActivity implements View.OnClickListener {


    private Movie movies;


    TextView mTitle;
    TextView mUserRating;
    TextView mReleaseDate;
    TextView mDescription;
    TextView mReviewContent;
    TextView mNoReview;
    Button mButton;


    String title;
    String releaseDate;
    String userRating;
    String overView;
    String poster;
    String id;
    String backdrop;

    private List<Review> reviewList = new ArrayList<>();
    private List<Trailer> TrailerList = new ArrayList<>();
    private List<FavoriteEntry> FavoriteList = new ArrayList<>();
    private String Tag;
    private ReviewAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;
    private FavoriteAdapter mFavoriteAdapter;
    private MovieDatabase mDb;
    private boolean isFavorite = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final RecyclerView recyclerView2 = (RecyclerView) findViewById(R.id.Favorite_rv);
        recyclerView2.setLayoutManager(new LinearLayoutManager(DetailActivity.this));
        mFavoriteAdapter = new FavoriteAdapter(FavoriteList, this);
        recyclerView2.setAdapter(mFavoriteAdapter);


        setContentView(R.layout.activity_detail);
        mNoReview = (TextView) findViewById(R.id.NoReview_tv);
        mButton = findViewById(R.id.DetailsaveButton);
        mButton.setOnClickListener(this);

        if (savedInstanceState == null || !savedInstanceState.containsKey(Constants.BUNDLE_KEY)) {
            Intent intent = getIntent();
            if (intent.hasExtra("original_name")) {
                title = intent.getStringExtra("original_name");
                releaseDate = intent.getStringExtra("release_date");
                userRating = intent.getStringExtra("user_rating");
                overView = intent.getStringExtra("overview");
                poster = intent.getStringExtra("backdrop_path");
                id = intent.getStringExtra("id");
                backdrop = intent.getStringExtra("poster_image");


                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                FetchReviews(id);
                FetchTrailer(id);


                populateUI(movies);

            }
            mDb = MovieDatabase.getInstance(getApplicationContext());


            initializeFavoriteButton(id);


        }

    }


    public void SaveFavorite() {
//        String title= movies.getOriginalName();
//        String id=movies.getId();
//        String poster=movies.getPosterImage();
        final FavoriteEntry favoriteEntry = new FavoriteEntry(title, id, backdrop);
        MovieExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.favoriteDao().insertFavorite(favoriteEntry);


            }
        });

    }

    private void FetchReviews(String movie_id) {
        RetrofitService retrofitService = new RetrofitService();
        ServiceInterface serviceInterface = retrofitService.getRetrofit().create(ServiceInterface.class);
        Call<BasicReview> call = serviceInterface.getReviews(movie_id, Constants.API_KEY);
        call.enqueue(new Callback<BasicReview>() {
            @Override
            public void onResponse(Call<BasicReview> call, Response<BasicReview> response) {
                final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.Review_rv);
                recyclerView.setLayoutManager(new LinearLayoutManager(DetailActivity.this));
                reviewList = response.body().getResults();
                if (reviewList.isEmpty()) {
                    mNoReview.setVisibility(View.VISIBLE);
                }
                mReviewAdapter = new ReviewAdapter(reviewList, getApplicationContext());
                recyclerView.setAdapter(mReviewAdapter);

            }

            @Override
            public void onFailure(Call<BasicReview> call, Throwable t) {

            }
        });
    }

    private void FetchTrailer(String movie_id) {
        RetrofitService retrofitService = new RetrofitService();
        ServiceInterface serviceInterface = retrofitService.getRetrofit().create(ServiceInterface.class);
        Call<BasicTrailer> call = serviceInterface.getTrailer(movie_id, Constants.API_KEY);
        call.enqueue(new Callback<BasicTrailer>() {
            @Override
            public void onResponse(Call<BasicTrailer> call, Response<BasicTrailer> response) {
                final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.Trailer_rv);
                recyclerView.setLayoutManager(new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
                TrailerList = response.body().getResults();
                mTrailerAdapter = new TrailerAdapter(TrailerList, getApplicationContext(), poster);
                recyclerView.setAdapter(mTrailerAdapter);

            }

            @Override
            public void onFailure(Call<BasicTrailer> call, Throwable t) {

            }
        });
    }

    private void populateUI(Movie movie) {

        ImageView Imageshown = findViewById(R.id.Poster_tv);

        mTitle = (TextView) findViewById(R.id.Original_tv);
        mDescription = (TextView) findViewById(R.id.Overview_tv);
        mUserRating = (TextView) findViewById(R.id.Vote_tv);
        mReleaseDate = (TextView) findViewById(R.id.Release_tv);
        mReviewContent = (TextView) findViewById(R.id.Reviews_tv);

        mTitle.setText(title);
        mDescription.setText(overView);
        mUserRating.setText(userRating);
        mReleaseDate.setText(releaseDate);

        Picasso.with(this).load(poster).into(Imageshown);


    }

    private void Delete() {
        final FavoriteEntry favoriteEntry = new FavoriteEntry(title, id, backdrop);

        mDb.favoriteDao().deleteFavorite(id);

    }



    private void initializeFavoriteButton(final String movieId) {
       DetailViewModel detailViewModel= ViewModelProviders.of(this).get(DetailViewModel.class);
        detailViewModel.getFavorites().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> strings) {
                if (strings.isEmpty()) {
                    isFavorite = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mButton.setText(Constants.AddFav);
                        }
                    });
                } else {
                    isFavorite = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mButton.setText(Constants.DeleteFav);
                        }
                    });
                }
            }
        });
    }


    private void favoriteButtonHandler(String movieId) {
        if (isFavorite) {
            Log.d(Tag, "deleting favorite");
            Delete();
            isFavorite=false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mButton.setText(Constants.AddFav);
                    Snackbar.make(findViewById(R.id.coordinate), "Movie deleted from Favorites", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mButton.setText(Constants.DeleteFav);
                            SaveFavorite();
                        }
                    }).show();
                }
            });


        } else {
            SaveFavorite();
            isFavorite=true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mButton.setText(Constants.DeleteFav);
                    Snackbar.make(findViewById(R.id.coordinate), "Movie added to Favorites", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mButton.setText(Constants.DeleteFav);
                            Delete();
                        }
                    }).show();
                }
            });

        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.DetailsaveButton:
                MovieExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {

                        favoriteButtonHandler(id);
                    }

                });

                break;

        }
    }
}
