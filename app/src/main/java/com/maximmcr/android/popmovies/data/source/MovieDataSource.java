package com.maximmcr.android.popmovies.data.source;

import android.content.Context;

import com.maximmcr.android.popmovies.data.model.Movie;

import java.util.List;

/**
 * Created by Frei on 04.10.2017.
 */

public interface MovieDataSource {

    interface LoadMovieCallback {

        void onMovieLoaded(Movie movie);

        void onLoadFailed();

    }

    interface LoadMovieListCallback {

        void onMovieListLoaded(List<Movie> movie);

        void onLoadFailed();

    }

    void getMovie(LoadMovieCallback callback, int id);

    void getMovieList(LoadMovieListCallback callback, String filterType);

    void insertMovie(Movie movie);

    void deleteMovie(int id);

    void setContext(Context context);

}
