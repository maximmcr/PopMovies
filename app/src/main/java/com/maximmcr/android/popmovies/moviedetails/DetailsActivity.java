package com.maximmcr.android.popmovies.moviedetails;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.maximmcr.android.popmovies.R;
import com.maximmcr.android.popmovies.data.source.MovieRepository;
import com.maximmcr.android.popmovies.movies.MoviesActivity;

public class DetailsActivity extends AppCompatActivity {

    public static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    private static final String SAVE_MOVIE_TAG = "movie";

    private DetailsContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        DetailsFragment detailsFragment =
                (DetailsFragment) getSupportFragmentManager().findFragmentById(R.id.details_fragment);
        attachPresenter(detailsFragment);
    }

    private void attachPresenter(DetailsContract.View view) {
        if (getLastNonConfigurationInstance() == null) {
            mPresenter = new DetailsPresenter(
                    MovieRepository.getInstance(getApplicationContext()),
                    getIntent().getIntExtra(MoviesActivity.MOVIE_ID_TAG, 0)
            );
        } else {
            mPresenter = (DetailsContract.Presenter) getLastCustomNonConfigurationInstance();
        }
        mPresenter.attachView(view);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mPresenter;
    }
}
