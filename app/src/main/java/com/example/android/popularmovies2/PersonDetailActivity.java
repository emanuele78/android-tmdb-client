package com.example.android.popularmovies2;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.popularmovies2.databinding.ActivityPersonDetailBinding;
import com.example.android.popularmovies2.model.LinkedPerson;
import com.example.android.popularmovies2.model.Movie;
import com.example.android.popularmovies2.model.Person;
import com.example.android.popularmovies2.model.TvShow;
import com.example.android.popularmovies2.network.NetworkUtils;
import com.example.android.popularmovies2.network.TmdbClient;
import com.example.android.popularmovies2.ux.CombinedCastRecyclerViewAdapter;
import com.example.android.popularmovies2.ux.CombinedCrewRecyclerViewAdapter;
import com.example.android.popularmovies2.ux.UxUtils;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonDetailActivity extends AppCompatActivity implements Callback<Person> {

    private String preferredContentLang;
    private int personId;
    private Person person;
    private SearchView searchView;
    private ActivityPersonDetailBinding binding;
    private static final int NO_PERSON = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_person_detail);
        //setting toolbar
        setSupportActionBar(binding.toolbar.bar);
        binding.toolbar.toolbarTitleTv.setText(R.string.person_detail_title);
        preferredContentLang = UxUtils.getContentLangFromPreference(this);
        if (savedInstanceState != null && savedInstanceState.containsKey(Person.PERSON_BUNDLE_KEY)) {
            person = savedInstanceState.getParcelable(Person.PERSON_BUNDLE_KEY);
            personId = person.getPersonId();
            preferredContentLang = savedInstanceState.getString(UxUtils.CONTENT_LANG_KEY);
            settingUpPerson();
        } else {
            //getting intent with extra
            Intent intent = getIntent();
            personId = intent.getIntExtra(Person.PERSON_KEY, NO_PERSON);
            if (personId == NO_PERSON) {
                UxUtils.showToast(this, getString(R.string.unable_to_open_person));
                finish();
                return;
            }
            doNetworkCall();
        }
    }

    private void readLinkedListPreference() {
        //reading from preference
        ArrayList<LinkedPerson> linkedPeople = UxUtils.getLinkedPeopleFromPreference(this);
        if (linkedPeople == null) {
            binding.toolbar.linkedTv.setVisibility(View.INVISIBLE);
        } else {
            int size = linkedPeople.size();
            binding.toolbar.linkedTv.setText(String.valueOf(size));
            binding.toolbar.linkedTv.setVisibility(View.VISIBLE);
        }
    }

    private void doNetworkCall() {
        binding.loadingBar.setVisibility(View.VISIBLE);
        NetworkUtils.doNetworkCallForPersonDetail(
                personId,
                preferredContentLang,
                this);
    }

    @Override
    public void onResponse(Call<Person> call, Response<Person> response) {
        if (response.isSuccessful()) {
            person = response.body();
            settingUpPerson();
        } else {
            UxUtils.showToast(PersonDetailActivity.this,
                    getString(R.string.unable_to_open_person));
            finish();
        }
    }

    @Override
    public void onFailure(Call<Person> call, Throwable t) {
        binding.loadingBar.setVisibility(View.INVISIBLE);
        UxUtils.showToast(PersonDetailActivity.this,
                getString(R.string.unable_to_open_person));
        finish();
    }

    public void goToHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Person.PERSON_BUNDLE_KEY, person);
        outState.putString(UxUtils.CONTENT_LANG_KEY, preferredContentLang);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (searchView != null && !searchView.isIconified()) {
            invalidateOptionsMenu();
        }
        readLinkedListPreference();
        //check for content lang changed
        String contentLangValue = UxUtils.getContentLangFromPreference(this);
        if (!preferredContentLang.equals(contentLangValue)) {
            preferredContentLang = contentLangValue;
            doNetworkCall();
        }
    }

    private void settingUpPerson() {
        String emptyString = getString(R.string.not_available_short);
        //setting cast recycler view
        CombinedCastRecyclerViewAdapter castRecyclerViewAdapter =
                new CombinedCastRecyclerViewAdapter(person.getCredits().getCreditCasts(), emptyString,
                        new CombinedCastRecyclerViewAdapter.OnMediaCastItemClick() {
                            @Override
                            public void onClick(int mediaId, String mediaType) {
                                openMediaDetails(mediaId, mediaType);
                            }
                        });
        LinearLayoutManager castLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        binding.content.castRv.setLayoutManager(castLayoutManager);
        binding.content.castRv.setHasFixedSize(true);
        binding.content.castRv.setAdapter(castRecyclerViewAdapter);
        //setting crew recycler view
        CombinedCrewRecyclerViewAdapter crewRecyclerViewAdapter =
                new CombinedCrewRecyclerViewAdapter(person.getCredits().getCreditCrews(), emptyString,
                        new CombinedCrewRecyclerViewAdapter.OnMediaCrewItemClick() {
                            @Override
                            public void onClick(int mediaId, String mediaType) {
                                openMediaDetails(mediaId, mediaType);
                            }
                        });
        LinearLayoutManager crewLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        binding.content.crewRv.setLayoutManager(crewLayoutManager);
        binding.content.crewRv.setHasFixedSize(true);
        binding.content.crewRv.setAdapter(crewRecyclerViewAdapter);
        loadPersonData();
    }

    private void loadPersonData() {
        //name, birth day and birth place, age
        setText(binding.content.intro.nameTv, person.getName());
        setText(binding.content.intro.birthdayTv, UxUtils.getFormattedDate(person.getBirthday()));
        if (person.getBirthday() == null) {
            //birthday not available
            binding.content.intro.ageTv.setText(getString(R.string.age_value,
                    getString(R.string.not_available_short)));
        } else {
            //calculate age
            int age;
            if (person.getDeathday() == null) {
                //still living
                binding.content.intro.deathTv.setVisibility(View.INVISIBLE);
                age = UxUtils.getAge(person.getBirthday(), null);
            } else {
                //death
                binding.content.intro.deathLabel.setVisibility(View.VISIBLE);
                setText(binding.content.intro.deathTv, UxUtils.getFormattedDate(person.getDeathday()));
                age = UxUtils.getAge(person.getBirthday(), person.getDeathday());
            }
            binding.content.intro.ageTv.setText(getString(R.string.age_value, String.valueOf(age)));
        }
        setText(binding.content.intro.placeTv, person.getPlaceOfBirth());
        //person image
        if (person.getProfilePic() != null) {
            NetworkUtils.downloadImage(binding.content.intro.profileIv, person.getProfilePic(),
                    R.drawable.ic_person_shape, R.drawable.ic_person_shape);
        }
        //bio
        if (person.getBio() == null || person.getBio().isEmpty()) {
            binding.content.bio.bioEx.setText(getString(R.string.not_available_short));
        } else {
            binding.content.bio.bioEx.setText(person.getBio());
        }
        binding.loadingBar.setVisibility(View.INVISIBLE);
    }

    private void setText(TextView targetView, String text) {
        if (text == null || text.trim().isEmpty()) {
            targetView.setText(getString(R.string.not_available_short));
        } else {
            targetView.setText(text);
        }
    }

    private void openMediaDetails(int mediaId, String mediaType) {
        if (mediaType.equals(TmdbClient.MOVIE_MODE)) {
            openMovieDetails(mediaId);
        } else if (mediaType.equals(TmdbClient.TV_MODE)) {
            openTvShowDetails(mediaId);
        } else {
            UxUtils.showToast(this, getString(R.string.unable_to_open_media));
        }
    }

    private void openTvShowDetails(int tvShowId) {
        Intent openTvShowIntent = new Intent(PersonDetailActivity.this,
                TvShowDetailActivity.class);
        openTvShowIntent.putExtra(TvShow.TV_SHOW_KEY, tvShowId);
        startActivity(openTvShowIntent);
    }

    private void openMovieDetails(int movieId) {
        Intent openMovieIntent = new Intent(PersonDetailActivity.this,
                MovieDetailActivity.class);
        openMovieIntent.putExtra(Movie.MOVIE_KEY, movieId);
        startActivity(openMovieIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_settings:
                //go to home
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_add_to_linked_list:
                addToLinkedList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addToLinkedList() {
        //new linked person object
        LinkedPerson linkedPerson = new LinkedPerson(person.getProfilePic(),
                person.getPersonId(), person.getName());
        //reading from preference
        ArrayList<LinkedPerson> linkedPeople = UxUtils.getLinkedPeopleFromPreference(this);
        if (linkedPeople == null) {
            //no previous linked list found in preference - creating new one
            linkedPeople = new ArrayList<>();
            linkedPeople.add(linkedPerson);
            saveLinkedListInPreference(linkedPeople);
        } else {
            //linked list found in preference - avoid duplication
            if (!linkedPeople.contains(linkedPerson)) {
                //add person
                linkedPeople.add(linkedPerson);
                saveLinkedListInPreference(linkedPeople);
            } else {
                //current person found
                UxUtils.showToast(this, getString(R.string.person_already_exists));
            }
        }
    }

    private void saveLinkedListInPreference(ArrayList<LinkedPerson> linkedPeople) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                this);
        int linkedListSize = linkedPeople.size();
        String jsonLinkedList = new Gson().toJson(linkedPeople);
        String linkedListKey = getString(R.string.linked_list_key);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(linkedListKey, jsonLinkedList);
        editor.apply();
        //show view
        binding.toolbar.linkedTv.setText(String.valueOf(linkedListSize));
        binding.toolbar.linkedTv.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        UxUtils.setMenu(this, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search_view).getActionView();
        ComponentName cn = new ComponentName(this, ManualSearchActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
        return true;
    }

    public void linkPeople(View view) {
        ArrayList<LinkedPerson> people = UxUtils.getLinkedPeopleFromPreference(this);
        if (people == null || people.isEmpty()) {
            UxUtils.showToast(this, getString(R.string.unable_to_link_people));
        } else if (people.size() == 1) {
            UxUtils.showToast(this, getString(R.string.need_more_people));
        } else {
            Intent intent = new Intent(this, LinkedPeopleActivity.class);
            startActivity(intent);
        }
    }
}
