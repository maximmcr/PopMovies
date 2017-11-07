package com.maximmcr.android.popmovies.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frei on 24.01.2017.
 */

public class Movie implements Parcelable {
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

    private ArrayList<Review> mReviews;
    private ArrayList<Video> mYoutubeAddresses;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;

        if (!(getClass() == obj.getClass())) return false;
        else {
            Movie movie = (Movie) obj;

            ArrayList<Video> videos = movie.mYoutubeAddresses;
            ArrayList<Review> reviews = movie.mReviews;
            if (!(videos.size() == mYoutubeAddresses.size()) ||
                    !(reviews.size() == mReviews.size())) {
                return false;
            }
            for (int i = 0; i < mYoutubeAddresses.size(); i++) {
                if (!mYoutubeAddresses.get(i).equals(videos.get(i))) return false;
            }
            for (int i = 0; i < mReviews.size(); i++) {
                if (!mReviews.get(i).equals(reviews.get(i))) return false;
            }

            if (
                    mId != movie.mId ||
                    !mTitle.equals(movie.mTitle) ||
                    !mTagline.equals(movie.mTagline) ||
                    !mPosterPath.equals(movie.mPosterPath) ||
                    !mReleaseDate.equals(movie.mReleaseDate) ||
                    mRuntime != movie.mRuntime ||
                    mRating != movie.mRating ||
                    mPopularity != movie.mPopularity ||
                    !mOverview.equals(movie.mOverview)
                    ) {
                return false;
            } else {
                return true;
            }
        }
    }

    public Movie(int id, String title, String tagline, String poster, String releaseDate,
                 int runtime, double rating, double popularity, String overview,
                 ArrayList<Review> reviews, ArrayList<Video> youtubeAdresses) {
        mId = id;
        mTitle = new String(new StringBuffer(title));
        mTagline = new String(new StringBuffer(tagline));
        mPosterPath = new String(new StringBuffer(poster));
        mReleaseDate = new String(new StringBuffer(releaseDate));
        mRuntime = runtime;
        mRating = rating;
        mPopularity = popularity;
        mOverview = new String(new StringBuffer(overview));

        mReviews = new ArrayList<>(reviews);
        mYoutubeAddresses = new ArrayList<>(youtubeAdresses);
    }
    public Movie(Movie other) {
        this(other.mId, other.mTitle, other.mTagline, other.mPosterPath, other.mReleaseDate,
                other.mRuntime, other.mRating, other.mPopularity, other.mOverview,
                other.mReviews, other.mYoutubeAddresses);
    }
    public Movie(String posterId, int id) {
        this.mPosterPath = posterId;
        this.mId = id;
    }
    public Movie() {
        mReviews = new ArrayList<>();
        mYoutubeAddresses = new ArrayList<>();
    }
    private Movie(Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mTagline = in.readString();
        mPosterPath = in.readString();
        mReleaseDate = in.readString();
        mRuntime = in.readInt();
        mRating = in.readDouble();
        mPopularity = in.readDouble();
        mOverview = in.readString();

        in.readTypedList(mReviews, Review.CREATOR);
        in.readTypedList(mYoutubeAddresses, Video.CREATOR);
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

    public ArrayList<Review> getReviews() {
        return mReviews;
    }
    public void setReviews(ArrayList<Review> reviews) {
        mReviews = reviews;
    }

    public ArrayList<Video> getVideos() {
        return mYoutubeAddresses;
    }
    public void setVideos(ArrayList<Video> videos) {
        mYoutubeAddresses = videos;
    }

    public void fetchBaseInfo (Movie movie) {
        mId = movie.mId;
        mTitle = movie.mTitle;
        mTagline = movie.mTagline;
        mPosterPath = movie.mPosterPath;
        mReleaseDate = movie.mReleaseDate;
        mRuntime = movie.mRuntime;
        mRating = movie.mRating;
        mPopularity = movie.mPopularity;
        mOverview = movie.mOverview;
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

        dest.writeTypedList(mReviews);
        dest.writeTypedList(mYoutubeAddresses);
    }

    public static final Parcelable.Creator<Movie> CREATOR =
            new Creator<Movie>() {

                @Override
                public Movie createFromParcel(Parcel source) {
                    return new Movie(source);
                }

                @Override
                public Movie[] newArray(int size) {
                    return new Movie[size];
                }
            };

    public static final class Response {
        @SerializedName("results")
        public List<Movie> movies = new ArrayList<>();
    }
}
