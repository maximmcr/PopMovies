package com.example.android.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Frei on 24.01.2017.
 */

public class MovieInfo implements Parcelable {
    String posterId;
    String title;
    String releaseDate;
    String overview;
    String adult;
    String tagline;
    double rating;
    double popularity;
    int id;

    public MovieInfo(String title, String posterId, String adult, String tagline,
                     String overview, double rating, double popularity) {
        this.overview = overview;
        this.posterId = posterId;
        this.rating = rating;
        this.adult = adult;
        this.title = title;
        this.popularity = popularity;
        this.tagline = tagline;

        id = 0;
        releaseDate = "";
    }

    public MovieInfo(String title, String posterId, String releaseDate,
                     String overview, double rating, double popularity, int id) {
        this.overview = overview;
        this.posterId = posterId;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.title = title;
        this.popularity = popularity;
        this.id = id;

        adult = "";
        tagline = "";
    }

    private MovieInfo(Parcel in) {
        this.overview = in.readString();
        this.posterId = in.readString();
        this.releaseDate = in.readString();
        this.title = in.readString();
        this.rating = in.readDouble();
        this.popularity = in.readDouble();
        this.id = in.readInt();

        this.tagline = in.readString();
        this.adult = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterId);
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(overview);
        dest.writeDouble(rating);
        dest.writeDouble(popularity);
        dest.writeInt(id);

        dest.writeString(tagline);
        dest.writeString(adult);
    }

    public static final Parcelable.Creator<MovieInfo> CREATOR =
            new Creator<MovieInfo>() {

                @Override
                public MovieInfo createFromParcel(Parcel source) {
                    return new MovieInfo(source);
                }

                @Override
                public MovieInfo[] newArray(int size) {
                    return new MovieInfo[size];
                }
            };

}
