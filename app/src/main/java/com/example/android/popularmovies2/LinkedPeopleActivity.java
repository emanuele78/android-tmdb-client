package com.example.android.popularmovies2;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.popularmovies2.databinding.ActivityLinkedPeopleBinding;
import com.example.android.popularmovies2.model.LinkedPerson;
import com.example.android.popularmovies2.model.Movie;
import com.example.android.popularmovies2.model.Person;
import com.example.android.popularmovies2.model.VideoList;
import com.example.android.popularmovies2.network.NetworkUtils;
import com.example.android.popularmovies2.network.TmdbClient;
import com.example.android.popularmovies2.ux.EndlessRecyclerViewScrollListener;
import com.example.android.popularmovies2.ux.GenericLinkedPeopleAdapter;
import com.example.android.popularmovies2.ux.LinkedPeopleAdapter;
import com.example.android.popularmovies2.ux.UxUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LinkedPeopleActivity extends AppCompatActivity {

    private GenericLinkedPeopleAdapter movieAdapter;
    private SearchView searchView;
    private int movieLastPage;
    private int movieCurrentPage;
    private int movieItemCount;
    private ArrayList<LinkedPerson> people;
    private ActivityLinkedPeopleBinding binding;
    private String preferredContentLang;
    private LinkedPeopleAdapter peopleAdapter;
    private EndlessRecyclerViewScrollListener movieScrollListener;
    private static final int MIN_PEOPLE_TO_COMPARE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linked_people);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_linked_people);
        setSupportActionBar(binding.toolbar.bar);
        people = UxUtils.getLinkedPeopleFromPreference(this);
        if (people == null || people.size() < MIN_PEOPLE_TO_COMPARE) {
            UxUtils.showToast(this, getString(R.string.unable_to_link_people));
            finish();
            return;
        }
        preferredContentLang = UxUtils.getContentLangFromPreference(this);
        binding.content.labelTv.setText(getString(R.string.people_to_link_label, people.size()));
        binding.toolbar.toolbarTitleTv.setText(getString(R.string.linked_people_title));
        //linked people recycler view
        peopleAdapter = new LinkedPeopleAdapter(people, new LinkedPeopleAdapter.OnPersonItemClick() {
            @Override
            public void onClick(int personId, int adapterPosition) {
                showItemDialog(personId, adapterPosition);
            }

            @Override
            public void onRemove(int personId) {
                personRemovedFromLikedList();
            }
        });
        LinearLayoutManager peopleLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        settingUpRecyclerView(binding.content.peopleRv, peopleLayoutManager, peopleAdapter, null);
        ItemTouchHelper touchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        peopleAdapter.remove(viewHolder.getAdapterPosition());
                    }
                });
        touchHelper.attachToRecyclerView(binding.content.peopleRv);
        String emptyString = getString(R.string.not_available_short);
        //setting movie recycler view
        movieAdapter = new GenericLinkedPeopleAdapter(emptyString, new GenericLinkedPeopleAdapter.ListItemClickListener() {
            @Override
            public void onListItemClick(int itemId) {
                openMovieDetail(itemId);
            }
        });
        LinearLayoutManager movieLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        movieScrollListener = new EndlessRecyclerViewScrollListener(movieLayoutManager,
                TmdbClient.STARTING_PAGE_INDEX, TmdbClient.STARTING_PAGE_INDEX) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (page <= movieLastPage) {
                    doNetworkCall(page);
                }
            }

            @Override
            public void onScrolling(int lastPosition) {
            }
        };
        settingUpRecyclerView(binding.content.movieRv, movieLayoutManager, movieAdapter,
                movieScrollListener);
        doNetworkCall(TmdbClient.STARTING_PAGE_INDEX);
    }

    private String getLinkedPeopleStringFromList(ArrayList<LinkedPerson> people) {
        String separator = ",";
        String list = null;
        for (LinkedPerson linkedPerson : people) {
            if (list == null) {
                list = String.valueOf(linkedPerson.getPersonId());
            } else {
                list = list.concat(separator).concat(String.valueOf(linkedPerson.getPersonId()));
            }
        }
        return list;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (searchView != null && !searchView.isIconified()) {
            super.onBackPressed();
        }
        boolean updateResults = false;
        //check for changed linked people list
        ArrayList<LinkedPerson> linkedPeople = UxUtils.getLinkedPeopleFromPreference(this);
        if (linkedPeople == null || linkedPeople.size() < MIN_PEOPLE_TO_COMPARE) {
            UxUtils.showToast(this, getString(R.string.need_more_people));
            movieAdapter.setData(null);
            return;
        } else {
            Collections.sort(linkedPeople);
            Collections.sort(this.people);
            if (!this.people.equals(linkedPeople)) {
                people = linkedPeople;
                peopleAdapter.setNewData(linkedPeople);
                updateResults = true;
            }
        }
        //check for changed language
        String contentLangValue = UxUtils.getContentLangFromPreference(this);
        if (!preferredContentLang.equals(contentLangValue)) {
            preferredContentLang = contentLangValue;
            movieScrollListener.resetState();
            updateResults = true;
        }
        if (updateResults) {
            doNetworkCall(TmdbClient.STARTING_PAGE_INDEX);
        }
    }

    private void settingUpRecyclerView(RecyclerView recyclerView,
                                       RecyclerView.LayoutManager manager,
                                       RecyclerView.Adapter adapter,
                                       EndlessRecyclerViewScrollListener scrollListener) {
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        if (scrollListener != null) {
            recyclerView.addOnScrollListener(scrollListener);
        }
    }

    private void doNetworkCall(int page) {
        binding.content.moviePb.setVisibility(View.VISIBLE);
        String linkedPeople = getLinkedPeopleStringFromList(this.people);
        NetworkUtils.doNetworkCallForlinkedPeople(page, TmdbClient.MOVIE_MODE, preferredContentLang,
                linkedPeople, new Callback<VideoList>() {
                    @Override
                    public void onResponse(Call<VideoList> call, Response<VideoList> response) {
                        binding.content.moviePb.setVisibility(View.INVISIBLE);
                        if (response.isSuccessful()) {
                            VideoList list = response.body();
                            movieCurrentPage = list.getCurrentPage();
                            movieLastPage = list.getTotalPages();
                            movieItemCount = list.getTotalMovies();
                            if (movieCurrentPage == UxUtils.STARTING_PAGE) {
                                movieAdapter.setData(list.getVideos());
                            } else {
                                movieAdapter.appendData(list.getVideos());
                            }
                            binding.content.movieResultLabel.setText(
                                    getString(R.string.result_for_movie_with_people_label, movieItemCount));
                        } else {
                            UxUtils.showToast(LinkedPeopleActivity.this,
                                    getString(R.string.server_error));
                        }
                    }

                    @Override
                    public void onFailure(Call<VideoList> call, Throwable t) {
                        UxUtils.showToast(LinkedPeopleActivity.this,
                                getString(R.string.unable_to_load_movies));
                        binding.content.moviePb.setVisibility(View.INVISIBLE);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        UxUtils.setMenu(this, menu);
        //search view
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search_view).getActionView();
        ComponentName cn = new ComponentName(this, ManualSearchActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openMovieDetail(int movieId) {
        Intent openMovieIntent = new Intent(this, MovieDetailActivity.class);
        openMovieIntent.putExtra(Movie.MOVIE_KEY, movieId);
        startActivity(openMovieIntent);
    }

    private void showItemDialog(final int personId, final int adapterPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog_theme);
        builder.setMessage(R.string.what_do_u_want_to_do);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setNeutralButton(R.string.open_details, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent openPersonIntent = new Intent(LinkedPeopleActivity.this,
                        PersonDetailActivity.class);
                openPersonIntent.putExtra(Person.PERSON_KEY, personId);
                startActivity(openPersonIntent);
            }
        });
        builder.setPositiveButton(R.string.remove_item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                peopleAdapter.remove(adapterPosition);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void goToHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void personRemovedFromLikedList() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String linkedListKey = getString(R.string.linked_list_key);
        if (this.people.isEmpty()) {
            editor.remove(linkedListKey);
        } else {
            String jsonLinkedList = new Gson().toJson(this.people);
            editor.putString(linkedListKey, jsonLinkedList);
        }
        editor.apply();
        peopleAdapter.setNewData(this.people);
        binding.content.labelTv.setText(getString(R.string.people_to_link_label, people.size()));
        if (people.size() < MIN_PEOPLE_TO_COMPARE) {
            UxUtils.showToast(this, getString(R.string.need_more_people));
            movieAdapter.setData(null);
        } else {
            doNetworkCall(TmdbClient.STARTING_PAGE_INDEX);
        }
    }
}
