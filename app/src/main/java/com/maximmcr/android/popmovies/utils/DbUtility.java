package com.maximmcr.android.popmovies.utils;

import android.content.Context;
import android.database.Cursor;

import com.maximmcr.android.popmovies.data.source.local.MoviesContract;

/**
 * Created by Frei on 19.10.2017.
 */

public class DbUtility {

    private static final String[] MOVIE_LIST_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID
    };
    private static final int COLUMN_LIST_ID = 0;

    public static boolean isMovieInDb(Context context, int id) {
        String[] selectionArgs = { Integer.toString(id) };
        Cursor cursor = context.getContentResolver().query(
                MoviesContract.BASE_CONTENT_URI,
                MOVIE_LIST_COLUMNS,
                MOVIE_LIST_COLUMNS[COLUMN_LIST_ID],
                selectionArgs,
                null
        );
        if (cursor != null && cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
