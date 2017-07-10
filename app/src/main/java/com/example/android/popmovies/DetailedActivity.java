package com.example.android.popmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
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

        ListView lv = (ListView) findViewById(R.id.detail_video_listview);
        VideoAdapter videoAdapter =
                new VideoAdapter(getApplicationContext(), movieInfo.mYoutubeAdresses);
        lv.setAdapter(videoAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("movie", movieInfo);
        super.onSaveInstanceState(outState);
    }

    public class FetchDetailedMovieInfo extends AsyncTask<String, Void, String[]> {

        private static final String TMDB_REQUEST_BASE = "http://api.themoviedb.org/3/movie/";
        @Override
        protected String[] doInBackground(String... params) {
            final String API_KEY = BuildConfig.API_KEY_TMDB;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String[] jsonData = null;
            try {
                jsonData = new String[3];

                //Request for movie's details
                Uri request = Uri.parse(TMDB_REQUEST_BASE).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter("api_key", API_KEY)
                        .build();
                URL url = new URL(request.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream stream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                jsonData[0] = reader.readLine();

                urlConnection.disconnect();
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Request for movie's reviews
                request = Uri.parse(TMDB_REQUEST_BASE).buildUpon()
                        .appendPath(params[0])
                        .appendPath("reviews")
                        .appendQueryParameter("api_key", API_KEY)
                        .build();
                url = new URL(request.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                stream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                jsonData[1] = reader.readLine();

                urlConnection.disconnect();
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Request for movie's video materials(trailers, teasers etc.)
                request = Uri.parse(TMDB_REQUEST_BASE).buildUpon()
                        .appendPath(params[0])
                        .appendPath("videos")
                        .appendQueryParameter("api_key", API_KEY)
                        .build();
                url = new URL(request.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                stream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                jsonData[2] = reader.readLine();
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
                ArrayList<MovieInfo.Video> youtubeAdresses = new ArrayList<>();
                JSONArray jVideos = new JSONObject(s[2]).getJSONArray("results");
                for (int i = 0; i < jVideos.length(); i++) {
                    JSONObject obj = jVideos.getJSONObject(i);
                    MovieInfo.Video video = new MovieInfo.Video(
                            obj.getString("key"),
                            obj.getString("name"),
                            obj.getString("type")
                    );
                    youtubeAdresses.add(video);
                }

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
