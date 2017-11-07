package com.maximmcr.android.popmovies.data.source;

import com.maximmcr.android.popmovies.data.model.Movie;

import java.util.ArrayList;

/**
 * Created by Frei on 04.10.2017.
 */

public interface MovieDataSource {

    void getMovie(int id, LoadMovieCallback callback);

    void getMovieList(String filterType, LoadMovieListCallback callback);

    void insertMovie(Movie movie);

    void deleteMovie(int id);

    boolean isMovieInDb(int id);

    void addMovieDeletedListener(MovieDeleteCallback listener);

    interface LoadMovieCallback {

        void onMovieLoaded(Movie movie);

        void onLoadFailed();

    }

    interface LoadMovieListCallback {

        void onMovieListLoaded(ArrayList<Movie> movies);

        void onLoadFailed();

    }

    interface MovieDeleteCallback {

        void onMovieDeleted(int id);

    }

}
