package com.example.android.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Emanuele on 02/03/2018.
 */
public class CombinedCredits implements Parcelable {

    @SerializedName("cast")
    private ArrayList<CombinedCreditsCast> creditCasts;
    @SerializedName("crew")
    private ArrayList<CombinedCreditCrew> creditCrews;

    public ArrayList<CombinedCreditsCast> getCreditCasts() {
        return creditCasts;
    }

    public ArrayList<CombinedCreditCrew> getCreditCrews() {
        return creditCrews;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.creditCasts);
        dest.writeTypedList(this.creditCrews);
    }

    public CombinedCredits() {
    }

    protected CombinedCredits(Parcel in) {
        this.creditCasts = in.createTypedArrayList(CombinedCreditsCast.CREATOR);
        this.creditCrews = in.createTypedArrayList(CombinedCreditCrew.CREATOR);
    }

    public static final Parcelable.Creator<CombinedCredits> CREATOR =
            new Parcelable.Creator<CombinedCredits>() {
                @Override
                public CombinedCredits createFromParcel(Parcel source) {
                    return new CombinedCredits(source);
                }

                @Override
                public CombinedCredits[] newArray(int size) {
                    return new CombinedCredits[size];
                }
            };
}
