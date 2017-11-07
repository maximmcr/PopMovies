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

    private MovieDataSource mLocalStorage;
    private MovieDataSource mRemoteStorage;

    private MovieRepository(Context context) {
        mContext = context;
        mRemoteStorage = TmdbDataSource.getInstance();
        mLocalStorage = LocalDataSource.getInstance(context);
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
            mLocalStorage.getMovie(id, callback);
        } else {
            mRemoteStorage.getMovie(id, callback);
        }
    }

    @Override
    public void getMovieList(String filterType, LoadMovieListCallback callback) {
        if (SharedPrefsRepoImpl.getInstance(mContext).isOptionSaved()) {
            mLocalStorage.getMovieList(filterType, callback);
        } else {
            mRemoteStorage.getMovieList(filterType, callback);
        }
    }

    @Override
    public void insertMovie(Movie movie) {
        mLocalStorage.insertMovie(movie);
    }

    @Override
    public void deleteMovie(int id) {
        mLocalStorage.deleteMovie(id);
    }

    @Override
    public boolean isMovieInDb(int id) {
        return mLocalStorage.isMovieInDb(id);
    }

    @Override
    public void addMovieDeletedListener(MovieDeleteCallback listener) {
        mLocalStorage.addMovieDeletedListener(listener);
    }
}
