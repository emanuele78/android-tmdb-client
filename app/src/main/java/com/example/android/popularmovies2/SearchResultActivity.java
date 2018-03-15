package com.example.android.popularmovies2;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.popularmovies2.databinding.ActivitySearchResultsBinding;
import com.example.android.popularmovies2.model.LinkedPerson;
import com.example.android.popularmovies2.model.Movie;
import com.example.android.popularmovies2.model.Person;
import com.example.android.popularmovies2.model.TvShow;
import com.example.android.popularmovies2.model.Video;
import com.example.android.popularmovies2.model.VideoList;
import com.example.android.popularmovies2.network.NetworkUtils;
import com.example.android.popularmovies2.network.TmdbClient;
import com.example.android.popularmovies2.ux.EndlessRecyclerViewScrollListener;
import com.example.android.popularmovies2.ux.GridSpacingDecoration;
import com.example.android.popularmovies2.ux.NetworkBroadcastReceiver;
import com.example.android.popularmovies2.ux.SearchResultAdapter;
import com.example.android.popularmovies2.ux.UxUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultActivity extends AppCompatActivity
        implements Callback<VideoList>,
        SearchResultAdapter.ListItemClickListener, NetworkBroadcastReceiver.OnNetworkStatusChange {

    private ActivitySearchResultsBinding binding;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private int itemCount;
    private int mLastPage;
    private SearchView searchView;
    private String preferredContentLang;
    private SearchResultAdapter mListAdapter;
    @TmdbClient.WorkingMode
    private String mWorkingMode;
    @UxUtils.Tag
    private String tag;
    private int tagValue;
    private NetworkBroadcastReceiver broadcastReceiver;
    private IntentFilter networkFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    private final static int NO_VALUE = -1;
    private String extraStringValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        //binding reference
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_results);
        //setting toolbar
        setSupportActionBar(binding.toolbar.bar);
        binding.toolbar.toolbarTitleTv.setText(getString(R.string.search_result_title));
        //setting and registering receiver
        broadcastReceiver = new NetworkBroadcastReceiver(this);
        registerReceiver(broadcastReceiver, networkFilter);
        preferredContentLang = UxUtils.getContentLangFromPreference(this);
        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        } else {
            //reading extra
            Intent intent = getIntent();
            mWorkingMode = intent.getStringExtra(UxUtils.SEARCH_MODE);
            tag = intent.getStringExtra(UxUtils.TAG_KEY);
            tagValue = intent.getIntExtra(UxUtils.TAG_VALUE_KEY, NO_VALUE);
            if (intent.hasExtra(UxUtils.EXTRA_STRING_VALUE_KEY)) {
                extraStringValue = intent.getStringExtra(UxUtils.EXTRA_STRING_VALUE_KEY);
            }
            settingUpRecyclerView(binding.content.mainListRecyclerView,
                    TmdbClient.STARTING_PAGE_INDEX);
            if (NetworkUtils.isDeviceConnected(this)) {
                doNetworkCall(UxUtils.STARTING_PAGE);
            } else {
                checkForEmptyList();
                UxUtils.showSnackbar(getString(R.string.no_internet_connection),
                        binding.content.upperBarImage);
            }
        }
        settingTitle();
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

    private void restoreInstanceState(Bundle savedInstanceState) {
        ArrayList<Video> videos = savedInstanceState.getParcelableArrayList(UxUtils.RECYCLER_VIEW_DATA_KEY);
        int position = savedInstanceState.getInt(UxUtils.RECYCLER_VIEW_POSITION_KEY);
        int pagesLoaded = savedInstanceState.getInt(UxUtils.PAGES_LOADED_KEY);
        mLastPage = savedInstanceState.getInt(UxUtils.LAST_PAGE_ON_SERVER_KEY);
        mWorkingMode = savedInstanceState.getString(UxUtils.CURRENT_MODE_KEY);
        tag = savedInstanceState.getString(UxUtils.TAG_KEY);
        tagValue = savedInstanceState.getInt(UxUtils.TAG_VALUE_KEY);
        extraStringValue = savedInstanceState.getString(UxUtils.EXTRA_STRING_VALUE_KEY);
        itemCount = savedInstanceState.getInt(UxUtils.TOTAL_ITEM_KEY);
        preferredContentLang = savedInstanceState.getString(UxUtils.CONTENT_LANG_KEY);
        //setting recycler view
        settingUpRecyclerView(binding.content.mainListRecyclerView, pagesLoaded);
        mListAdapter.setData(videos);
        //move to previous position
        binding.content.mainListRecyclerView.scrollToPosition(position);
        checkForEmptyList();
    }

    private void settingUpRecyclerView(RecyclerView recyclerView, int pagesLoaded) {
        int columns = UxUtils.getColumnNumber(this);
        int totalSpace = UxUtils.getAvailableBlankSpace(this, columns);
        int space = totalSpace / (columns + 1);
        GridSpacingDecoration itemDecoration = new GridSpacingDecoration(columns, space, true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, columns,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        //decorator for custom spacing
        recyclerView.addItemDecoration(itemDecoration);
        //setting scroll listener
        mScrollListener = new EndlessRecyclerViewScrollListener(layoutManager,
                TmdbClient.STARTING_PAGE_INDEX, pagesLoaded) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (page <= mLastPage) {
                    Log.d("MYTAG", "onLoadMore - call doNetworkCall");
                    doNetworkCall(page);
                }
            }

            @Override
            public void onScrolling(int lastPosition) {
                showPosition(lastPosition);
            }
        };
        recyclerView.addOnScrollListener(mScrollListener);
        mListAdapter = new SearchResultAdapter(this,
                getString(R.string.not_available_short), mWorkingMode);
        recyclerView.setAdapter(mListAdapter);
    }

    public void goToHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void checkForEmptyList() {
        if (mListAdapter.getItemCount() == 0) {
            binding.content.emptyView.emptyViewLayout.setVisibility(View.VISIBLE);
        } else {
            binding.content.emptyView.emptyViewLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(UxUtils.RECYCLER_VIEW_POSITION_KEY, mListAdapter.getCurrentPosition());
        outState.putInt(UxUtils.PAGES_LOADED_KEY, mScrollListener.getCurrentPage());
        outState.putInt(UxUtils.LAST_PAGE_ON_SERVER_KEY, mLastPage);
        outState.putParcelableArrayList(UxUtils.RECYCLER_VIEW_DATA_KEY, mListAdapter.getData());
        outState.putString(UxUtils.CURRENT_MODE_KEY, mWorkingMode);
        outState.putInt(UxUtils.TOTAL_ITEM_KEY, itemCount);
        outState.putString(UxUtils.TAG_KEY, tag);
        outState.putInt(UxUtils.TAG_VALUE_KEY, tagValue);
        outState.putString(UxUtils.EXTRA_STRING_VALUE_KEY, extraStringValue);
        outState.putString(UxUtils.CONTENT_LANG_KEY, preferredContentLang);
    }

    private void doNetworkCall(int page) {
        Log.d("MYTAG", "doNetworkCall");
        binding.loadingBar.setVisibility(View.VISIBLE);
        switch (tag) {
            case UxUtils.RATING_TAG:
                NetworkUtils.doNetworkCallForSearch(page, mWorkingMode, tag, 0, 0,
                        tagValue, 0, 0, preferredContentLang, this);
                break;
            case UxUtils.RUNTIME_TAG:
                NetworkUtils.doNetworkCallForSearch(page, mWorkingMode, tag, tagValue, 0,
                        0, 0, 0, preferredContentLang, this);
                break;
            case UxUtils.GENRE_TAG:
                NetworkUtils.doNetworkCallForSearch(page, mWorkingMode, tag, 0, tagValue,
                        0, 0, 0, preferredContentLang, this);
                break;
            case UxUtils.YEAR_TAG:
                NetworkUtils.doNetworkCallForSearch(page, mWorkingMode, tag, 0, 0,
                        0, tagValue, 0, preferredContentLang, this);
                break;
            case UxUtils.COMPANY_TAG:
                NetworkUtils.doNetworkCallForSearch(page, mWorkingMode, tag, 0, 0,
                        0, 0, tagValue, preferredContentLang, this);
                break;
            case UxUtils.SIMILAR_TAG:
                NetworkUtils.doNetworkCallForSimilar(tagValue, mWorkingMode, preferredContentLang,
                        page, this);
                break;
            case UxUtils.KEYWORD_TAG:
                NetworkUtils.doNetworkCallForManualSearch(mWorkingMode, preferredContentLang,
                        extraStringValue, page, this);
                break;
        }
    }

    private void settingTitle() {
        String title;
        final String space = " ";
        switch (mWorkingMode) {
            case TmdbClient.MOVIE_MODE:
                title = getString(R.string.movie_with_label).concat(space);
                break;
            case TmdbClient.PEOPLE_MODE:
                title = getString(R.string.people_with_label).concat(space);
                break;
            case TmdbClient.TV_MODE:
                title = getString(R.string.tv_show_with_label).concat(space);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        switch (tag) {
            case UxUtils.COMPANY_TAG:
                title = title.concat(getString(R.string.production_company_equals_to));
                binding.content.modeTv.setText(extraStringValue);
                break;
            case UxUtils.YEAR_TAG:
                title = title.concat(getString(R.string.release_year_equals_to));
                binding.content.modeTv.setText(String.valueOf(tagValue));
                break;
            case UxUtils.RATING_TAG:
                title = title.concat(getString(R.string.rating_greater_than));
                binding.content.modeTv.setText(String.valueOf(tagValue));
                break;
            case UxUtils.GENRE_TAG:
                title = title.concat(getString(R.string.genre_equals_to));
                binding.content.modeTv.setText(extraStringValue);
                break;
            case UxUtils.SIMILAR_TAG:
                if (mWorkingMode.equals(TmdbClient.MOVIE_MODE)) {
                    title = getString(R.string.movies_similar_to);
                } else {
                    title = getString(R.string.tvshows_similar_to);
                }
                binding.content.modeTv.setText(extraStringValue);
                break;
            case UxUtils.RUNTIME_TAG:
                title = title.concat(getString(R.string.runtime_equals_to));
                binding.content.modeTv.setText(String.valueOf(tagValue));
                break;
            case UxUtils.KEYWORD_TAG:
                title = title.concat(getString(R.string.keyword));
                binding.content.modeTv.setText(extraStringValue);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        binding.content.labelTv.setText(title);
    }

    private void showPosition(int currentLastPosition) {
        String paging = ++currentLastPosition + "/" + itemCount;
        binding.content.itemCountTv.setText(paging);
    }

    @Override
    public void onListItemClick(int position) {
        int itemId = mListAdapter.getData().get(position).getVideoId();
        if (mWorkingMode.equals(TmdbClient.MOVIE_MODE)) {
            openMovieDetail(itemId);
        } else if (mWorkingMode.equals(TmdbClient.TV_MODE)) {
            openTvShowDetail(itemId);
        } else {
            openPersonDetail(itemId);
        }
    }

    private void openTvShowDetail(int tvShowId) {
        Intent openTvShowIntent = new Intent(this, TvShowDetailActivity.class);
        openTvShowIntent.putExtra(TvShow.TV_SHOW_KEY, tvShowId);
        startActivity(openTvShowIntent);
    }

    private void openMovieDetail(int movieId) {
        Intent openMovieIntent = new Intent(this, MovieDetailActivity.class);
        openMovieIntent.putExtra(Movie.MOVIE_KEY, movieId);
        startActivity(openMovieIntent);
    }

    private void openPersonDetail(int personId) {
        Intent openPersonIntent = new Intent(this, PersonDetailActivity.class);
        openPersonIntent.putExtra(Person.PERSON_KEY, personId);
        startActivity(openPersonIntent);
    }

    @Override
    public void onResponse(Call<VideoList> call, Response<VideoList> response) {
        binding.loadingBar.setVisibility(View.INVISIBLE);
        if (response.isSuccessful()) {
            VideoList list = response.body();
            int currentPage = list.getCurrentPage();
            mLastPage = list.getTotalPages();
            itemCount = list.getTotalMovies();
            if (currentPage == UxUtils.STARTING_PAGE) {
                mListAdapter.setData(list.getVideos());
            } else {
                mListAdapter.appendData(list.getVideos());
            }
        } else {
            UxUtils.showToast(this, getString(R.string.server_error));
        }
        checkForEmptyList();
    }

    @Override
    public void onFailure(Call<VideoList> call, Throwable t) {
        binding.loadingBar.setVisibility(View.INVISIBLE);
        //connection error
        if (NetworkUtils.isDeviceConnected(this)) {
            UxUtils.showToast(this, getString(R.string.connection_error));
        } else {
            UxUtils.showSnackbar(getString(R.string.no_internet_connection),
                    binding.content.mainListRecyclerView);
        }
        checkForEmptyList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (searchView != null && !searchView.isIconified()) {
            invalidateOptionsMenu();
        }
        readLinkedListPreference();
        registerReceiver(broadcastReceiver, networkFilter);
        String contentLangValue = UxUtils.getContentLangFromPreference(this);
        if (!preferredContentLang.equals(contentLangValue)) {
            preferredContentLang = contentLangValue;
            doNetworkCall(UxUtils.STARTING_PAGE);
        }
    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        if (!isConnected) {
            String message = getString(R.string.no_internet_connection);
            UxUtils.showSnackbar(message, binding.content.mainListRecyclerView);
        } else {
            mScrollListener.resetLoading();
        }
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
