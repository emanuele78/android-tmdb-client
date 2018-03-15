package com.example.android.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Emanuele Mazzante on 25/02/2018.
 */
public class Reviews implements Parcelable {

    @SerializedName("total_results")
    private int reviewCount;
    @SerializedName("results")
    private ArrayList<Review> reviews;

    @Override
    public int describeContents() {
        return 0;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.reviewCount);
        dest.writeList(this.reviews);
    }

    public Reviews() {
    }

    protected Reviews(Parcel in) {
        this.reviewCount = in.readInt();
        this.reviews = new ArrayList<Review>();
        in.readList(this.reviews, Review.class.getClassLoader());
    }

    public static final Parcelable.Creator<Reviews> CREATOR = new Parcelable.Creator<Reviews>() {
        @Override
        public Reviews createFromParcel(Parcel source) {
            return new Reviews(source);
        }

        @Override
        public Reviews[] newArray(int size) {
            return new Reviews[size];
        }
    };
}
