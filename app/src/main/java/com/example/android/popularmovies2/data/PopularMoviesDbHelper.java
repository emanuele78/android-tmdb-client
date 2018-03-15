package com.example.android.popularmovies2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies2.data.PopularMoviesContract.BackdropEntry;
import com.example.android.popularmovies2.data.PopularMoviesContract.CompanyEntry;
import com.example.android.popularmovies2.data.PopularMoviesContract.GenreEntry;
import com.example.android.popularmovies2.data.PopularMoviesContract.ItemCompanyEntry;
import com.example.android.popularmovies2.data.PopularMoviesContract.ItemEntry;
import com.example.android.popularmovies2.data.PopularMoviesContract.ItemGenreEntry;
import com.example.android.popularmovies2.data.PopularMoviesContract.ItemPersonEntry;
import com.example.android.popularmovies2.data.PopularMoviesContract.PersonEntry;

/**
 * Created by Emanuele on 12/03/2018.
 */
public class PopularMoviesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "popularmovies.db";
    private static final String PRAGMA_FOREIGN_KEY = "PRAGMA foreign_keys=ON;";

    public PopularMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //items table
        String SQL_CREATE_ENTRIES = "CREATE TABLE " + ItemEntry.TABLE_NAME +
                " (" + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", " + ItemEntry.COLUMN_ITEM_TYPE + " INTEGER NOT NULL" +
                ", " + ItemEntry.COLUMN_ITEM_ID + " INTEGER NOT NULL" +
                ", " + ItemEntry.COLUMN_TITLE + " TEXT NOT NULL" +
                ", " + ItemEntry.COLUMN_ORIGINAL_TITLE + " TEXT" +
                ", " + ItemEntry.COLUMN_RATING + " REAL" +
                ", " + ItemEntry.COLUMN_VOTE_COUNT + " INTEGER" +
                ", " + ItemEntry.COLUMN_POPULARITY + " REAL" +
                ", " + ItemEntry.COLUMN_RELEASE_DATE + " TEXT" +
                ", " + ItemEntry.COLUMN_LAST_DATE + " TEXT" +
                ", " + ItemEntry.COLUMN_PLOT + " TEXT" +
                ", " + ItemEntry.COLUMN_BUDGET + " INTEGER" +
                ", " + ItemEntry.COLUMN_REVENUE + " INTEGER" +
                ", " + ItemEntry.COLUMN_COUNTRY + " TEXT" +
                ", " + ItemEntry.COLUMN_EPISODE_COUNT + " INTEGER" +
                ", " + ItemEntry.COLUMN_SEASON_COUNT + " INTEGER" +
                ", " + ItemEntry.COLUMN_STATUS + " TEXT" +
                ", " + ItemEntry.COLUMN_POSTER + " TEXT" +
                ", " + ItemEntry.COLUMN_RUNTIME + " TEXT" +
                ", " + getUniqueConstraint(ItemEntry.COLUMN_ITEM_ID) +
                ")";
        db.execSQL(SQL_CREATE_ENTRIES);
        //backdrops table
        SQL_CREATE_ENTRIES = "CREATE TABLE " + BackdropEntry.TABLE_NAME +
                " (" + BackdropEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", " + BackdropEntry.COLUMN_BACKDROP_PATH + " TEXT" +
                ", " + BackdropEntry.COLUMN_B_ITEM_ID + " INTEGER" +
                ", " + getForeignKeyStatement(
                BackdropEntry.COLUMN_B_ITEM_ID,
                ItemEntry.TABLE_NAME,
                ItemEntry.COLUMN_ITEM_ID) +
                ")";
        db.execSQL(SQL_CREATE_ENTRIES);
        //genres table
        SQL_CREATE_ENTRIES = "CREATE TABLE " + GenreEntry.TABLE_NAME +
                " (" + GenreEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", " + GenreEntry.COLUMN_GENRE_ID + " INTEGER NOT NULL" +
                ", " + GenreEntry.COLUMN_GENRE_NAME + " TEXT" +
                ", " + getUniqueConstraint(GenreEntry.COLUMN_GENRE_ID) +
                ")";
        db.execSQL(SQL_CREATE_ENTRIES);
        //items_genres table
        SQL_CREATE_ENTRIES = "CREATE TABLE " + ItemGenreEntry.TABLE_NAME +
                " (" + ItemGenreEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", " + ItemGenreEntry.COLUMN_IG_GENRE_ID + " INTEGER " +
                ", " + ItemGenreEntry.COLUMN_IG_ITEM_ID + " INTEGER " +
                ", " + getForeignKeyStatement(
                ItemGenreEntry.COLUMN_IG_GENRE_ID,
                GenreEntry.TABLE_NAME,
                GenreEntry.COLUMN_GENRE_ID
        ) +
                ", " + getForeignKeyStatement(
                ItemGenreEntry.COLUMN_IG_ITEM_ID,
                ItemEntry.TABLE_NAME,
                ItemEntry.COLUMN_ITEM_ID
        ) +
                ")";
        db.execSQL(SQL_CREATE_ENTRIES);
        //people table
        SQL_CREATE_ENTRIES = "CREATE TABLE " + PersonEntry.TABLE_NAME +
                " (" + PersonEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", " + PersonEntry.COLUMN_PERSON_ID + " INTEGER NOT NULL" +
                ", " + PersonEntry.COLUMN_NAME + " TEXT" +
                ", " + PersonEntry.COLUMN_PROFILE_PIC + " TEXT" +
                ", " + getUniqueConstraint(PersonEntry.COLUMN_PERSON_ID) +
                ")";
        db.execSQL(SQL_CREATE_ENTRIES);
        //items_people table
        SQL_CREATE_ENTRIES = "CREATE TABLE " + ItemPersonEntry.TABLE_NAME +
                " (" + ItemPersonEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", " + ItemPersonEntry.COLUMN_IP_ITEM_ID + " INTEGER " +
                ", " + ItemPersonEntry.COLUMN_IP_PERSON_ID + " INTEGER " +
                ", " + ItemPersonEntry.COLUMN_ROLE_NAME + " TEXT " +
                ", " + ItemPersonEntry.COLUMN_ROLE_TYPE + " TEXT " +
                ", " + getForeignKeyStatement(
                ItemPersonEntry.COLUMN_IP_PERSON_ID,
                PersonEntry.TABLE_NAME,
                PersonEntry.COLUMN_PERSON_ID
        ) +
                ", " + getForeignKeyStatement(
                ItemPersonEntry.COLUMN_IP_ITEM_ID,
                ItemEntry.TABLE_NAME,
                ItemEntry.COLUMN_ITEM_ID
        ) +
                ")";
        db.execSQL(SQL_CREATE_ENTRIES);
        //companies table
        SQL_CREATE_ENTRIES = "CREATE TABLE " + CompanyEntry.TABLE_NAME +
                " (" + CompanyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", " + CompanyEntry.COLUMN_COMPANY_ID + " INTEGER NOT NULL" +
                ", " + CompanyEntry.COLUMN_NAME + " TEXT" +
                ", " + getUniqueConstraint(CompanyEntry.COLUMN_COMPANY_ID) +
                ")";
        db.execSQL(SQL_CREATE_ENTRIES);
        //items_companies table
        SQL_CREATE_ENTRIES = "CREATE TABLE " + ItemCompanyEntry.TABLE_NAME +
                " (" + ItemCompanyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", " + ItemCompanyEntry.COLUMN_IC_ITEM_ID + " INTEGER " +
                ", " + ItemCompanyEntry.COLUMN_IC_COMPANY_ID + " INTEGER " +
                ", " + getForeignKeyStatement(
                ItemCompanyEntry.COLUMN_IC_COMPANY_ID,
                CompanyEntry.TABLE_NAME,
                CompanyEntry.COLUMN_COMPANY_ID
        ) +
                ", " + getForeignKeyStatement(
                ItemCompanyEntry.COLUMN_IC_ITEM_ID,
                ItemEntry.TABLE_NAME,
                ItemEntry.COLUMN_ITEM_ID
        ) +
                ")";
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    private static String getForeignKeyStatement(String foreignKey, String tableName, String column) {
        return "FOREIGN KEY (" + foreignKey + ") REFERENCES " + tableName + "(" + column + ") ON DELETE CASCADE";
    }

    private static String getUniqueConstraint(String column) {
        return "UNIQUE (" + column + ") ON CONFLICT IGNORE";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL(PRAGMA_FOREIGN_KEY);
    }
}
