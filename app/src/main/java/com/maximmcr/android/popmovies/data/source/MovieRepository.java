package com.maximmcr.android.popmovies.data.source;

import android.content.Context;
import android.support.annotation.Nullable;

import com.maximmcr.android.popmovies.data.model.Movie;
import com.maximmcr.android.popmovies.data.source.local.LocalDataSource;
import com.maximmcr.android.popmovies.data.source.remote.TmdbDataSource;
import com.maximmcr.android.popmovies.settings.SharedPrefsRepoImpl;

import java.util.ArrayList;

/**
 * Created by Frei on 04.10.2017.
 */
public class MovieRepository implements MovieDataSource {

    private static MovieRepository INSTANCE = null;
    private Context mContext;

    private MovieDataSource mLocalStorage;
    private MovieDataSource mRemoteStorage;

    private boolean mMovieCacheIsDirty = true;
    private Movie mCachedMovie;
    private boolean mMovieListCacheIsDirty = true;
    private ArrayList<Movie> mCachedMovieList;

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
    public void getMovie(int id, final LoadMovieCallback callback) {

        if (mCachedMovie != null && !mMovieCacheIsDirty) {
            callback.onMovieLoaded(mCachedMovie);
            return;
        }

        LoadMovieCallback resultCallback = new LoadMovieCallback() {
            @Override
            public void onMovieLoaded(Movie movie) {
                refreshMovieCache(movie);
                callback.onMovieLoaded(movie);
            }

            @Override
            public void onLoadFailed() {
                callback.onLoadFailed();
            }
        };

        if (SharedPrefsRepoImpl.getInstance(mContext).isOptionSaved()) {
            mLocalStorage.getMovie(id, resultCallback);
        } else {
            mRemoteStorage.getMovie(id, resultCallback);
        }
    }

    @Override
    public void getMovieList(String filterType, final LoadMovieListCallback callback) {

        if (mCachedMovieList != null && !mMovieListCacheIsDirty) {
            callback.onMovieListLoaded(mCachedMovieList);
            return;
        }

        LoadMovieListCallback resultCallback = new LoadMovieListCallback() {
            @Override
            public void onMovieListLoaded(ArrayList<Movie> movies) {
                refreshMovieListCache(movies);
                callback.onMovieListLoaded(movies);
            }

            @Override
            public void onLoadFailed() {
                callback.onLoadFailed();
            }
        };

        if (SharedPrefsRepoImpl.getInstance(mContext).isOptionSaved()) {
            mLocalStorage.getMovieList(filterType, resultCallback);
        } else {
            mRemoteStorage.getMovieList(filterType, resultCallback);
        }
    }

    @Override
    public void insertMovie(@Nullable Movie movie) {
        mLocalStorage.insertMovie(mCachedMovie);
    }

    @Override
    public void deleteMovie(int id) {
        mLocalStorage.deleteMovie(id);
        if (SharedPrefsRepoImpl.getInstance(mContext).isOptionSaved()) {
            mMovieListCacheIsDirty = true;
        }

    }

    @Override
    public boolean isMovieInDb(int id) {
        return mLocalStorage.isMovieInDb(id);
    }

    @Override
    public void refreshMovie() {
        mMovieCacheIsDirty = true;
    }

    private void refreshMovieCache(Movie movie) {
        mCachedMovie = new Movie(movie);
        mMovieCacheIsDirty = false;
    }

    @Override
    public void refreshMovieList() {
        mMovieListCacheIsDirty = true;
    }

    private void refreshMovieListCache(ArrayList<Movie> movies) {
        mCachedMovieList = new ArrayList<>(movies);
        mMovieListCacheIsDirty = false;
    }
}
