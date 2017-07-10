package com.example.android.popmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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

public class DetailedActivity extends AppCompatActivity {

    MovieInfo movieInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        if (savedInstanceState == null || !savedInstanceState.containsKey("movie")) {
            String id = getIntent().getStringExtra("id");
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
                .load("http://image.tmdb.org/t/p/w342/" + movieInfo.mPoster)
                .into(img);

        TextView title = (TextView) findViewById(R.id.detail_title);
        title.setText(movieInfo.mTitle);

        TextView tagline = (TextView) findViewById(R.id.detail_tagline);
        tagline.setText(movieInfo.mTagline);

        TextView releaseDate = (TextView) findViewById(R.id.detail_date);
        releaseDate.setText(getString(
                R.string.format_release_date,
                Utility.formatDate(movieInfo.mReleaseDate)));

        TextView runtime = (TextView) findViewById(R.id.detail_runtime);
        runtime.setText(getString(R.string.format_runtime, movieInfo.mRuntime));

        TextView popularity = (TextView) findViewById(R.id.detail_popularity);
        popularity.setText(getString(R.string.format_popular, movieInfo.mPopularity));

        TextView rating = (TextView) findViewById(R.id.detail_rating);
        rating.setText(getString(R.string.format_rating, movieInfo.mRating));

        TextView overview = (TextView) findViewById(R.id.detail_overview);
        overview.setText(movieInfo.mOverview);

        ViewPager vp = (ViewPager) findViewById(R.id.detail_comment_viewpager);
        CommentVPAdapter vpAdapter =
                new CommentVPAdapter(getApplicationContext(), movieInfo.mComments);
        vp.setAdapter(vpAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("movie", movieInfo);
        super.onSaveInstanceState(outState);
    }

    public class FetchDetailedMovieInfo extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            final String API_KEY = BuildConfig.API_KEY_TMDB;
            HttpURLConnection urlConnectionBody = null;
            HttpURLConnection urlConnectionComment = null;
            BufferedReader readerBody = null;
            BufferedReader readerComment = null;
            String[] jsonData = null;
            try {
                Uri request = Uri.parse(getString(R.string.tmdb_request_adress)).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter("api_key", API_KEY)
                        .build();
                URL url = new URL(request.toString());
                urlConnectionBody = (HttpURLConnection) url.openConnection();
                urlConnectionBody.setRequestMethod("GET");
                urlConnectionBody.connect();

                InputStream stream = urlConnectionBody.getInputStream();
                readerBody = new BufferedReader(new InputStreamReader(stream));
                jsonData = new String[2];
                jsonData[0] = new String();
                jsonData[0] = readerBody.readLine();

                request = Uri.parse(getString(R.string.tmdb_request_adress)).buildUpon()
                        .appendPath(params[0])
                        .appendPath("reviews")
                        .appendQueryParameter("api_key", API_KEY)
                        .build();
                url = new URL(request.toString());

                urlConnectionComment = (HttpURLConnection) url.openConnection();
                urlConnectionComment.setRequestMethod("GET");
                urlConnectionComment.connect();
                stream = urlConnectionComment.getInputStream();
                readerComment = new BufferedReader(new InputStreamReader(stream));

                jsonData[1] = readerComment.readLine();
            } catch (IOException e) {
                Log.e(DetailedActivity.class.getSimpleName(), "Class haven't get info", e);
            } finally {
                if (urlConnectionBody != null) {
                    urlConnectionBody.disconnect();
                }
                if (urlConnectionComment != null) {
                    urlConnectionComment.disconnect();
                }
                try {
                    readerBody.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    readerComment.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return jsonData;
        }

        @Override
        protected void onPostExecute(String s[]) {
            super.onPostExecute(s);
            try {
                JSONObject movie = new JSONObject(s[0]);

                JSONArray jComments = new JSONObject(s[1]).getJSONArray("results");
                ArrayList<MovieInfo.Comment> comments = new ArrayList<>();
                for (int i = 0; i < jComments.length(); i++) {
                    JSONObject obj = jComments.getJSONObject(i);
                    MovieInfo.Comment comment = new MovieInfo.Comment(
                            obj.getString("author"),
                            obj.getString("content"),
                            obj.getString("url")
                    );
                    comments.add(comment);
                }

                // TODO fetch @youtubeAdresses
                ArrayList<String> youtubeAdresses = new ArrayList<>();

                movieInfo = new MovieInfo(
                        movie.getInt("id"),
                        movie.getString("title"),
                        movie.getString("tagline"),
                        movie.getString("poster_path"),
                        movie.getString("release_date"),
                        movie.getInt("runtime"),
                        movie.getDouble("vote_average"),
                        movie.getDouble("popularity"),
                        movie.getString("overview"),
                        comments,
                        youtubeAdresses
                        );

                Log.i("onPostExecute", movie.getString("tagline"));
                updateInfo();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
