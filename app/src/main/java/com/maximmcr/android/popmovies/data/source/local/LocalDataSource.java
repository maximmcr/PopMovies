package com.maximmcr.android.popmovies.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.maximmcr.android.popmovies.R;
import com.maximmcr.android.popmovies.data.model.Movie;
import com.maximmcr.android.popmovies.data.model.Review;
import com.maximmcr.android.popmovies.data.model.Video;
import com.maximmcr.android.popmovies.data.source.MovieDataSource;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Frei on 05.10.2017.
 */

public class LocalDataSource implements MovieDataSource {

    private static LocalDataSource INSTANCE = null;

    private static final String LOG_TAG = LocalDataSource.class.getSimpleName();

    //Strings for access to list of movies in DB
    private static final String[] MOVIE_LIST_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_POSTER
    };
    private static final int COLUMN_LIST_ID = 0;
    private static final int COLUMN_LIST_POSTER = 1;

    //Strings for access to movie info in DB
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

    //Strings for access to movie's reviews in DB
    private static final String[] REVIEW_COLUMNS = {
            MoviesContract.ReviewEntry.COLUMN_MOVIE_KEY,
            MoviesContract.ReviewEntry.COLUMN_URL,
            MoviesContract.ReviewEntry.COLUMN_CONTENT,
            MoviesContract.ReviewEntry.COLUMN_AUTHOR
    };
    private static final int COLUMN_REVIEW_KEY = 0;
    private static final int COLUMN_REVIEW_URL = 1;
    private static final int COLUMN_REVIEW_CONTENT = 2;
    private static final int COLUMN_REVIEW_AUTHOR = 3;

    //Strings for access to movie's videos/clips/etc. in DB
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

    private Context mContext;

    private LocalDataSource(Context context) {
        mContext = context;
    }

    public static LocalDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void getMovie(int id, LoadMovieCallback callback) {
        Cursor reviewCursor = mContext.getContentResolver().query(
                MoviesContract.ReviewEntry.buildMovieUri(id),
                REVIEW_COLUMNS,
                null,
                null,
                null
        );
        ArrayList<Review> reviews = new ArrayList<>();
        if (reviewCursor != null) {
            if (reviewCursor.getCount() > 0) {
                reviewCursor.moveToFirst();
                for (int i = 0; i < reviewCursor.getCount(); i++) {
                    String author = reviewCursor.getString(COLUMN_REVIEW_AUTHOR);
                    String content = reviewCursor.getString(COLUMN_REVIEW_CONTENT);
                    String url = reviewCursor.getString(COLUMN_REVIEW_URL);
                    Review review = new Review(
                            author,
                            content,
                            url
                    );
                    reviews.add(review);
                    reviewCursor.moveToNext();
                }
            }
            reviewCursor.close();
        } else {
            Log.d(LOG_TAG, "Loading reviews from DB failed");
        }

        Cursor videoCursor = mContext.getContentResolver().query(
                MoviesContract.VideoEntry.buildMovieUri(id),
                VIDEO_COLUMNS,
                null,
                null,
                null
        );
        ArrayList<Video> videos = new ArrayList<>();
        if (videoCursor != null) {
            if (videoCursor.getCount() > 0) {
                videoCursor.moveToFirst();
                for (int i = 0; i < videoCursor.getCount(); i++) {
                    String path = videoCursor.getString(COLUMN_VIDEO_PATH);
                    String name = videoCursor.getString(COLUMN_VIDEO_NAME);
                    String type = videoCursor.getString(COLUMN_VIDEO_TYPE);
                    Video video = new Video(
                            path,
                            name,
                            type
                    );
                    videos.add(video);
                    videoCursor.moveToNext();
                }
            }
            videoCursor.close();
        } else {
            Log.d(LOG_TAG, "Loading videos from DB failed");
        }

        Cursor movieCursor = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.buildMovieUri(id),
                MOVIE_COLUMNS,
                null,
                null,
                null
        );

        if (movieCursor != null && movieCursor.getCount() > 0) {
            movieCursor.moveToFirst();
            Movie movie = new Movie(
                    movieCursor.getInt(COLUMN_MOVIE_ID),
                    movieCursor.getString(COLUMN_MOVIE_TITLE),
                    movieCursor.getString(COLUMN_MOVIE_TAGLINE),
                    movieCursor.getString(COLUMN_MOVIE_POSTER),
                    movieCursor.getString(COLUMN_MOVIE_DATE),
                    movieCursor.getInt(COLUMN_MOVIE_RUNTIME),
                    movieCursor.getDouble(COLUMN_MOVIE_RATING),
                    movieCursor.getDouble(COLUMN_MOVIE_POPULARITY),
                    movieCursor.getString(COLUMN_MOVIE_OVERVIEW),
                    reviews,
                    videos
            );
            movieCursor.close();
            callback.onMovieLoaded(movie);
        } else {
            callback.onLoadFailed();
        }

    }

    @Override
    public void getMovieList(String filterType, LoadMovieListCallback callback) {
        Cursor c = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                MOVIE_LIST_COLUMNS,
                null,
                null,
                null
        );

        if (c == null || c.getCount() < 1) {
            callback.onLoadFailed();
        } else {
            ArrayList<Movie> result = new ArrayList<>();
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                int id = c.getInt(COLUMN_LIST_ID);
                String poster = c.getString(COLUMN_LIST_POSTER);
                result.add(new Movie(poster, id));
                c.moveToNext();
            }
            c.close();
            callback.onMovieListLoaded(result);
        }
    }

    @Override
    public void insertMovie(Movie movie) {

        int id = movie.getId();
        //inserting base movie info
        ContentValues movieValues = new ContentValues();

        File dir = mContext.getDir(mContext.getString(R.string.poster_dir_name), Context.MODE_PRIVATE);
        File image = new File(dir, getPosterName(movie.getPosterPath()));
//        File image = new File(dir, movie.getPosterPath());
        downloadImage(image);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER, "file://" + image.getAbsolutePath());

        movieValues.put(MoviesContract.MovieEntry._ID, id);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        movieValues.put(MoviesContract.MovieEntry.COLUMN_TAGLINE, movie.getTagline());
        movieValues.put(MoviesContract.MovieEntry.COLUMN_DATE, movie.getReleaseDate());
        movieValues.put(MoviesContract.MovieEntry.COLUMN_RUNTIME, movie.getRuntime());
        movieValues.put(MoviesContract.MovieEntry.COLUMN_RATING, movie.getRating());
        movieValues.put(MoviesContract.MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
        movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());

        mContext.getContentResolver().insert(
                MoviesContract.MovieEntry.buildMovieUri(id),
                movieValues);

        //inserting reviews
        if (movie.getReviews() != null) {
            Vector<ContentValues> commentValues = new Vector<>();
            for (Review review : movie.getReviews()) {
                ContentValues value = new ContentValues();
                value.put(MoviesContract.ReviewEntry.COLUMN_MOVIE_KEY, id);
                value.put(MoviesContract.ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
                value.put(MoviesContract.ReviewEntry.COLUMN_CONTENT, review.getContent());
                value.put(MoviesContract.ReviewEntry.COLUMN_URL, review.getUrl());
                commentValues.add(value);
            }

            mContext.getContentResolver().bulkInsert(
                    MoviesContract.ReviewEntry.buildMovieUri(id),
                    commentValues.toArray(new ContentValues[commentValues.size()]));
        }

        //inserting videos
        if (movie.getVideos() != null) {
            Vector<ContentValues> videoValues = new Vector<>();
            for (Video video : movie.getVideos()) {
                ContentValues value = new ContentValues();
                value.put(MoviesContract.VideoEntry.COLUMN_MOVIE_KEY, id);
                value.put(MoviesContract.VideoEntry.COLUMN_NAME, video.getName());
                value.put(MoviesContract.VideoEntry.COLUMN_PATH, video.getPath());
                value.put(MoviesContract.VideoEntry.COLUMN_TYPE, video.getType());
                videoValues.add(value);
            }

            mContext.getContentResolver().bulkInsert(
                    MoviesContract.VideoEntry.buildMovieUri(id),
                    videoValues.toArray(new ContentValues[videoValues.size()]));
        }
    }

    private void downloadImage(final File image) {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            image.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(image);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                            ostream.close();
                        } catch (IOException e) {
                            Log.e("IOException", e.getLocalizedMessage());
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Picasso.with(mContext)
                .load(mContext.getString(R.string.poster_base_path) + "/" + getPosterName(image.getName()))
//                .load(image.getName())
                .into(target);
    }

    private String getPosterName(String poster) {
        String[] pathParts = poster.split("/");
        return pathParts[pathParts.length - 1];
    }

    @Override
    public void deleteMovie(int id) {
        String[] selectionArgs = { Integer.toString(id) };
        Cursor poster = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.buildMovieUri(id),
                MOVIE_LIST_COLUMNS,
                MOVIE_LIST_COLUMNS[COLUMN_LIST_ID],
                selectionArgs,
                null
        );
        String posterPath = null;
        if (poster != null && poster.getCount() != 0) {
            poster.moveToFirst();
            posterPath = poster.getString(COLUMN_LIST_POSTER);
        }
        if (poster != null) poster.close();

        if (posterPath != null) {
            File image = new File(posterPath);
            if (image.delete()) Log.d(LOG_TAG, "image on the disk deleted successfully!");
        }

        int count = mContext.getContentResolver().delete(
                MoviesContract.MovieEntry.buildMovieUri(id),
                null,
                null);
        Log.d(LOG_TAG, "Deleted " + count + "rows from db");
    }

    @Override
    public boolean isMovieInDb(int id) {
        String[] selectionArgs = { Integer.toString(id) };
        Cursor poster = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.buildMovieUri(id),
                MOVIE_LIST_COLUMNS,
                MOVIE_LIST_COLUMNS[COLUMN_LIST_ID],
                selectionArgs,
                null
        );
        boolean result = (poster != null && poster.getCount() != 0);
        if (poster != null) poster.close();
        return result;
    }

    //not required because of repo impl.
    @Override
    public void refreshMovie() {

    }

    @Override
    public void refreshMovieList() {

    }
}
