package com.example.android.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Emanuele Mazzante on 25/02/2018.
 */
public class Images implements Parcelable {

    private ArrayList<Backdrop> backdrops;

    public ArrayList<Backdrop> getBackdrops() {
        return backdrops;
    }

    public void setBackdrops(ArrayList<Backdrop> backdrops) {
        this.backdrops = backdrops;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.backdrops);
    }

    public Images(ArrayList<Backdrop> backdrops) {
        this.backdrops = backdrops;
    }

    protected Images(Parcel in) {
        this.backdrops = in.createTypedArrayList(Backdrop.CREATOR);
    }

    public static final Parcelable.Creator<Images> CREATOR = new Parcelable.Creator<Images>() {
        @Override
        public Images createFromParcel(Parcel source) {
            return new Images(source);
        }

        @Override
        public Images[] newArray(int size) {
            return new Images[size];
        }
    };
}
