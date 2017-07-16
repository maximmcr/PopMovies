package com.example.android.popmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Frei on 12.07.2017.
 */

public class MoviesProvider extends ContentProvider {

    private MoviesDBHelper mOpenHelper;
    private static final String LOG_TAG = MoviesProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // Codes for Uri matcher
    static final int MOVIES = 100;
    static final int MOVIE_WITH_DATA = 101;
    static final int COMMENTS = 200;
    static final int VIDEOS = 300;

    private static final SQLiteQueryBuilder sMovieWithDataQueryBuilder;
    static {
        sMovieWithDataQueryBuilder = new SQLiteQueryBuilder();

        sMovieWithDataQueryBuilder.setTables(
                MoviesContract.MovieEntry.TABLE_NAME +

                        " INNER JOIN " + MoviesContract.CommentEntry.TABLE_NAME +
                        " ON " + MoviesContract.CommentEntry.TABLE_NAME +
                        "." + MoviesContract.CommentEntry.COLUMN_MOVIE_KEY +
                        " = " + MoviesContract.MovieEntry.TABLE_NAME +
                        "." + MoviesContract.MovieEntry._ID +

                        " INNER JOIN " + MoviesContract.VideoEntry.TABLE_NAME +
                        " ON " + MoviesContract.VideoEntry.TABLE_NAME +
                        "." + MoviesContract.VideoEntry.COLUMN_MOVIE_KEY +
                        " = " + MoviesContract.MovieEntry.TABLE_NAME +
                        "." + MoviesContract.MovieEntry._ID
        );
    }

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIES);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/#", MOVIE_WITH_DATA);
        matcher.addURI(authority, MoviesContract.PATH_COMMENTS + "/#", COMMENTS);
        matcher.addURI(authority, MoviesContract.PATH_VIDEOS + "/#", VIDEOS);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final int match = sUriMatcher.match(uri);
        Cursor result;

        switch (match) {
            case (MOVIES): {
                result = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                Log.d(LOG_TAG, result.toString());
                break;
            }
            case (MOVIE_WITH_DATA): {
                String id = MoviesContract.MovieEntry.getMovieIdFromUri(uri);

                result = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        null,
                        MoviesContract.MovieEntry._ID + " = " + id,
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case (COMMENTS): {
                String id = MoviesContract.MovieEntry.getMovieIdFromUri(uri);

                result = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.CommentEntry.TABLE_NAME,
                        null,
                        MoviesContract.CommentEntry.COLUMN_MOVIE_KEY + " = " + id,
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case (VIDEOS): {
                String id = MoviesContract.MovieEntry.getMovieIdFromUri(uri);

                result = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.VideoEntry.TABLE_NAME,
                        null,
                        MoviesContract.VideoEntry.COLUMN_MOVIE_KEY + " = " + id,
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default: throw new UnsupportedOperationException(LOG_TAG + " - query exception:" + uri);
        }
        result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return MoviesContract.MovieEntry.CONTENT_DIR_TYPE;
            case MOVIE_WITH_DATA:
                return MoviesContract.MovieEntry.CONTENT_ITEM_TYPE;
            default: throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long id;
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE_WITH_DATA: {
                id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = MoviesContract.MovieEntry.buildMovieUri(id);
                    Log.d(LOG_TAG, returnUri.toString());
                } else
                    throw new SQLException("Failed to insert row (movie) into " + uri);
                break;
            }

            case COMMENTS: {
                id = db.insert(MoviesContract.CommentEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = MoviesContract.CommentEntry.buildMovieUri(id);
                } else
                    throw new SQLException("Failed to insert row (comment) into " + uri);
                break;
            }

            case VIDEOS: {
                id = db.insert(MoviesContract.VideoEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = MoviesContract.VideoEntry.buildMovieUri(id);
                } else
                    throw new SQLException("Failed to insert row (video) into " + uri);
                break;
            }
            default: throw new UnsupportedOperationException(LOG_TAG + " - insert exception:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int result = 0;
        final int match = sUriMatcher.match(uri);
        String id = MoviesContract.MovieEntry.getMovieIdFromUri(uri);

        switch (match) {
            case MOVIE_WITH_DATA: {
                result += db.delete(
                        MoviesContract.CommentEntry.TABLE_NAME,
                        MoviesContract.CommentEntry.COLUMN_MOVIE_KEY + " = " + id,
                        null);
                result += db.delete(
                        MoviesContract.VideoEntry.TABLE_NAME,
                        MoviesContract.VideoEntry.COLUMN_MOVIE_KEY + " = " + id,
                        null);
                result += db.delete(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        MoviesContract.MovieEntry._ID + " = " + id,
                        null);
                break;
            }
            default: throw new UnsupportedOperationException(LOG_TAG + " - delete exception:" + uri);
        }

        if (result > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

    // TODO (optionally) - code update
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COMMENTS: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(MoviesContract.CommentEntry.TABLE_NAME, null, value);
                        if (id != -1) returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return  returnCount;
            }
            case VIDEOS: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(MoviesContract.VideoEntry.TABLE_NAME, null, value);
                        if (id != -1) returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return  returnCount;
            }
            default: {
                return super.bulkInsert(uri, values);
            }
        }
    }
}
