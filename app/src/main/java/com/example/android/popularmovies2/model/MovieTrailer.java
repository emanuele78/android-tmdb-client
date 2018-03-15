package com.example.android.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Emanuele Mazzante on 25/02/2018.
 */
public class MovieTrailer implements Parcelable {

    @SerializedName("key")
    private String trailerKey;
    @SerializedName("name")
    private String trailerName;
    private String site;

    public String getTrailerKey() {
        return trailerKey;
    }

    public String getTrailerName() {
        return trailerName;
    }

    public String getSite() {
        return site;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.trailerKey);
        dest.writeString(this.trailerName);
        dest.writeString(this.site);
    }

    public MovieTrailer() {
    }

    protected MovieTrailer(Parcel in) {
        this.trailerKey = in.readString();
        this.trailerName = in.readString();
        this.site = in.readString();
    }

    public static final Parcelable.Creator<MovieTrailer> CREATOR =
            new Parcelable.Creator<MovieTrailer>() {
                @Override
                public MovieTrailer createFromParcel(Parcel source) {
                    return new MovieTrailer(source);
                }

                @Override
                public MovieTrailer[] newArray(int size) {
                    return new MovieTrailer[size];
                }
            };
}
