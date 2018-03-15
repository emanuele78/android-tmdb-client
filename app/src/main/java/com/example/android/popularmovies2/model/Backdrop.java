package com.example.android.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Emanuele Mazzante on 25/02/2018.
 */
public class Backdrop implements Parcelable {

    @SerializedName("file_path")
    private String imagePath;

    public String getImagePath() {
        return imagePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imagePath);
    }

    public Backdrop(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    protected Backdrop(Parcel in) {
        this.imagePath = in.readString();
    }

    public static final Parcelable.Creator<Backdrop> CREATOR = new Parcelable.Creator<Backdrop>() {
        @Override
        public Backdrop createFromParcel(Parcel source) {
            return new Backdrop(source);
        }

        @Override
        public Backdrop[] newArray(int size) {
            return new Backdrop[size];
        }
    };
}
