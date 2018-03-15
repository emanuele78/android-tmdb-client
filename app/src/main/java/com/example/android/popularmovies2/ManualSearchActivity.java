package com.example.android.popularmovies2;

import android.app.SearchManager;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.popularmovies2.databinding.ActivityManualSearchBinding;
import com.example.android.popularmovies2.model.LinkedPerson;
import com.example.android.popularmovies2.model.Movie;
import com.example.android.popularmovies2.model.OnItemClick;
import com.example.android.popularmovies2.model.Person;
import com.example.android.popularmovies2.model.TvShow;
import com.example.android.popularmovies2.model.Video;
import com.example.android.popularmovies2.model.VideoList;
import com.example.android.popularmovies2.network.NetworkUtils;
import com.example.android.popularmovies2.network.TmdbClient;
import com.example.android.popularmovies2.ux.GenericRecyclerViewAdapter;
import com.example.android.popularmovies2.ux.UxUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManualSearchActivity extends AppCompatActivity {

    private String loadMoreLabel;
    private ActivityManualSearchBinding binding;
    private String userQuery;
    private GenericRecyclerViewAdapter movieAdapter;
    private GenericRecyclerViewAdapter tvShowAdapter;
    private GenericRecyclerViewAdapter peopleAdapter;
    private String preferredContentLang;
    private static final String MOVIE_BUNDLE_KEY = "movie_bundle_key";
    private static final String TV_BUNDLE_KEY = "tv_bundle_key";
    private static final String PEOPLE_BUNDLE_KEY = "people_bundle_key";
    private static final int LOAD_MORE_ID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_search);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manual_search);
        Intent intent = getIntent();
        if (!Intent.ACTION_SEARCH.equals(intent.getAction())) {
            UxUtils.showToast(this, getString(R.string.unable_to_perform_search));
            finish();
            return;
        }
        userQuery = intent.getStringExtra(SearchManager.QUERY);
        binding.content.userTextTv.setText(userQuery);
        setSupportActionBar(binding.toolbar.bar);
        binding.toolbar.toolbarTitleTv.setText(getString(R.string.search_result_title));
        String emptyString = getString(R.string.not_available_short);
        loadMoreLabel = getString(R.string.load_more_label);
        preferredContentLang = UxUtils.getContentLangFromPreference(this);
        //setting movie recycler view
        movieAdapter = new GenericRecyclerViewAdapter(TmdbClient.MOVIE_MODE, emptyString,
                new OnItemClick() {
                    @Override
                    public void onClick(int itemId) {
                        openMovieDetail(itemId);
                    }
                });
        LinearLayoutManager movieLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        settingUpRecyclerView(binding.content.movieRv, movieLayoutManager, movieAdapter);
        //setting tv show recycler view
        tvShowAdapter = new GenericRecyclerViewAdapter(TmdbClient.TV_MODE, emptyString,
                new OnItemClick() {
                    @Override
                    public void onClick(int itemId) {
                        openTvShowDetail(itemId);
                    }
                });
        LinearLayoutManager tvShowLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        settingUpRecyclerView(binding.content.tvshowRv, tvShowLayoutManager, tvShowAdapter);
        //setting people recycler view
        peopleAdapter = new GenericRecyclerViewAdapter(TmdbClient.PEOPLE_MODE, emptyString,
                new OnItemClick() {
                    @Override
                    public void onClick(int itemId) {
                        openPersonDetail(itemId);
                    }
                });
        LinearLayoutManager peopleLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        settingUpRecyclerView(binding.content.peopleRv, peopleLayoutManager, peopleAdapter);
        checkInstance(savedInstanceState);
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

    private void checkInstance(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            preferredContentLang = savedInstanceState.getString(UxUtils.CONTENT_LANG_KEY);
            ArrayList<Video> movie = savedInstanceState.getParcelableArrayList(MOVIE_BUNDLE_KEY);
            movieAdapter.setData(movie, loadMoreLabel);
            ArrayList<Video> tvShow = savedInstanceState.getParcelableArrayList(TV_BUNDLE_KEY);
            tvShowAdapter.setData(tvShow, loadMoreLabel);
            ArrayList<Video> people = savedInstanceState.getParcelableArrayList(PEOPLE_BUNDLE_KEY);
            peopleAdapter.setData(people, loadMoreLabel);
            binding.content.moviePb.setVisibility(View.INVISIBLE);
            binding.content.tvshowPb.setVisibility(View.INVISIBLE);
            binding.content.peoplePb.setVisibility(View.INVISIBLE);
        } else {
            loadMovies();
            loadTvShows();
            loadPeople();
        }
    }

    public void goToHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        UxUtils.setMenu(this, menu);
        return true;
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        readLinkedListPreference();
        String contentLangValue = UxUtils.getContentLangFromPreference(this);
        if (!preferredContentLang.equals(contentLangValue)) {
            preferredContentLang = contentLangValue;
            loadMovies();
            loadTvShows();
            loadPeople();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_BUNDLE_KEY, movieAdapter.getData());
        outState.putParcelableArrayList(TV_BUNDLE_KEY, tvShowAdapter.getData());
        outState.putParcelableArrayList(PEOPLE_BUNDLE_KEY, peopleAdapter.getData());
        outState.putString(UxUtils.CONTENT_LANG_KEY, preferredContentLang);
    }

    private void settingUpRecyclerView(RecyclerView recyclerView, RecyclerView.LayoutManager manager,
                                       RecyclerView.Adapter adapter) {
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void loadMovies() {
        NetworkUtils.doNetworkCallForManualSearch(TmdbClient.MOVIE_MODE, preferredContentLang,
                userQuery, TmdbClient.STARTING_PAGE_INDEX, new Callback<VideoList>() {
                    @Override
                    public void onResponse(Call<VideoList> call, Response<VideoList> response) {
                        if (response.isSuccessful()) {
                            VideoList videoList = response.body();
                            ArrayList<Video> list = videoList.getVideos();
                            //replacing last element
                            if (videoList.getTotalPages() > TmdbClient.STARTING_PAGE_INDEX) {
                                list.set(list.size() - 1, new Video(loadMoreLabel));
                            }
                            movieAdapter.setData(list, loadMoreLabel);
                            binding.content.moviePb.setVisibility(View.INVISIBLE);
                        } else {
                            UxUtils.showToast(ManualSearchActivity.this,
                                    getString(R.string.unable_to_load_movies));
                            binding.content.moviePb.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<VideoList> call, Throwable t) {
                        UxUtils.showToast(ManualSearchActivity.this,
                                getString(R.string.unable_to_load_movies));
                        binding.content.moviePb.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void loadTvShows() {
        NetworkUtils.doNetworkCallForManualSearch(TmdbClient.TV_MODE, preferredContentLang,
                userQuery, TmdbClient.STARTING_PAGE_INDEX, new Callback<VideoList>() {
                    @Override
                    public void onResponse(Call<VideoList> call, Response<VideoList> response) {
                        if (response.isSuccessful()) {
                            VideoList videoList = response.body();
                            ArrayList<Video> list = videoList.getVideos();
                            //replacing last element
                            if (videoList.getTotalPages() > TmdbClient.STARTING_PAGE_INDEX) {
                                list.set(list.size() - 1, new Video(loadMoreLabel));
                            }
                            tvShowAdapter.setData(list, loadMoreLabel);
                            binding.content.tvshowPb.setVisibility(View.INVISIBLE);
                        } else {
                            UxUtils.showToast(ManualSearchActivity.this,
                                    getString(R.string.unable_to_load_movies));
                            binding.content.tvshowPb.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<VideoList> call, Throwable t) {
                        if (NetworkUtils.isDeviceConnected(ManualSearchActivity.this)) {
                            UxUtils.showToast(ManualSearchActivity.this,
                                    getString(R.string.unable_to_load_movies));
                        } else {
                            UxUtils.showSnackbar(getString(R.string.no_internet_connection),
                                    binding.content.movieRv);
                        }
                        binding.content.tvshowPb.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void loadPeople() {
        NetworkUtils.doNetworkCallForManualSearch(TmdbClient.PEOPLE_MODE, preferredContentLang,
                userQuery, TmdbClient.STARTING_PAGE_INDEX, new Callback<VideoList>() {
                    @Override
                    public void onResponse(Call<VideoList> call, Response<VideoList> response) {
                        if (response.isSuccessful()) {
                            VideoList videoList = response.body();
                            ArrayList<Video> list = videoList.getVideos();
                            //replacing last element
                            if (videoList.getTotalPages() > TmdbClient.STARTING_PAGE_INDEX) {
                                list.set(list.size() - 1, new Video(loadMoreLabel));
                            }
                            peopleAdapter.setData(list, loadMoreLabel);
                            binding.content.peoplePb.setVisibility(View.INVISIBLE);
                        } else {
                            UxUtils.showToast(ManualSearchActivity.this,
                                    getString(R.string.unable_to_load_movies));
                            binding.content.peoplePb.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<VideoList> call, Throwable t) {
                        UxUtils.showToast(ManualSearchActivity.this,
                                getString(R.string.unable_to_load_movies));
                        binding.content.peoplePb.setVisibility(View.INVISIBLE);
                    }
                });
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

    private void openMovieDetail(int itemId) {
        if (itemId == LOAD_MORE_ID) {
            Intent searchMovieByKeyword = new Intent(this, SearchResultActivity.class);
            searchMovieByKeyword.putExtra(UxUtils.TAG_KEY, UxUtils.KEYWORD_TAG);
            searchMovieByKeyword.putExtra(UxUtils.SEARCH_MODE, TmdbClient.MOVIE_MODE);
            searchMovieByKeyword.putExtra(UxUtils.EXTRA_STRING_VALUE_KEY, userQuery);
            startActivity(searchMovieByKeyword);
        } else {
            Intent openMovieIntent = new Intent(this, MovieDetailActivity.class);
            openMovieIntent.putExtra(Movie.MOVIE_KEY, itemId);
            startActivity(openMovieIntent);
        }
    }

    private void openTvShowDetail(int itemId) {
        if (itemId == LOAD_MORE_ID) {
            Intent searchTvShowByKeyword = new Intent(this, SearchResultActivity.class);
            searchTvShowByKeyword.putExtra(UxUtils.TAG_KEY, UxUtils.KEYWORD_TAG);
            searchTvShowByKeyword.putExtra(UxUtils.SEARCH_MODE, TmdbClient.TV_MODE);
            searchTvShowByKeyword.putExtra(UxUtils.EXTRA_STRING_VALUE_KEY, userQuery);
            startActivity(searchTvShowByKeyword);
        } else {
            Intent openTvShowIntent = new Intent(this, TvShowDetailActivity.class);
            openTvShowIntent.putExtra(TvShow.TV_SHOW_KEY, itemId);
            startActivity(openTvShowIntent);
        }
    }

    private void openPersonDetail(int itemId) {
        if (itemId == LOAD_MORE_ID) {
            Intent searchPersonByKeyword = new Intent(this, SearchResultActivity.class);
            searchPersonByKeyword.putExtra(UxUtils.TAG_KEY, UxUtils.KEYWORD_TAG);
            searchPersonByKeyword.putExtra(UxUtils.SEARCH_MODE, TmdbClient.PEOPLE_MODE);
            searchPersonByKeyword.putExtra(UxUtils.EXTRA_STRING_VALUE_KEY, userQuery);
            startActivity(searchPersonByKeyword);
        } else {
            Intent openPersonIntent = new Intent(this, PersonDetailActivity.class);
            openPersonIntent.putExtra(Person.PERSON_KEY, itemId);
            startActivity(openPersonIntent);
        }
    }
}
