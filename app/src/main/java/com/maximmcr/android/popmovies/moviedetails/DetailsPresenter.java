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

    private Movie mMovie;
    private int mId;

    private MovieRepository mData;
    private DetailsContract.View mView;

    public DetailsPresenter(MovieRepository data, DetailsContract.View view) {
        mData = data;
        mView = view;
        //mId = id;

        mView.setPresenter(this);
    }

    @Override
    public void setId(int id) {
        mId = id;
    }

    @Override
    public void start() {

    }

    @Override
    public void loadMovie() {
        mView.showLoadingStatus(true);
        mData.getMovie(mId, new MovieDataSource.LoadMovieCallback() {
            @Override
            public void onMovieLoaded(Movie movie) {
                if (movie != null && movie.getId() > -1) {
                    mMovie = movie;
                    mView.showMovie(mMovie, new DetailsContract.OnPosterLoadedCallback() {
                        @Override
                        public void onSuccess() {
                            mView.showLoadingStatus(false);
                        }

                        @Override
                        public void onFailure() {
                            mView.showLoadingStatus(false);
                        }
                    });
                } else {
                    mView.showNoInternet();
                }
            }

            @Override
            public void onLoadFailed() {
                Log.d(LOG_TAG, "onLoadFailed -- movie");
            }
        });
    }

    @Override
    public void saveMovie() {
        if (mData.isMovieInDb(mMovie.getId())) {
            mData.deleteMovie(mMovie.getId());
            mView.showMovieRemoved();
        } else {
            mData.insertMovie(mMovie);
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
