package com.maximmcr.android.popmovies.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.maximmcr.android.popmovies.R;
import com.maximmcr.android.popmovies.data.source.MovieRepository;
import com.maximmcr.android.popmovies.settings.SettingsActivity;
import com.maximmcr.android.popmovies.settings.SharedPrefsRepoImpl;

public class MoviesActivity extends AppCompatActivity {

    public static final String LOG_TAG = MoviesActivity.class.getSimpleName();
    public static final String MOVIE_ID_TAG = "movie_id";

    private MoviesContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        MoviesFragment moviesFragment =
                (MoviesFragment) getSupportFragmentManager().findFragmentById(R.id.movies_fragment);

        attachPresenter(moviesFragment);
    }

    private void attachPresenter(MoviesContract.View view) {
        if (getLastCustomNonConfigurationInstance() == null) {
            mPresenter = new MoviesPresenter(
                    MovieRepository.getInstance(getApplicationContext()),
                    SharedPrefsRepoImpl.getInstance(getApplicationContext())
            );
        } else {
            mPresenter = (MoviesContract.Presenter) getLastCustomNonConfigurationInstance();
        }
        mPresenter.attachView(view);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mPresenter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return false;
    }
}
