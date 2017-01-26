package com.example.android.popmovies;

/**
 * Created by Frei on 24.01.2017.
 */

public class MovieInfo {
    String posterId;
    String title;
    String releaseDate;
    double rating;
    double popularity;
    String overview;
    int id;

    public MovieInfo(String title, String posterId, String releaseDate,
                     String overview, double rating, double popularity, int id) {
        this.overview = overview;
        this.posterId = posterId;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.title = title;
        this.popularity = popularity;
        this.id = id;
    }
    public MovieInfo(MovieInfo movie) {
        posterId = movie.posterId;
        title = movie.title;
        releaseDate = movie.releaseDate;
        rating = movie.rating;
        popularity = movie.popularity;
        overview = movie.overview;
        id = movie.id;
    }
}
