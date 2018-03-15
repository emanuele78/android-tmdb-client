package com.example.android.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Emanuele Mazzante on 25/02/2018.
 */
public class Crew implements Parcelable {

    @SerializedName("id")
    private int crewId;
    private String department;
    private int gender;
    private String job;
    @SerializedName("name")
    private String crewName;
    @SerializedName("profile_path")
    private String profilePic;

    public String getJob() {
        return job;
    }

    public Crew(String job, String crewName, String profilePic, int crewId) {
        this.crewName = crewName;
        this.crewId = crewId;
        this.profilePic = profilePic;
        this.job = job;
    }

    public String getCrewName() {
        return crewName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public int getCrewId() {
        return crewId;
    }

    public void setCrewId(int crewId) {
        this.crewId = crewId;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public void setCrewName(String crewName) {
        this.crewName = crewName;
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
        dest.writeInt(this.crewId);
        dest.writeString(this.department);
        dest.writeInt(this.gender);
        dest.writeString(this.job);
        dest.writeString(this.crewName);
        dest.writeString(this.profilePic);
    }

    public Crew(String name) {
        this.crewName = name;
    }

    protected Crew(Parcel in) {
        this.crewId = in.readInt();
        this.department = in.readString();
        this.gender = in.readInt();
        this.job = in.readString();
        this.crewName = in.readString();
        this.profilePic = in.readString();
    }

    public static final Parcelable.Creator<Crew> CREATOR = new Parcelable.Creator<Crew>() {
        @Override
        public Crew createFromParcel(Parcel source) {
            return new Crew(source);
        }

        @Override
        public Crew[] newArray(int size) {
            return new Crew[size];
        }
    };
}
