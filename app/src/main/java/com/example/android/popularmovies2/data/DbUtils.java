package com.example.android.popularmovies2.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.android.popularmovies2.data.PopularMoviesContract.BackdropEntry;
import com.example.android.popularmovies2.data.PopularMoviesContract.CompanyEntry;
import com.example.android.popularmovies2.data.PopularMoviesContract.GenreEntry;
import com.example.android.popularmovies2.data.PopularMoviesContract.ItemCompanyEntry;
import com.example.android.popularmovies2.data.PopularMoviesContract.ItemEntry;
import com.example.android.popularmovies2.data.PopularMoviesContract.ItemGenreEntry;
import com.example.android.popularmovies2.data.PopularMoviesContract.ItemPersonEntry;
import com.example.android.popularmovies2.data.PopularMoviesContract.PersonEntry;
import com.example.android.popularmovies2.model.Author;
import com.example.android.popularmovies2.model.Backdrop;
import com.example.android.popularmovies2.model.Cast;
import com.example.android.popularmovies2.model.Country;
import com.example.android.popularmovies2.model.Credits;
import com.example.android.popularmovies2.model.Crew;
import com.example.android.popularmovies2.model.Genre;
import com.example.android.popularmovies2.model.Images;
import com.example.android.popularmovies2.model.Movie;
import com.example.android.popularmovies2.model.ProductionCompany;
import com.example.android.popularmovies2.model.TvShow;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Emanuele on 12/03/2018.
 */
public class DbUtils {

    private static final int CREDITS_MAX_SIZE = 10;
    private static final int BACKDROPS_MAX_SIZE = 3;

    public static ContentValues[] getContentValuesForCompaniesRelationships(Movie movie) {
        ArrayList<ProductionCompany> companies = movie.getCompanies();
        if (companies != null) {
            ArrayList<ContentValues> valuesArrayList = new ArrayList<>();
            for (ProductionCompany company : companies) {
                ContentValues values = new ContentValues();
                values.put(ItemCompanyEntry.COLUMN_IC_COMPANY_ID, company.getProductionCompanyId());
                values.put(ItemCompanyEntry.COLUMN_IC_ITEM_ID, movie.getMovieId());
                valuesArrayList.add(values);
            }
            return valuesArrayList.toArray(new ContentValues[valuesArrayList.size()]);
        } else {
            return null;
        }
    }

    public static ContentValues[] getContentValuesForCompaniesRelationships(TvShow tvShow) {
        ArrayList<ProductionCompany> companies = tvShow.getCompanies();
        if (companies != null) {
            ArrayList<ContentValues> valuesArrayList = new ArrayList<>();
            for (ProductionCompany company : companies) {
                ContentValues values = new ContentValues();
                values.put(ItemCompanyEntry.COLUMN_IC_COMPANY_ID, company.getProductionCompanyId());
                values.put(ItemCompanyEntry.COLUMN_IC_ITEM_ID, tvShow.getTvShowId());
                valuesArrayList.add(values);
            }
            return valuesArrayList.toArray(new ContentValues[valuesArrayList.size()]);
        } else {
            return null;
        }
    }

    public static ContentValues[] getContentValuesForCompanies(Movie movie) {
        ArrayList<ProductionCompany> companies = movie.getCompanies();
        if (companies != null) {
            ArrayList<ContentValues> valuesArrayList = new ArrayList<>();
            for (ProductionCompany company : companies) {
                ContentValues values = new ContentValues();
                values.put(CompanyEntry.COLUMN_COMPANY_ID, company.getProductionCompanyId());
                values.put(CompanyEntry.COLUMN_NAME, company.getProductionCompanyName());
                valuesArrayList.add(values);
            }
            return valuesArrayList.toArray(new ContentValues[valuesArrayList.size()]);
        } else {
            return null;
        }
    }

    public static ContentValues[] getContentValuesForCompaniesTvShow(TvShow tvShow) {
        ArrayList<ProductionCompany> companies = tvShow.getCompanies();
        if (companies != null) {
            ArrayList<ContentValues> valuesArrayList = new ArrayList<>();
            for (ProductionCompany company : companies) {
                ContentValues values = new ContentValues();
                values.put(CompanyEntry.COLUMN_COMPANY_ID, company.getProductionCompanyId());
                values.put(CompanyEntry.COLUMN_NAME, company.getProductionCompanyName());
                valuesArrayList.add(values);
            }
            return valuesArrayList.toArray(new ContentValues[valuesArrayList.size()]);
        } else {
            return null;
        }
    }

    public static ContentValues[] getContentValuesForGenres(Movie movie) {
        ArrayList<Genre> genres = movie.getGenreList();
        if (genres != null) {
            ArrayList<ContentValues> valuesArrayList = new ArrayList<>();
            for (Genre genre : genres) {
                ContentValues values = new ContentValues();
                values.put(GenreEntry.COLUMN_GENRE_ID, genre.getGenreId());
                values.put(GenreEntry.COLUMN_GENRE_NAME, genre.getGenreName());
                valuesArrayList.add(values);
            }
            return valuesArrayList.toArray(new ContentValues[valuesArrayList.size()]);
        } else {
            return null;
        }
    }

    public static ContentValues[] getContentValuesForGenres(TvShow tvShow) {
        ArrayList<Genre> genres = tvShow.getGenreList();
        if (genres != null) {
            ArrayList<ContentValues> valuesArrayList = new ArrayList<>();
            for (Genre genre : genres) {
                ContentValues values = new ContentValues();
                values.put(GenreEntry.COLUMN_GENRE_ID, genre.getGenreId());
                values.put(GenreEntry.COLUMN_GENRE_NAME, genre.getGenreName());
                valuesArrayList.add(values);
            }
            return valuesArrayList.toArray(new ContentValues[valuesArrayList.size()]);
        } else {
            return null;
        }
    }

    public static ContentValues[] getContentValuesForGenresRelationships(Movie movie) {
        ArrayList<Genre> genres = movie.getGenreList();
        if (genres != null) {
            ArrayList<ContentValues> valuesArrayList = new ArrayList<>();
            for (Genre genre : genres) {
                ContentValues values = new ContentValues();
                values.put(ItemGenreEntry.COLUMN_IG_GENRE_ID, genre.getGenreId());
                values.put(ItemGenreEntry.COLUMN_IG_ITEM_ID, movie.getMovieId());
                valuesArrayList.add(values);
            }
            return valuesArrayList.toArray(new ContentValues[valuesArrayList.size()]);
        } else {
            return null;
        }
    }

    public static ContentValues[] getContentValuesForGenresRelationships(TvShow tvShow) {
        ArrayList<Genre> genres = tvShow.getGenreList();
        if (genres != null) {
            ArrayList<ContentValues> valuesArrayList = new ArrayList<>();
            for (Genre genre : genres) {
                ContentValues values = new ContentValues();
                values.put(ItemGenreEntry.COLUMN_IG_GENRE_ID, genre.getGenreId());
                values.put(ItemGenreEntry.COLUMN_IG_ITEM_ID, tvShow.getTvShowId());
                valuesArrayList.add(values);
            }
            return valuesArrayList.toArray(new ContentValues[valuesArrayList.size()]);
        } else {
            return null;
        }
    }

    private static ArrayList<Backdrop> getBackdropList(Movie movie) {
        ArrayList<Backdrop> backdrops;
        if (movie.getBackdropImages().getBackdrops() != null && movie.getBackdropImages().getBackdrops().size() > BACKDROPS_MAX_SIZE) {
            backdrops = new ArrayList<>();
            for (int i = 0; i < BACKDROPS_MAX_SIZE; i++) {
                backdrops.add(movie.getBackdropImages().getBackdrops().get(i));
            }
        } else {
            backdrops = movie.getBackdropImages().getBackdrops();
        }
        return backdrops;
    }

    private static ArrayList<Backdrop> getBackdropList(TvShow tvShow) {
        ArrayList<Backdrop> backdrops;
        if (tvShow.getBackdropImages().getBackdrops() != null && tvShow.getBackdropImages().getBackdrops().size() > BACKDROPS_MAX_SIZE) {
            backdrops = new ArrayList<>();
            for (int i = 0; i < BACKDROPS_MAX_SIZE; i++) {
                backdrops.add(tvShow.getBackdropImages().getBackdrops().get(i));
            }
        } else {
            backdrops = tvShow.getBackdropImages().getBackdrops();
        }
        return backdrops;
    }

    public static ContentValues[] getContentValuesForBackdrops(Movie movie) {
        ArrayList<Backdrop> backdrops = getBackdropList(movie);
        if (backdrops != null) {
            ArrayList<ContentValues> valuesArrayList = new ArrayList<>();
            for (Backdrop backdrop : backdrops) {
                ContentValues values = new ContentValues();
                values.put(BackdropEntry.COLUMN_BACKDROP_PATH, backdrop.getImagePath());
                values.put(BackdropEntry.COLUMN_B_ITEM_ID, movie.getMovieId());
                valuesArrayList.add(values);
            }
            return valuesArrayList.toArray(new ContentValues[valuesArrayList.size()]);
        } else {
            return null;
        }
    }

    public static ContentValues[] getContentValuesForBackdrops(TvShow tvShow) {
        ArrayList<Backdrop> backdrops = getBackdropList(tvShow);
        if (backdrops != null) {
            ArrayList<ContentValues> valuesArrayList = new ArrayList<>();
            for (Backdrop backdrop : backdrops) {
                ContentValues values = new ContentValues();
                values.put(BackdropEntry.COLUMN_BACKDROP_PATH, backdrop.getImagePath());
                values.put(BackdropEntry.COLUMN_B_ITEM_ID, tvShow.getTvShowId());
                valuesArrayList.add(values);
            }
            return valuesArrayList.toArray(new ContentValues[valuesArrayList.size()]);
        } else {
            return null;
        }
    }

    public static ContentValues getContentValuesForItem(Movie movie) {
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_TYPE, ItemEntry.MOVIE_MEDIA_TYPE);
        values.put(ItemEntry.COLUMN_ITEM_ID, movie.getMovieId());
        values.put(ItemEntry.COLUMN_TITLE, movie.getTitle());
        if (movie.getOriginalTitle() != null && !movie.getOriginalTitle().isEmpty()) {
            values.put(ItemEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
        }
        values.put(ItemEntry.COLUMN_RATING, movie.getVoteAverage());
        values.put(ItemEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());
        values.put(ItemEntry.COLUMN_POPULARITY, movie.getPopularityIndex());
        if (movie.getReleaseDate() != null && !movie.getReleaseDate().isEmpty()) {
            values.put(ItemEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        }
        if (movie.getPlot() != null && !movie.getPlot().isEmpty()) {
            values.put(ItemEntry.COLUMN_PLOT, movie.getPlot());
        }
        values.put(ItemEntry.COLUMN_BUDGET, movie.getBudget());
        values.put(ItemEntry.COLUMN_REVENUE, movie.getRevenue());
        if (movie.getCountries() != null && !movie.getCountries().isEmpty()) {
            ArrayList<Country> countries = movie.getCountries();
            StringBuilder builder = new StringBuilder();
            String separator = ", ";
            for (int i = 0; i < countries.size(); i++) {
                builder.append(countries.get(i).getCountryCode());
                if (i < countries.size() - 1) {
                    builder.append(separator);
                }
            }
            values.put(ItemEntry.COLUMN_COUNTRY, builder.toString());
        }
        values.put(ItemEntry.COLUMN_RUNTIME, String.valueOf(movie.getRuntime()));
        values.put(ItemEntry.COLUMN_POSTER, movie.getPosterPath());
        return values;
    }

    public static ContentValues getContentValuesForItem(TvShow tvShow) {
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_TYPE, ItemEntry.TV_MEDIA_TYPE);
        values.put(ItemEntry.COLUMN_ITEM_ID, tvShow.getTvShowId());
        values.put(ItemEntry.COLUMN_TITLE, tvShow.getTitle());
        if (tvShow.getOriginalTitle() != null && !tvShow.getOriginalTitle().isEmpty()) {
            values.put(ItemEntry.COLUMN_ORIGINAL_TITLE, tvShow.getOriginalTitle());
        }
        values.put(ItemEntry.COLUMN_RATING, tvShow.getVoteAverage());
        values.put(ItemEntry.COLUMN_VOTE_COUNT, tvShow.getVoteCount());
        values.put(ItemEntry.COLUMN_POPULARITY, tvShow.getPopularityIndex());
        if (tvShow.getFirstAirDate() != null && !tvShow.getFirstAirDate().isEmpty()) {
            values.put(ItemEntry.COLUMN_RELEASE_DATE, tvShow.getFirstAirDate());
        }
        if (tvShow.getLastAirDate() != null && !tvShow.getLastAirDate().isEmpty()) {
            values.put(ItemEntry.COLUMN_LAST_DATE, tvShow.getLastAirDate());
        }
        if (tvShow.getPlot() != null && !tvShow.getPlot().isEmpty()) {
            values.put(ItemEntry.COLUMN_PLOT, tvShow.getPlot());
        }
        if (tvShow.getCountries() != null && !tvShow.getCountries().isEmpty()) {
            ArrayList<String> countries = tvShow.getCountries();
            StringBuilder builder = new StringBuilder();
            String separator = ", ";
            for (int i = 0; i < countries.size(); i++) {
                builder.append(countries.get(i));
                if (i < countries.size() - 1) {
                    builder.append(separator);
                }
            }
            values.put(ItemEntry.COLUMN_COUNTRY, builder.toString());
        }
        values.put(ItemEntry.COLUMN_EPISODE_COUNT, tvShow.getEpisodeCount());
        values.put(ItemEntry.COLUMN_SEASON_COUNT, tvShow.getSeasonCount());
        if (tvShow.getStatus() != null && !tvShow.getStatus().isEmpty()) {
            values.put(ItemEntry.COLUMN_STATUS, tvShow.getStatus());
        }
        if (tvShow.getRuntime() != null && !tvShow.getRuntime().isEmpty()) {
            ArrayList<Integer> runtimes = tvShow.getRuntime();
            StringBuilder builder = new StringBuilder();
            String separator = ", ";
            for (int i = 0; i < runtimes.size(); i++) {
                builder.append(runtimes.get(i));
                if (i < runtimes.size() - 1) {
                    builder.append(separator);
                }
            }
            values.put(ItemEntry.COLUMN_RUNTIME, builder.toString());
        }
        values.put(ItemEntry.COLUMN_POSTER, tvShow.getPosterPath());
        return values;
    }

    private static ArrayList<Cast> getCastList(Movie movie) {
        ArrayList<Cast> casts;
        if (movie.getCredits().getCasts() != null && movie.getCredits().getCasts().size() > CREDITS_MAX_SIZE) {
            casts = new ArrayList<>();
            for (int i = 0; i < CREDITS_MAX_SIZE; i++) {
                //saving only first 10 elements
                casts.add(movie.getCredits().getCasts().get(i));
            }
        } else {
            casts = movie.getCredits().getCasts();
        }
        return casts;
    }

    private static ArrayList<Crew> getCrewList(Movie movie) {
        ArrayList<Crew> crews;
        if (movie.getCredits().getCasts() != null && movie.getCredits().getCrews().size() > CREDITS_MAX_SIZE) {
            crews = new ArrayList<>();
            for (int i = 0; i < CREDITS_MAX_SIZE; i++) {
                //saving only first 10 elements
                crews.add(movie.getCredits().getCrews().get(i));
            }
        } else {
            crews = movie.getCredits().getCrews();
        }
        return crews;
    }

    private static ArrayList<Cast> getCastList(TvShow tvShow) {
        ArrayList<Cast> casts;
        if (tvShow.getCredits().getCasts() != null && tvShow.getCredits().getCasts().size() > CREDITS_MAX_SIZE) {
            casts = new ArrayList<>();
            for (int i = 0; i < CREDITS_MAX_SIZE; i++) {
                //saving only first 10 elements
                casts.add(tvShow.getCredits().getCasts().get(i));
            }
        } else {
            casts = tvShow.getCredits().getCasts();
        }
        return casts;
    }

    private static ArrayList<Author> getAuthorList(TvShow tvShow) {
        ArrayList<Author> authors;
        if (tvShow.getAuthors() != null && tvShow.getAuthors().size() > CREDITS_MAX_SIZE) {
            authors = new ArrayList<>();
            for (int i = 0; i < CREDITS_MAX_SIZE; i++) {
                //saving only first 10 elements
                authors.add(tvShow.getAuthors().get(i));
            }
        } else {
            authors = tvShow.getAuthors();
        }
        return authors;
    }

    public static ContentValues[] getContentValuesForPeople(Movie movie) {
        ArrayList<Cast> casts = getCastList(movie);
        ArrayList<Crew> crews = getCrewList(movie);
        ArrayList<ContentValues> valuesArrayList = new ArrayList<>();
        if (casts != null) {
            for (Cast cast : casts) {
                ContentValues values = new ContentValues();
                values.put(PersonEntry.COLUMN_PERSON_ID, cast.getPersonId());
                values.put(PersonEntry.COLUMN_NAME, cast.getName());
                values.put(PersonEntry.COLUMN_PROFILE_PIC, cast.getProfilePic());
                valuesArrayList.add(values);
            }
        }
        if (crews != null) {
            for (Crew crew : crews) {
                ContentValues values = new ContentValues();
                values.put(PersonEntry.COLUMN_PERSON_ID, crew.getCrewId());
                values.put(PersonEntry.COLUMN_NAME, crew.getCrewName());
                values.put(PersonEntry.COLUMN_PROFILE_PIC, crew.getProfilePic());
                valuesArrayList.add(values);
            }
        }
        if (valuesArrayList.isEmpty()) {
            return null;
        } else {
            return valuesArrayList.toArray(new ContentValues[valuesArrayList.size()]);
        }
    }

    public static ContentValues[] getContentValuesForPeople(TvShow tvShow) {
        ArrayList<Cast> casts = getCastList(tvShow);
        ArrayList<Author> authors = getAuthorList(tvShow);
        ArrayList<ContentValues> valuesArrayList = new ArrayList<>();
        if (casts != null) {
            for (Cast cast : casts) {
                ContentValues values = new ContentValues();
                values.put(PersonEntry.COLUMN_PERSON_ID, cast.getPersonId());
                values.put(PersonEntry.COLUMN_NAME, cast.getName());
                values.put(PersonEntry.COLUMN_PROFILE_PIC, cast.getProfilePic());
                valuesArrayList.add(values);
            }
        }
        if (authors != null) {
            for (Author author : authors) {
                ContentValues values = new ContentValues();
                values.put(PersonEntry.COLUMN_PERSON_ID, author.getAuthorId());
                values.put(PersonEntry.COLUMN_NAME, author.getAuthorName());
                values.put(PersonEntry.COLUMN_PROFILE_PIC, author.getAuthorPic());
                valuesArrayList.add(values);
            }
        }
        if (valuesArrayList.isEmpty()) {
            return null;
        } else {
            return valuesArrayList.toArray(new ContentValues[valuesArrayList.size()]);
        }
    }

    public static ContentValues[] getContentValuesForPeopleRelationships(Movie movie) {
        ArrayList<Cast> casts = getCastList(movie);
        ArrayList<Crew> crews = getCrewList(movie);
        ArrayList<ContentValues> valuesArrayList = new ArrayList<>();
        if (casts != null) {
            for (Cast cast : casts) {
                ContentValues values = new ContentValues();
                values.put(ItemPersonEntry.COLUMN_IP_ITEM_ID, movie.getMovieId());
                values.put(ItemPersonEntry.COLUMN_IP_PERSON_ID, cast.getPersonId());
                values.put(ItemPersonEntry.COLUMN_ROLE_NAME, cast.getCharacter());
                values.put(ItemPersonEntry.COLUMN_ROLE_TYPE, ItemPersonEntry.ROLE_TYPE_CAST);
                valuesArrayList.add(values);
            }
        }
        if (crews != null) {
            for (Crew crew : crews) {
                ContentValues values = new ContentValues();
                values.put(ItemPersonEntry.COLUMN_IP_ITEM_ID, movie.getMovieId());
                values.put(ItemPersonEntry.COLUMN_IP_PERSON_ID, crew.getCrewId());
                values.put(ItemPersonEntry.COLUMN_ROLE_NAME, crew.getJob());
                values.put(ItemPersonEntry.COLUMN_ROLE_TYPE, ItemPersonEntry.ROLE_TYPE_CREW);
                valuesArrayList.add(values);
            }
        }
        if (valuesArrayList.isEmpty()) {
            return null;
        } else {
            return valuesArrayList.toArray(new ContentValues[valuesArrayList.size()]);
        }
    }

    public static ContentValues[] getContentValuesForPeopleRelationships(TvShow tvShow) {
        ArrayList<Cast> casts = getCastList(tvShow);
        ArrayList<Author> authors = getAuthorList(tvShow);
        ArrayList<ContentValues> valuesArrayList = new ArrayList<>();
        if (casts != null) {
            for (Cast cast : casts) {
                ContentValues values = new ContentValues();
                values.put(ItemPersonEntry.COLUMN_IP_ITEM_ID, tvShow.getTvShowId());
                values.put(ItemPersonEntry.COLUMN_IP_PERSON_ID, cast.getPersonId());
                values.put(ItemPersonEntry.COLUMN_ROLE_NAME, cast.getCharacter());
                values.put(ItemPersonEntry.COLUMN_ROLE_TYPE, ItemPersonEntry.ROLE_TYPE_CAST);
                valuesArrayList.add(values);
            }
        }
        if (authors != null) {
            for (Author author : authors) {
                ContentValues values = new ContentValues();
                values.put(ItemPersonEntry.COLUMN_IP_ITEM_ID, tvShow.getTvShowId());
                values.put(ItemPersonEntry.COLUMN_IP_PERSON_ID, author.getAuthorId());
                values.put(ItemPersonEntry.COLUMN_ROLE_TYPE, ItemPersonEntry.ROLE_TYPE_CREW);
                valuesArrayList.add(values);
            }
        }
        if (valuesArrayList.isEmpty()) {
            return null;
        } else {
            return valuesArrayList.toArray(new ContentValues[valuesArrayList.size()]);
        }
    }

    public static Movie loadMovieInfo(long recordId, ContentResolver contentResolver) {
        Movie movie = new Movie();
        String[] projection;
        projection = new String[]{
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_ID,
                ItemEntry.COLUMN_ITEM_TYPE,
                ItemEntry.COLUMN_TITLE,
                ItemEntry.COLUMN_POSTER,
                ItemEntry.COLUMN_ORIGINAL_TITLE,
                ItemEntry.COLUMN_RATING,
                ItemEntry.COLUMN_VOTE_COUNT,
                ItemEntry.COLUMN_POPULARITY,
                ItemEntry.COLUMN_RELEASE_DATE,
                ItemEntry.COLUMN_PLOT,
                ItemEntry.COLUMN_BUDGET,
                ItemEntry.COLUMN_REVENUE,
                ItemEntry.COLUMN_COUNTRY,
                ItemEntry.COLUMN_RUNTIME
        };
        String stringId = String.valueOf(recordId);
        Uri uri = PopularMoviesContract.ItemEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToNext()) {
            int titleIndex = cursor.getColumnIndex(ItemEntry.COLUMN_TITLE);
            int itemIdIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_ID);
            int originalTitleIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ORIGINAL_TITLE);
            int ratingIndex = cursor.getColumnIndex(ItemEntry.COLUMN_RATING);
            int voteCountIndex = cursor.getColumnIndex(ItemEntry.COLUMN_VOTE_COUNT);
            int popularityIndex = cursor.getColumnIndex(ItemEntry.COLUMN_POPULARITY);
            int releaseDateIndex = cursor.getColumnIndex(ItemEntry.COLUMN_RELEASE_DATE);
            int plotIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PLOT);
            int budgetIndex = cursor.getColumnIndex(ItemEntry.COLUMN_BUDGET);
            int revenueIndex = cursor.getColumnIndex(ItemEntry.COLUMN_REVENUE);
            int countryIndex = cursor.getColumnIndex(ItemEntry.COLUMN_COUNTRY);
            int posterIndex = cursor.getColumnIndex(ItemEntry.COLUMN_POSTER);
            int runtimeIndex = cursor.getColumnIndex(ItemEntry.COLUMN_RUNTIME);
            String title = cursor.getString(titleIndex);
            int itemId = cursor.getInt(itemIdIndex);
            String originalTitle = cursor.getString(originalTitleIndex);
            float rating = cursor.getFloat(ratingIndex);
            int voteCount = cursor.getInt(voteCountIndex);
            float popularity = cursor.getFloat(popularityIndex);
            String releaseDate = cursor.getString(releaseDateIndex);
            String plot = cursor.getString(plotIndex);
            int budget = cursor.getInt(budgetIndex);
            int revenue = cursor.getInt(revenueIndex);
            String country = cursor.getString(countryIndex);
            String poster = cursor.getString(posterIndex);
            String runtime = cursor.getString(runtimeIndex);
            movie.setTitle(title);
            movie.setMovieId(itemId);
            movie.setOriginalTitle(originalTitle);
            movie.setVoteAverage(rating);
            movie.setVoteCount(voteCount);
            movie.setPopularityIndex(popularity);
            movie.setPlot(plot);
            movie.setReleaseDate(releaseDate);
            movie.setBudget(budget);
            movie.setRevenue(revenue);
            movie.setPosterPath(poster);
            movie.setRuntime(Integer.valueOf(runtime));
            String[] countries = country.split(",");
            ArrayList<Country> countryList = new ArrayList<>();
            for (String s : countries) {
                countryList.add(new Country(s));
            }
            movie.setCountries(countryList);
        } else {
            movie = null;
        }
        if (cursor != null) {
            cursor.close();
        }
        return movie;
    }

    public static TvShow loadTvShowInfo(long recordId, ContentResolver contentResolver) {
        TvShow tvShow = new TvShow();
        String[] projection;
        projection = new String[]{
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_ID,
                ItemEntry.COLUMN_ITEM_TYPE,
                ItemEntry.COLUMN_TITLE,
                ItemEntry.COLUMN_POSTER,
                ItemEntry.COLUMN_ORIGINAL_TITLE,
                ItemEntry.COLUMN_RATING,
                ItemEntry.COLUMN_VOTE_COUNT,
                ItemEntry.COLUMN_POPULARITY,
                ItemEntry.COLUMN_RELEASE_DATE,
                ItemEntry.COLUMN_LAST_DATE,
                ItemEntry.COLUMN_PLOT,
                ItemEntry.COLUMN_EPISODE_COUNT,
                ItemEntry.COLUMN_SEASON_COUNT,
                ItemEntry.COLUMN_STATUS,
                ItemEntry.COLUMN_COUNTRY,
                ItemEntry.COLUMN_RUNTIME
        };
        String stringId = String.valueOf(recordId);
        Uri uri = ItemEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToNext()) {
            int titleIndex = cursor.getColumnIndex(ItemEntry.COLUMN_TITLE);
            int itemIdIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_ID);
            int originalTitleIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ORIGINAL_TITLE);
            int ratingIndex = cursor.getColumnIndex(ItemEntry.COLUMN_RATING);
            int voteCountIndex = cursor.getColumnIndex(ItemEntry.COLUMN_VOTE_COUNT);
            int popularityIndex = cursor.getColumnIndex(ItemEntry.COLUMN_POPULARITY);
            int releaseDateIndex = cursor.getColumnIndex(ItemEntry.COLUMN_RELEASE_DATE);
            int lastDateIndex = cursor.getColumnIndex(ItemEntry.COLUMN_LAST_DATE);
            int plotIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PLOT);
            int seasonCountIndex = cursor.getColumnIndex(ItemEntry.COLUMN_SEASON_COUNT);
            int episodeCountIndex = cursor.getColumnIndex(ItemEntry.COLUMN_EPISODE_COUNT);
            int countryIndex = cursor.getColumnIndex(ItemEntry.COLUMN_COUNTRY);
            int posterIndex = cursor.getColumnIndex(ItemEntry.COLUMN_POSTER);
            int runtimeIndex = cursor.getColumnIndex(ItemEntry.COLUMN_RUNTIME);
            int statusIndex = cursor.getColumnIndex(ItemEntry.COLUMN_STATUS);
            String title = cursor.getString(titleIndex);
            int itemId = cursor.getInt(itemIdIndex);
            String originalTitle = cursor.getString(originalTitleIndex);
            float rating = cursor.getFloat(ratingIndex);
            int voteCount = cursor.getInt(voteCountIndex);
            float popularity = cursor.getFloat(popularityIndex);
            String releaseDate = cursor.getString(releaseDateIndex);
            String lastDate = cursor.getString(lastDateIndex);
            String plot = cursor.getString(plotIndex);
            int episodeCount = cursor.getInt(episodeCountIndex);
            int seasonCount = cursor.getInt(seasonCountIndex);
            String country = cursor.getString(countryIndex);
            String poster = cursor.getString(posterIndex);
            String status = cursor.getString(statusIndex);
            String runtimes = cursor.getString(runtimeIndex);
            tvShow.setTitle(title);
            tvShow.setTvShowId(itemId);
            tvShow.setOriginalTitle(originalTitle);
            tvShow.setVoteAverage(rating);
            tvShow.setVoteCount(voteCount);
            tvShow.setPopularityIndex(popularity);
            tvShow.setPlot(plot);
            tvShow.setFirstAirDate(releaseDate);
            tvShow.setLastAirDate(lastDate);
            tvShow.setEpisodeCount(episodeCount);
            tvShow.setSeasonCount(seasonCount);
            tvShow.setPosterPath(poster);
            tvShow.setStatus(status);
            String[] runtimeArray = runtimes.split(",");
            ArrayList<Integer> runtimeList = new ArrayList<>();
            for (String s : runtimeArray) {
                runtimeList.add(Integer.valueOf(s));
            }
            tvShow.setRuntime(runtimeList);
            String[] countries = country.split(",");
            tvShow.setCountries(new ArrayList<>(Arrays.asList(countries)));
        } else {
            tvShow = null;
        }
        if (cursor != null) {
            cursor.close();
        }
        return tvShow;
    }

    public static void loadBackdrops(Movie movie, ContentResolver contentResolver) {
        Uri uri = PopularMoviesContract.JOIN_BACKDROPS_CONTENT_URI;
        String[] projection = new String[]{BackdropEntry.COLUMN_BACKDROP_PATH};
        String selection = ItemEntry.COLUMN_ITEM_ID + "=?";
        String[] selectionArgs = {String.valueOf(movie.getMovieId())};
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);
        ArrayList<Backdrop> backdrops = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            int backdropIndex = cursor.getColumnIndex(BackdropEntry.COLUMN_BACKDROP_PATH);
            String backdrop = cursor.getString(backdropIndex);
            backdrops.add(new Backdrop(backdrop));
        }
        movie.setBackdropImages(new Images(backdrops));
        if (cursor != null) {
            cursor.close();
        }
    }

    public static void loadBackdrops(TvShow tvShow, ContentResolver contentResolver) {
        Uri uri = PopularMoviesContract.JOIN_BACKDROPS_CONTENT_URI;
        String[] projection = new String[]{BackdropEntry.COLUMN_BACKDROP_PATH};
        String selection = ItemEntry.COLUMN_ITEM_ID + "=?";
        String[] selectionArgs = {String.valueOf(tvShow.getTvShowId())};
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);
        ArrayList<Backdrop> backdrops = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            int backdropIndex = cursor.getColumnIndex(BackdropEntry.COLUMN_BACKDROP_PATH);
            String backdrop = cursor.getString(backdropIndex);
            backdrops.add(new Backdrop(backdrop));
        }
        tvShow.setBackdropImages(new Images(backdrops));
        if (cursor != null) {
            cursor.close();
        }
    }

    public static void loadGenres(Movie movie, ContentResolver contentResolver) {
        Uri uri = PopularMoviesContract.JOIN_GENRES_CONTENT_URI;
        String[] projection = new String[]{GenreEntry.COLUMN_GENRE_NAME, GenreEntry.COLUMN_GENRE_ID};
        String selection = ItemEntry.COLUMN_ITEM_ID + "=?";
        String[] selectionArgs = {String.valueOf(movie.getMovieId())};
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);
        ArrayList<Genre> genres = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            int genreNameIndex = cursor.getColumnIndex(GenreEntry.COLUMN_GENRE_NAME);
            int genreIdIndex = cursor.getColumnIndex(GenreEntry.COLUMN_GENRE_ID);
            String genreName = cursor.getString(genreNameIndex);
            int genreId = cursor.getInt(genreIdIndex);
            genres.add(new Genre(genreName, genreId));
        }
        movie.setGenreList(genres);
        if (cursor != null) {
            cursor.close();
        }
    }

    public static void loadGenres(TvShow tvShow, ContentResolver contentResolver) {
        Uri uri = PopularMoviesContract.JOIN_GENRES_CONTENT_URI;
        String[] projection = new String[]{GenreEntry.COLUMN_GENRE_NAME, GenreEntry.COLUMN_GENRE_ID};
        String selection = ItemEntry.COLUMN_ITEM_ID + "=?";
        String[] selectionArgs = {String.valueOf(tvShow.getTvShowId())};
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);
        ArrayList<Genre> genres = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            int genreNameIndex = cursor.getColumnIndex(GenreEntry.COLUMN_GENRE_NAME);
            int genreIdIndex = cursor.getColumnIndex(GenreEntry.COLUMN_GENRE_ID);
            String genreName = cursor.getString(genreNameIndex);
            int genreId = cursor.getInt(genreIdIndex);
            genres.add(new Genre(genreName, genreId));
        }
        tvShow.setGenreList(genres);
        if (cursor != null) {
            cursor.close();
        }
    }

    public static void loadCompanies(Movie movie, ContentResolver contentResolver) {
        Uri uri = PopularMoviesContract.JOIN_COMPANIES_CONTENT_URI;
        String[] projection = new String[]{CompanyEntry.COLUMN_COMPANY_ID, CompanyEntry.COLUMN_NAME};
        String selection = ItemEntry.COLUMN_ITEM_ID + "=?";
        String[] selectionArgs = {String.valueOf(movie.getMovieId())};
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);
        ArrayList<ProductionCompany> companies = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            int companyNameIndex = cursor.getColumnIndex(CompanyEntry.COLUMN_NAME);
            int companyIdIndex = cursor.getColumnIndex(CompanyEntry.COLUMN_COMPANY_ID);
            String companyName = cursor.getString(companyNameIndex);
            int companyId = cursor.getInt(companyIdIndex);
            companies.add(new ProductionCompany(companyName, companyId));
        }
        movie.setCompanies(companies);
        if (cursor != null) {
            cursor.close();
        }
    }

    public static void loadCompanies(TvShow tvShow, ContentResolver contentResolver) {
        Uri uri = PopularMoviesContract.JOIN_COMPANIES_CONTENT_URI;
        String[] projection = new String[]{CompanyEntry.COLUMN_COMPANY_ID, CompanyEntry.COLUMN_NAME};
        String selection = ItemEntry.COLUMN_ITEM_ID + "=?";
        String[] selectionArgs = {String.valueOf(tvShow.getTvShowId())};
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);
        ArrayList<ProductionCompany> companies = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            int companyNameIndex = cursor.getColumnIndex(CompanyEntry.COLUMN_NAME);
            int companyIdIndex = cursor.getColumnIndex(CompanyEntry.COLUMN_COMPANY_ID);
            String companyName = cursor.getString(companyNameIndex);
            int companyId = cursor.getInt(companyIdIndex);
            companies.add(new ProductionCompany(companyName, companyId));
        }
        tvShow.setCompanies(companies);
        if (cursor != null) {
            cursor.close();
        }
    }

    public static void loadCredits(Movie movie, ContentResolver contentResolver) {
        Uri uri = PopularMoviesContract.JOIN_PEOPLE_CONTENT_URI;
        String[] projection = new String[]{
                PersonEntry.COLUMN_PROFILE_PIC,
                PersonEntry.COLUMN_NAME,
                PersonEntry.COLUMN_PERSON_ID,
                ItemPersonEntry.COLUMN_ROLE_NAME};
        String selection = ItemEntry.COLUMN_ITEM_ID + "=? AND " +
                ItemPersonEntry.COLUMN_ROLE_TYPE + "=? ";
        String[] selectionArgs = {
                String.valueOf(movie.getMovieId()),
                String.valueOf(ItemPersonEntry.ROLE_TYPE_CAST)};
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);
        Credits credits = new Credits();
        ArrayList<Cast> casts = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            int profilePicIndex = cursor.getColumnIndex(PersonEntry.COLUMN_PROFILE_PIC);
            int personNameIndex = cursor.getColumnIndex(PersonEntry.COLUMN_NAME);
            int personIdIndex = cursor.getColumnIndex(PersonEntry.COLUMN_PERSON_ID);
            int roleNameIndex = cursor.getColumnIndex(ItemPersonEntry.COLUMN_ROLE_NAME);
            String profilePic = cursor.getString(profilePicIndex);
            String personName = cursor.getString(personNameIndex);
            String roleName = cursor.getString(roleNameIndex);
            int personId = cursor.getInt(personIdIndex);
            casts.add(new Cast(roleName, personName, personId, profilePic));
        }
        if (cursor != null) {
            cursor.close();
        }
        credits.setCasts(casts);
        //crew
        selectionArgs = new String[]{
                String.valueOf(movie.getMovieId()),
                String.valueOf(ItemPersonEntry.ROLE_TYPE_CREW)};
        cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);
        ArrayList<Crew> crews = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            int profilePicIndex = cursor.getColumnIndex(PersonEntry.COLUMN_PROFILE_PIC);
            int personNameIndex = cursor.getColumnIndex(PersonEntry.COLUMN_NAME);
            int personIdIndex = cursor.getColumnIndex(PersonEntry.COLUMN_PERSON_ID);
            int roleNameIndex = cursor.getColumnIndex(ItemPersonEntry.COLUMN_ROLE_NAME);
            String profilePic = cursor.getString(profilePicIndex);
            String personName = cursor.getString(personNameIndex);
            String roleName = cursor.getString(roleNameIndex);
            int personId = cursor.getInt(personIdIndex);
            crews.add(new Crew(roleName, personName, profilePic, personId));
        }
        if (cursor != null) {
            cursor.close();
        }
        credits.setCrews(crews);
        movie.setCredits(credits);
    }

    public static void loadCredits(TvShow tvShow, ContentResolver contentResolver) {
        Uri uri = PopularMoviesContract.JOIN_PEOPLE_CONTENT_URI;
        String[] projection = new String[]{
                PersonEntry.COLUMN_PROFILE_PIC,
                PersonEntry.COLUMN_NAME,
                PersonEntry.COLUMN_PERSON_ID,
                ItemPersonEntry.COLUMN_ROLE_NAME};
        String selection = ItemEntry.COLUMN_ITEM_ID + "=? AND " +
                ItemPersonEntry.COLUMN_ROLE_TYPE + "=? ";
        String[] selectionArgs = {
                String.valueOf(tvShow.getTvShowId()),
                String.valueOf(ItemPersonEntry.ROLE_TYPE_CAST)};
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);
        Credits credits = new Credits();
        ArrayList<Cast> casts = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            int profilePicIndex = cursor.getColumnIndex(PersonEntry.COLUMN_PROFILE_PIC);
            int personNameIndex = cursor.getColumnIndex(PersonEntry.COLUMN_NAME);
            int personIdIndex = cursor.getColumnIndex(PersonEntry.COLUMN_PERSON_ID);
            int roleNameIndex = cursor.getColumnIndex(ItemPersonEntry.COLUMN_ROLE_NAME);
            String profilePic = cursor.getString(profilePicIndex);
            String personName = cursor.getString(personNameIndex);
            String roleName = cursor.getString(roleNameIndex);
            int personId = cursor.getInt(personIdIndex);
            casts.add(new Cast(roleName, personName, personId, profilePic));
        }
        if (cursor != null) {
            cursor.close();
        }
        credits.setCasts(casts);
        tvShow.setCredits(credits);
        //authors
        selectionArgs = new String[]{
                String.valueOf(tvShow.getTvShowId()),
                String.valueOf(ItemPersonEntry.ROLE_TYPE_CREW)};
        cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);
        ArrayList<Author> authors = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            int profilePicIndex = cursor.getColumnIndex(PersonEntry.COLUMN_PROFILE_PIC);
            int personNameIndex = cursor.getColumnIndex(PersonEntry.COLUMN_NAME);
            int personIdIndex = cursor.getColumnIndex(PersonEntry.COLUMN_PERSON_ID);
            String profilePic = cursor.getString(profilePicIndex);
            String personName = cursor.getString(personNameIndex);
            int personId = cursor.getInt(personIdIndex);
            authors.add(new Author(personName, personId, profilePic));
        }
        if (cursor != null) {
            cursor.close();
        }
        tvShow.setAuthors(authors);
    }

    public static void checkIfPreferred(final int mediaType, final int itemId,
                                        final Context context, final AsyncDbOperations listener) {
        new AsyncTask<Void, Void, Cursor>() {
            @Override
            protected Cursor doInBackground(Void... voids) {
                String[] projection = {PopularMoviesContract.ItemEntry.COLUMN_ITEM_ID};
                String selection = PopularMoviesContract.ItemEntry.COLUMN_ITEM_TYPE + "=? AND "
                        + PopularMoviesContract.ItemEntry.COLUMN_ITEM_ID + "=?";
                String[] selectionArgs = {String.valueOf(mediaType), String.valueOf(itemId)};
                return context.getContentResolver().query(
                        PopularMoviesContract.ItemEntry.CONTENT_URI,
                        projection, selection, selectionArgs, null);
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                super.onPostExecute(cursor);
                listener.isPreferred((cursor != null && cursor.getCount() == 1));
            }
        }.execute();
    }

    public interface AsyncDbOperations {

        void isSavingSuccessful(boolean completed);
        void isDeletingSuccessful(boolean completed);
        void isPreferred(boolean preferred);
    }

    public static void removeItemFromDb(final int itemId, final Context context,
                                        final AsyncDbOperations listener) {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                String selection = PopularMoviesContract.ItemEntry.COLUMN_ITEM_ID + "=?";
                String[] selectionArgs = {String.valueOf(itemId)};
                return context.getContentResolver().delete(ItemEntry.CONTENT_URI,
                        selection, selectionArgs);
            }

            @Override
            protected void onPostExecute(Integer row) {
                super.onPostExecute(row);
                listener.isDeletingSuccessful(row == 1);
            }
        }.execute();
    }

    public static void saveItemAsPreferred(final Movie movie, final Context context,
                                           final AsyncDbOperations listener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                boolean successful = true;
                //saving movie
                ContentValues movieValues = getContentValuesForItem(movie);
                Uri uri = context.getContentResolver().insert(ItemEntry.CONTENT_URI, movieValues);
                if (uri == null) {
                    successful = false;
                }
                //saving companies
                ContentValues[] companiesValues = getContentValuesForCompanies(movie);
                if (companiesValues != null) {
                    context.getContentResolver().bulkInsert(
                            CompanyEntry.CONTENT_URI, companiesValues);
                }
                //saving companies relationships
                ContentValues[] itemsCompaniesValues = getContentValuesForCompaniesRelationships(movie);
                if (itemsCompaniesValues != null) {
                    int rows = context.getContentResolver().bulkInsert(
                            ItemCompanyEntry.CONTENT_URI, itemsCompaniesValues);
                    if (rows != itemsCompaniesValues.length) {
                        successful = false;
                    }
                }
                //saving backdrops
                ContentValues[] backdropsValues = getContentValuesForBackdrops(movie);
                if (backdropsValues != null) {
                    int rows = context.getContentResolver().bulkInsert(
                            BackdropEntry.CONTENT_URI, backdropsValues);
                    if (rows != backdropsValues.length) {
                        successful = false;
                    }
                }
                //saving genres
                ContentValues[] genresValues = getContentValuesForGenres(movie);
                if (genresValues != null) {
                    context.getContentResolver().bulkInsert(
                            GenreEntry.CONTENT_URI, genresValues);
                }
                //saving genres relationships
                ContentValues[] itemsGenresValues = getContentValuesForGenresRelationships(movie);
                if (itemsGenresValues != null) {
                    int rows = context.getContentResolver().bulkInsert(
                            ItemGenreEntry.CONTENT_URI, itemsGenresValues);
                    if (rows != itemsGenresValues.length) {
                        successful = false;
                    }
                }
                //saving cast/crew
                ContentValues[] creditsValues = getContentValuesForPeople(movie);
                if (creditsValues != null) {
                    context.getContentResolver().bulkInsert(
                            PersonEntry.CONTENT_URI, creditsValues);
                }
                //saving cast/crew relationships
                ContentValues[] creditsRelationshipsValues = getContentValuesForPeopleRelationships(movie);
                if (creditsRelationshipsValues != null) {
                    int rows = context.getContentResolver().bulkInsert(
                            ItemPersonEntry.CONTENT_URI, creditsRelationshipsValues);
                    if (rows != creditsRelationshipsValues.length) {
                        successful = false;
                    }
                }
                return successful;
            }

            @Override
            protected void onPostExecute(Boolean successful) {
                super.onPostExecute(successful);
                listener.isSavingSuccessful(successful);
            }
        }.execute();
    }

    public static void saveItemAsPreferred(final TvShow tvShow, final Context context, final AsyncDbOperations listener) {
        new AsyncTask<Void, Void, Boolean>() {
            boolean successful = true;

            @Override
            protected Boolean doInBackground(Void... voids) {
                //saving tvshow
                ContentValues tvShowValues = getContentValuesForItem(tvShow);
                Uri uri = context.getContentResolver().insert(ItemEntry.CONTENT_URI, tvShowValues);
                if (uri == null) {
                    successful = false;
                }
                //saving companies
                ContentValues[] companiesValues = getContentValuesForCompaniesTvShow(tvShow);
                if (companiesValues != null) {
                    context.getContentResolver().bulkInsert(
                            CompanyEntry.CONTENT_URI, companiesValues);
                }
                //saving companies relationships
                ContentValues[] itemsCompaniesValues = getContentValuesForCompaniesRelationships(tvShow);
                if (itemsCompaniesValues != null) {
                    int rows = context.getContentResolver().bulkInsert(
                            ItemCompanyEntry.CONTENT_URI, itemsCompaniesValues);
                    if (rows != itemsCompaniesValues.length) {
                        successful = false;
                    }
                }
                //saving backdrops
                ContentValues[] backdropsValues = getContentValuesForBackdrops(tvShow);
                if (backdropsValues != null) {
                    int rows = context.getContentResolver().bulkInsert(
                            BackdropEntry.CONTENT_URI, backdropsValues);
                    if (rows != backdropsValues.length) {
                        successful = false;
                    }
                }
                //saving genres
                ContentValues[] genresValues = getContentValuesForGenres(tvShow);
                if (genresValues != null) {
                    context.getContentResolver().bulkInsert(
                            GenreEntry.CONTENT_URI, genresValues);
                }
                //saving genres relationships
                ContentValues[] itemsGenresValues = getContentValuesForGenresRelationships(tvShow);
                if (itemsGenresValues != null) {
                    int rows = context.getContentResolver().bulkInsert(
                            ItemGenreEntry.CONTENT_URI, itemsGenresValues);
                    if (rows != itemsGenresValues.length) {
                        successful = false;
                    }
                }
                //saving cast/crew
                ContentValues[] creditsValues = getContentValuesForPeople(tvShow);
                if (creditsValues != null) {
                    context.getContentResolver().bulkInsert(
                            PersonEntry.CONTENT_URI, creditsValues);
                }
                //saving cast/crew relationships
                ContentValues[] creditsRelationshipsValues = getContentValuesForPeopleRelationships(tvShow);
                if (creditsRelationshipsValues != null) {
                    int rows = context.getContentResolver().bulkInsert(
                            ItemPersonEntry.CONTENT_URI, creditsRelationshipsValues);
                    if (rows != creditsRelationshipsValues.length) {
                        successful = false;
                    }
                }
                return successful;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                listener.isSavingSuccessful(successful);
            }
        }.execute();
    }
}
