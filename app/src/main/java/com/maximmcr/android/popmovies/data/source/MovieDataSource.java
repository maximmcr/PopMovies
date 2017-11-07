package com.maximmcr.android.popmovies.data.source;

import com.maximmcr.android.popmovies.data.model.Movie;

import java.util.ArrayList;

/**
 * Created by Frei on 04.10.2017.
 */

public interface MovieDataSource {

    //common
    void getMovie(int id, LoadMovieCallback callback);

    void getMovieList(String filterType, LoadMovieListCallback callback);

    //only for local db and repo
    void insertMovie(Movie movie);

    void deleteMovie(int id);

    boolean isMovieInDb(int id);

    //only for repo
    void refreshMovie();

    void refreshMovieList();

    interface LoadMovieCallback {

        void onMovieLoaded(Movie movie);

        void onLoadFailed();

    }

    interface LoadMovieListCallback {

        void onMovieListLoaded(ArrayList<Movie> movies);

        void onLoadFailed();

    }
}
