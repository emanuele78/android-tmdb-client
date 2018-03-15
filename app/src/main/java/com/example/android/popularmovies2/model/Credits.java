package com.example.android.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Emanuele Mazzante on 25/02/2018.
 */
public class Credits implements Parcelable {

    @SerializedName("cast")
    private ArrayList<Cast> casts;
    @SerializedName("crew")
    private ArrayList<Crew> crews;

    public ArrayList<Cast> getCasts() {
        return casts;
    }

    public ArrayList<Crew> getCrews() {
        return crews;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setCasts(ArrayList<Cast> casts) {
        this.casts = casts;
    }

    public void setCrews(ArrayList<Crew> crews) {
        this.crews = crews;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.casts);
        dest.writeList(this.crews);
    }

    public Credits() {
    }

    protected Credits(Parcel in) {
        this.casts = new ArrayList<Cast>();
        in.readList(this.casts, Cast.class.getClassLoader());
        this.crews = new ArrayList<Crew>();
        in.readList(this.crews, Crew.class.getClassLoader());
    }

    public static final Parcelable.Creator<Credits> CREATOR = new Parcelable.Creator<Credits>() {
        @Override
        public Credits createFromParcel(Parcel source) {
            return new Credits(source);
        }

        @Override
        public Credits[] newArray(int size) {
            return new Credits[size];
        }
    };
}
