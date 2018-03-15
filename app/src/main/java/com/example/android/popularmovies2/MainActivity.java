package com.example.android.popularmovies2;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.popularmovies2.data.DbUtils;
import com.example.android.popularmovies2.data.PopularMoviesContract;
import com.example.android.popularmovies2.databinding.ActivityMainBinding;
import com.example.android.popularmovies2.model.LinkedPerson;
import com.example.android.popularmovies2.model.Movie;
import com.example.android.popularmovies2.model.TvShow;
import com.example.android.popularmovies2.model.Video;
import com.example.android.popularmovies2.model.VideoList;
import com.example.android.popularmovies2.network.NetworkUtils;
import com.example.android.popularmovies2.network.TmdbClient;
import com.example.android.popularmovies2.ux.EndlessRecyclerViewScrollListener;
import com.example.android.popularmovies2.ux.FavoritesCursorAdapter;
import com.example.android.popularmovies2.ux.GridSpacingDecoration;
import com.example.android.popularmovies2.ux.MainListAdapter;
import com.example.android.popularmovies2.ux.NetworkBroadcastReceiver;
import com.example.android.popularmovies2.ux.UxUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Callback<VideoList>,
        MainListAdapter.ListItemClickListener, NetworkBroadcastReceiver.OnNetworkStatusChange,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String PREFERRED_MODE_KEY = "preferred_mode_key";
    private final int ITEMS_LOADER = 1;
    private FavoritesCursorAdapter cursorAdapter;
    private MainListAdapter mListAdapter;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private int itemCount;
    private int mLastPage;
    private int mCurrentPage;
    private SearchView searchView;
    private String mPreferredLanguage;
    @TmdbClient.WorkingMode
    private String mWorkingMode;
    @TmdbClient.WorkingMode
    private String mSortingMode;
    private NetworkBroadcastReceiver broadcastReceiver;
    private IntentFilter networkFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    private ActivityMainBinding binding;
    private boolean preferredMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //binding reference
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        //setting and registering receiver
        broadcastReceiver = new NetworkBroadcastReceiver(this);
        registerReceiver(broadcastReceiver, networkFilter);
        //setting toolbar
        setSupportActionBar(binding.main.toolbar.bar);
        binding.main.toolbar.toolbarTitleTv.setText(getString(R.string.app_name));
        //hiding app icon for home
        binding.main.toolbar.appIcon.setVisibility(View.GONE);
        //setting navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawer, binding.main.toolbar.bar, R.string.drawer_open,
                R.string.drawer_close);
        binding.drawer.addDrawerListener(toggle);
        toggle.syncState();
        //setting listener for navigation
        binding.navigationView.setNavigationItemSelectedListener(this);
        //setting recycler view
        //check for previous instance
        if (savedInstanceState != null && savedInstanceState.containsKey(UxUtils.RECYCLER_VIEW_DATA_KEY)) {
            //previous instance found
            restoreInstanceState(savedInstanceState);
            if (preferredMode) {
                binding.main.content.mainListRecyclerView.setAdapter(cursorAdapter);
                mScrollListener.setEnabled(false);
                getLoaderManager().initLoader(ITEMS_LOADER, null, this);
            }
        } else {
            //no previous instance, reading user preference for work/sort mode and language
            readUserPreferences();
            //no previous instance found
            settingUpRecyclerView(TmdbClient.STARTING_PAGE_INDEX);
            if (NetworkUtils.isDeviceConnected(this)) {
                doNetworkCall(UxUtils.STARTING_PAGE);
            } else {
                checkForEmptyList();
                UxUtils.showSnackbar(getString(R.string.no_internet_connection), binding.drawer);
            }
        }
        settingTitle();
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        //reading values
        ArrayList<Video> movies =
                savedInstanceState.getParcelableArrayList(UxUtils.RECYCLER_VIEW_DATA_KEY);
        int position = savedInstanceState.getInt(UxUtils.RECYCLER_VIEW_POSITION_KEY);
        int pagesLoaded = savedInstanceState.getInt(UxUtils.PAGES_LOADED_KEY);
        mLastPage = savedInstanceState.getInt(UxUtils.LAST_PAGE_ON_SERVER_KEY);
        mWorkingMode = savedInstanceState.getString(UxUtils.CURRENT_MODE_KEY);
        mSortingMode = savedInstanceState.getString(UxUtils.CURRENT_SORTING_KEY);
        itemCount = savedInstanceState.getInt(UxUtils.TOTAL_ITEM_KEY);
        mPreferredLanguage = savedInstanceState.getString(UxUtils.CONTENT_LANG_KEY);
        //setting recycler view
        settingUpRecyclerView(pagesLoaded);
        preferredMode = savedInstanceState.getBoolean(PREFERRED_MODE_KEY);
        mListAdapter.setData(movies, mWorkingMode, mSortingMode);
        //move to previous position
        binding.main.content.mainListRecyclerView.scrollToPosition(position);
        cursorAdapter = new FavoritesCursorAdapter(
                new FavoritesCursorAdapter.ListItemClickListener() {
                    @Override
                    public void onListItemClick(int itemId, int itemType, long recordId) {
                        openPreferredItem(itemId, itemType, recordId);
                    }
                });
        checkForEmptyList();
    }

    private void settingUpRecyclerView(int pagesLoaded) {
        int columns = UxUtils.getColumnNumber(this);
        int totalSpace = UxUtils.getAvailableBlankSpace(this, columns);
        int space = totalSpace / (columns + 1);
        GridSpacingDecoration itemDecoration = new GridSpacingDecoration(columns, space, true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, columns,
                LinearLayoutManager.VERTICAL, false);
        binding.main.content.mainListRecyclerView.setLayoutManager(layoutManager);
        binding.main.content.mainListRecyclerView.setHasFixedSize(true);
        //decorator for custom spacing
        binding.main.content.mainListRecyclerView.addItemDecoration(itemDecoration);
        //setting scroll listener
        mScrollListener = new EndlessRecyclerViewScrollListener(layoutManager,
                TmdbClient.STARTING_PAGE_INDEX, pagesLoaded) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (page <= mLastPage) {
                    doNetworkCall(page);
                }
            }

            @Override
            public void onScrolling(int lastPosition) {
                showPosition(lastPosition);
            }
        };
        binding.main.content.mainListRecyclerView.addOnScrollListener(mScrollListener);
        mListAdapter = new MainListAdapter(this);
        binding.main.content.mainListRecyclerView.setAdapter(mListAdapter);
    }

    private void checkForEmptyList() {
        if (mListAdapter.getItemCount() == 0) {
            binding.main.content.emptyView.emptyViewLayout.setVisibility(View.VISIBLE);
        } else {
            binding.main.content.emptyView.emptyViewLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void showPosition(int currentLastPosition) {
        String paging = ++currentLastPosition + "/" + itemCount;
        binding.main.content.itemCountTv.setText(paging);
    }

    private void readLinkedListPreference() {
        //reading from preference
        ArrayList<LinkedPerson> linkedPeople = UxUtils.getLinkedPeopleFromPreference(this);
        if (linkedPeople == null) {
            binding.main.toolbar.linkedTv.setVisibility(View.INVISIBLE);
        } else {
            int size = linkedPeople.size();
            binding.main.toolbar.linkedTv.setText(String.valueOf(size));
            binding.main.toolbar.linkedTv.setVisibility(View.VISIBLE);
        }
    }

    private void readUserPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String startScreenKey = getString(R.string.settings_start_screen_key);
        String startScreenDefault = getString(R.string.settings_start_screen_default_value);
        String startScreenValue = sharedPreferences.getString(startScreenKey, startScreenDefault);
        if (startScreenValue.equals(
                getString(R.string.settings_start_screen_popular_movies_value))) {
            mWorkingMode = TmdbClient.MOVIE_MODE;
            mSortingMode = TmdbClient.POPULAR_SORTING;
        } else if (startScreenValue.equals(
                getString(R.string.settings_start_screen_popular_tv_value))) {
            mWorkingMode = TmdbClient.TV_MODE;
            mSortingMode = TmdbClient.POPULAR_SORTING;
        } else if (startScreenValue.equals(
                getString(R.string.settings_start_screen_top_movies_value))) {
            mWorkingMode = TmdbClient.MOVIE_MODE;
            mSortingMode = TmdbClient.TOP_RATED_SORTING;
        } else if (startScreenValue.equals(
                getString(R.string.settings_start_screen_top_tv_value))) {
            mWorkingMode = TmdbClient.TV_MODE;
            mSortingMode = TmdbClient.TOP_RATED_SORTING;
        }
        mPreferredLanguage = UxUtils.getContentLangFromPreference(this);
    }

    private void settingTitle() {
        String title;
        if (preferredMode) {
            title = getString(R.string.favorites_title);
        } else {
            if (mWorkingMode.equals(TmdbClient.MOVIE_MODE)
                    && mSortingMode.equals(TmdbClient.TOP_RATED_SORTING)) {
                title = getString(R.string.top_rated_movie_title);
            } else if (mWorkingMode.equals(TmdbClient.MOVIE_MODE)
                    && mSortingMode.equals(TmdbClient.POPULAR_SORTING)) {
                title = getString(R.string.popular_movies_title);
            } else if (mWorkingMode.equals(TmdbClient.TV_MODE)
                    && mSortingMode.equals(TmdbClient.POPULAR_SORTING)) {
                title = getString(R.string.popular_tv_shows_title);
            } else {
                title = getString(R.string.top_rated_tv_shows_title);
            }
        }
        binding.main.content.modeTv.setText(title);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.top_rated_movie:
                changeMode(TmdbClient.MOVIE_MODE, TmdbClient.TOP_RATED_SORTING);
                return true;
            case R.id.most_popular_movie:
                changeMode(TmdbClient.MOVIE_MODE, TmdbClient.POPULAR_SORTING);
                return true;
            case R.id.top_rated_tv:
                changeMode(TmdbClient.TV_MODE, TmdbClient.TOP_RATED_SORTING);
                return true;
            case R.id.most_popular_tv:
                changeMode(TmdbClient.TV_MODE, TmdbClient.POPULAR_SORTING);
                return true;
            case R.id.favorite_media:
                showFavorites();
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                binding.drawer.closeDrawer(GravityCompat.START);
                return true;
            default:
                return true;
        }
    }

    private void changeMode(String mode, String sort) {
        binding.drawer.closeDrawer(GravityCompat.START);
        if (NetworkUtils.isDeviceConnected(this)) {
            preferredMode = false;
            mWorkingMode = mode;
            mSortingMode = sort;
            mCurrentPage = UxUtils.STARTING_PAGE;
            mListAdapter.setData(null, mode, sort);
            mScrollListener.resetState();
            mScrollListener.setEnabled(true);
            binding.main.content.mainListRecyclerView.setAdapter(mListAdapter);
            doNetworkCall(mCurrentPage);
            settingTitle();
        } else {
            UxUtils.showToast(this, getString(R.string.no_internet_connection));
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
        outState.putString(UxUtils.CURRENT_SORTING_KEY, mSortingMode);
        outState.putInt(UxUtils.TOTAL_ITEM_KEY, itemCount);
        outState.putString(UxUtils.CONTENT_LANG_KEY, mPreferredLanguage);
        outState.putBoolean(PREFERRED_MODE_KEY, preferredMode);
    }

    @Override
    public void onBackPressed() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
    public void onResponse(Call<VideoList> call, Response<VideoList> response) {
        binding.main.loadingBar.setVisibility(View.INVISIBLE);
        if (response.isSuccessful()) {
            VideoList list = response.body();
            mCurrentPage = list.getCurrentPage();
            mLastPage = list.getTotalPages();
            itemCount = list.getTotalMovies();
            if (mCurrentPage == UxUtils.STARTING_PAGE) {
                mListAdapter.setData(list.getVideos(), mWorkingMode, mSortingMode);
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
        binding.main.loadingBar.setVisibility(View.INVISIBLE);
        //connection error
        if (NetworkUtils.isDeviceConnected(this)) {
            UxUtils.showToast(this, getString(R.string.connection_error));
        } else {
            UxUtils.showSnackbar(getString(R.string.no_internet_connection), binding.drawer);
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
        registerReceiver(broadcastReceiver, networkFilter);
        readLinkedListPreference();
        //check for content lang changed
        String contentLangValue = UxUtils.getContentLangFromPreference(this);
        if (!mPreferredLanguage.equals(contentLangValue)) {
            mPreferredLanguage = contentLangValue;
            if (!preferredMode) {
                changeMode(mWorkingMode, mSortingMode);
            }
        }
    }

    @Override
    public void onListItemClick(int position) {
        int itemId = mListAdapter.getData().get(position).getVideoId();
        if (mWorkingMode.equals(TmdbClient.MOVIE_MODE)) {
            openMovieDetail(itemId);
        } else {
            openTvShowDetail(itemId);
        }
    }

    private void openTvShowDetail(int tvShowId) {
        Intent openTvShowIntent = new Intent(MainActivity.this, TvShowDetailActivity.class);
        openTvShowIntent.putExtra(TvShow.TV_SHOW_KEY, tvShowId);
        startActivity(openTvShowIntent);
    }

    private void openMovieDetail(int movieId) {
        Intent openMovieIntent = new Intent(MainActivity.this, MovieDetailActivity.class);
        openMovieIntent.putExtra(Movie.MOVIE_KEY, movieId);
        startActivity(openMovieIntent);
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

    @Override
    public void onNetworkChange(boolean isConnected) {
        if (!isConnected) {
            String message = getString(R.string.no_internet_connection);
            UxUtils.showSnackbar(message, binding.drawer);
        } else {
            mScrollListener.resetLoading();
        }
    }

    private void doNetworkCall(int page) {
        binding.main.loadingBar.setVisibility(View.VISIBLE);
        NetworkUtils.doNetworkCallForEntryList(
                page,
                mWorkingMode,
                mSortingMode,
                mPreferredLanguage,
                this);
    }

    private void showFavorites() {
        preferredMode = true;
        settingTitle();
        binding.drawer.closeDrawer(GravityCompat.START);
        cursorAdapter = new FavoritesCursorAdapter(new FavoritesCursorAdapter.ListItemClickListener() {
            @Override
            public void onListItemClick(int itemId, int itemType, long recordId) {
                openPreferredItem(itemId, itemType, recordId);
            }
        });
        binding.main.content.mainListRecyclerView.setAdapter(cursorAdapter);
        mScrollListener.resetState();
        mScrollListener.setEnabled(false);
        getLoaderManager().initLoader(ITEMS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                PopularMoviesContract.ItemEntry.COLUMN_ITEM_ID,
                PopularMoviesContract.ItemEntry.COLUMN_POSTER,
                PopularMoviesContract.ItemEntry.COLUMN_TITLE,
                PopularMoviesContract.ItemEntry.COLUMN_ITEM_TYPE,
                PopularMoviesContract.ItemEntry._ID
        };
        return new CursorLoader(this,
                PopularMoviesContract.ItemEntry.CONTENT_URI,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.setData(data);
        if (data != null) {
            itemCount = data.getCount();
        } else {
            itemCount = 0;
        }
        if (itemCount == 0) {
            binding.main.content.emptyView.emptyViewLayout.setVisibility(View.VISIBLE);
        } else {
            binding.main.content.emptyView.emptyViewLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.setData(null);
    }

    private void openPreferredMovie(long recordId) {
        ContentResolver contentResolver = getContentResolver();
        Movie movie = DbUtils.loadMovieInfo(recordId, contentResolver);
        DbUtils.loadBackdrops(movie, contentResolver);
        DbUtils.loadGenres(movie, getContentResolver());
        DbUtils.loadCompanies(movie, getContentResolver());
        DbUtils.loadCredits(movie, getContentResolver());
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(Movie.MOVIE_PREFERRED_KEY, movie);
        startActivity(intent);
    }

    private void openPreferredItem(int itemId, int itemType, long recordId) {
        if (itemType == PopularMoviesContract.ItemEntry.MOVIE_MEDIA_TYPE) {
            openPreferredMovie(recordId);
        } else {
            openPreferredTvShow(recordId);
        }
    }

    private void openPreferredTvShow(long recordId) {
        ContentResolver contentResolver = getContentResolver();
        TvShow tvShow = DbUtils.loadTvShowInfo(recordId, contentResolver);
        DbUtils.loadBackdrops(tvShow, contentResolver);
        DbUtils.loadGenres(tvShow, getContentResolver());
        DbUtils.loadCompanies(tvShow, getContentResolver());
        DbUtils.loadCredits(tvShow, getContentResolver());
        Intent intent = new Intent(this, TvShowDetailActivity.class);
        intent.putExtra(TvShow.TVSHOW_PREFERRED_KEY, tvShow);
        startActivity(intent);
    }
}

