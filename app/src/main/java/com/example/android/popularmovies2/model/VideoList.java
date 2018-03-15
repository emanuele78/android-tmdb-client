package com.example.android.popularmovies2.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Emanuele on 21/02/2018.
 */
public class VideoList {

    @SerializedName("page")
    private Integer currentPage;
    @SerializedName("total_results")
    private Integer totalMovies;
    @SerializedName("total_pages")
    private Integer totalPages;
    @SerializedName("results")
    private ArrayList<Video> videos;

    public ArrayList<Video> getVideos() {
        return videos;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public Integer getTotalMovies() {
        return totalMovies;
    }

    public Integer getTotalPages() {
        return totalPages;
    }
}
