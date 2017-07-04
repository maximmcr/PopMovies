package com.example.android.popmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

    MovieInfoAdapter movieInfoAdapter;
    ArrayList<MovieInfo> movies;
    public final String CLASS_TAG = MainActivity.class.getSimpleName();
    private String mSortingType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            movies = new ArrayList<>();
            updateMovieInfo();
        } else {
            movies = savedInstanceState.getParcelableArrayList("movies");
        }

        RecyclerView view = (RecyclerView) findViewById(R.id.activity_main);
        movieInfoAdapter = new MovieInfoAdapter(movies, MainActivity.this);
        view.setLayoutManager(new LLMWrapper(this, 2));
        view.setAdapter(movieInfoAdapter);

        mSortingType = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext())
                .getString(getString(R.string.pref_list_key), getString(R.string.pref_list_default));
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(CLASS_TAG, "onStart");
        //updateMovieInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
        String currentSorting = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext())
                .getString(getString(R.string.pref_list_key), getString(R.string.pref_list_default));
        if (mSortingType != currentSorting) {
            mSortingType = currentSorting;
            updateMovieInfo();
        }
        Log.i(CLASS_TAG, "onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(CLASS_TAG, "onStop");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(CLASS_TAG, "onPause");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", movies);
        super.onSaveInstanceState(outState);
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

    public void updateMovieInfo() {
        if (isOnline()) {
            String option = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext())
                    .getString(getString(R.string.pref_list_key), getString(R.string.pref_list_default));
            new FetchMovieInfo().execute(option);
        } else {
            Snackbar.make(findViewById(R.id.activity_main), "There is no internet connection!", Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public class FetchMovieInfo extends AsyncTask<String, Void, ArrayList<MovieInfo>> {
        @Override
        protected ArrayList<MovieInfo> doInBackground(String... params) {

            final String API_KEY = BuildConfig.API_KEY;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonMovieInfo = null;

            try {
                Uri TMDBrequest = Uri.parse(getString(R.string.tmdb_address)).buildUpon()
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
            ArrayList<MovieInfo> result = null;
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

        private ArrayList<MovieInfo> getMovieInfoFromJson(String jsonStr) throws JSONException {
            ArrayList<MovieInfo> moviesInfo = new ArrayList<>();
            try {
                JSONArray movieList = new JSONObject(jsonStr).getJSONArray("results");
                for (int i = 0; i < movieList.length(); i++) {
                    JSONObject jsonMovie = movieList.getJSONObject(i);

                    String overview = jsonMovie.getString("overview");
                    String title = jsonMovie.getString("title");
                    String posterId = jsonMovie.getString("poster_path");
                    String releaseDate = jsonMovie.getString("release_date");
                    double popularity = Math.round(jsonMovie.getDouble("popularity") * 100d) / 100d;
                    double rating = jsonMovie.getDouble("vote_average");
                    int id = jsonMovie.getInt("id");

                    MovieInfo movie = new MovieInfo(title, posterId, releaseDate,
                            overview, rating, popularity, id);
                    moviesInfo.add(movie);
                }
            } catch (JSONException e) {
                Log.e("JSON Formatter", "Json string from asynctask not formatted", e);
            }
            return moviesInfo;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieInfo> moviesInfo) {
            super.onPostExecute(moviesInfo);
            if (movieInfoAdapter.getItemCount() > 0) {
                movieInfoAdapter.clear();
            }
            movieInfoAdapter.addAll(moviesInfo);
        }

    }

    public class LLMWrapper extends GridLayoutManager {

        public LLMWrapper(Context context, int spanCount) {
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
