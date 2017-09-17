package com.example.android.popmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    static MovieInfoAdapter movieInfoAdapter;
    private ArrayList<MovieModel> mMovies;

    private String mSortingType;

    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_POSTER
    };
    private static final int COLUMN_ID = 0;
    private static final int COLUMN_POSTER = 1;

    private static final String SAVED_MOVIE_TAG = "mMovies";
    private static final String SAVED_SORTING_TAG = "sorting";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "onCreate");
        if (savedInstanceState != null) {
            mMovies = savedInstanceState.getParcelableArrayList(SAVED_MOVIE_TAG);
            movieInfoAdapter = new MovieInfoAdapter(mMovies, this);
            mSortingType = savedInstanceState.getString(SAVED_SORTING_TAG);
        } else {
            mMovies = new ArrayList<>();
            movieInfoAdapter = new MovieInfoAdapter(mMovies, this);

            mSortingType = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext())
                    .getString(getString(R.string.pref_list_key), getString(R.string.pref_list_default));
            updateMovieInfo();
        }

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.activity_main);
        mRecyclerView.setLayoutManager(new WrappedGLM(getApplicationContext(), 2));
        mRecyclerView.setAdapter(movieInfoAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
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
            movieInfoAdapter.clearAll();
            updateMovieInfo();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState");
        outState.putParcelableArrayList(SAVED_MOVIE_TAG, mMovies);
        outState.putString(SAVED_SORTING_TAG, mSortingType);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(LOG_TAG, "onRestoreInstanceState");
        //mMovies = savedInstanceState.getParcelableArrayList("mMovies");
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
        Log.d(LOG_TAG, "query to db started");

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

        Log.d(LOG_TAG, "query to db ended");
        return result;
    }

    private void updateMovieInfo() {
        if (Utility.isOptionSaved(this)) {
            movieInfoAdapter.addAll(getMovieListFromDB());
        } else if (Utility.isOnline(this)) {
            new FetchMovieInfo().execute(mSortingType);
        } else {
            Snackbar.make(findViewById(R.id.activity_main), R.string.snackbar_no_internet, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    private class FetchMovieInfo extends AsyncTask<String, Void, ArrayList<MovieModel>> {

        private static final String TMDB_REQUEST_BASE = "http://api.themoviedb.org/3/movie/";
        @Override
        protected ArrayList<MovieModel> doInBackground(String... params) {

            final String API_KEY = BuildConfig.API_KEY_TMDB;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonMovieInfo = null;

            try {
                Uri TMDBrequest = Uri.parse(TMDB_REQUEST_BASE).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter("api_key", API_KEY)
                        .build();

                URL url = new URL(TMDBrequest.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream stream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                jsonMovieInfo = reader.readLine();

            } catch (IOException e) {
                Log.e("FetchMovieInfo", "Class haven't get info", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e("FetchMovieInfo", "Error closing reader", e);
                    }
                }
            }
            ArrayList<MovieModel> result = null;
            if (jsonMovieInfo != null) {
                try {
                    Log.i("doInBackground", "movie info is starting transferring");
                    result = getMovieInfoFromJson(jsonMovieInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        private ArrayList<MovieModel> getMovieInfoFromJson(String jsonStr) throws JSONException {
            ArrayList<MovieModel> moviesInfo = new ArrayList<>();
            try {
                JSONArray movieList = new JSONObject(jsonStr).getJSONArray("results");
                for (int i = 0; i < movieList.length(); i++) {
                    JSONObject jsonMovie = movieList.getJSONObject(i);

                    String posterId = jsonMovie.getString("poster_path");
                    int id = jsonMovie.getInt("id");

                    moviesInfo.add(new MovieModel(posterId, id));
                }
            } catch (JSONException e) {
                Log.e("JSON Formatter", "Json string from asynctask not formatted", e);
            }
            return moviesInfo;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieModel> moviesInfo) {
            super.onPostExecute(moviesInfo);
//            if (movieInfoAdapter.getItemCount() > 0) {
//                movieInfoAdapter.clearAll();
//            }
            movieInfoAdapter.addAll(moviesInfo);
            movieInfoAdapter.notifyDataSetChanged();
        }

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
