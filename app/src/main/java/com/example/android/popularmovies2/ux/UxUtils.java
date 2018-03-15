package com.example.android.popularmovies2.ux;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.StringDef;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.example.android.popularmovies2.LinkedPeopleActivity;
import com.example.android.popularmovies2.MainActivity;
import com.example.android.popularmovies2.ManualSearchActivity;
import com.example.android.popularmovies2.MovieDetailActivity;
import com.example.android.popularmovies2.PersonDetailActivity;
import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.SearchResultActivity;
import com.example.android.popularmovies2.TvShowDetailActivity;
import com.example.android.popularmovies2.model.LinkedPerson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.android.popularmovies2.network.TmdbClient.TMDB_DATE_PATTERN;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

/**
 * Created by Emanuele on 22/02/2018.
 */
public class UxUtils {

    public static final int STARTING_PAGE = 1;
    public static final String RECYCLER_VIEW_POSITION_KEY = "recycler_view_position";
    public static final String RECYCLER_VIEW_DATA_KEY = "recycler_view_data";
    public static final String PAGES_LOADED_KEY = "pages_loaded";
    public static final String LAST_PAGE_ON_SERVER_KEY = "last_page_on_server";
    public static final String CURRENT_MODE_KEY = "current_mode";
    public static final String CURRENT_SORTING_KEY = "current_sorting";
    public static final String TOTAL_ITEM_KEY = "total_item";
    public static final String SEARCH_MODE = "search_mode_key";
    public static final String RUNTIME_TAG = "runtime_tag";
    public static final String GENRE_TAG = "genre_tag";
    public static final String EXTRA_STRING_VALUE_KEY = "extra_string_value_key";
    public static final String RATING_TAG = "rating_tag";
    public static final String YEAR_TAG = "year_tag";
    public static final String COMPANY_TAG = "company_tag";
    public static final String KEYWORD_TAG = "keyword_tag";
    public static final String TAG_KEY = "tag_key";
    public static final String TAG_VALUE_KEY = "tag_value_key";
    public static final String SIMILAR_TAG = "similar_tag";
    public static final String CONTENT_LANG_KEY = "content_lang_key";

    @StringDef({RUNTIME_TAG, GENRE_TAG, RATING_TAG, YEAR_TAG, COMPANY_TAG, KEYWORD_TAG, SIMILAR_TAG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Tag {

    }

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static int getDisplayWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static int getAvailableBlankSpace(Context context, int columns) {
        int totalColumnSpace = Math.round(context.getResources().
                getDimension(R.dimen.poster_image_width) * columns);
        return getDisplayWidth(context) - totalColumnSpace;
    }

    public static int getColumnNumber(Context context) {
        int singleColumnSpace = Math.round(context.getResources().
                getDimension(R.dimen.poster_image_width));
        int columns = Math.round(getDisplayWidth(context) / singleColumnSpace);
        int minSpace = Math.round(context.getResources().getDimension(R.dimen.min_space_dimen));
        int minColumns = Math.round(context.getResources().getInteger(R.integer.min_columns));
        while (getAvailableBlankSpace(context, columns) < minSpace && columns > minColumns) {
            columns--;
        }
        return columns;
    }

    public static void showSnackbar(String message, View view) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public static String getFormattedDate(String stringDate) {
        if (stringDate == null || stringDate.isEmpty()) {
            return null;
        }
        return DateFormat.getDateInstance(DateFormat.SHORT).format(getDateFromString(stringDate));
    }

    public static Date getDateFromString(String stringDate) {
        if (stringDate == null || stringDate.isEmpty()) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(TMDB_DATE_PATTERN, Locale.getDefault());
        try {
            return format.parse(stringDate);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getFormattedRuntime(int runtime, Context context) {
        int hourString = runtime / 60;
        int minuteString = runtime % 60;
        String hourLabel = context.getString(R.string.hour_label);
        String minuteLabel = context.getString(R.string.minute_label);
        if (hourString > 0) {
            return String.format(Locale.getDefault(), "%d " + hourLabel + " %02d " +
                    minuteLabel, hourString, minuteString);
        } else {
            return String.format(Locale.getDefault(), "%02d " + minuteLabel, minuteString);
        }
    }

    public static String getFormattedTile(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        String separator = " ";
        String[] words = text.split(separator);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
            builder.append(word);
            if (i < words.length - 1) {
                builder.append(separator);
            }
        }
        return builder.toString();
    }

    public static int getAge(String birthString, String lastString) {
        Date birth = getDateFromString(birthString);
        Date last = new Date();/* now*/
        if (lastString != null) {
            last = getDateFromString(lastString);
        }
        Calendar birthCal = Calendar.getInstance(Locale.getDefault());
        birthCal.setTime(birth);
        Calendar lastCal = Calendar.getInstance(Locale.getDefault());
        lastCal.setTime(last);
        int age = lastCal.get(YEAR) - birthCal.get(YEAR);
        //check day and month
        if (birthCal.get(MONTH) > lastCal.get(MONTH)) {
            age--;
        } else if (birthCal.get(MONTH) == lastCal.get(MONTH) &&
                birthCal.get(DATE) > lastCal.get(DATE)) {
            age--;
        }
        return age;
    }

    public static void setMenu(Context context, Menu menu) {
        final String className = context.getClass().getSimpleName();
        if (className.equals(MainActivity.class.getSimpleName())) {
            menu.findItem(R.id.search_view).setVisible(true);
        } else if (className.equals(ManualSearchActivity.class.getSimpleName())) {
            menu.findItem(R.id.action_settings).setVisible(true);
        } else if (className.equals(MovieDetailActivity.class.getSimpleName())) {
            menu.findItem(R.id.action_settings).setVisible(true);
            menu.findItem(R.id.search_view).setVisible(true);
            menu.findItem(R.id.action_find_similar_movie).setVisible(true);
        } else if (className.equals(PersonDetailActivity.class.getSimpleName())) {
            menu.findItem(R.id.action_settings).setVisible(true);
            menu.findItem(R.id.search_view).setVisible(true);
            menu.findItem(R.id.action_add_to_linked_list).setVisible(true);
        } else if (className.equals(SearchResultActivity.class.getSimpleName())) {
            menu.findItem(R.id.action_settings).setVisible(true);
            menu.findItem(R.id.search_view).setVisible(true);
        } else if (className.equals(TvShowDetailActivity.class.getSimpleName())) {
            menu.findItem(R.id.action_settings).setVisible(true);
            menu.findItem(R.id.search_view).setVisible(true);
            menu.findItem(R.id.action_find_similar_tvshow).setVisible(true);
        } else if (className.equals(LinkedPeopleActivity.class.getSimpleName())) {
            menu.findItem(R.id.action_settings).setVisible(true);
            menu.findItem(R.id.search_view).setVisible(true);
        }
    }

    public static String getContentLangFromPreference(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String contentLangKey = context.getString(R.string.settings_content_lang_key);
        String contentLangDefault = context.getString(R.string.settings_content_lang_default);
        return sharedPreferences.getString(contentLangKey, contentLangDefault);
    }

    public static ArrayList<LinkedPerson> getLinkedPeopleFromPreference(Context context) {
        //reading from preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String linkedListKey = context.getString(R.string.linked_list_key);
        String jsonLinkedList = sharedPreferences.getString(linkedListKey, null);
        if (jsonLinkedList == null) {
            return null;
        } else {
            Type linkedPersonType = new TypeToken<ArrayList<LinkedPerson>>() {
            }.getType();
            return new Gson().fromJson(jsonLinkedList,
                    linkedPersonType);
        }
    }
}
