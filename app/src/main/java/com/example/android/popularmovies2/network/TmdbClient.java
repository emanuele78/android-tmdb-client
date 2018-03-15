package com.example.android.popularmovies2.network;

import android.support.annotation.StringDef;

import com.example.android.popularmovies2.model.Movie;
import com.example.android.popularmovies2.model.Person;
import com.example.android.popularmovies2.model.TvShow;
import com.example.android.popularmovies2.model.VideoList;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Emanuele on 19/02/2018.
 */
public interface TmdbClient {

    String TMDB_HOME_URL = "https://www.themoviedb.org";
    String TMDB_DATE_PATTERN = "yyyy-MM-dd";
    String MOVIE_MODE = "movie";
    String TV_MODE = "tv";
    String PEOPLE_MODE = "person";
    String TOP_RATED_SORTING = "top_rated";
    String POPULAR_SORTING = "popular";
    String BASE_URL = "https://api.themoviedb.org/3/";
    String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    String IMAGE_QUALITY = "w342";
    String API_KEY = ""; /* <== PLEASE PROVIDE YOUR API KEY HERE */
    String TMDB_MOVIE_APPENDED_INFO = "credits,videos,reviews,images";
    String TMDB_TVSHOW_APPENDED_INFO = "credits,images";
    String TMDB_PERSON_APPENDED_INFO = "combined_credits";
    String TMDB_DISCOVER_BASE_URL = "https://api.themoviedb.org/3/discover/";
    int STARTING_PAGE_INDEX = 1;

    @StringDef({MOVIE_MODE, TV_MODE, PEOPLE_MODE})
    @Retention(RetentionPolicy.SOURCE)
    @interface WorkingMode {

    }

    @StringDef({TOP_RATED_SORTING, POPULAR_SORTING})
    @Retention(RetentionPolicy.SOURCE)
    @interface SortMode {

    }
    @GET("{mode}/{sort}")
    Call<VideoList> getVideoList(@WorkingMode
                                 @Path("mode") String mode,
                                 @SortMode
                                 @Path("sort") String sort,
                                 @Query("api_key") String apiKey,
                                 @Query("language") String lang,
                                 @Query("page") Integer page
    );
    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetail(@Path("movie_id") int movieId,
                               @Query("api_key") String apiKey,
                               @Query("language") String lang,
                               @Query("include_image_language") String imageLang,
                               @Query("append_to_response") String appendInfo
    );
    @GET("tv/{tv_id}")
    Call<TvShow> getTvShowDetail(@Path("tv_id") int tvShowId,
                                 @Query("api_key") String apiKey,
                                 @Query("language") String lang,
                                 @Query("include_image_language") String imageLang,
                                 @Query("append_to_response") String appendInfo
    );
    @GET("person/{person_id}")
    Call<Person> getPersonDetail(@Path("person_id") int tvShowId,
                                 @Query("api_key") String apiKey,
                                 @Query("language") String lang,
                                 @Query("append_to_response") String appendInfo
    );
    @GET("{mode}")
    Call<VideoList> getSimilarRating(@Path("mode") String mode,
                                     @Query("api_key") String apiKey,
                                     @Query("language") String lang,
                                     @Query("page") Integer page,
                                     @Query("vote_average.gte") Float rating
    );
    @GET("{mode}")
    Call<VideoList> getSimilarRuntime(@Path("mode") String mode,
                                      @Query("api_key") String apiKey,
                                      @Query("language") String lang,
                                      @Query("page") Integer page,
                                      @Query("with_runtime.gte") Integer runtimeG,
                                      @Query("with_runtime.lte") Integer runtimeL
    );
    @GET("{mode}")
    Call<VideoList> getSimilarGenre(@Path("mode") String mode,
                                    @Query("api_key") String apiKey,
                                    @Query("language") String lang,
                                    @Query("page") Integer page,
                                    @Query("with_genres") Integer genre
    );
    @GET("movie")
    Call<VideoList> getSimilarYearMovie(@Query("api_key") String apiKey,
                                        @Query("language") String lang,
                                        @Query("page") Integer page,
                                        @Query("year") Integer year
    );
    @GET("tv")
    Call<VideoList> getSimilarYearTv(@Query("api_key") String apiKey,
                                     @Query("language") String lang,
                                     @Query("page") Integer page,
                                     @Query("first_air_date_year") Integer year
    );
    @GET("movie")
    Call<VideoList> getSimilarCompany(@Query("api_key") String apiKey,
                                      @Query("language") String lang,
                                      @Query("page") Integer page,
                                      @Query("with_companies") Integer company
    );
    @GET("{mode}/{item_id}/similar")
    Call<VideoList> getSimilar(@Path("mode") String mode,
                               @Path("item_id") int itemId,
                               @Query("api_key") String apiKey,
                               @Query("language") String lang,
                               @Query("page") Integer page
    );
    @GET("search/{mode}")
    Call<VideoList> search(@Path("mode") String mode,
                           @Query("api_key") String apiKey,
                           @Query("query") String query,
                           @Query("language") String lang,
                           @Query("page") Integer page
    );
    @GET("{mode}")
    Call<VideoList> getMediaWithLinkedPeople(@Path("mode") String mode,
                                             @Query("api_key") String apiKey,
                                             @Query("with_people") String people,
                                             @Query("language") String lang,
                                             @Query("page") Integer page
    );
}
