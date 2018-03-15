package com.example.android.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Emanuele on 02/03/2018.
 */
public class Person implements Parcelable {

    public static final String PERSON_KEY = "person_key";
    public static final String PERSON_BUNDLE_KEY = "person_bundle_key";
    private String name;
    private String birthday;
    private String deathday;
    @SerializedName("id")
    private int personId;
    @SerializedName("biography")
    private String bio;
    @SerializedName("place_of_birth")
    private String placeOfBirth;
    @SerializedName("combined_credits")
    private CombinedCredits credits;
    @SerializedName("profile_path")
    private String profilePic;

    public String getName() {
        return name;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getDeathday() {
        return deathday;
    }

    public int getPersonId() {
        return personId;
    }

    public String getBio() {
        return bio;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public CombinedCredits getCredits() {
        return credits;
    }

    public String getProfilePic() {
        return profilePic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.birthday);
        dest.writeString(this.deathday);
        dest.writeInt(this.personId);
        dest.writeString(this.bio);
        dest.writeString(this.placeOfBirth);
        dest.writeParcelable(this.credits, flags);
        dest.writeString(this.profilePic);
    }

    public Person() {
    }

    protected Person(Parcel in) {
        this.name = in.readString();
        this.birthday = in.readString();
        this.deathday = in.readString();
        this.personId = in.readInt();
        this.bio = in.readString();
        this.placeOfBirth = in.readString();
        this.credits = in.readParcelable(CombinedCredits.class.getClassLoader());
        this.profilePic = in.readString();
    }

    public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel source) {
            return new Person(source);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };
}
