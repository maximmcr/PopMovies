package com.example.android.popmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frei on 24.01.2017.
 */

public class MovieModel implements Parcelable {
    @SerializedName("id")
    private int mId;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("tagline")
    private String mTagline;

    @SerializedName("poster_path")
    private String mPosterPath;

    @SerializedName("release_date")
    private String mReleaseDate;

    @SerializedName("runtime")
    private int mRuntime;

    @SerializedName("vote_average")
    private double mRating;

    @SerializedName("popularity")
    private double mPopularity;

    @SerializedName("overview")
    private String mOverview;

    private ArrayList<CommentModel> mComments;
    private ArrayList<VideoModel> mYoutubeAddresses;

    public MovieModel(int id, String title, String tagline, String poster, String releaseDate,
                      int runtime, double rating, double popularity, String overview,
                      ArrayList<CommentModel> comments, ArrayList<VideoModel> youtubeAdresses) {
        mId = id;
        mTitle = title;
        mTagline = tagline;
        mPosterPath = poster;
        mReleaseDate = releaseDate;
        mRuntime = runtime;
        mRating = rating;
        mPopularity = popularity;
        mOverview = overview;

        mComments = comments;
        mYoutubeAddresses = youtubeAdresses;
    }
    public MovieModel(String posterId, int id) {
        this.mPosterPath = posterId;
        this.mId = id;
    }
    private MovieModel(Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mTagline = in.readString();
        mPosterPath = in.readString();
        mReleaseDate = in.readString();
        mRuntime = in.readInt();
        mRating = in.readDouble();
        mPopularity = in.readDouble();
        mOverview = in.readString();

        in.readTypedList(mComments, CommentModel.CREATOR);
        in.readTypedList(mYoutubeAddresses, VideoModel.CREATOR);
    }

    public Integer getId() {
        return mId;
    }
    public void setId(Integer id) {
        this.mId = id;
    }

    public String getOverview() {
        return mOverview;
    }
    public void setOverview(String overview) {
        this.mOverview = overview;
    }

    public Double getPopularity() {
        return mPopularity;
    }
    public void setPopularity(Double popularity) {
        this.mPopularity = popularity;
    }

    public String getPosterPath() {
        return mPosterPath;
    }
    public void setPosterPath(String posterPath) {
        this.mPosterPath = posterPath;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }
    public void setReleaseDate(String releaseDate) {
        this.mReleaseDate = releaseDate;
    }

    public Integer getRuntime() {
        return mRuntime;
    }
    public void setRuntime(Integer runtime) {
        this.mRuntime = runtime;
    }

    public String getTagline() {
        return mTagline;
    }
    public void setTagline(String tagline) {
        this.mTagline = tagline;
    }

    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String title) {
        this.mTitle = title;
    }

    public Double getRating() {
        return mRating;
    }
    public void setRating(Double rating) {
        this.mRating = rating;
    }

    public ArrayList<CommentModel> getComments() {
        return mComments;
    }
    public void setComments(ArrayList<CommentModel> comments) {
        mComments = comments;
    }

    public ArrayList<VideoModel> getVideos() {
        return mYoutubeAddresses;
    }
    public void setVideos(ArrayList<VideoModel> videos) {
        mYoutubeAddresses = videos;
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
        dest.writeString(mPosterPath);
        dest.writeString(mReleaseDate);
        dest.writeInt(mRuntime);
        dest.writeDouble(mRating);
        dest.writeDouble(mPopularity);
        dest.writeString(mOverview);

        dest.writeTypedList(mComments);
        dest.writeTypedList(mYoutubeAddresses);
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

    public static final class Response {
        @SerializedName("results")
        public List<MovieModel> movies = new ArrayList<>();
    }
}
