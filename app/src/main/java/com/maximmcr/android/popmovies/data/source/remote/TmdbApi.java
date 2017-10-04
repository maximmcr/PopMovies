package com.maximmcr.android.popmovies.data.source.remote;

import com.maximmcr.android.popmovies.data.model.Review;
import com.maximmcr.android.popmovies.data.model.Movie;
import com.maximmcr.android.popmovies.data.model.Video;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Frei on 12.09.2017.
 */

public interface TmdbApi {
    @GET("{type}")
    Call<Movie.Response> getMovieList(@Path("type") String filterType,
                                      @Query("api_key") String apiKey);

    @GET("{movie_id}")
    Call<Movie> getMovie(@Path("movie_id") int movieId,
                         @Query("api_key") String apiKey);

    @GET("{movie_id}/videos")
    Call<Video.Response> getVideoList(@Path("movie_id") int movieId,
                                      @Query("api_key") String apiKey);

    @GET("{movie_id}/reviews")
    Call<Review.Response> getReviewList(@Path("movie_id") int movieId,
                                        @Query("api_key") String apiKey);
}
