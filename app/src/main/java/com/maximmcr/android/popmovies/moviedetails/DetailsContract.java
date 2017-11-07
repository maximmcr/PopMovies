package com.maximmcr.android.popmovies.moviedetails;

import com.maximmcr.android.popmovies.BasePresenter;
import com.maximmcr.android.popmovies.BaseView;
import com.maximmcr.android.popmovies.data.model.Movie;
import com.maximmcr.android.popmovies.data.model.Review;
import com.maximmcr.android.popmovies.data.model.Video;

/**
 * Created by Frei on 26.10.2017.
 */

public interface DetailsContract {

    interface View extends BaseView<Presenter> {

        void showMovie(Movie movie, OnPosterLoadedCallback callback);

        void showVideo(String url);

        void showReview(String url);

        void showMovieAdded();

        void showMovieRemoved();

        void showNoInternet();

        void showLoadingStatus(boolean status);

        boolean isOnline();
    }

    interface Presenter extends BasePresenter<View> {

        void updateView();

        void saveMovie();

        void openVideo(Video video);

        void openReview(Review review);

        boolean isMovieInDb(int id);

    }

    interface OnPosterLoadedCallback {

        void onSuccess();

        void onFailure();

    }
}
