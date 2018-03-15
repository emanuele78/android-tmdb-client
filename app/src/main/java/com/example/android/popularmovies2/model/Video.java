package com.example.android.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Emanuele on 21/02/2018.
 */
public class Video implements Parcelable {

    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("vote_average")
    private float averageVote;
    @SerializedName("profile_path")
    private String profilePath;
    @SerializedName("id")
    private int videoId;
    @SerializedName("first_air_date")
    private String firstAired;
    private float popularity;
    private String title;
    private String name;

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public float getAverageVote() {
        return averageVote;
    }

    public int getVideoId() {
        return videoId;
    }

    public String getFirstAired() {
        return firstAired;
    }

    public float getPopularity() {
        return popularity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.releaseDate);
        dest.writeString(this.posterPath);
        dest.writeFloat(this.averageVote);
        dest.writeInt(this.videoId);
        dest.writeString(this.firstAired);
        dest.writeFloat(this.popularity);
        dest.writeString(this.title);
        dest.writeString(this.name);
        dest.writeString(this.profilePath);
    }

    public Video(String name) {
        this.title = name;
        this.name = name;
        this.videoId = -1;
    }

    protected Video(Parcel in) {
        this.releaseDate = in.readString();
        this.posterPath = in.readString();
        this.averageVote = in.readFloat();
        this.videoId = in.readInt();
        this.firstAired = in.readString();
        this.popularity = in.readFloat();
        this.title = in.readString();
        this.name = in.readString();
        this.profilePath = in.readString();
    }

    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel source) {
            return new Video(source);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
