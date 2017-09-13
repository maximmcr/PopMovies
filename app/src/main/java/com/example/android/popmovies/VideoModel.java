package com.example.android.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Frei on 13.09.2017.
 */

public class VideoModel implements Parcelable {

    String mPath;
    String mName;
    String mType;

    public VideoModel(String path, String name, String type) {
        mName = name;
        mPath = path;
        mType = type;
    }

    protected VideoModel(Parcel in) {
        mPath = in.readString();
        mName = in.readString();
        mType = in.readString();
    }

    public static final Creator<VideoModel> CREATOR = new Creator<VideoModel>() {
        @Override
        public VideoModel createFromParcel(Parcel in) {
            return new VideoModel(in);
        }

        @Override
        public VideoModel[] newArray(int size) {
            return new VideoModel[size];
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
