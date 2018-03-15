package com.example.android.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Emanuele on 02/03/2018.
 */
public class Author implements Parcelable {

    @SerializedName("id")
    private int authorId;
    @SerializedName("name")
    private String authorName;
    @SerializedName("profile_path")
    private String authorPic;

    public int getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorPic() {
        return authorPic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.authorId);
        dest.writeString(this.authorName);
        dest.writeString(this.authorPic);
    }

    public Author(String name) {
        this.authorName = name;
    }

    public Author(String name, int id, String profilePic) {
        this.authorName = name;
        this.authorPic = profilePic;
        this.authorId = id;
    }

    protected Author(Parcel in) {
        this.authorId = in.readInt();
        this.authorName = in.readString();
        this.authorPic = in.readString();
    }

    public static final Parcelable.Creator<Author> CREATOR = new Parcelable.Creator<Author>() {
        @Override
        public Author createFromParcel(Parcel source) {
            return new Author(source);
        }

        @Override
        public Author[] newArray(int size) {
            return new Author[size];
        }
    };
}
