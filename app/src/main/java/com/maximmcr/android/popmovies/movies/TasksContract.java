package com.maximmcr.android.popmovies.movies;

import com.maximmcr.android.popmovies.BasePresenter;
import com.maximmcr.android.popmovies.BaseView;

/**
 * Created by Frei on 03.10.2017.
 */

public interface TasksContract {

    public interface View extends BaseView<Presenter> {

        void showMovieList();

        void showMovieDetails();

        void showNoMoviesInDb();

        void showNoInternet();

        void showLoadingIndicator(boolean status);

    }

    public interface Presenter extends BasePresenter {

        void loadMovies();

        void openMovieDetails(long id);

    }
}
