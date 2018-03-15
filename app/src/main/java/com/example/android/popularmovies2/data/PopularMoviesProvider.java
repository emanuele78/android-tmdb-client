package com.example.android.popularmovies2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Emanuele on 12/03/2018.
 */
public class PopularMoviesProvider extends ContentProvider {

    private PopularMoviesDbHelper dbHelper;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int ITEMS = 100;
    private static final int ITEM_ID = 101;
    private static final int COMPANIES = 200;
    private static final int COMPANY_ID = 201;
    private static final int PERSON_ID = 301;
    private static final int PEOPLE = 300;
    private static final int JOIN_PEOPLE = 302;
    private static final int ITEMS_COMPANIES = 400;
    private static final int JOIN_COMPANIES = 402;
    private static final int ITEMS_COMPANIES_ID = 401;
    private static final int BACKDROPS = 500;
    private static final int BACKDROP_ID = 501;
    private static final int JOIN_BACKDROPS = 502;
    private static final int GENRE_ID = 601;
    private static final int GENRES = 600;
    private static final int JOIN_GENRES = 602;
    private static final int ITEMS_GENRE_ID = 701;
    private static final int ITEMS_GENRES = 700;
    private static final int ITEMS_PEOPLE = 800;
    private static final int ITEMS_PEOPLE_ID = 801;
    static {
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.PATH_ITEMS, ITEMS);
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.PATH_ITEMS + "/#", ITEM_ID);
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.PATH_COMPANIES, COMPANIES);
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.PATH_COMPANIES + "/#", COMPANY_ID);
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.PATH_PEOPLE, PEOPLE);
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.PATH_PEOPLE + "/#", PERSON_ID);
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.PATH_ITEMS_COMPANIES, ITEMS_COMPANIES);
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.PATH_ITEMS_COMPANIES + "/#", ITEMS_COMPANIES_ID);
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.PATH_BACKDROPS, BACKDROPS);
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.PATH_BACKDROPS + "/#", BACKDROP_ID);
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.PATH_GENRES, GENRES);
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.PATH_GENRES + "/#", GENRE_ID);
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.PATH_ITEMS_GENRES, ITEMS_GENRES);
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.PATH_ITEMS_GENRES + "/#", ITEMS_GENRE_ID);
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.PATH_ITEMS_PEOPLE, ITEMS_PEOPLE);
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.PATH_ITEMS_PEOPLE + "/#", ITEMS_PEOPLE_ID);
        //joins
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.PATH_JOIN_BACKDROPS, JOIN_BACKDROPS);
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.PATH_JOIN_GENRES, JOIN_GENRES);
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.PATH_JOIN_PEOPLE, JOIN_PEOPLE);
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.PATH_JOIN_COMPANIES, JOIN_COMPANIES);
    }
    @Override
    public boolean onCreate() {
        dbHelper = new PopularMoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return getMultipleItems(uri, projection, selection, selectionArgs, sortOrder);
            case ITEM_ID:
                return getSingleItem(uri, projection, selection, selectionArgs, sortOrder);
            case JOIN_BACKDROPS:
                return getBackdropsJoin(uri, projection, selection, selectionArgs, sortOrder);
            case JOIN_GENRES:
                return getGenresJoin(uri, projection, selection, selectionArgs, sortOrder);
            case JOIN_PEOPLE:
                return getPeopleJoin(uri, projection, selection, selectionArgs, sortOrder);
            case JOIN_COMPANIES:
                return getCompaniesJoin(uri, projection, selection, selectionArgs, sortOrder);
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    private Cursor getCompaniesJoin(Uri uri, String[] projection, String selection,
                                    String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String tables = "companies INNER JOIN items_companies ON "+
                "(companies.company_id=items_companies.ic_company_id) " +
                "INNER JOIN items ON (items_companies.ic_item_id=items.item_id)";
        queryBuilder.setTables(tables);
        return queryBuilder.query(database, projection, selection, selectionArgs,
                null, null, sortOrder);
    }

    private Cursor getPeopleJoin(Uri uri, String[] projection, String selection,
                                 String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String tables = "people INNER JOIN items_people ON " +
                "(people.person_id=items_people.ip_person_id) " +
                "INNER JOIN items ON (items_people.ip_item_id=items.item_id)";
        queryBuilder.setTables(tables);
        return queryBuilder.query(database, projection, selection, selectionArgs,
                null, null, sortOrder);
    }

    private Cursor getBackdropsJoin(Uri uri, String[] projection, String selection,
                                    String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables("items INNER JOIN backdrops ON "+
                "(items.item_id=backdrops.b_item_id)");
        return queryBuilder.query(database, projection, selection, selectionArgs,
                null, null, sortOrder);
    }

    private Cursor getGenresJoin(Uri uri, String[] projection, String selection,
                                 String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String tables = "genres INNER JOIN items_genres ON " +
                "(genres.genre_id=items_genres.ig_genre_id) " +
                "INNER JOIN items ON (items_genres.ig_item_id=items.item_id)";
        queryBuilder.setTables(tables);
        return queryBuilder.query(database, projection, selection, selectionArgs,
                null, null, sortOrder);
    }

    private Cursor getMultipleItems(Uri uri, String[] projection, String selection,
                                    String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(PopularMoviesContract.ItemEntry.TABLE_NAME,
                projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Cursor getSingleItem(Uri uri, String[] projection, String selection,
                                 String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        selection = PopularMoviesContract.ItemEntry._ID + "=?";
        long arg = ContentUris.parseId(uri);
        selectionArgs = new String[]{String.valueOf(arg)};
        //eseguo query
        return database.query(PopularMoviesContract.ItemEntry.TABLE_NAME,
                projection, selection,
                selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                long id = db.insert(PopularMoviesContract.ItemEntry.TABLE_NAME, null, values);
                if (id == -1) {
                    return null;
                } else {
                    getContext().getContentResolver().notifyChange(uri, null);
                    return Uri.withAppendedPath(uri, String.valueOf(id));
                }
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return deleteItem(uri, selection, selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    private int deleteItem(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        int row = database.delete(PopularMoviesContract.ItemEntry.TABLE_NAME, selection,
                selectionArgs);
        if (row != -1) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        //removing orphan child with raw query
        String raw = "delete from companies where companies.company_id in " +
                "(select companies.company_id from companies left join " +
                "items_companies on companies.company_id = items_companies.ic_company_id " +
                "where items_companies.ic_company_id is null);";
        database.execSQL(raw);
        raw = "delete from people where people.person_id in " +
                "(select people.person_id from people left join " +
                "items_people on people.person_id = items_people.ip_person_id " +
                "where items_people.ip_person_id is null);";
        database.execSQL(raw);
        raw = "delete from genres where genres.genre_id in " +
                "(select genres.genre_id from genres left join " +
                "items_genres on genres.genre_id = items_genres.ig_genre_id " +
                "where items_genres.ig_genre_id is null);";
        database.execSQL(raw);
        return row;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        //update not implemented
        throw new UnsupportedOperationException("Unknown Uri: " + uri);
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        switch (sUriMatcher.match(uri)) {
            case COMPANIES:
                return genericInsert(values, PopularMoviesContract.CompanyEntry.TABLE_NAME);
            case PEOPLE:
                return genericInsert(values, PopularMoviesContract.PersonEntry.TABLE_NAME);
            case ITEMS_COMPANIES:
                return genericInsert(values, PopularMoviesContract.ItemCompanyEntry.TABLE_NAME);
            case BACKDROPS:
                return genericInsert(values, PopularMoviesContract.BackdropEntry.TABLE_NAME);
            case GENRES:
                return genericInsert(values, PopularMoviesContract.GenreEntry.TABLE_NAME);
            case ITEMS_GENRES:
                return genericInsert(values, PopularMoviesContract.ItemGenreEntry.TABLE_NAME);
            case ITEMS_PEOPLE:
                return genericInsert(values, PopularMoviesContract.ItemPersonEntry.TABLE_NAME);
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    private int genericInsert(ContentValues[] values, String tableName) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        int rowsInserted = 0;
        try {
            for (ContentValues value : values) {
                long _id = db.insert(tableName, null, value);
                if (_id != -1) {
                    rowsInserted++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return rowsInserted;
    }
}
