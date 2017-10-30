package com.maximmcr.android.popmovies.data.source.local;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Frei on 12.07.2017.
 */

public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.maximmcr.android.popmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_REVIEWS = "review";
    public static final String PATH_VIDEOS= "video";
    public static final String PATH_MOVIES = "movie";

    public static final class ReviewEntry implements BaseColumns {

        public static final String TABLE_NAME = "review";

        public static final String COLUMN_MOVIE_KEY = "movie_id";

        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class VideoEntry implements BaseColumns {

        public static final String TABLE_NAME = "video";

        public static final String COLUMN_MOVIE_KEY = "movie_id";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_PATH = "path";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEOS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEOS;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_TAGLINE = "tagline";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_OVERVIEW = "overview";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
