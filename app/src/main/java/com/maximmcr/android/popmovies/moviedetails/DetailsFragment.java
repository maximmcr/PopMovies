package com.maximmcr.android.popmovies.moviedetails;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.maximmcr.android.popmovies.R;
import com.maximmcr.android.popmovies.data.model.Movie;
import com.maximmcr.android.popmovies.data.model.Review;
import com.maximmcr.android.popmovies.data.model.Video;
import com.maximmcr.android.popmovies.movies.MoviesActivity;
import com.maximmcr.android.popmovies.utils.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Frei on 26.10.2017.
 */

public class DetailsFragment extends Fragment implements DetailsContract.View {

    public static final String LOG_TAG = DetailsFragment.class.getSimpleName();

    private static final String YOUTUBE_REQUEST_BASE = "https://www.youtube.com/watch";

    @BindView(R.id.detail_image)
    ImageView mPoster;
    @BindView(R.id.detail_title)
    TextView mTitle;
    @BindView(R.id.detail_tagline)
    TextView mTagline;
    @BindView(R.id.detail_date)
    TextView mReleaseDate;
    @BindView(R.id.detail_runtime)
    TextView mRuntime;
    @BindView(R.id.detail_popularity)
    TextView mPopularity;
    @BindView(R.id.detail_rating)
    TextView mRating;
    @BindView(R.id.detail_overview)
    TextView mOverview;

    @BindView(R.id.detail_review_vp)
    ReviewViewPager mReviewViewPager;
    @BindView(R.id.detail_video_listview)
    ListViewCompat mVideoListView;

    // элементы, которые появляются при доступности раздела (обзоры или видео)
    @BindViews({R.id.detail_review_header, R.id.detail_review_divider})
    List<View> mReviewElements;
    @BindViews({R.id.detail_video_header, R.id.detail_video_divider})
    List<View> mVideoElements;

    @BindView(R.id.detail_progressbar)
    ProgressBar mProgressbar;

    @BindView(R.id.detail_scrollview)
    NestedScrollView mScrollview;

    @BindView(R.id.detail_fab_favourite)
    FloatingActionButton mFabFavourite;

    private Unbinder mUnbinder;

    static final ButterKnife.Action<View> GONE = new ButterKnife.Action<View>() {
        @Override
        public void apply(@NonNull View view, int index) {
            view.setVisibility(View.GONE);
        }
    };
    static final ButterKnife.Setter<View, Boolean> VISIBLE = new ButterKnife.Setter<View, Boolean>() {
        @Override
        public void set(@NonNull View view, Boolean value, int index) {
            if (value) view.setVisibility(View.VISIBLE);
            else view.setVisibility(View.INVISIBLE);
        }
    };

    private DetailsContract.Presenter mPresenter;

    ReviewVPAdapter mReviewAdapter;
    VideoAdapter mVideoAdapter;

    public DetailsFragment() {
    }

    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mReviewAdapter = new ReviewVPAdapter(getContext(), new ArrayList<Review>(),
                new ReviewVPAdapter.OnReviewClickListener() {
                    @Override
                    public void onReviewClick(Review review) {
                        mPresenter.openReview(review);
                    }
                });

        mVideoAdapter = new VideoAdapter(getContext(), new ArrayList<Video>(),
                new VideoAdapter.OnVideoClickListener() {
                    @Override
                    public void onVideoClick(Video video) {
                        mPresenter.openVideo(video);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.loadMovie();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.frag_details, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        mReviewViewPager.setAdapter(mReviewAdapter);
        mVideoListView.setAdapter(mVideoAdapter);
        mFabFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.saveMovie();
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void setPresenter(DetailsContract.Presenter presenter) {
        mPresenter = presenter;
        // FIXME: 29.10.2017 refactor it for using dynamic fragments at tablet ui
        int id = getActivity().getIntent().getIntExtra(MoviesActivity.MOVIE_ID_TAG, 0);
        mPresenter.setId(id);
    }

    @Override
    public void showMovie(Movie movie, final DetailsContract.OnPosterLoadedCallback callback) {
        Picasso.with(getContext())
                .load(movie.getPosterPath())
                .into(mPoster, new Callback() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess();
                    }

                    @Override
                    public void onError() {
                        callback.onFailure();
                    }
                });
        mTitle.setText(movie.getTitle());
        mTagline.setText(movie.getTitle());
        mReleaseDate.setText(getString(
                R.string.format_release_date,
                Utility.formatDate(movie.getReleaseDate())));
        mRuntime.setText(getString(R.string.format_runtime, movie.getRuntime()));
        mPopularity.setText(getString(R.string.format_popular, movie.getPopularity()));
        mRating.setText(getString(R.string.format_rating, movie.getRating()));
        mOverview.setText(movie.getOverview());

        ArrayList<Review> reviews = movie.getReviews();
        if (reviews == null || reviews.size() == 0) {
            ButterKnife.apply(mReviewElements, GONE);
            ButterKnife.apply(mReviewViewPager, GONE);
        } else {
            mReviewAdapter.replace(reviews);
        }

        ArrayList<Video> videos = movie.getVideos();
        if (videos == null || videos.size() == 0) {
            ButterKnife.apply(mVideoElements, GONE);
            ButterKnife.apply(mVideoListView, GONE);
        } else {
            mVideoAdapter.replace(videos);
        }
        setListViewHeightBasedOnChildren(mVideoListView);

        if (mPresenter.isMovieInDb(movie.getId())) {
            mFabFavourite.setImageResource(R.drawable.ic_favorite_white_24dp);
        } else {
            mFabFavourite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }
    }

    private void setListViewHeightBasedOnChildren(ListView lv) {
        ListAdapter adapter = lv.getAdapter();
        if (adapter == null) {
            Log.d(LOG_TAG, "ListView adapter is null");
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View child = adapter.getView(i, null, lv);
            child.measure(0, 0);
            totalHeight += child.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = lv.getLayoutParams();
        params.height = totalHeight + (lv.getDividerHeight() * (lv.getChildCount() - 1));
        lv.setLayoutParams(params);
    }

    @Override
    public void showVideo(String url) {
        Uri uri = Uri.parse(YOUTUBE_REQUEST_BASE)
                .buildUpon()
                .appendQueryParameter("v", url)
                .build();
        Intent playVideo = new Intent(Intent.ACTION_VIEW, uri);
        if (playVideo.resolveActivity(getContext().getPackageManager()) != null) {
            Intent chooser = Intent.createChooser(playVideo, "Play via");
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(chooser);
        }
    }

    @Override
    public void showReview(String url) {
        Uri uri = Uri.parse(url);
        Intent openComment = new Intent(Intent.ACTION_VIEW, uri);
        openComment.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(openComment);
    }

    @Override
    public void showMovieAdded() {
        mFabFavourite.setImageResource(R.drawable.ic_favorite_white_24dp);
        showMessage(getString(R.string.snackbar_movie_added));
    }

    @Override
    public void showMovieRemoved() {
        mFabFavourite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        showMessage(getString(R.string.snackbar_movie_removed));
    }

    @Override
    public void showNoInternet() {
        showMessage(getString(R.string.snackbar_no_internet));
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void showLoadingStatus(boolean status) {
        View[] mainUi = new View[] {mScrollview, mFabFavourite};
        if (status) {
            ButterKnife.apply(mainUi, VISIBLE, false);
            ButterKnife.apply(mProgressbar, VISIBLE, true);
        } else {
            ButterKnife.apply(mProgressbar, VISIBLE, false);
            ButterKnife.apply(mainUi, VISIBLE, true);
        }
    }

    @Override
    public boolean isOnline() {
        return Utility.isOnline(getContext());
    }
}
