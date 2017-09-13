package com.example.android.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Frei on 24.01.2017.
 */

public class MovieModel implements Parcelable {
    int mId;
    String mTitle;
    String mTagline;
    String mPoster;
    String mReleaseDate;
    int mRuntime;
    double mRating;
    double mPopularity;
    String mOverview;

    ArrayList<CommentModel> mCommentModels;
    ArrayList<VideoModel> mYoutubeAdresses;

    public MovieModel(int id, String title, String tagline, String poster, String releaseDate,
                      int runtime, double rating, double popularity, String overview,
                      ArrayList<CommentModel> commentModels, ArrayList<VideoModel> youtubeAdresses) {
        mId = id;
        mTitle = title;
        mTagline = tagline;
        mPoster = poster;
        mReleaseDate = releaseDate;
        mRuntime = runtime;
        mRating = rating;
        mPopularity = popularity;
        mOverview = overview;

        mCommentModels = commentModels;
        mYoutubeAdresses = youtubeAdresses;
    }

    public MovieModel(String posterId, int id) {
        this.mPoster = posterId;
        this.mId = id;
    }

    private MovieModel(Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mTagline = in.readString();
        mPoster = in.readString();
        mReleaseDate = in.readString();
        mRuntime = in.readInt();
        mRating = in.readDouble();
        mPopularity = in.readDouble();
        mOverview = in.readString();

        in.readTypedList(mCommentModels, CommentModel.CREATOR);
        in.readTypedList(mYoutubeAdresses, VideoModel.CREATOR);
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

        dest.writeTypedList(mCommentModels);
        dest.writeTypedList(mYoutubeAdresses);
    }

    public static final Parcelable.Creator<MovieModel> CREATOR =
            new Creator<MovieModel>() {

                @Override
                public MovieModel createFromParcel(Parcel source) {
                    return new MovieModel(source);
                }

                @Override
                public MovieModel[] newArray(int size) {
                    return new MovieModel[size];
                }
            };
}
