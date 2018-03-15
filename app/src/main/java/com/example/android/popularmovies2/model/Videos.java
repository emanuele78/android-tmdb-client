package com.example.android.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Emanuele Mazzante on 25/02/2018.
 */
public class Videos implements Parcelable {

    @SerializedName("results")
    private ArrayList<MovieTrailer> trailers;

    public ArrayList<MovieTrailer> getTrailers() {
        return trailers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.trailers);
    }

    public Videos() {
    }

    protected Videos(Parcel in) {
        this.trailers = new ArrayList<MovieTrailer>();
        in.readList(this.trailers, MovieTrailer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Videos> CREATOR = new Parcelable.Creator<Videos>() {
        @Override
        public Videos createFromParcel(Parcel source) {
            return new Videos(source);
        }

        @Override
        public Videos[] newArray(int size) {
            return new Videos[size];
        }
    };
}
