package com.example.android.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Emanuele on 02/03/2018.
 */
public class CombinedCreditsCast implements Parcelable {

    @SerializedName("media_type")
    private String mediaType;
    private String title;
    @SerializedName("character")
    private String characterName;
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

    public String getCharacterName() {
        return characterName;
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
        dest.writeString(this.characterName);
        dest.writeInt(this.mediaId);
        dest.writeString(this.posterPath);
    }

    public CombinedCreditsCast(String name) {
        characterName = name;
    }

    protected CombinedCreditsCast(Parcel in) {
        this.mediaType = in.readString();
        this.title = in.readString();
        this.characterName = in.readString();
        this.mediaId = in.readInt();
        this.posterPath = in.readString();
    }

    public static final Parcelable.Creator<CombinedCreditsCast> CREATOR =
            new Parcelable.Creator<CombinedCreditsCast>() {
                @Override
                public CombinedCreditsCast createFromParcel(Parcel source) {
                    return new CombinedCreditsCast(source);
                }

                @Override
                public CombinedCreditsCast[] newArray(int size) {
                    return new CombinedCreditsCast[size];
                }
            };
}
