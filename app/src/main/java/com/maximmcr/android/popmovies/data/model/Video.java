package com.maximmcr.android.popmovies.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Frei on 13.09.2017.
 */

public class Video implements Parcelable {

    @SerializedName("key")
    private String mPath;
    @SerializedName("name")
    private String mName;
    @SerializedName("type")
    private String mType;

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

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;

        if (!(getClass() == obj.getClass())) return false;
        else {
            Video video = (Video) obj;
            if (
                    !mName.equals(video.mName) ||
                    !mPath.equals(video.mPath) ||
                    !mType.equals(video.mType)) {
                return false;
            } else {
                return true;
            }
        }
    }

    public static final class Response {
        @SerializedName("results")
        public ArrayList<Video> videos = new ArrayList<>();
    }
}
