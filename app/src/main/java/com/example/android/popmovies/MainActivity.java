package com.example.android.popmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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

    MovieInfoAdapter movieInfoAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView view = (RecyclerView) findViewById(R.id.activity_main);
        FetchMovieInfo task = new FetchMovieInfo();
        movieInfoAdapter = new MovieInfoAdapter(new ArrayList<MovieInfo>());
        task.execute("top_rated");
        view.setAdapter(movieInfoAdapter);
    }

    public class FetchMovieInfo extends AsyncTask<String, Void, ArrayList<MovieInfo>> {
        @Override
        protected ArrayList<MovieInfo> doInBackground(String... params) {

            final String API_KEY = "bf9bf395590685ddee545dfac0fac978";

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonMovieInfo = null;

            try {
                Uri TMDBrequest = Uri.parse("http://api.themoviedb.org/3/movie/").buildUpon()
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

            }
            catch (IOException e) {
                Log.e("FetchMovieInfo", "Class haven't get info", e);
            }
            finally {
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
            if (jsonMovieInfo != null) {
                try {
                    return getMovieInfoFromJson(jsonMovieInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
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
                    double popularity = jsonMovie.getDouble("popularity");
                    double rating = jsonMovie.getDouble("vote_average");
                    int id = jsonMovie.getInt("id");

                    MovieInfo movie = new MovieInfo(title,posterId,releaseDate,
                            overview,rating,popularity,id);
                    moviesInfo.add(movie);
                }
            }
            catch (JSONException e) {
                Log.e("JSON Formatter", "Json string from asynctask not formatted", e);
            }
            return moviesInfo;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieInfo> moviesInfo) {
            super.onPostExecute(moviesInfo);
            movieInfoAdapter.clear();
            movieInfoAdapter.addAll(moviesInfo);
        }


    }
}
