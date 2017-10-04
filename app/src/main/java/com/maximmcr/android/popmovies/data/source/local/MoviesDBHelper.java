package com.maximmcr.android.popmovies.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.maximmcr.android.popmovies.data.source.local.MoviesContract.ReviewEntry;
import com.maximmcr.android.popmovies.data.source.local.MoviesContract.MovieEntry;
import com.maximmcr.android.popmovies.data.source.local.MoviesContract.VideoEntry;

/**
 * Created by Frei on 12.07.2017.
 */

public class MoviesDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = MoviesDBHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " ( " +
                MovieEntry._ID + " INTEGER PRIMARY KEY NOT NULL, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_TAGLINE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER + " BLOB, " +
                MovieEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RUNTIME + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_RATING + " REAL, " +
                MovieEntry.COLUMN_POPULARITY + " REAL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL " +
                " )";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " ( " +
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ReviewEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                ReviewEntry.COLUMN_AUTHOR + " TEXT, " +
                ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_URL + " TEXT, " +

                "FOREIGN KEY ( " + ReviewEntry.COLUMN_MOVIE_KEY + " ) REFERENCES " +
                MovieEntry.TABLE_NAME + " ( " + MovieEntry._ID + " ) " +
                " ) ";

        db.execSQL(SQL_CREATE_REVIEW_TABLE);

        final String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE " + VideoEntry.TABLE_NAME + " ( " +
                VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VideoEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                VideoEntry.COLUMN_NAME + " TEXT, " +
                VideoEntry.COLUMN_TYPE + " TEXT, " +
                VideoEntry.COLUMN_PATH + " TEXT NOT NULL, " +

                "FOREIGN KEY ( " + VideoEntry.COLUMN_MOVIE_KEY + " ) REFERENCES " +
                MovieEntry.TABLE_NAME + " ( " + MovieEntry._ID + " ) " +
                " ) ";

        db.execSQL(SQL_CREATE_VIDEO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);

        onCreate(db);
    }
}
