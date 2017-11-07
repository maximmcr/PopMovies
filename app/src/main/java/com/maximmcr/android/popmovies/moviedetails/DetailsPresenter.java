package com.maximmcr.android.popmovies.moviedetails;

import android.util.Log;

import com.maximmcr.android.popmovies.data.model.Movie;
import com.maximmcr.android.popmovies.data.model.Review;
import com.maximmcr.android.popmovies.data.model.Video;
import com.maximmcr.android.popmovies.data.source.MovieDataSource;
import com.maximmcr.android.popmovies.data.source.MovieRepository;

/**
 * Created by Frei on 26.10.2017.
 */

public class DetailsPresenter implements DetailsContract.Presenter {

    public static final String LOG_TAG = DetailsPresenter.class.getSimpleName();

    private int mId;
    private boolean mFirstLoad;

    private MovieRepository mData;
    private DetailsContract.View mView;

    public DetailsPresenter(MovieRepository data, int id) {
        mData = data;
        mId = id;
        mFirstLoad = true;
    }

    @Override
    public void attachView(DetailsContract.View view) {
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
        if (mFirstLoad) {
            mView.showLoadingStatus(true);
            mFirstLoad = false;
            mData.refreshMovie();
        }

        mData.getMovie(mId, new MovieDataSource.LoadMovieCallback() {
            @Override
            public void onMovieLoaded(Movie movie) {
                mView.showMovie(movie, new DetailsContract.OnPosterLoadedCallback() {
                    @Override
                    public void onSuccess() {
                        mView.showLoadingStatus(false);
                    }

                    @Override
                    public void onFailure() {
                        mView.showLoadingStatus(false);
                    }
                });
            }

            @Override
            public void onLoadFailed() {
                mView.showLoadingStatus(false);
                mView.showNoInternet();
                Log.d(LOG_TAG, "onLoadFailed -- movie");
            }
        });
    }

    @Override
    public void saveMovie() {
        if (mData.isMovieInDb(mId)) {
            mData.deleteMovie(mId);
            mView.showMovieRemoved();
        } else {
            //repo have cached movie, it don't need movie as parameter
            mData.insertMovie(null);
            mView.showMovieAdded();
        }
    }

    @Override
    public void openVideo(Video video) {
        if (mView.isOnline()) {
            mView.showVideo(video.getPath());
        } else {
            mView.showNoInternet();
        }
    }

    @Override
    public void openReview(Review review) {
        if (mView.isOnline()) {
            mView.showReview(review.getUrl());
        } else {
            mView.showNoInternet();
        }
    }

    @Override
    public boolean isMovieInDb(int id) {
        return mData.isMovieInDb(id);
    }
}
