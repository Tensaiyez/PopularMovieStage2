package com.example.tensaiye.popularmovie;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tensaiye.popularmovie.API.RetrofitService;
import com.example.tensaiye.popularmovie.API.ServiceInterface;
import com.example.tensaiye.popularmovie.Adapters.FavoriteAdapter;
import com.example.tensaiye.popularmovie.Adapters.MovieAdapter;
import com.example.tensaiye.popularmovie.Database.FavoriteEntry;
import com.example.tensaiye.popularmovie.Database.MovieDatabase;
import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private static final String TAG = MainActivity.class.getSimpleName();
    private MovieAdapter mAdapter;
    private FavoriteAdapter favoriteAdapter;
    RecyclerView recyclerView;
    private List<Movie> movies;
    private List<Review> reviewList;
    private int SpinnerPos = 0;
    private MovieDatabase mDb;
    private String RETREIVE="retreive";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);
        Spinner spinner = findViewById(R.id.spinner_m);
        recyclerView = (RecyclerView) findViewById(R.id.MovieList);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.SortArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
//                MovieExecutors.getInstance().diskIO().execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        int position = viewHolder.getAdapterPosition();
//                        List<FavoriteEntry> favEntry = favoriteAdapter.getFavoriteList();
//                        mDb.favoriteDao().deleteFavorite(favEntry.get(position));
//
//                        retrieveFavorites();
//                    }
//                });
//            }
//        }).attachToRecyclerView(recyclerView);


        if (savedInstanceState == null || !savedInstanceState.containsKey(Constants.MOVIEBUNDLE)) {
            if (isOnline()) {
                FetchFromTMDB(Constants.Popular);


                spinner.setOnItemSelectedListener(this);
            } else if (!isOnline()) {
                Toast.makeText(this, "No Internet Connection...Please Connect To The Internet", Toast.LENGTH_SHORT).show();
            }


        } else {
            movies = savedInstanceState.getParcelableArrayList(Constants.MOVIEBUNDLE);
        }
        mDb = MovieDatabase.getInstance(getApplicationContext());

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_xml, menu);
        return true;
    }


    public void FetchFromTMDB(String sort) {
        RetrofitService retrofitService = new RetrofitService();
        ServiceInterface serviceInterface = retrofitService.getRetrofit().create(ServiceInterface.class);
        Call<Basicmovie> call = serviceInterface.getMovies(sort, Constants.API_KEY);
        call.enqueue(new Callback<Basicmovie>() {
            @Override
            public void onResponse(Call<Basicmovie> call, Response<Basicmovie> response) {
                recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2, GridLayoutManager.VERTICAL, false));
                movies = response.body().getResults();
                mAdapter = new MovieAdapter(movies, R.layout.moviecard, getApplicationContext());
                recyclerView.setAdapter(mAdapter);

            }


            @Override
            public void onFailure(Call<Basicmovie> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });


    }

    public void FetchFromDatabase() {
        List<FavoriteEntry> favEntry = new ArrayList<>();
        favoriteAdapter = new FavoriteAdapter(favEntry, getApplicationContext());
        setViewModel();

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(favoriteAdapter);
    }

    private void setViewModel() {
       MainViewModel viewModel= ViewModelProviders.of(this).get(MainViewModel.class);

        viewModel.getFavorites().observe(this, new Observer<List<FavoriteEntry>>() {
            @Override
            public void onChanged(@Nullable List<FavoriteEntry> favoriteEntries) {
                Log.d(TAG,"Updataing list of favorites from ViewModel ");
                favoriteAdapter.setFavoriteList(favoriteEntries);
            }
        });


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String word = parent.getItemAtPosition(position).toString();
        if (word.equals(Constants.Popular_xml_Val)) {
            FetchFromTMDB(Constants.Popular);
            TextView popular_tv = MainActivity.this.findViewById(R.id.PopularTitle);
            popular_tv.setVisibility(view.VISIBLE);
            popular_tv.setText(Constants.Popular_xml_Val);
            TextView Highest_tv = MainActivity.this.findViewById(R.id.HighestTitle);
            Highest_tv.setVisibility(view.INVISIBLE);
        } else if (word.equals(Constants.TopRated_xml_Val)) {
            FetchFromTMDB(Constants.TopRated);
            TextView Highest_tv = MainActivity.this.findViewById(R.id.HighestTitle);
            Highest_tv.setVisibility(view.VISIBLE);
            TextView popular_tv = MainActivity.this.findViewById(R.id.PopularTitle);
            popular_tv.setVisibility(view.INVISIBLE);
        } else if (word.equals(Constants.Favorite_xml_Val)) {
            FetchFromDatabase();
            TextView popular_tv = MainActivity.this.findViewById(R.id.PopularTitle);
            popular_tv.setVisibility(view.VISIBLE);
            popular_tv.setText(Constants.Favorite_xml_Val);
            TextView Highest_tv = MainActivity.this.findViewById(R.id.HighestTitle);
            Highest_tv.setVisibility(view.INVISIBLE);
        }

//        if (SpinnerPos == position) {
//            FetchFromTMDB(Constants.Popular);
//            TextView popular_tv = MainActivity.this.findViewById(R.id.PopularTitle);
//            popular_tv.setVisibility(view.VISIBLE);
//            TextView Highest_tv = MainActivity.this.findViewById(R.id.HighestTitle);
//            Highest_tv.setVisibility(view.INVISIBLE);
//            popular_tv.setText("Most Popular");
//            ;
//        }

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // The method below checks if the phone is connected to a network.
    //https://developer.android.com/training/monitoring-device-state/connectivity-monitoring#java This site has helped me find a way in which to check the Network Status of the app.
    public boolean isOnline() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkStatus = connectivityManager.getActiveNetworkInfo();
        return networkStatus != null;


    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.DeleteFavorite:
//                MovieExecutors.getInstance().diskIO().execute(new Runnable() {
//                    @Override
//                    public void run() {
//
//
//
//                        List<FavoriteEntry> favEntry = favoriteAdapter.getFavoriteList();
//                        mDb.favoriteDao().deleteFavorite(favEntry.get(position));
//
//                        retrieveFavorites();
//                    }
//                });
//                Toast.makeText(this, "Movie added to favorites", Toast.LENGTH_SHORT).show();
//                break;
//        }
//
//    }
}





