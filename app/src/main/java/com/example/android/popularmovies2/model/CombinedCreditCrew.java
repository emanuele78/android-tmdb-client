package com.example.android.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Emanuele on 02/03/2018.
 */
public class CombinedCreditCrew implements Parcelable {

    @SerializedName("media_type")
    private String mediaType;
    private String title;
    private String job;
    @SerializedName("id")
    private int mediaId;
    @SerializedName("poster_path")
    private String posterPath;

    public String getMediaType() {
        return mediaType;
    }

    public String getTitle() {
        return title;
    }

    public String getJob() {
        return job;
    }

    public int getMediaId() {
        return mediaId;
    }

    public String getPosterPath() {
        return posterPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mediaType);
        dest.writeString(this.title);
        dest.writeString(this.job);
        dest.writeInt(this.mediaId);
        dest.writeString(this.posterPath);
    }

    public CombinedCreditCrew(String name) {
        job = name;
    }

    protected CombinedCreditCrew(Parcel in) {
        this.mediaType = in.readString();
        this.title = in.readString();
        this.job = in.readString();
        this.mediaId = in.readInt();
        this.posterPath = in.readString();
    }

    public static final Parcelable.Creator<CombinedCreditCrew> CREATOR = new Parcelable.Creator<CombinedCreditCrew>() {
        @Override
        public CombinedCreditCrew createFromParcel(Parcel source) {
            return new CombinedCreditCrew(source);
        }

        @Override
        public CombinedCreditCrew[] newArray(int size) {
            return new CombinedCreditCrew[size];
        }
    };
}
