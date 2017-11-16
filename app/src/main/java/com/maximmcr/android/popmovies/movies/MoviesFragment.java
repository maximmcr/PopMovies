package com.maximmcr.android.popmovies.movies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maximmcr.android.popmovies.R;
import com.maximmcr.android.popmovies.data.model.Movie;
import com.maximmcr.android.popmovies.moviedetails.DetailsActivity;
import com.maximmcr.android.popmovies.utils.Utility;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Frei on 23.10.2017.
 */

public class MoviesFragment extends Fragment implements MoviesContract.View, MoviesAdapter.OnItemClickListener {

    public static final String LOG_TAG = MoviesFragment.class.getSimpleName();
    private static final String SAVED_MOVIE_TAG = "movies";

    private MoviesContract.Presenter mPresenter;
    private MoviesAdapter mAdapter;
    private Unbinder mUnbinder;

    static final ButterKnife.Setter<View, Boolean> VISIBILITY = new ButterKnife.Setter<View, Boolean>() {
        @Override
        public void set(@NonNull View view, Boolean value, int index) {
            if (value) view.setVisibility(View.VISIBLE);
            else view.setVisibility(View.INVISIBLE);
        }
    };

    @BindView(R.id.movies_available_ll)
    LinearLayout mAvailableLL;

    @BindView(R.id.movies_rv)
    RecyclerView mMoviesRV;

    @BindView(R.id.movies_unavailable_ll)
    RelativeLayout mUnavailableLL;

    @BindView(R.id.movies_unavailable_tv)
    TextView mUnavailableTV;

    @BindView(R.id.movies_progressbar)
    ProgressBar mProgressBar;

    public MoviesFragment() {}

    public static MoviesFragment newInstance() {
        return new MoviesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //setRetainInstance(true);
        mAdapter = new MoviesAdapter(getContext(), new ArrayList<Movie>(), this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.frag_movies, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        mMoviesRV.setLayoutManager(new WrappedGLM(getContext(), 2));
        mMoviesRV.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movies, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "option handler entry");
        switch (item.getItemId()) {
            case R.id.action_refresh:
                mPresenter.updateView();
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "onResume");
        super.onResume();
        mPresenter.updateView();
    }

    @Override
    public void setPresenter(MoviesContract.Presenter presenter) {
        Log.d(LOG_TAG, "setPresenter");
        this.mPresenter = presenter;
    }

    @Override
    public void showMovieList(ArrayList<Movie> movies) {
        ButterKnife.apply(mUnavailableLL, VISIBILITY, false);
        ButterKnife.apply(mAvailableLL, VISIBILITY, true);
        mAdapter.replaceMovies(movies);
    }

    @Override
    public void showMovieDetails(int id) {
        Intent intent = new Intent(getContext(), DetailsActivity.class);
        intent.putExtra(MoviesActivity.MOVIE_ID_TAG, id);

        Pair<View, String> pairImg = Pair.create(
                mAdapter.getImageView(), getContext().getText(R.string.transition_poster).toString());

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(), pairImg);

        startActivity(intent, options.toBundle());

    }

    @Override
    public void showNoMoviesInDb() {
        ButterKnife.apply(mAvailableLL, VISIBILITY, false);
        ButterKnife.apply(mUnavailableLL, VISIBILITY, true);
    }

    @Override
    public void showNoInternet() {
        ButterKnife.apply(mUnavailableLL, VISIBILITY, false);
        ButterKnife.apply(mAvailableLL, VISIBILITY, true);
        showMessage(getString(R.string.snackbar_no_internet));
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void showLoadingIndicator(boolean status) {
        ButterKnife.apply(mProgressBar, VISIBILITY, status);
        ButterKnife.apply(mAvailableLL, VISIBILITY, !status);
    }

    @Override
    public boolean isOnline() {
        return Utility.isOnline(getContext());
    }

    @Override
    public void onItemClick(Movie movie) {
        mPresenter.openMovieDetails(movie);
    }

    private class WrappedGLM extends GridLayoutManager {
        private WrappedGLM(Context context, int spanCount) {
            super(context, spanCount);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("probe", "meet a IOOBE in RecyclerView");
            }
        }
    }
}
