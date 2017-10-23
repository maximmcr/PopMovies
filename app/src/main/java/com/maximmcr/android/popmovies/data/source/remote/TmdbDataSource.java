package com.maximmcr.android.popmovies.data.source.remote;

import android.os.AsyncTask;
import android.util.Log;

import com.maximmcr.android.popmovies.BuildConfig;
import com.maximmcr.android.popmovies.data.model.Movie;
import com.maximmcr.android.popmovies.data.model.Review;
import com.maximmcr.android.popmovies.data.model.Video;
import com.maximmcr.android.popmovies.data.source.MovieDataSource;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Frei on 04.10.2017.
 */

public class TmdbDataSource implements MovieDataSource{

    private static TmdbDataSource INSTANCE = null;

    private static final String LOG_TAG = TmdbDataSource.class.getSimpleName();

    private static final String API_KEY = BuildConfig.API_KEY_TMDB;
    private static final String TMDB_REQUEST_BASE = "http://api.themoviedb.org/3/movie/";
    private static TmdbApi tmdbApi;

    private TmdbDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TMDB_REQUEST_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tmdbApi = retrofit.create(TmdbApi.class);
    }

    public static TmdbDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TmdbDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void getMovie(final LoadMovieCallback callback, int id) {

        new getMovieAsync(new LoadMovieCallback() {
            @Override
            public void onMovieLoaded(Movie movie) {
                callback.onMovieLoaded(movie);
            }

            @Override
            public void onLoadFailed() {
                callback.onLoadFailed();
            }
        }).execute(id);

    }

    @Override
    public void getMovieList(final LoadMovieListCallback callback, String filterType) {
        tmdbApi
                .getMovieList(filterType, API_KEY)
                .enqueue(new Callback<Movie.Response>() {
                    @Override
                    public void onResponse(Call<Movie.Response> call, Response<Movie.Response> response) {
                        callback.onMovieListLoaded(response.body().movies);
                    }

                    @Override
                    public void onFailure(Call<Movie.Response> call, Throwable t) {
                        Log.d(LOG_TAG, "Retrofit method " + t);
                        callback.onLoadFailed();
                    }
                });
    }

    @Override
    public void insertMovie(Movie movie) { }

    @Override
    public void deleteMovie(int id) { }

    private class getMovieAsync extends AsyncTask<Integer, Void, Movie> {

        private LoadMovieCallback listener;

        public getMovieAsync(LoadMovieCallback listener) {
            this.listener = listener;
        }

        @Override
        protected Movie doInBackground(Integer... params) {
            int id = params[0];
            Movie movie = new Movie();
            try {
                Response<Movie> movieResponse = tmdbApi
                        .getMovie(id, API_KEY)
                        .execute();
                if (movieResponse.isSuccessful()) movie.fetchBaseInfo(movieResponse.body());
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "Crash on movie loading");
            }

            try {
                Response<Video.Response> videoResponse = tmdbApi
                        .getVideoList(id, API_KEY)
                        .execute();
                if (videoResponse.isSuccessful()) movie.setVideos(videoResponse.body().videos);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "Crash on video loading");
            }

            try {
                Response<Review.Response> reviewResponse = tmdbApi
                        .getReviewList(id, API_KEY)
                        .execute();
                if (reviewResponse.isSuccessful()) movie.setReviews(reviewResponse.body().reviews);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "Crash on review loading");
            }

            return movie;
        }

        @Override
        protected void onPostExecute(Movie movie) {
            super.onPostExecute(movie);

            if (movie != null && movie.getId() > -1) listener.onMovieLoaded(movie);
            else listener.onLoadFailed();
        }
    }
}
