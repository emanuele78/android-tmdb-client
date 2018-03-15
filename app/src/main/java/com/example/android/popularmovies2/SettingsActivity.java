/*
 * Copyright (C) 2018 Emanuele Mazzante
 */

package com.example.android.popularmovies2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.example.android.popularmovies2.network.TmdbClient;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getString(R.string.settings));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //preference fragment class
    public static class MovieListPreferences extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //inflate layout preference
            addPreferencesFromResource(R.xml.setting_preferences);
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();
            int count = preferenceScreen.getPreferenceCount();
            for (int i = 0; i < count; i++) {
                Preference preference = preferenceScreen.getPreference(i);
                if (preference instanceof ListPreference) {
                    String preferenceKey = preference.getKey();
                    String value = sharedPreferences.getString(preferenceKey, "");
                    setPreferenceSummary(preference, value);
                    if (preferenceKey.equals(getString(R.string.settings_content_lang_key))) {
                        preference.setOnPreferenceChangeListener(
                                new Preference.OnPreferenceChangeListener() {
                                    @Override
                                    public boolean onPreferenceChange(Preference preference,
                                                                      Object newValue) {
                                        return true;
                                    }
                                });
                    }
                }
            }
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }

        private void setPreferenceSummary(Preference preference, String value) {
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(value);
                if (prefIndex >= 0) {
                    String summary = listPreference.getEntries()[prefIndex].toString();
                    listPreference.setSummary(summary);
                }
            }
        }

        //metodo di callback
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference preference = findPreference(key);
            if (preference != null) {
                if (preference instanceof ListPreference) {
                    String preferenceKey = preference.getKey();
                    String value = sharedPreferences.getString(preferenceKey, "");
                    setPreferenceSummary(preference, value);
                }
            }
        }

        @Override
        public void onDestroy() {
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
            super.onDestroy();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openTmdb(View view) {
        //manage TMDB logo click
        Uri uri = Uri.parse(TmdbClient.TMDB_HOME_URL);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}