package com.example.android.popularmovies2.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Emanuele on 12/03/2018.
 */
public final class PopularMoviesContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEMS = "items";
    public static final String PATH_COMPANIES = "companies";
    public static final String PATH_PEOPLE = "people";
    public static final String PATH_GENRES = "genres";
    public static final String PATH_ITEMS_COMPANIES = "itemscompanies";
    public static final String PATH_ITEMS_PEOPLE = "itemspeople";
    public static final String PATH_ITEMS_GENRES = "itemsgenres";
    public static final String PATH_BACKDROPS = "backdrops";
    //CONTENT URI FOR JOINS
    public static final String PATH_JOIN_BACKDROPS = "join_backdrops";
    public static final Uri JOIN_BACKDROPS_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_JOIN_BACKDROPS);
    public static final String PATH_JOIN_GENRES = "join_genres";
    public static final Uri JOIN_GENRES_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_JOIN_GENRES);
    public static final String PATH_JOIN_PEOPLE = "join_people";
    public static final Uri JOIN_PEOPLE_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_JOIN_PEOPLE);
    public static final String PATH_JOIN_COMPANIES = "join_companies";
    public static final Uri JOIN_COMPANIES_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_JOIN_COMPANIES);

    private PopularMoviesContract() {
    }

    public static final class ItemEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);
        public static final String TABLE_NAME = "items";
        public static final String COLUMN_ITEM_TYPE = "item_type";
        public static final String COLUMN_ITEM_ID = "item_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_LAST_DATE = "last_date";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_BUDGET = "budget";
        public static final String COLUMN_REVENUE = "revenue";
        public static final String COLUMN_COUNTRY = "country";
        public static final String COLUMN_EPISODE_COUNT = "episode_count";
        public static final String COLUMN_SEASON_COUNT = "season_count";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final int TV_MEDIA_TYPE = 2;
        public static final int MOVIE_MEDIA_TYPE = 1;
    }

    public static final class BackdropEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BACKDROPS);
        public static final String TABLE_NAME = "backdrops";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_B_ITEM_ID = "b_item_id";
    }

    public static final class GenreEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_GENRES);
        public static final String TABLE_NAME = "genres";
        public static final String COLUMN_GENRE_ID = "genre_id";
        public static final String COLUMN_GENRE_NAME = "genre_name";
    }

    public static final class ItemGenreEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS_GENRES);
        public static final String TABLE_NAME = "items_genres";
        public static final String COLUMN_IG_GENRE_ID = "ig_genre_id";
        public static final String COLUMN_IG_ITEM_ID = "ig_item_id";
    }

    public static final class PersonEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PEOPLE);
        public static final String TABLE_NAME = "people";
        public static final String COLUMN_PERSON_ID = "person_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PROFILE_PIC = "profile_pic";
    }

    public static final class ItemPersonEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS_PEOPLE);
        public static final String TABLE_NAME = "items_people";
        public static final String COLUMN_IP_ITEM_ID = "ip_item_id";
        public static final String COLUMN_IP_PERSON_ID = "ip_person_id";
        public static final String COLUMN_ROLE_NAME = "role_name";
        public static final String COLUMN_ROLE_TYPE = "role_type";
        public static final int ROLE_TYPE_CAST = 1;
        public static final int ROLE_TYPE_CREW = 2;
    }

    public static final class CompanyEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_COMPANIES);
        public static final String TABLE_NAME = "companies";
        public static final String COLUMN_COMPANY_ID = "company_id";
        public static final String COLUMN_NAME = "name";
    }

    public static final class ItemCompanyEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS_COMPANIES);
        public static final String TABLE_NAME = "items_companies";
        public static final String COLUMN_IC_ITEM_ID = "ic_item_id";
        public static final String COLUMN_IC_COMPANY_ID = "ic_company_id";
    }
}
