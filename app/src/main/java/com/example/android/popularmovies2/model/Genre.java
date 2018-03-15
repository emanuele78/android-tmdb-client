package com.example.android.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Emanuele Mazzante on 25/02/2018.
 */
public class Genre implements Parcelable {

    @SerializedName("name")
    private String genreName;
    @SerializedName("id")
    private int genreId;

    public String getGenreName() {
        return genreName;
    }

    public int getGenreId() {
        return genreId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.genreName);
        dest.writeInt(this.genreId);
    }

    public Genre(String name) {
        genreName = name;
    }

    public Genre(String name, int id) {
        this.genreName = name;
        this.genreId = id;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    protected Genre(Parcel in) {
        this.genreName = in.readString();
        this.genreId = in.readInt();
    }

    public static final Parcelable.Creator<Genre> CREATOR = new Parcelable.Creator<Genre>() {
        @Override
        public Genre createFromParcel(Parcel source) {
            return new Genre(source);
        }

        @Override
        public Genre[] newArray(int size) {
            return new Genre[size];
        }
    };
}
