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

    public MoviesPresenter(MoviesContract.View view, MovieRepository data,
                           SharedPreferenceRepository prefs) {
        mData = data;
        mView = view;
        mPrefs = prefs;

        mView.setPresenter(this);
    }

    @Override
    public void start() {
        Log.d(LOG_TAG, "start");
        loadMovies();
    }

    @Override
    public void loadMovies() {
        Log.d(LOG_TAG, "loadMovies");
        mData.getMovieList(getFiltering(), new MovieDataSource.LoadMovieListCallback() {
            @Override
            public void onMovieListLoaded(ArrayList<Movie> movies) {
                mView.showMovieList(movies);
            }

            @Override
            public void onLoadFailed() {
                if (mPrefs.isOptionSaved()) mView.showNoMoviesInDb();
                else mView.showNoInternet();
            }
        });
    }

    @Override
    public void openMovieDetails(Movie selectedMovie) {
        mView.showMovieDetails(selectedMovie.getId());
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
