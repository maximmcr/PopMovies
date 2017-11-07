package com.maximmcr.android.popmovies.movies;

import com.maximmcr.android.popmovies.BasePresenter;
import com.maximmcr.android.popmovies.BaseView;
import com.maximmcr.android.popmovies.data.model.Movie;

import java.util.ArrayList;

/**
 * Created by Frei on 03.10.2017.
 */

public interface MoviesContract {

    interface View extends BaseView<Presenter> {

        void showMovieList(ArrayList<Movie> movies);

        void showMovieDetails(int id);

        void showNoMoviesInDb();

        void showNoInternet();

        void showLoadingIndicator(boolean status);

        boolean isOnline();

    }

    interface Presenter extends BasePresenter<View> {

        void updateView();

        void openMovieDetails(Movie selectedMovie);

        String getFiltering();

        boolean isOptionSaved();
    }
}
