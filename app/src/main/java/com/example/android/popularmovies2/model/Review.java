package com.example.android.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Emanuele Mazzante on 25/02/2018.
 */
public class Review implements Parcelable {

    private String author;
    private String content;

    @Override
    public int describeContents() {
        return 0;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.author);
        dest.writeString(this.content);
    }

    public Review() {
    }

    protected Review(Parcel in) {
        this.author = in.readString();
        this.content = in.readString();
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
