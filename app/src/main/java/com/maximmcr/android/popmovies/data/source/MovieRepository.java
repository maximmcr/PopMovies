package com.maximmcr.android.popmovies.data.source;

import android.content.Context;

import com.maximmcr.android.popmovies.data.model.Movie;

/**
 * Created by Frei on 04.10.2017.
 */

public class MovieRepository implements MovieDataSource {

    private Context mContext;

    @Override
    public void getMovie(LoadMovieCallback callback, int id) {

    }

    @Override
    public void getMovieList(LoadMovieListCallback callback, String filterType) {

    }

    @Override
    public void insertMovie(Movie movie) {

    }

    @Override
    public void deleteMovie(int id) {

    }

    @Override
    public void setContext(Context context) {
        mContext = context;
    }
}
