package com.example.android.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Frei on 24.01.2017.
 */

public class MovieInfo implements Parcelable {
    int mId;
    String mTitle;
    String mTagline;
    String mPoster;
    String mReleaseDate;
    int mRuntime;
    double mRating;
    double mPopularity;
    String mOverview;

    ArrayList<Comment> mComments;
    ArrayList<Video> mYoutubeAdresses;

    public MovieInfo(int id, String title, String tagline, String poster, String releaseDate,
                     int runtime, double rating, double popularity, String overview,
                     ArrayList<Comment> comments, ArrayList<Video> youtubeAdresses) {
        mId = id;
        mTitle = title;
        mTagline = tagline;
        mPoster = poster;
        mReleaseDate = releaseDate;
        mRuntime = runtime;
        mRating = rating;
        mPopularity = popularity;
        mOverview = overview;

        mComments = comments;
        mYoutubeAdresses = youtubeAdresses;
    }

    public MovieInfo(String posterId, int id) {
        this.mPoster = posterId;
        this.mId = id;
    }

    private MovieInfo(Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mTagline = in.readString();
        mPoster = in.readString();
        mReleaseDate = in.readString();
        mRuntime = in.readInt();
        mRating = in.readDouble();
        mPopularity = in.readDouble();
        mOverview = in.readString();

        in.readTypedList(mComments, Comment.CREATOR);
        in.readTypedList(mYoutubeAdresses, Video.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mTitle);
        dest.writeString(mTagline);
        dest.writeString(mPoster);
        dest.writeString(mReleaseDate);
        dest.writeInt(mRuntime);
        dest.writeDouble(mRating);
        dest.writeDouble(mPopularity);
        dest.writeString(mOverview);

        dest.writeTypedList(mComments);
        dest.writeTypedList(mYoutubeAdresses);
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

    public static class Video implements Parcelable {

        String mPath;
        String mName;
        String mType;

        public Video(String path, String name, String type) {
            mName = name;
            mPath = path;
            mType = type;
        }

        protected Video(Parcel in) {
            mPath = in.readString();
            mName = in.readString();
            mType = in.readString();
        }

        public static final Creator<Video> CREATOR = new Creator<Video>() {
            @Override
            public Video createFromParcel(Parcel in) {
                return new Video(in);
            }

            @Override
            public Video[] newArray(int size) {
                return new Video[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mPath);
            dest.writeString(mName);
            dest.writeString(mType);
        }
    }
}
