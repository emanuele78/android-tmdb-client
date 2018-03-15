package com.example.android.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Emanuele Mazzante on 25/02/2018.
 */
public class Cast implements Parcelable {

    @SerializedName("cast_id")
    private int castId;
    private String character;
    @SerializedName("id")
    private int personId;
    private int gender;
    private String name;
    @SerializedName("profile_path")
    private String profilePic;

    public String getCharacter() {
        return character;
    }

    public int getPersonId() {
        return personId;
    }

    public String getName() {
        return name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public Cast(String character, String name, int personId, String profilePic) {
        this.character = character;
        this.name = name;
        this.personId = personId;
        this.profilePic = profilePic;
    }

    public void setCastId(int castId) {
        this.castId = castId;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.castId);
        dest.writeString(this.character);
        dest.writeInt(this.personId);
        dest.writeInt(this.gender);
        dest.writeString(this.name);
        dest.writeString(this.profilePic);
    }

    public Cast(String name) {
        this.name = name;
    }

    protected Cast(Parcel in) {
        this.castId = in.readInt();
        this.character = in.readString();
        this.personId = in.readInt();
        this.gender = in.readInt();
        this.name = in.readString();
        this.profilePic = in.readString();
    }

    public static final Parcelable.Creator<Cast> CREATOR = new Parcelable.Creator<Cast>() {
        @Override
        public Cast createFromParcel(Parcel source) {
            return new Cast(source);
        }

        @Override
        public Cast[] newArray(int size) {
            return new Cast[size];
        }
    };
}
