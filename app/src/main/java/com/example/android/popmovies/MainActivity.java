package com.example.android.popmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popmovies.data.MoviesContract;
import com.example.android.popmovies.model.MovieModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    static MovieInfoAdapter mMovieInfoAdapter;
    private ArrayList<MovieModel> mMovies;
    @BindView(R.id.activity_main)
    RecyclerView mRecyclerView;

    private String mSortingType;

    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_POSTER
    };
    private static final int COLUMN_ID = 0;
    private static final int COLUMN_POSTER = 1;

    private static final String SAVED_MOVIE_TAG = "mMovies";
    private static final String SAVED_SORTING_TAG = "sorting";

    // TODO: 21.09.2017 add loading circle to show data is loading
    // TODO: 21.09.2017 add ability to refresh page 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_MOVIE_TAG)) {
            mMovies = savedInstanceState.getParcelableArrayList(SAVED_MOVIE_TAG);
            mMovieInfoAdapter = new MovieInfoAdapter(mMovies, this);
            mSortingType = savedInstanceState.getString(SAVED_SORTING_TAG);
        } else {
            mMovies = new ArrayList<>();
            mMovieInfoAdapter = new MovieInfoAdapter(mMovies, this);

            mSortingType = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext())
                    .getString(getString(R.string.pref_list_key), getString(R.string.pref_list_default));
            updateMovieInfo();
        }
        mRecyclerView.setLayoutManager(new WrappedGLM(getApplicationContext(), 2));
        mRecyclerView.setAdapter(mMovieInfoAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        String newOption = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext())
                .getString(getString(R.string.pref_list_key), getString(R.string.pref_list_default));
        if (!newOption.equals(mSortingType)) {
            mSortingType = newOption;
            updateMovieInfo();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState");
        if (!Utility.isOptionSaved(this)) {
            outState.putParcelableArrayList(SAVED_MOVIE_TAG, mMovies);
            outState.putString(SAVED_SORTING_TAG, mSortingType);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return true;
    }

    private ArrayList<MovieModel> getMovieListFromDB() {
        Cursor c = getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                null,
                null,
                null
        );

        ArrayList<MovieModel> result = new ArrayList<>();
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            int id = c.getInt(COLUMN_ID);
            String poster = Utility.byteArrayToString(c.getBlob(COLUMN_POSTER));
            result.add(new MovieModel(poster, id));
            c.moveToNext();
        }
        return result;
    }

    private void updateMovieInfo() {
        mMovies.clear();
        mMovieInfoAdapter.notifyDataSetChanged();
        if (Utility.isOptionSaved(this)) {
            mMovies.addAll(getMovieListFromDB());
            mMovieInfoAdapter.notifyDataSetChanged();
        } else if (Utility.isOnline(this)) {
            getMovieList(mSortingType);
        } else {
            Snackbar.make(findViewById(R.id.activity_main), R.string.snackbar_no_internet, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    private void getMovieList(String sortingType) {
        final String API_KEY = BuildConfig.API_KEY_TMDB;
        PopMoviesApplication.getTmdbApi()
                .getMovieList(sortingType, API_KEY)
                .enqueue(new Callback<MovieModel.Response>() {
                    @Override
                    public void onResponse(Call<MovieModel.Response> call, Response<MovieModel.Response> response) {
                        mMovies.addAll(response.body().movies);
                        mMovieInfoAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<MovieModel.Response> call, Throwable t) {
                        Snackbar.make(
                                findViewById(R.id.activity_main),
                                R.string.snackbar_no_internet,
                                Snackbar.LENGTH_LONG
                        ).show();
                        Log.d(LOG_TAG, "Retrofit method " + t);
                    }
                });
    }

    public class WrappedGLM extends GridLayoutManager {
        public WrappedGLM(Context context, int spanCount) {
            super(context, spanCount);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("probe", "meet a IOOBE in RecyclerView");
            }
        }
    }
}
