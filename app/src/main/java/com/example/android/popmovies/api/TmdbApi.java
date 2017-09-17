package com.example.android.popmovies.api;

import com.example.android.popmovies.model.CommentModel;
import com.example.android.popmovies.model.MovieModel;
import com.example.android.popmovies.model.VideoModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Frei on 12.09.2017.
 */

public interface TmdbApi {
    @GET("{type}")
    Call<MovieModel.Response> getMovieList(@Path("type") String type, @Query("api_key") String apiKey);

    @GET("{movie_id}")
    Call<MovieModel> getMovie(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    @GET("{movie_id}/videos")
    Call<List<VideoModel>> getVideoList(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    @GET("{movie_id}/reviews")
    Call<List<CommentModel>> getReviewList(@Path("movie_id") int movieId, @Query("api_key") String apiKey);
}
