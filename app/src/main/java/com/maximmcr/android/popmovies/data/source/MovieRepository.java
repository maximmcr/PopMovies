package com.maximmcr.android.popmovies.data.source;

import android.content.Context;

import com.maximmcr.android.popmovies.utils.DbUtility;
import com.maximmcr.android.popmovies.data.model.Movie;
import com.maximmcr.android.popmovies.data.source.local.LocalDataSource;
import com.maximmcr.android.popmovies.data.source.remote.TmdbDataSource;

/**
 * Created by Frei on 04.10.2017.
 */
// TODO: 18.10.2017 code MovieRepo
public class MovieRepository implements MovieDataSource {

    private static MovieRepository INSTANCE = null;
    private Context mContext;

    private MovieDataSource localStorage;
    private MovieDataSource remoteStorage;

    private MovieRepository(Context context) {
        mContext = context;
        remoteStorage = TmdbDataSource.getInstance();
        localStorage = LocalDataSource.getInstance(context);
    }

    public static MovieRepository getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new MovieRepository(context);
        }
        return INSTANCE;
    }

    @Override
    public void getMovie(LoadMovieCallback callback, int id) {
        if (DbUtility.isOptionSaved(mContext)) {
            localStorage.getMovie(callback, id);
        } else {
            remoteStorage.getMovie(callback, id);
        }
    }

    @Override
    public void getMovieList(LoadMovieListCallback callback, String filterType) {
        if (DbUtility.isOptionSaved(mContext)) {
            localStorage.getMovieList(callback, filterType);
        } else {
            remoteStorage.getMovieList(callback, filterType);
        }
    }

    @Override
    public void insertMovie(Movie movie) {
        localStorage.insertMovie(movie);
    }

    @Override
    public void deleteMovie(int id) {
        localStorage.deleteMovie(id);
    }
}
