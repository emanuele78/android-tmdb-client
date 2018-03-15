package com.example.android.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Emanuele on 02/03/2018.
 */
public class TvShow implements Parcelable {

    public static final String TV_SHOW_KEY = "tv_show_key";
    public static final String TV_SHOW_BUNDLE_KEY = "tv_show_bundle_key";
    public static final String TVSHOW_PREFERRED_KEY = "tvshow_preferred_key";
    @SerializedName("backdrop_path")
    private String mainBackdrop;
    @SerializedName("genres")
    private ArrayList<Genre> genreList;
    @SerializedName("id")
    private int tvShowId;
    @SerializedName("original_name")
    private String originalTitle;
    @SerializedName("overview")
    private String plot;
    @SerializedName("popularity")
    private float popularityIndex;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("production_companies")
    private ArrayList<ProductionCompany> companies;
    @SerializedName("origin_country")
    private ArrayList<String> countries;
    @SerializedName("first_air_date")
    private String firstAirDate;
    @SerializedName("last_air_date")
    private String lastAirDate;
    @SerializedName("number_of_episodes")
    private int episodeCount;
    @SerializedName("number_of_seasons")
    private int seasonCount;
    @SerializedName("episode_run_time")
    private ArrayList<Integer> runtime;
    private String status;
    @SerializedName("name")
    private String title;
    @SerializedName("vote_average")
    private float voteAverage;
    @SerializedName("vote_count")
    private int voteCount;
    private Credits credits;
    @SerializedName("images")
    private Images backdropImages;
    @SerializedName("created_by")
    private ArrayList<Author> authors;

    public void setGenreList(ArrayList<Genre> genreList) {
        this.genreList = genreList;
    }

    public void setTvShowId(int tvShowId) {
        this.tvShowId = tvShowId;
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

    public void setCountries(ArrayList<String> countries) {
        this.countries = countries;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    public void setLastAirDate(String lastAirDate) {
        this.lastAirDate = lastAirDate;
    }

    public void setEpisodeCount(int episodeCount) {
        this.episodeCount = episodeCount;
    }

    public void setSeasonCount(int seasonCount) {
        this.seasonCount = seasonCount;
    }

    public void setRuntime(ArrayList<Integer> runtime) {
        this.runtime = runtime;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public ArrayList<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<Author> authors) {
        this.authors = authors;
    }

    public String getMainBackdrop() {
        return mainBackdrop;
    }

    public ArrayList<Genre> getGenreList() {
        return genreList;
    }

    public int getTvShowId() {
        return tvShowId;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPlot() {
        return plot;
    }

    public float getPopularityIndex() {
        return popularityIndex;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public ArrayList<ProductionCompany> getCompanies() {
        return companies;
    }

    public ArrayList<String> getCountries() {
        return countries;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public String getLastAirDate() {
        return lastAirDate;
    }

    public int getEpisodeCount() {
        return episodeCount;
    }

    public int getSeasonCount() {
        return seasonCount;
    }

    public ArrayList<Integer> getRuntime() {
        return runtime;
    }

    public String getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public Credits getCredits() {
        return credits;
    }

    public Images getBackdropImages() {
        return backdropImages;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mainBackdrop);
        dest.writeTypedList(this.genreList);
        dest.writeInt(this.tvShowId);
        dest.writeString(this.originalTitle);
        dest.writeString(this.plot);
        dest.writeFloat(this.popularityIndex);
        dest.writeString(this.posterPath);
        dest.writeTypedList(this.companies);
        dest.writeStringList(this.countries);
        dest.writeString(this.firstAirDate);
        dest.writeString(this.lastAirDate);
        dest.writeInt(this.episodeCount);
        dest.writeInt(this.seasonCount);
        dest.writeList(this.runtime);
        dest.writeString(this.status);
        dest.writeString(this.title);
        dest.writeFloat(this.voteAverage);
        dest.writeInt(this.voteCount);
        dest.writeParcelable(this.credits, flags);
        dest.writeParcelable(this.backdropImages, flags);
        dest.writeTypedList(this.authors);
    }

    public TvShow() {
    }

    protected TvShow(Parcel in) {
        this.mainBackdrop = in.readString();
        this.genreList = in.createTypedArrayList(Genre.CREATOR);
        this.tvShowId = in.readInt();
        this.originalTitle = in.readString();
        this.plot = in.readString();
        this.popularityIndex = in.readFloat();
        this.posterPath = in.readString();
        this.companies = in.createTypedArrayList(ProductionCompany.CREATOR);
        this.countries = in.createStringArrayList();
        this.firstAirDate = in.readString();
        this.lastAirDate = in.readString();
        this.episodeCount = in.readInt();
        this.seasonCount = in.readInt();
        this.runtime = new ArrayList<Integer>();
        in.readList(this.runtime, Integer.class.getClassLoader());
        this.status = in.readString();
        this.title = in.readString();
        this.voteAverage = in.readFloat();
        this.voteCount = in.readInt();
        this.credits = in.readParcelable(Credits.class.getClassLoader());
        this.backdropImages = in.readParcelable(Images.class.getClassLoader());
        this.authors = in.createTypedArrayList(Author.CREATOR);
    }

    public static final Parcelable.Creator<TvShow> CREATOR = new Parcelable.Creator<TvShow>() {
        @Override
        public TvShow createFromParcel(Parcel source) {
            return new TvShow(source);
        }

        @Override
        public TvShow[] newArray(int size) {
            return new TvShow[size];
        }
    };
}
