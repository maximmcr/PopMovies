package com.maximmcr.android.popmovies.moviedetails;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.maximmcr.android.popmovies.R;
import com.maximmcr.android.popmovies.data.model.Movie;
import com.maximmcr.android.popmovies.data.model.Review;
import com.maximmcr.android.popmovies.data.model.Video;
import com.maximmcr.android.popmovies.data.source.local.MoviesContract;
import com.maximmcr.android.popmovies.settings.SharedPrefsRepoImpl;
import com.maximmcr.android.popmovies.utils.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

import static com.maximmcr.android.popmovies.R.id.detail_fab_favourite;

public class DetailedActivity extends AppCompatActivity {

    public static final String LOG_TAG = DetailedActivity.class.getSimpleName();

    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_TAGLINE,
            MoviesContract.MovieEntry.COLUMN_POSTER,
            MoviesContract.MovieEntry.COLUMN_DATE,
            MoviesContract.MovieEntry.COLUMN_RUNTIME,
            MoviesContract.MovieEntry.COLUMN_RATING,
            MoviesContract.MovieEntry.COLUMN_POPULARITY,
            MoviesContract.MovieEntry.COLUMN_OVERVIEW
    };
    private static final int COLUMN_MOVIE_ID = 0;
    private static final int COLUMN_MOVIE_TITLE = 1;
    private static final int COLUMN_MOVIE_TAGLINE = 2;
    private static final int COLUMN_MOVIE_POSTER = 3;
    private static final int COLUMN_MOVIE_DATE = 4;
    private static final int COLUMN_MOVIE_RUNTIME = 5;
    private static final int COLUMN_MOVIE_RATING = 6;
    private static final int COLUMN_MOVIE_POPULARITY = 7;
    private static final int COLUMN_MOVIE_OVERVIEW = 8;

    private static final String[] COMMENT_COLUMNS = {
            MoviesContract.ReviewEntry.COLUMN_MOVIE_KEY,
            MoviesContract.ReviewEntry.COLUMN_URL,
            MoviesContract.ReviewEntry.COLUMN_CONTENT,
            MoviesContract.ReviewEntry.COLUMN_AUTHOR
    };
    private static final int COLUMN_COMMENT_KEY = 0;
    private static final int COLUMN_COMMENT_URL = 1;
    private static final int COLUMN_COMMENT_CONTENT = 2;
    private static final int COLUMN_COMMENT_AUTHOR = 3;

    private static final String[] VIDEO_COLUMNS = {
            MoviesContract.VideoEntry.COLUMN_MOVIE_KEY,
            MoviesContract.VideoEntry.COLUMN_NAME,
            MoviesContract.VideoEntry.COLUMN_PATH,
            MoviesContract.VideoEntry.COLUMN_TYPE
    };
    private static final int COLUMN_VIDEO_KEY = 0;
    private static final int COLUMN_VIDEO_NAME = 1;
    private static final int COLUMN_VIDEO_PATH = 2;
    private static final int COLUMN_VIDEO_TYPE = 3;

    private static final String SAVE_MOVIE_TAG = "movie";

    private Movie mMovie;

    @BindView(R.id.detail_scrollview)
    NestedScrollView scrollView;

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

    @BindView(R.id.detail_comment_viewpager)
    CommentViewPager mCommentViewPager;
    @BindView(R.id.detail_video_listview)
    ListViewCompat mVideoListView;

    // элементы, которые появляются при доступности раздела (обзоры или видео)
    @BindViews({R.id.detail_review_header, R.id.detail_review_divider})
    List<View> mReviewElements;
    @BindViews({R.id.detail_video_header, R.id.detail_video_divider})
    List<View> mVideoElements;

    // TODO: 21.09.2017 hide button before all data will be loaded
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        ButterKnife.bind(this);
        if (savedInstanceState == null || !savedInstanceState.containsKey("movie")) {
            String id = getIntent().getStringExtra("id");
            if (SharedPrefsRepoImpl.getInstance(getApplicationContext()).isOptionSaved()) {
                getMovieFromDB(Long.parseLong(id));
                updateInfoOnScreen();
            } else {
                //getMovie(id);
            }
        } else {
            mMovie = savedInstanceState.getParcelable(SAVE_MOVIE_TAG);
            updateInfoOnScreen();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void updateInfoOnScreen() {
        updateMovieInfo();
        updateCommentInfo();
        updateVideoInfo();
        initializeFAB();
    }

    private void updateMovieInfo() {
        if (SharedPrefsRepoImpl.getInstance(getApplicationContext()).isOptionSaved()) {
            mPoster.setImageBitmap(Utility.stringToBitmap(mMovie.getPosterPath()));
        } else {
            Picasso.with(getApplicationContext())
                    .load("http://image.tmdb.org/t/p/w185/" + mMovie.getPosterPath())
                    .into(mPoster);
        }
        mTitle.setText(mMovie.getTitle());
        mTagline.setText(mMovie.getTitle());
        mReleaseDate.setText(getString(
                R.string.format_release_date,
                Utility.formatDate(mMovie.getReleaseDate())));
        mRuntime.setText(getString(R.string.format_runtime, mMovie.getRuntime()));
        mPopularity.setText(getString(R.string.format_popular, mMovie.getPopularity()));
        mRating.setText(getString(R.string.format_rating, mMovie.getRating()));
        mOverview.setText(mMovie.getOverview());
    }

    private void updateCommentInfo() {
        if (mCommentViewPager.getAdapter() == null) {
            mCommentViewPager.setAdapter(new CommentVPAdapter(getApplicationContext(), mMovie.getReviews()));
        } else {
            CommentVPAdapter ca = (CommentVPAdapter) mCommentViewPager.getAdapter();
            ca.mReview.clear();
            ca.mReview.addAll(mMovie.getReviews());
            ca.notifyDataSetChanged();
        }
        if (mCommentViewPager.getAdapter().getCount() == 0 ||
                mCommentViewPager.getAdapter() == null) {
            ButterKnife.apply(mReviewElements, GONE);
        }
    }

    private void updateVideoInfo() {
        if (mVideoListView.getAdapter() == null) {
            mVideoListView.setAdapter(new VideoAdapter(getApplicationContext(), mMovie.getVideos()));
            setListViewHeightBasedOnChildren(mVideoListView);
        } else {
            VideoAdapter va = (VideoAdapter) mVideoListView.getAdapter();
            va.clear();
            va.addAll(mMovie.getVideos());
            va.notifyDataSetChanged();
        }
        if (mVideoListView.getAdapter().getCount() == 0 ||
                mVideoListView.getAdapter() == null) {
            ButterKnife.apply(mVideoElements, GONE);
        }
    }

    private void initializeFAB() {
        final FloatingActionButton fabFavourites = (FloatingActionButton) findViewById(detail_fab_favourite);
        if (isMovieInDB(mMovie.getId()))
            fabFavourites.setImageResource(R.drawable.ic_favorite_white_24dp);
        else fabFavourites.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        // TODO: 04.08.2017 refactor onClick for delete action
        fabFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int movieId = mMovie.getId();
                boolean isMovieInDB = isMovieInDB(movieId);
                if (!isMovieInDB) {
                    insertMovieToDB(movieId);
                    fabFavourites.setImageResource(R.drawable.ic_favorite_white_24dp);
                    Snackbar.make(findViewById(R.id.activity_detailed),
                            R.string.snackbar_movie_added, Snackbar.LENGTH_SHORT)
                            .show();
                } else {
                    deleteMovieFromDB(movieId);
//                    if (SharedPrefsRepoImpl.getInstance(getApplicationContext()).isOptionSaved()) {
//                        CallbackMovieRemoved callback = MoviesActivity.mMovieInfoAdapter;
//                        callback.removeMovie(movieId);
//                    }
                    fabFavourites.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    Snackbar.make(findViewById(R.id.activity_detailed),
                            R.string.snackbar_movie_removed, Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });
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

    //methods for working with db
    private boolean isMovieInDB(long id) {
        boolean result = false;
        Uri query = MoviesContract.MovieEntry.buildMovieUri(id);
        Cursor cursor;
        cursor = getContentResolver().query(
                query,
                null,
                null,
                null,
                null);
        if (cursor.moveToFirst()) result = true;
        Log.d(LOG_TAG, cursor.toString());
        cursor.close();
        return result;
    }

    private void insertMovieToDB(long id) {
        //inserting base movie info
        ContentValues movieValues = new ContentValues();
        movieValues.put(MoviesContract.MovieEntry._ID, id);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, mMovie.getTitle());
        movieValues.put(MoviesContract.MovieEntry.COLUMN_TAGLINE, mMovie.getTagline());
        ImageView img = (ImageView) findViewById(R.id.detail_image);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER,
                Utility.drawableToByteArray(img.getDrawable()));
        movieValues.put(MoviesContract.MovieEntry.COLUMN_DATE, mMovie.getReleaseDate());
        movieValues.put(MoviesContract.MovieEntry.COLUMN_RUNTIME, mMovie.getRuntime());
        movieValues.put(MoviesContract.MovieEntry.COLUMN_RATING, mMovie.getRating());
        movieValues.put(MoviesContract.MovieEntry.COLUMN_POPULARITY, mMovie.getPopularity());
        movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, mMovie.getOverview());

        getContentResolver().insert(
                MoviesContract.MovieEntry.buildMovieUri(id),
                movieValues);

        //inserting reviews
        if (mMovie.getReviews() != null) {
            Vector<ContentValues> commentValues = new Vector<>();
            for (Review review : mMovie.getReviews()) {
                ContentValues value = new ContentValues();
                value.put(MoviesContract.ReviewEntry.COLUMN_MOVIE_KEY, id);
                value.put(MoviesContract.ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
                value.put(MoviesContract.ReviewEntry.COLUMN_CONTENT, review.getContent());
                value.put(MoviesContract.ReviewEntry.COLUMN_URL, review.getUrl());
                commentValues.add(value);
            }

            getContentResolver().bulkInsert(
                    MoviesContract.ReviewEntry.buildMovieUri(id),
                    commentValues.toArray(new ContentValues[commentValues.size()]));
        }

        //inserting videos
        if (mMovie.getVideos() != null) {
            Vector<ContentValues> videoValues = new Vector<>();
            for (Video video : mMovie.getVideos()) {
                ContentValues value = new ContentValues();
                value.put(MoviesContract.VideoEntry.COLUMN_MOVIE_KEY, id);
                value.put(MoviesContract.VideoEntry.COLUMN_NAME, video.getName());
                value.put(MoviesContract.VideoEntry.COLUMN_PATH, video.getPath());
                value.put(MoviesContract.VideoEntry.COLUMN_TYPE, video.getType());
                videoValues.add(value);
            }

            getContentResolver().bulkInsert(
                    MoviesContract.VideoEntry.buildMovieUri(id),
                    videoValues.toArray(new ContentValues[videoValues.size()]));
        }
    }

    private void deleteMovieFromDB(long id) {
        int count = getContentResolver().delete(
                MoviesContract.MovieEntry.buildMovieUri(id),
                null,
                null);
        Log.d(LOG_TAG, "Deleted " + count + "rows from db");
    }

    private void getMovieFromDB(long id) {

        Cursor commentCursor = getContentResolver().query(
                MoviesContract.ReviewEntry.buildMovieUri(id),
                COMMENT_COLUMNS,
                null,
                null,
                null
        );
        ArrayList<Review> reviews = new ArrayList<>();
        if (commentCursor.getCount() > 0) {
            commentCursor.moveToFirst();
            for (int i = 0; i < commentCursor.getCount(); i++) {
                String author = commentCursor.getString(COLUMN_COMMENT_AUTHOR);
                String content = commentCursor.getString(COLUMN_COMMENT_CONTENT);
                String url = commentCursor.getString(COLUMN_COMMENT_URL);
                Review review = new Review(
                        author,
                        content,
                        url
                );
                reviews.add(review);
                commentCursor.moveToNext();
            }
        }
        commentCursor.close();

        Cursor videoCursor = getContentResolver().query(
                MoviesContract.VideoEntry.buildMovieUri(id),
                VIDEO_COLUMNS,
                null,
                null,
                null
        );
        ArrayList<Video> videos = new ArrayList<>();
        if (videoCursor.getCount() > 0) {
            videoCursor.moveToFirst();
            for (int i = 0; i < videoCursor.getCount(); i++) {
                String path = videoCursor.getString(COLUMN_VIDEO_PATH);
                String name = videoCursor.getString(COLUMN_VIDEO_NAME);
                String type = videoCursor.getString(COLUMN_VIDEO_TYPE);
                Video video = new Video(
                        path,
                        name,
                        type
                );
                videos.add(video);
                videoCursor.moveToNext();
            }
        }
        videoCursor.close();

        Cursor movieCursor = getContentResolver().query(
                MoviesContract.MovieEntry.buildMovieUri(id),
                MOVIE_COLUMNS,
                null,
                null,
                null
        );
        movieCursor.moveToFirst();

        mMovie = new Movie(
                movieCursor.getInt(COLUMN_MOVIE_ID),
                movieCursor.getString(COLUMN_MOVIE_TITLE),
                movieCursor.getString(COLUMN_MOVIE_TAGLINE),
                Utility.byteArrayToString(movieCursor.getBlob(COLUMN_MOVIE_POSTER)),
                movieCursor.getString(COLUMN_MOVIE_DATE),
                movieCursor.getInt(COLUMN_MOVIE_RUNTIME),
                movieCursor.getDouble(COLUMN_MOVIE_RATING),
                movieCursor.getDouble(COLUMN_MOVIE_POPULARITY),
                movieCursor.getString(COLUMN_MOVIE_OVERVIEW),
                reviews,
                videos
        );
        movieCursor.close();
    }

    private String getPosterFromDB (long id) {
        final String[] COLUMN_POSTER = {MoviesContract.MovieEntry.COLUMN_POSTER };
        final int COLUMN_MOVIE_POSTER = 0;
        Cursor movieCursor = getContentResolver().query(
                MoviesContract.MovieEntry.buildMovieUri(id),
                COLUMN_POSTER,
                null,
                null,
                null
        );
        movieCursor.moveToFirst();
        return Utility.byteArrayToString(movieCursor.getBlob(COLUMN_MOVIE_POSTER));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVE_MOVIE_TAG, mMovie);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

//    private void getMovie(String id) {
//        final String API_KEY = BuildConfig.API_KEY_TMDB;
//        mMovie = new Movie();
//        PopMoviesApplication.getTmdbApi().getMovie(id, API_KEY).enqueue(new Callback<Movie>() {
//            @Override
//            public void onResponse(Call<Movie> call, Response<Movie> response) {
//                if (response.isSuccessful()) {
//                    mMovie.fetchBaseInfo(response.body());
//                    updateMovieInfo();
//                    initializeFAB();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Movie> call, Throwable t) {
//
//            }
//        });
//        PopMoviesApplication.getTmdbApi().getReviewList(id, API_KEY).enqueue(new Callback<Review.Response>() {
//            @Override
//            public void onResponse(Call<Review.Response> call, Response<Review.Response> response) {
//                if (response.isSuccessful()) {
//                    mMovie.setReviews(response.body().reviews);
//                    updateCommentInfo();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Review.Response> call, Throwable t) {
//
//            }
//        });
//        PopMoviesApplication.getTmdbApi().getVideoList(id, API_KEY).enqueue(new Callback<Video.Response>() {
//            @Override
//            public void onResponse(Call<Video.Response> call, Response<Video.Response> response) {
//                if (response.isSuccessful()) {
//                    mMovie.setVideos(response.body().videos);
//                    updateVideoInfo();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Video.Response> call, Throwable t) {
//
//            }
//        });
//    }

    interface CallbackMovieRemoved {
        void removeMovie(int id);
    }

    static final ButterKnife.Action<View> GONE = new ButterKnife.Action<View>() {
        @Override
        public void apply(@NonNull View view, int index) {
            view.setVisibility(View.GONE);
        }
    };
}
