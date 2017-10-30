package com.maximmcr.android.popmovies.moviedetails;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.maximmcr.android.popmovies.R;
import com.maximmcr.android.popmovies.data.source.MovieRepository;

public class DetailsActivity extends AppCompatActivity {

    public static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    private static final String SAVE_MOVIE_TAG = "movie";

    private DetailsContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
//        Intent comingIntent = getIntent();
//        int id = comingIntent.getIntExtra(MoviesActivity.MOVIE_ID_TAG, 0);

        DetailsFragment detailsFragment =
                (DetailsFragment) getSupportFragmentManager().findFragmentById(R.id.details_fragment);

        mPresenter = new DetailsPresenter(
                MovieRepository.getInstance(getApplicationContext()),
                detailsFragment
        );
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
