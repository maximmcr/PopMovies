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
    private ArrayList<Movie> mMovies;
    private String mCurrentFilter;

    public MoviesPresenter(MovieRepository data,
                           SharedPreferenceRepository prefs) {
        Log.d(LOG_TAG, "MoviePresenter constructor");
        mData = data;
        mData.addMovieDeletedListener(new MovieDataSource.MovieDeleteCallback() {
            @Override
            public void onMovieDeleted(int id) {
                for (Movie movie:
                        mMovies) {
                    if (movie.getId() == id) {
                        mMovies.remove(movie);
                        return;
                    }
                }
            }
        });
        mPrefs = prefs;
        mCurrentFilter = "";
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
        if (mMovies == null || !mCurrentFilter.equals(mPrefs.getCurrentFiltering())) {
            if (mMovies != null) mMovies.clear();
            else mMovies = new ArrayList<>();
            mData.getMovieList(getFiltering(), new MovieDataSource.LoadMovieListCallback() {
                @Override
                public void onMovieListLoaded(ArrayList<Movie> movies) {
                    if (movies.size() > 0) {
                        mMovies = movies;
                        mView.showMovieList(mMovies);
                        mCurrentFilter = mPrefs.getCurrentFiltering();
                    } else if (mPrefs.isOptionSaved()) {
                        mView.showNoMoviesInDb();
                        mCurrentFilter = mPrefs.getCurrentFiltering();
                    } else {
                        mView.showMovieList(mMovies);
                        mView.showNoInternet();
                    }
                    Log.d(LOG_TAG, "Movies downloaded successful");
                }

                @Override
                public void onLoadFailed() {
                    if (mPrefs.isOptionSaved()) {
                        mCurrentFilter = mPrefs.getCurrentFiltering();
                        mView.showNoMoviesInDb();
                    }
                    else {
                        mView.showMovieList(mMovies);
                        mView.showNoInternet();
                    }
                    Log.d(LOG_TAG, "Movies downloaded unsuccessful");
                }
            });
        } else {
            mView.showMovieList(mMovies);
        }
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
