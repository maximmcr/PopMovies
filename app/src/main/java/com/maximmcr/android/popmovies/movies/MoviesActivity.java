package com.maximmcr.android.popmovies.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.maximmcr.android.popmovies.R;
import com.maximmcr.android.popmovies.data.source.MovieRepository;
import com.maximmcr.android.popmovies.settings.SettingsActivity;
import com.maximmcr.android.popmovies.settings.SharedPrefsRepoImpl;

public class MoviesActivity extends AppCompatActivity {

    public static final String LOG_TAG = MoviesActivity.class.getSimpleName();

    private MoviesContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        MoviesFragment moviesFragment = (MoviesFragment)
                getSupportFragmentManager().findFragmentById(R.id.frag_movies);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frag_movies, moviesFragment)
                .commit();

        mPresenter = new MoviesPresenter(
                moviesFragment,
                MovieRepository.getInstance(getApplicationContext()),
                SharedPrefsRepoImpl.getInstance(getApplicationContext())
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // TODO: 24.10.2017 move menu handler to fragment
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return true;
    }
}
