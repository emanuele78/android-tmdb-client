package com.example.android.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Emanuele Mazzante on 25/02/2018.
 */
public class Movie implements Parcelable {

    public static final String MOVIE_KEY = "movie_key";
    public static final String MOVIE_BUNDLE_KEY = "movie_bundle_key";
    public static final String MOVIE_PREFERRED_KEY = "movie_preferred_key";
    @SerializedName("backdrop_path")
    private String mainBackdrop;
    private int budget;
    @SerializedName("genres")
    private ArrayList<Genre> genreList;
    @SerializedName("id")
    private int movieId;
    @SerializedName("imdb_id")
    private String imdbId;
    @SerializedName("original_title")
    private String originalTitle;
    @SerializedName("overview")
    private String plot;
    @SerializedName("popularity")
    private float popularityIndex;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("production_companies")
    private ArrayList<ProductionCompany> companies;
    @SerializedName("production_countries")
    private ArrayList<Country> countries;
    @SerializedName("release_date")
    private String releaseDate;
    private int revenue;
    private int runtime;
    private String status;
    private String tagline;
    private String title;
    @SerializedName("vote_average")
    private float voteAverage;
    @SerializedName("vote_count")
    private int voteCount;
    private Credits credits;
    private Videos videos;
    private Reviews reviews;
    @SerializedName("images")
    private Images backdropImages;

    public Reviews getReviews() {
        return reviews;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public void setGenreList(ArrayList<Genre> genreList) {
        this.genreList = genreList;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public void setPopularityIndex(float popularityIndex) {
        this.popularityIndex = popularityIndex;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setCompanies(ArrayList<ProductionCompany> companies) {
        this.companies = companies;
    }

    public void setCountries(ArrayList<Country> countries) {
        this.countries = countries;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public void setCredits(Credits credits) {
        this.credits = credits;
    }

    public void setBackdropImages(Images backdropImages) {
        this.backdropImages = backdropImages;
    }

    public Videos getVideos() {
        return videos;
    }

    public Credits getCredits() {
        return credits;
    }

    public String getPlot() {
        return plot;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public ArrayList<Genre> getGenreList() {
        return genreList;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<ProductionCompany> getCompanies() {
        return companies;
    }

    public int getBudget() {
        return budget;
    }

    public ArrayList<Country> getCountries() {
        return countries;
    }

    public int getRevenue() {
        return revenue;
    }

    public int getMovieId() {
        return movieId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Images getBackdropImages() {
        return backdropImages;
    }

    public float getPopularityIndex() {
        return popularityIndex;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public int getRuntime() {
        return runtime;
    }

    public int getVoteCount() {
        return voteCount;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mainBackdrop);
        dest.writeInt(this.budget);
        dest.writeTypedList(this.genreList);
        dest.writeInt(this.movieId);
        dest.writeString(this.imdbId);
        dest.writeString(this.originalTitle);
        dest.writeString(this.plot);
        dest.writeFloat(this.popularityIndex);
        dest.writeString(this.posterPath);
        dest.writeTypedList(this.companies);
        dest.writeTypedList(this.countries);
        dest.writeString(this.releaseDate);
        dest.writeInt(this.revenue);
        dest.writeInt(this.runtime);
        dest.writeString(this.status);
        dest.writeString(this.tagline);
        dest.writeString(this.title);
        dest.writeFloat(this.voteAverage);
        dest.writeInt(this.voteCount);
        dest.writeParcelable(this.credits, flags);
        dest.writeParcelable(this.videos, flags);
        dest.writeParcelable(this.reviews, flags);
        dest.writeParcelable(this.backdropImages, flags);
    }

    public Movie(String title) {
        this.title = title;
    }

    public Movie() {
    }

    protected Movie(Parcel in) {
        this.mainBackdrop = in.readString();
        this.budget = in.readInt();
        this.genreList = in.createTypedArrayList(Genre.CREATOR);
        this.movieId = in.readInt();
        this.imdbId = in.readString();
        this.originalTitle = in.readString();
        this.plot = in.readString();
        this.popularityIndex = in.readFloat();
        this.posterPath = in.readString();
        this.companies = in.createTypedArrayList(ProductionCompany.CREATOR);
        this.countries = in.createTypedArrayList(Country.CREATOR);
        this.releaseDate = in.readString();
        this.revenue = in.readInt();
        this.runtime = in.readInt();
        this.status = in.readString();
        this.tagline = in.readString();
        this.title = in.readString();
        this.voteAverage = in.readFloat();
        this.voteCount = in.readInt();
        this.credits = in.readParcelable(Credits.class.getClassLoader());
        this.videos = in.readParcelable(Videos.class.getClassLoader());
        this.reviews = in.readParcelable(Reviews.class.getClassLoader());
        this.backdropImages = in.readParcelable(Images.class.getClassLoader());
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
