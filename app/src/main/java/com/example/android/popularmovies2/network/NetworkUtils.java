package com.example.android.popularmovies2.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.ImageView;

import com.example.android.popularmovies2.model.Movie;
import com.example.android.popularmovies2.model.Person;
import com.example.android.popularmovies2.model.TvShow;
import com.example.android.popularmovies2.model.VideoList;
import com.example.android.popularmovies2.ux.UxUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Emanuele on 22/02/2018.
 */
public class NetworkUtils {

    public static final String YOUTUBE_IMAGE_BASE_URL = "https://img.youtube.com/vi/";
    public static final String YOUTUBE_IMAGE_SELECTED = "0.jpg";
    public static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch";
    public static final String YOUTUBE_QUERY_PARAMETER = "v";
    public static final String YOUTUBE_MIME_TYPE = "text/plain";

    public static boolean isDeviceConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void doNetworkCallForEntryList(final int page,
                                                 @TmdbClient.WorkingMode final String mode,
                                                 @TmdbClient.SortMode final String sort,
                                                 String preferredLang,
                                                 Callback<VideoList> callback) {
        //setting retrofit
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(TmdbClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        //setting client
        TmdbClient client = retrofit.create(TmdbClient.class);
        Call<VideoList> call = client.getVideoList(mode, sort, TmdbClient.API_KEY,
                preferredLang, page);
        //async call
        call.enqueue(callback);
    }

    public static void doNetworkCallForMovieDetail(final int movieId,
                                                   String preferredLang,
                                                   String imageLang,
                                                   Callback<Movie> callback) {
        //setting retrofit
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(TmdbClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        //setting client
        TmdbClient client = retrofit.create(TmdbClient.class);
        Call<Movie> call = client.getMovieDetail(movieId, TmdbClient.API_KEY, preferredLang,
                imageLang, TmdbClient.TMDB_MOVIE_APPENDED_INFO);
        //async call
        call.enqueue(callback);
    }

    public static void doNetworkCallForTvShowDetail(final int tvShowId,
                                                    String preferredLang,
                                                    String imageLang,
                                                    Callback<TvShow> callback) {
        //setting retrofit
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(TmdbClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        //setting client
        TmdbClient client = retrofit.create(TmdbClient.class);
        Call<TvShow> call = client.getTvShowDetail(tvShowId, TmdbClient.API_KEY,
                preferredLang, imageLang, TmdbClient.TMDB_TVSHOW_APPENDED_INFO);
        //async call
        call.enqueue(callback);
    }

    public static void doNetworkCallForPersonDetail(final int personId,
                                                    String preferredLang,
                                                    Callback<Person> callback) {
        //setting retrofit
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(TmdbClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        //setting client
        TmdbClient client = retrofit.create(TmdbClient.class);
        Call<Person> call = client.getPersonDetail(personId, TmdbClient.API_KEY,
                preferredLang, TmdbClient.TMDB_PERSON_APPENDED_INFO);
        //async call
        call.enqueue(callback);
    }

    public static void downloadImage(ImageView targetView, String relativeImagePath,
                                     int placeholderImage, int errorImage) {
        if (relativeImagePath != null) {
            Uri builtUri = Uri.parse(TmdbClient.BASE_IMAGE_URL).buildUpon()
                    .appendPath(TmdbClient.IMAGE_QUALITY)
                    .appendPath(relativeImagePath.replace("/", "")).build();
            Picasso picasso = Picasso.with(targetView.getContext());
            picasso.load(builtUri).fit().centerCrop()
                    .placeholder(placeholderImage)
                    .error(errorImage)
                    .into(targetView);
        } else {
            Picasso picasso = Picasso.with(targetView.getContext());
            picasso.load(placeholderImage).error(errorImage).fit().centerCrop().into(targetView);
        }
    }

    public static void loadImage(ImageView targetView, String relativeImagePath,
                                 int placeholderImage, int errorImage) {
        if (relativeImagePath != null) {
            Uri builtUri = Uri.parse(TmdbClient.BASE_IMAGE_URL).buildUpon()
                    .appendPath(TmdbClient.IMAGE_QUALITY)
                    .appendPath(relativeImagePath.replace("/", "")).build();
            Picasso picasso = Picasso.with(targetView.getContext());
            picasso.load(builtUri).fit().centerCrop()
                    .placeholder(placeholderImage)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .error(errorImage)
                    .into(targetView);
        } else {
            Picasso picasso = Picasso.with(targetView.getContext());
            picasso.load(placeholderImage).error(errorImage).fit().centerCrop().into(targetView);
        }
    }

    public static void downloadYoutubeImage(ImageView targetView, String trailerKey,
                                            int placeholderImage, int errorImage) {
        Uri builtUri = Uri.parse(YOUTUBE_IMAGE_BASE_URL).buildUpon()
                .appendPath(trailerKey).appendPath(YOUTUBE_IMAGE_SELECTED).build();
        Picasso picasso = Picasso.with(targetView.getContext());
        picasso.load(builtUri).fit().centerCrop()
                .placeholder(placeholderImage)
                .error(errorImage)
                .into(targetView);
    }

    public static void doNetworkCallForSearch(final int page,
                                              @TmdbClient.WorkingMode final String mode,
                                              @UxUtils.Tag String tag,
                                              int runtime,
                                              int genre,
                                              float rating,
                                              int releaseYear,
                                              int company,
                                              String preferredLang,
                                              Callback<VideoList> callback) {
        //setting retrofit
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(TmdbClient.TMDB_DISCOVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        //setting client
        TmdbClient client = retrofit.create(TmdbClient.class);
        switch (tag) {
            case UxUtils.RATING_TAG:
                Call<VideoList> ratingCall = client.getSimilarRating(mode, TmdbClient.API_KEY,
                        preferredLang, page, rating);
                //async call
                ratingCall.enqueue(callback);
                break;
            case UxUtils.RUNTIME_TAG:
                int runtimeGte = runtime - 1;
                int runtimeLte = runtime + 1;
                Call<VideoList> runtimeCall = client.getSimilarRuntime(mode, TmdbClient.API_KEY,
                        preferredLang, page, runtimeGte, runtimeLte);
                //async call
                runtimeCall.enqueue(callback);
                break;
            case UxUtils.GENRE_TAG:
                Call<VideoList> genreCall = client.getSimilarGenre(mode, TmdbClient.API_KEY,
                        preferredLang, page, genre);
                //async call
                genreCall.enqueue(callback);
                break;
            case UxUtils.YEAR_TAG:
                Call<VideoList> yearCall;
                if (mode.equals(TmdbClient.MOVIE_MODE)) {
                    yearCall = client.getSimilarYearMovie(TmdbClient.API_KEY, preferredLang,
                            page, releaseYear);
                } else {
                    yearCall = client.getSimilarYearTv(TmdbClient.API_KEY, preferredLang,
                            page, releaseYear);
                }
                //async call
                yearCall.enqueue(callback);
                break;
            case UxUtils.COMPANY_TAG:
                Call<VideoList> companyCall = client.getSimilarCompany(TmdbClient.API_KEY,
                        preferredLang, page, company);
                //async call
                companyCall.enqueue(callback);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static void doNetworkCallForlinkedPeople(final int page,
                                                    @TmdbClient.WorkingMode final String mode,
                                                    String preferredLang,
                                                    String people,
                                                    Callback<VideoList> callback) {
        //setting retrofit
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(TmdbClient.TMDB_DISCOVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        //setting client
        TmdbClient client = retrofit.create(TmdbClient.class);
        if (mode.equals(TmdbClient.MOVIE_MODE) || mode.equals(TmdbClient.TV_MODE)) {
            Call<VideoList> movieCall = client.getMediaWithLinkedPeople(mode, TmdbClient.API_KEY,
                    people, preferredLang, page);
            //async call
            movieCall.enqueue(callback);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static void doNetworkCallForSimilar(final int itemId,
                                               @TmdbClient.WorkingMode final String mode,
                                               String preferredLang,
                                               int page,
                                               Callback<VideoList> callback) {
        //setting retrofit
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(TmdbClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        //setting client
        TmdbClient client = retrofit.create(TmdbClient.class);
        Call<VideoList> similar;
        if (mode.equals(TmdbClient.MOVIE_MODE)) {
            similar = client.getSimilar(TmdbClient.MOVIE_MODE, itemId, TmdbClient.API_KEY,
                    preferredLang, page);
        } else {
            similar = client.getSimilar(TmdbClient.TV_MODE, itemId, TmdbClient.API_KEY,
                    preferredLang, page);
        }
        //async call
        similar.enqueue(callback);
    }

    public static void doNetworkCallForManualSearch(@TmdbClient.WorkingMode final String mode,
                                                    String preferredLang,
                                                    String query,
                                                    int page,
                                                    Callback<VideoList> callback) {
        //setting retrofit
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(TmdbClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        //setting client
        TmdbClient client = retrofit.create(TmdbClient.class);
        Call<VideoList> call = client.search(mode, TmdbClient.API_KEY, query, preferredLang, page);
        //async call
        call.enqueue(callback);
    }
}