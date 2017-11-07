package com.maximmcr.android.popmovies.movies;

import android.util.Log;

import com.maximmcr.android.popmovies.data.model.Movie;
import com.maximmcr.android.popmovies.data.source.MovieDataSource;
import com.maximmcr.android.popmovies.data.source.MovieRepository;
import com.maximmcr.android.popmovies.settings.SharedPreferenceRepository;

import java.util.ArrayList;

/**
 * Created by Frei on 04.10.2017.
 */

public class MoviesPresenter implements MoviesContract.Presenter {

    public static final String LOG_TAG = MoviesPresenter.class.getSimpleName();

    private MovieRepository mData;
    private MoviesContract.View mView;
    private SharedPreferenceRepository mPrefs;

    private String mCurrentFilter;
    private boolean mFirstLoad;

    public MoviesPresenter(MovieRepository data,
                           SharedPreferenceRepository prefs) {
        Log.d(LOG_TAG, "MoviePresenter constructor");
        mData = data;
        mPrefs = prefs;
        mCurrentFilter = "";
        mFirstLoad = true;
    }

    @Override
    public void attachView(MoviesContract.View view) {
        mView = view;
        mView.setPresenter(this);
        updateView();
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void updateView() {
        Log.d(LOG_TAG, "updateView");
        if (!mCurrentFilter.equals(mPrefs.getCurrentFiltering()) || mFirstLoad){
            mData.refreshMovieList();
            mCurrentFilter = mPrefs.getCurrentFiltering();
            mFirstLoad = false;
        }

        mData.getMovieList(mCurrentFilter, new MovieDataSource.LoadMovieListCallback() {
            @Override
            public void onMovieListLoaded(ArrayList<Movie> movies) {
                mView.showMovieList(movies);
                Log.d(LOG_TAG, "Movies downloaded successful");
            }

            @Override
            public void onLoadFailed() {
                if (mPrefs.isOptionSaved()) {
                    mView.showNoMoviesInDb();
                }
                else {
                    mView.showMovieList(new ArrayList<Movie>());
                    mView.showNoInternet();
                }
                Log.d(LOG_TAG, "Movies downloaded unsuccessful");
            }
        });
    }

    @Override
    public void openMovieDetails(Movie selectedMovie) {
        if (mView.isOnline() || isOptionSaved()) mView.showMovieDetails(selectedMovie.getId());
        else mView.showNoInternet();
    }

    @Override
    public String getFiltering() {
        return mPrefs.getCurrentFiltering();
    }

    @Override
    public boolean isOptionSaved() {
        return mPrefs.isOptionSaved();
    }
}
