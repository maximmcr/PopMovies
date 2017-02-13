package com.example.android.popmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailedActivity extends AppCompatActivity {

    MovieInfo movieInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        if (savedInstanceState == null || !savedInstanceState.containsKey("movie")) {
            String id = getIntent().getCharSequenceExtra("id").toString();
            new FetchDetailedMovieInfo().execute(id);
        }
        else {
            movieInfo = savedInstanceState.getParcelable("movie");
            updateInfo();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void updateInfo() {
        ImageView img = (ImageView) findViewById(R.id.detail_image);
        Picasso.with(getApplicationContext())
                .load("http://image.tmdb.org/t/p/w342/" + movieInfo.posterId)
                .into(img);

        TextView title = (TextView) findViewById(R.id.detail_title);
        title.setText(movieInfo.title);

        TextView tagline = (TextView) findViewById(R.id.detail_tagline);
        tagline.setText(movieInfo.tagline);

        TextView adult = (TextView) findViewById(R.id.detail_adult);
        adult.setText(movieInfo.adult);

        TextView overview = (TextView) findViewById(R.id.detail_overview);
        overview.setText(movieInfo.overview);

        TextView popularity = (TextView) findViewById(R.id.detail_popularity);
        popularity.setText(getIntent().getStringExtra("popularity"));

        TextView rating = (TextView) findViewById(R.id.detail_rating);
        rating.setText(getIntent().getStringExtra("rating"));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("movie", movieInfo);
        super.onSaveInstanceState(outState);
    }

    public class FetchDetailedMovieInfo extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            final String API_KEY = BuildConfig.API_KEY;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonData = null;
            try {
                Uri request = Uri.parse(getString(R.string.tmdb_address)).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter("api_key", API_KEY)
                        .build();
                URL url = new URL(request.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream stream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                jsonData = reader.readLine();
            } catch (IOException e) {
                Log.e(DetailedActivity.class.getSimpleName(), "Class haven't get info", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return jsonData;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject movie = new JSONObject(s);

                movieInfo = new MovieInfo(movie.getString("title"),
                        movie.getString("poster_path"),
                        "For adults only: " + movie.getString("adult") == "false" ? "No" : "18+",
                        movie.getString("tagline"),
                        movie.getString("overview"),
                        getIntent().getDoubleExtra("rating", 0.0d),
                        getIntent().getDoubleExtra("popularity", 0.0d));
                updateInfo();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
