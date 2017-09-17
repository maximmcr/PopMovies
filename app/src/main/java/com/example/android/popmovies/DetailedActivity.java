package com.example.android.popmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.popmovies.data.MoviesContract;
import com.example.android.popmovies.model.CommentModel;
import com.example.android.popmovies.model.MovieModel;
import com.example.android.popmovies.model.VideoModel;
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
import java.util.Vector;

public class DetailedActivity extends AppCompatActivity {

    public static final String LOG_TAG = DetailedActivity.class.getSimpleName();

    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_TAGLINE,
            MoviesContract.MovieEntry.COLUMN_POSTER,
            MoviesContract.MovieEntry.COLUMN_DATE,
            MoviesContract.MovieEntry.COLUMN_RUNTIME,
            MoviesContract.MovieEntry.COLUMN_RATING,
            MoviesContract.MovieEntry.COLUMN_POPULARITY,
            MoviesContract.MovieEntry.COLUMN_OVERVIEW
    };
    private static final int COLUMN_MOVIE_ID = 0;
    private static final int COLUMN_MOVIE_TITLE = 1;
    private static final int COLUMN_MOVIE_TAGLINE = 2;
    private static final int COLUMN_MOVIE_POSTER = 3;
    private static final int COLUMN_MOVIE_DATE = 4;
    private static final int COLUMN_MOVIE_RUNTIME = 5;
    private static final int COLUMN_MOVIE_RATING = 6;
    private static final int COLUMN_MOVIE_POPULARITY = 7;
    private static final int COLUMN_MOVIE_OVERVIEW = 8;

    private static final String[] COMMENT_COLUMNS = {
            MoviesContract.CommentEntry.COLUMN_MOVIE_KEY,
            MoviesContract.CommentEntry.COLUMN_URL,
            MoviesContract.CommentEntry.COLUMN_CONTENT,
            MoviesContract.CommentEntry.COLUMN_AUTHOR
    };
    private static final int COLUMN_COMMENT_KEY = 0;
    private static final int COLUMN_COMMENT_URL = 1;
    private static final int COLUMN_COMMENT_CONTENT = 2;
    private static final int COLUMN_COMMENT_AUTHOR = 3;

    private static final String[] VIDEO_COLUMNS = {
            MoviesContract.VideoEntry.COLUMN_MOVIE_KEY,
            MoviesContract.VideoEntry.COLUMN_NAME,
            MoviesContract.VideoEntry.COLUMN_PATH,
            MoviesContract.VideoEntry.COLUMN_TYPE
    };
    private static final int COLUMN_VIDEO_KEY = 0;
    private static final int COLUMN_VIDEO_NAME = 1;
    private static final int COLUMN_VIDEO_PATH = 2;
    private static final int COLUMN_VIDEO_TYPE = 3;

    private static final String SAVE_MOVIE_TAG = "movie";
    private static final String SAVE_SCROLLVIEW_TAG = "scrollview";

    private MovieModel movieModel;

    private NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        if (savedInstanceState == null || !savedInstanceState.containsKey("movie")) {
            scrollView = (NestedScrollView) findViewById(R.id.detail_scrollview);
            String id = getIntent().getStringExtra("id");
            if (Utility.isOptionSaved(getApplicationContext())) {
                fetchMovieInfoFromDB(Long.parseLong(id));
                initializeAndUpdateInfo();
            } else {
                new FetchDetailedMovieInfo().execute(id);
            }
        }
        else {
            movieModel = savedInstanceState.getParcelable(SAVE_MOVIE_TAG);
            initializeAndUpdateInfo();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initializeAndUpdateInfo() {
        ImageView img = (ImageView) findViewById(R.id.detail_image);
        if (Utility.isOptionSaved(getApplicationContext())) {
            img.setImageBitmap(Utility.stringToBitmap(movieModel.getPosterPath()));
        } else {
            Picasso.with(getApplicationContext())
                    .load("http://image.tmdb.org/t/p/w342/" + movieModel.getPosterPath())
                    .into(img);
        }

        TextView title = (TextView) findViewById(R.id.detail_title);
        title.setText(movieModel.getTitle());

        TextView tagline = (TextView) findViewById(R.id.detail_tagline);
        tagline.setText(movieModel.getTitle());

        TextView releaseDate = (TextView) findViewById(R.id.detail_date);
        releaseDate.setText(getString(
                R.string.format_release_date,
                Utility.formatDate(movieModel.getReleaseDate())));

        TextView runtime = (TextView) findViewById(R.id.detail_runtime);
        runtime.setText(getString(R.string.format_runtime, movieModel.getRuntime()));

        TextView popularity = (TextView) findViewById(R.id.detail_popularity);
        popularity.setText(getString(R.string.format_popular, movieModel.getPopularity()));

        TextView rating = (TextView) findViewById(R.id.detail_rating);
        rating.setText(getString(R.string.format_rating, movieModel.getRating()));

        TextView overview = (TextView) findViewById(R.id.detail_overview);
        overview.setText(movieModel.getOverview());

        CommentViewPager vp = (CommentViewPager) findViewById(R.id.detail_comment_viewpager);
        CommentVPAdapter vpAdapter =
                new CommentVPAdapter(getApplicationContext(), movieModel.getComments());
        vp.setAdapter(vpAdapter);


        ListView lv = (ListView) findViewById(R.id.detail_video_listview);
        VideoAdapter videoAdapter =
                new VideoAdapter(getApplicationContext(), movieModel.getVideos());
        lv.setAdapter(videoAdapter);
        setListViewHeightBasedOnChildren(lv);

        final FloatingActionButton fabFavourites = (FloatingActionButton) findViewById(R.id.detail_fab_favourite);
        if (isMovieInDB(movieModel.getId())) fabFavourites.setImageResource(R.drawable.ic_favorite_white_24dp);
        else fabFavourites.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        // TODO: 04.08.2017 refactor onClick for delete action
        fabFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int movieId = movieModel.getId();
                boolean isMovieInDB = isMovieInDB(movieId);
                if (!isMovieInDB) {
                    insertMovieToDB(movieId);
                    fabFavourites.setImageResource(R.drawable.ic_favorite_white_24dp);
                    Snackbar.make(findViewById(R.id.activity_detailed),
                            R.string.snackbar_movie_added, Snackbar.LENGTH_SHORT)
                    .show();
                } else {
                    deleteMovieFromDB(movieId);
                    if (Utility.isOptionSaved(getApplicationContext())) {
                        CallbackMovieRemoved callback = MainActivity.movieInfoAdapter;
                        callback.removeMovie(movieId);
                    }
                    fabFavourites.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    Snackbar.make(findViewById(R.id.activity_detailed),
                            R.string.snackbar_movie_removed, Snackbar.LENGTH_SHORT)
                    .show();
                }
            }
        });

        if (vpAdapter.getCount() == 0 || vpAdapter == null) {
            findViewById(R.id.detail_review_header).setVisibility(View.GONE);
            findViewById(R.id.detail_review_divider).setVisibility(View.GONE);
        }
        if (videoAdapter.getCount() == 0 || videoAdapter == null) {
            findViewById(R.id.detail_video_header).setVisibility(View.GONE);
            findViewById(R.id.detail_video_divider).setVisibility(View.GONE);
        }
    }
    private void setListViewHeightBasedOnChildren(ListView lv) {
        ListAdapter adapter = lv.getAdapter();
        if (adapter == null) {
            Log.d(LOG_TAG, "ListView adapter is null");
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View child = adapter.getView(i, null, lv);
            child.measure(0, 0);
            totalHeight += child.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = lv.getLayoutParams();
        params.height = totalHeight + (lv.getDividerHeight() * (lv.getChildCount() - 1));
        lv.setLayoutParams(params);
    }

    //methods for working with db
    private boolean isMovieInDB(long id) {
        boolean result = false;
        Uri query = MoviesContract.MovieEntry.buildMovieUri(id);
        Cursor cursor;
        cursor = getContentResolver().query(
                query,
                null,
                null,
                null,
                null);
        if (cursor.moveToFirst()) result = true;
        Log.d(LOG_TAG, cursor.toString());
        cursor.close();
        return result;
    }
    private void insertMovieToDB(long id) {
        //inserting base movie info
        ContentValues movieValues = new ContentValues();
        movieValues.put(MoviesContract.MovieEntry._ID, id);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, movieModel.getTitle());
        movieValues.put(MoviesContract.MovieEntry.COLUMN_TAGLINE, movieModel.getTagline());
        ImageView img = (ImageView) findViewById(R.id.detail_image);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER,
                Utility.drawableToByteArray(img.getDrawable()));
        movieValues.put(MoviesContract.MovieEntry.COLUMN_DATE, movieModel.getReleaseDate());
        movieValues.put(MoviesContract.MovieEntry.COLUMN_RUNTIME, movieModel.getRuntime());
        movieValues.put(MoviesContract.MovieEntry.COLUMN_RATING, movieModel.getRating());
        movieValues.put(MoviesContract.MovieEntry.COLUMN_POPULARITY, movieModel.getPopularity());
        movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, movieModel.getOverview());

        getContentResolver().insert(
                MoviesContract.MovieEntry.buildMovieUri(id),
                movieValues);

        //inserting comments
        Vector<ContentValues> commentValues = new Vector<>();
        for (CommentModel commentModel : movieModel.getComments()) {
            ContentValues value = new ContentValues();
            value.put(MoviesContract.CommentEntry.COLUMN_MOVIE_KEY, id);
            value.put(MoviesContract.CommentEntry.COLUMN_AUTHOR, commentModel.getAuthor());
            value.put(MoviesContract.CommentEntry.COLUMN_CONTENT, commentModel.getContent());
            value.put(MoviesContract.CommentEntry.COLUMN_URL, commentModel.getUrl());
            commentValues.add(value);
        }

        getContentResolver().bulkInsert(
                MoviesContract.CommentEntry.buildMovieUri(id),
                commentValues.toArray(new ContentValues[commentValues.size()]));

        //inserting videos
        Vector<ContentValues> videoValues = new Vector<>();
        for (VideoModel videoModel : movieModel.getVideos()) {
            ContentValues value = new ContentValues();
            value.put(MoviesContract.VideoEntry.COLUMN_MOVIE_KEY, id);
            value.put(MoviesContract.VideoEntry.COLUMN_NAME, videoModel.getName());
            value.put(MoviesContract.VideoEntry.COLUMN_PATH, videoModel.getPath());
            value.put(MoviesContract.VideoEntry.COLUMN_TYPE, videoModel.getType());
            videoValues.add(value);
        }

        getContentResolver().bulkInsert(
                MoviesContract.VideoEntry.buildMovieUri(id),
                videoValues.toArray(new ContentValues[videoValues.size()]));
    }
    private void deleteMovieFromDB(long id) {
        int count = getContentResolver().delete(
                MoviesContract.MovieEntry.buildMovieUri(id),
                null,
                null);
        Log.d(LOG_TAG, "Deleted " + count + "rows from db");
    }
    private void fetchMovieInfoFromDB(long id) {

        Cursor commentCursor = getContentResolver().query(
                MoviesContract.CommentEntry.buildMovieUri(id),
                COMMENT_COLUMNS,
                null,
                null,
                null
        );
        commentCursor.moveToFirst();
        ArrayList<CommentModel> commentModels = new ArrayList<>();
        for (int i = 0; i < commentCursor.getCount(); i++) {
            String author = commentCursor.getString(COLUMN_COMMENT_AUTHOR);
            String content = commentCursor.getString(COLUMN_COMMENT_CONTENT);
            String url = commentCursor.getString(COLUMN_COMMENT_URL);
            CommentModel commentModel = new CommentModel(
                    author,
                    content,
                    url
            );
            commentModels.add(commentModel);
            commentCursor.moveToNext();
        }
        commentCursor.close();

        Cursor videoCursor = getContentResolver().query(
                MoviesContract.VideoEntry.buildMovieUri(id),
                VIDEO_COLUMNS,
                null,
                null,
                null
        );
        videoCursor.moveToFirst();
        ArrayList<VideoModel> videoModels = new ArrayList<>();
        for (int i = 0; i < videoCursor.getCount(); i++) {
            String path = videoCursor.getString(COLUMN_VIDEO_PATH);
            String name = videoCursor.getString(COLUMN_VIDEO_NAME);
            String type = videoCursor.getString(COLUMN_VIDEO_TYPE);
            VideoModel videoModel = new VideoModel(
                    path,
                    name,
                    type
            );
            videoModels.add(videoModel);
            videoCursor.moveToNext();
        }
        videoCursor.close();

        Cursor movieCursor = getContentResolver().query(
                MoviesContract.MovieEntry.buildMovieUri(id),
                MOVIE_COLUMNS,
                null,
                null,
                null
        );
        movieCursor.moveToFirst();

        movieModel = new MovieModel(
                movieCursor.getInt(COLUMN_MOVIE_ID),
                movieCursor.getString(COLUMN_MOVIE_TITLE),
                movieCursor.getString(COLUMN_MOVIE_TAGLINE),
                Utility.byteArrayToString(movieCursor.getBlob(COLUMN_MOVIE_POSTER)),
                movieCursor.getString(COLUMN_MOVIE_DATE),
                movieCursor.getInt(COLUMN_MOVIE_RUNTIME),
                movieCursor.getDouble(COLUMN_MOVIE_RATING),
                movieCursor.getDouble(COLUMN_MOVIE_POPULARITY),
                movieCursor.getString(COLUMN_MOVIE_OVERVIEW),
                commentModels,
                videoModels
        );

        movieCursor.close();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVE_MOVIE_TAG, movieModel);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //movieModel = savedInstanceState.getParcelable("movie");
    }

    private class FetchDetailedMovieInfo extends AsyncTask<String, Void, String[]> {

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
                ArrayList<CommentModel> commentModels = new ArrayList<>();
                for (int i = 0; i < jComments.length(); i++) {
                    JSONObject obj = jComments.getJSONObject(i);
                    CommentModel commentModel = new CommentModel(
                            obj.getString("author"),
                            obj.getString("content"),
                            obj.getString("url")
                    );
                    commentModels.add(commentModel);
                }

                ArrayList<VideoModel> youtubeAdresses = new ArrayList<>();
                JSONArray jVideos = new JSONObject(s[2]).getJSONArray("results");
                for (int i = 0; i < jVideos.length(); i++) {
                    JSONObject obj = jVideos.getJSONObject(i);
                    VideoModel videoModel = new VideoModel(
                            obj.getString("key"),
                            obj.getString("name"),
                            obj.getString("type")
                    );
                    youtubeAdresses.add(videoModel);
                }

                movieModel = new MovieModel(
                        movie.getInt("id"),
                        movie.getString("title"),
                        movie.getString("tagline"),
                        movie.getString("poster_path"),
                        movie.getString("release_date"),
                        movie.getInt("runtime"),
                        movie.getDouble("vote_average"),
                        movie.getDouble("popularity"),
                        movie.getString("overview"),
                        commentModels,
                        youtubeAdresses
                        );

                Log.i("onPostExecute", movie.getString("tagline"));
                initializeAndUpdateInfo();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    interface CallbackMovieRemoved {
        void removeMovie(int id);
    }
}
