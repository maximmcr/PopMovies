package com.example.android.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

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

    ArrayList<Comment> comments = new ArrayList<>();

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

    public MovieInfo(String posterId, int id) {
        this.posterId = posterId;
        this.id = id;
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
        in.readTypedList(comments, Comment.CREATOR);
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
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

        dest.writeTypedList(comments);
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

    public static class Comment implements Parcelable {

        String mAuthor;
        String mContent;
        String mUrl;

        public Comment(String author, String content, String url) {
            mAuthor = author;
            mContent = content;
            mUrl = url;
        }

        protected Comment(Parcel in) {
            mAuthor = in.readString();
            mContent = in.readString();
            mUrl = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mAuthor);
            dest.writeString(mContent);
            dest.writeString(mUrl);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Comment> CREATOR = new Creator<Comment>() {
            @Override
            public Comment createFromParcel(Parcel in) {
                return new Comment(in);
            }

            @Override
            public Comment[] newArray(int size) {
                return new Comment[size];
            }
        };
    }
}
