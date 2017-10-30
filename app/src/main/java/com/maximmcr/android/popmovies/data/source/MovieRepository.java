package com.maximmcr.android.popmovies.data.source;

import android.content.Context;

import com.maximmcr.android.popmovies.data.model.Movie;
import com.maximmcr.android.popmovies.data.source.local.LocalDataSource;
import com.maximmcr.android.popmovies.data.source.remote.TmdbDataSource;
import com.maximmcr.android.popmovies.settings.SharedPrefsRepoImpl;

/**
 * Created by Frei on 04.10.2017.
 */
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
    public void getMovie(int id, LoadMovieCallback callback) {
        if (SharedPrefsRepoImpl.getInstance(mContext).isOptionSaved()) {
            localStorage.getMovie(id, callback);
        } else {
            remoteStorage.getMovie(id, callback);
        }
    }

    @Override
    public void getMovieList(String filterType, LoadMovieListCallback callback) {
        if (SharedPrefsRepoImpl.getInstance(mContext).isOptionSaved()) {
            localStorage.getMovieList(filterType, callback);
        } else {
            remoteStorage.getMovieList(filterType, callback);
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

    @Override
    public boolean isMovieInDb(int id) {
        return localStorage.isMovieInDb(id);
    }
}
