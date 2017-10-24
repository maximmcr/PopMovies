package com.maximmcr.android.popmovies.movies;

import com.maximmcr.android.popmovies.BasePresenter;
import com.maximmcr.android.popmovies.BaseView;
import com.maximmcr.android.popmovies.data.model.Movie;

import java.util.ArrayList;

/**
 * Created by Frei on 03.10.2017.
 */

public interface MoviesContract {

    public interface View extends BaseView<Presenter> {

        void showMovieList(ArrayList<Movie> movies);

        void showMovieDetails(int id);

        void showNoMoviesInDb();

        void showNoInternet();

        void showLoadingIndicator(boolean status);

    }

    public interface Presenter extends BasePresenter {

        void loadMovies();

        void openMovieDetails(Movie selectedMovie);

        //void setFiltering(String filtering);

        String getFiltering();

        boolean isOptionSaved();
    }
}
