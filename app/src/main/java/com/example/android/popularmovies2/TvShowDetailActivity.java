package com.example.android.popularmovies2;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.popularmovies2.data.DbUtils;
import com.example.android.popularmovies2.data.PopularMoviesContract;
import com.example.android.popularmovies2.databinding.ActivityTvDetailBinding;
import com.example.android.popularmovies2.model.Backdrop;
import com.example.android.popularmovies2.model.LinkedPerson;
import com.example.android.popularmovies2.model.Person;
import com.example.android.popularmovies2.model.TvShow;
import com.example.android.popularmovies2.network.NetworkUtils;
import com.example.android.popularmovies2.network.TmdbClient;
import com.example.android.popularmovies2.ux.AuthorRecyclerViewAdapter;
import com.example.android.popularmovies2.ux.BackdropViewPagerAdapter;
import com.example.android.popularmovies2.ux.CastRecyclerViewAdapter;
import com.example.android.popularmovies2.ux.CompaniesListAdapter;
import com.example.android.popularmovies2.ux.GenreListAdapter;
import com.example.android.popularmovies2.ux.TvShowRuntimeAdapter;
import com.example.android.popularmovies2.ux.UxUtils;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TvShowDetailActivity extends AppCompatActivity
        implements Callback<TvShow>, DbUtils.AsyncDbOperations {

    private boolean savedAsPreferred = false;
    private TvShow tvShow;
    private int tvShowId;
    private SearchView searchView;
    private String preferredContentLang;
    private ActivityTvDetailBinding binding;
    private static final int NO_TV_SHOW = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_detail);
        //finding reference
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tv_detail);
        preferredContentLang = UxUtils.getContentLangFromPreference(this);
        //setting toolbar
        setSupportActionBar(binding.toolbar.bar);
        binding.toolbar.toolbarTitleTv.setText(R.string.tv_show_detail_title);
        binding.content.intro.preferredIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!savedAsPreferred) {
                    saveCurrentItemAsPreferred();
                } else {
                    savedAsPreferred = false;
                    binding.content.intro.preferredIv.setImageResource(R.drawable.ic_poster_star);
                    removeCurrentItemAsPreferred();
                }
            }
        });
        //check for previous instance
        if (savedInstanceState != null && savedInstanceState.containsKey(TvShow.TV_SHOW_BUNDLE_KEY)) {
            tvShow = savedInstanceState.getParcelable(TvShow.TV_SHOW_BUNDLE_KEY);
            tvShowId = tvShow.getTvShowId();
            preferredContentLang = savedInstanceState.getString(UxUtils.CONTENT_LANG_KEY);
            settingUpTvShow();
            checkIfPreferred(tvShowId);
        } else {
            //getting intent with extra
            Intent intent = getIntent();
            //check for tvshow extra
            if (intent.hasExtra(TvShow.TVSHOW_PREFERRED_KEY)) {
                tvShow = intent.getParcelableExtra(TvShow.TVSHOW_PREFERRED_KEY);
                tvShowId = tvShow.getTvShowId();
                binding.content.intro.preferredIv.setImageResource(R.drawable.ic_preferred);
                savedAsPreferred = true;
                settingUpTvShow();
            } else {
                tvShowId = intent.getIntExtra(TvShow.TV_SHOW_KEY, NO_TV_SHOW);
                if (tvShowId == NO_TV_SHOW) {
                    UxUtils.showToast(this, getString(R.string.unable_to_open_tvshow));
                    finish();
                    return;
                }
                doNetworkCall();
                checkIfPreferred(tvShowId);
            }
        }
    }

    private void removeCurrentItemAsPreferred() {
        DbUtils.removeItemFromDb(tvShow.getTvShowId(), this, this);
    }

    private void checkIfPreferred(int tvShowId) {
        DbUtils.checkIfPreferred(PopularMoviesContract.ItemEntry.TV_MEDIA_TYPE, tvShowId, this,
                this);
    }

    @Override
    public void isDeletingSuccessful(boolean completed) {
        if (!completed) {
            UxUtils.showToast(this, getString(R.string.error_when_removing_from_db));
        }
    }

    @Override
    public void isSavingSuccessful(boolean completed) {
        if (!completed) {
            UxUtils.showToast(this, getString(R.string.error_while_marking_as_favourite));
        } else {
            this.savedAsPreferred = true;
            binding.content.intro.preferredIv.setImageResource(R.drawable.ic_preferred);
        }
    }

    @Override
    public void isPreferred(boolean preferred) {
        if (preferred) {
            binding.content.intro.preferredIv.setImageResource(R.drawable.ic_preferred);
            savedAsPreferred = true;
        } else {
            binding.content.intro.preferredIv.setImageResource(R.drawable.ic_poster_star);
            savedAsPreferred = false;
        }
    }

    private void saveCurrentItemAsPreferred() {
        if (tvShow == null) {
            UxUtils.showToast(this, getString(R.string.unable_to_save_as_preferred));
            return;
        }
        DbUtils.saveItemAsPreferred(tvShow, this, this);
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
        String imageLang = getString(R.string.image_language, preferredContentLang.split("-")[0]);
        NetworkUtils.doNetworkCallForTvShowDetail(
                tvShowId,
                preferredContentLang,
                imageLang,
                this);
    }

    @Override
    public void onResponse(Call<TvShow> call, Response<TvShow> response) {
        if (response.isSuccessful()) {
            tvShow = response.body();
            settingUpTvShow();
        } else {
            UxUtils.showToast(TvShowDetailActivity.this,
                    getString(R.string.unable_to_open_tvshow));
            finish();
        }
    }

    @Override
    public void onFailure(Call<TvShow> call, Throwable t) {
        binding.loadingBar.setVisibility(View.INVISIBLE);
        UxUtils.showToast(TvShowDetailActivity.this,
                getString(R.string.unable_to_open_tvshow));
        finish();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TvShow.TV_SHOW_BUNDLE_KEY, tvShow);
        outState.putString(UxUtils.CONTENT_LANG_KEY, preferredContentLang);
    }

    private void settingUpTvShow() {
        //setting genre recycler view
        String emptyString = getString(R.string.not_available_short);
        GenreListAdapter genreListAdapter = new GenreListAdapter(tvShow.getGenreList(),
                emptyString, new GenreListAdapter.OnGenreClick() {
            @Override
            public void onClick(int genreId, String genreName) {
                genreClick(genreId, genreName);
            }
        });
        LinearLayoutManager genreLayoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false);
        binding.content.genreRv.setLayoutManager(genreLayoutManager);
        binding.content.genreRv.setAdapter(genreListAdapter);
        //setting backdrop view pager
        ArrayList<Backdrop> backdrops = tvShow.getBackdropImages().getBackdrops();
        BackdropViewPagerAdapter backdropViewPagerAdapter = new BackdropViewPagerAdapter(backdrops);
        binding.content.backdrop.backdropVp.setAdapter(backdropViewPagerAdapter);
        binding.content.backdrop.backdropTab.setupWithViewPager(binding.content.backdrop.backdropVp,
                true);
        //setting runtime recycler view
        ArrayList<Integer> lengths = tvShow.getRuntime();
        TvShowRuntimeAdapter tvShowRuntimeAdapter = new TvShowRuntimeAdapter(lengths, emptyString,
                new TvShowRuntimeAdapter.OnRuntimeClick() {
                    @Override
                    public void onClick(int runtime) {
                        runtimeClick(runtime);
                    }
                });
        LinearLayoutManager runtimeLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        binding.content.intro.lengthRv.setLayoutManager(runtimeLayoutManager);
        binding.content.intro.lengthRv.setAdapter(tvShowRuntimeAdapter);
        //setting companies recycler view
        CompaniesListAdapter companiesListAdapter = new CompaniesListAdapter(tvShow.getCompanies(),
                emptyString, null);
        LinearLayoutManager companiesLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        binding.content.techInfoTv.companiesRv.setLayoutManager(companiesLayoutManager);
        binding.content.techInfoTv.companiesRv.setAdapter(companiesListAdapter);
        //setting author recycler view
        AuthorRecyclerViewAdapter authorRecyclerViewAdapter =
                new AuthorRecyclerViewAdapter(tvShow.getAuthors(), emptyString,
                        new AuthorRecyclerViewAdapter.OnAuthorItemClick() {
                            @Override
                            public void onClick(int castId) {
                                openPersonDetail(castId);
                            }
                        });
        LinearLayoutManager authorLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        binding.content.authorRv.setLayoutManager(authorLayoutManager);
        binding.content.authorRv.setHasFixedSize(true);
        binding.content.authorRv.setAdapter(authorRecyclerViewAdapter);
        //setting cast recycler view
        CastRecyclerViewAdapter castRecyclerViewAdapter =
                new CastRecyclerViewAdapter(tvShow.getCredits().getCasts(), emptyString,
                        new CastRecyclerViewAdapter.OnCastItemClick() {
                            @Override
                            public void onClick(int castId) {
                                openPersonDetail(castId);
                            }
                        });
        LinearLayoutManager castLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        binding.content.castRv.setLayoutManager(castLayoutManager);
        binding.content.castRv.setHasFixedSize(true);
        binding.content.castRv.setAdapter(castRecyclerViewAdapter);
        loadTvShowData();
    }

    private void openPersonDetail(int personId) {
        Intent openPersonIntent = new Intent(TvShowDetailActivity.this,
                PersonDetailActivity.class);
        openPersonIntent.putExtra(Person.PERSON_KEY, personId);
        startActivity(openPersonIntent);
    }

    private void loadTvShowData() {
        //title and original title
        setText(binding.content.intro.titleTv, UxUtils.getFormattedTile(tvShow.getTitle()));
        setText(binding.content.intro.originalTitleTv, tvShow.getOriginalTitle());
        //genre label
        setText(binding.content.genreLabel, getResources().getQuantityString(R.plurals.genre_label,
                tvShow.getGenreList().size()));
        //rating, popularity, release date
        setText(binding.content.generalInfoTv.ratingTv, tvShow.getVoteAverage());
        if (tvShow.getVoteCount() == 0) {
            setText(binding.content.generalInfoTv.voteCountTv, "");
        } else {
            String voteString = getString(R.string.votes_label, tvShow.getVoteCount());
            setText(binding.content.generalInfoTv.voteCountTv, voteString);
        }
        setText(binding.content.generalInfoTv.popularityTv, tvShow.getPopularityIndex());
        setText(binding.content.generalInfoTv.firstAirDateTv,
                UxUtils.getFormattedDate(tvShow.getFirstAirDate()));
        setText(binding.content.generalInfoTv.lastAirDateTv,
                UxUtils.getFormattedDate(tvShow.getLastAirDate()));
        //poster image
        NetworkUtils.downloadImage(binding.content.plot.posterIv, tvShow.getPosterPath(),
                R.drawable.ic_video_camera, R.drawable.ic_video_camera);
        //plot
        if (tvShow.getPlot() == null || tvShow.getPlot().isEmpty()) {
            binding.content.plot.plotEx.setText(getString(R.string.not_available_short));
        } else {
            binding.content.plot.plotEx.setText(tvShow.getPlot());
        }
        //technical data
        setText(binding.content.techInfoTv.seasonTv, tvShow.getSeasonCount());
        setText(binding.content.techInfoTv.episodesTv, tvShow.getEpisodeCount());
        setText(binding.content.techInfoTv.countryTv, tvShow.getCountries());
        setText(binding.content.techInfoTv.statusTv, tvShow.getStatus());
        binding.loadingBar.setVisibility(View.INVISIBLE);
    }

    private void setText(TextView targetView, String text) {
        if (text == null || text.trim().isEmpty()) {
            targetView.setText(getString(R.string.not_available_short));
        } else {
            targetView.setText(text);
        }
    }

    private void setText(TextView targetView, int text) {
        if (text == 0) {
            targetView.setText(getString(R.string.not_available_short));
        } else {
            targetView.setText(String.valueOf(text));
        }
    }

    private void setText(TextView targetView, float value) {
        String stringValue = String.format(Locale.getDefault(), "%.1f", value);
        if (stringValue.equals("0.0") || stringValue.equals("0,0")) {
            stringValue = null;
        }
        setText(targetView, stringValue);
    }

    private void setText(TextView targetView, ArrayList<String> list) {
        if (list == null || list.isEmpty()) {
            targetView.setText(getString(R.string.not_available_short));
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                String country = list.get(i);
                builder.append(country);
                if (i < list.size() - 1) {
                    builder.append(", ");
                }
            }
            setText(targetView, builder.toString());
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

    public void goToHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
            case R.id.action_find_similar_tvshow:
                Intent searchSimilarMovieIntent = new Intent(this,
                        SearchResultActivity.class);
                searchSimilarMovieIntent.putExtra(UxUtils.TAG_VALUE_KEY, tvShow.getTvShowId());
                searchSimilarMovieIntent.putExtra(UxUtils.TAG_KEY, UxUtils.SIMILAR_TAG);
                searchSimilarMovieIntent.putExtra(UxUtils.SEARCH_MODE, TmdbClient.TV_MODE);
                searchSimilarMovieIntent.putExtra(UxUtils.EXTRA_STRING_VALUE_KEY, tvShow.getTitle());
                startActivity(searchSimilarMovieIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void ratingClick(View view) {
        int vote = (int) tvShow.getVoteAverage();
        if (vote != 0) {
            Intent searchSimilarTvRatingIntent = new Intent(this,
                    SearchResultActivity.class);
            searchSimilarTvRatingIntent.putExtra(UxUtils.TAG_VALUE_KEY, vote);
            searchSimilarTvRatingIntent.putExtra(UxUtils.TAG_KEY, UxUtils.RATING_TAG);
            searchSimilarTvRatingIntent.putExtra(UxUtils.SEARCH_MODE, TmdbClient.TV_MODE);
            startActivity(searchSimilarTvRatingIntent);
        }
    }

    public void yearClick(View view) {
        String dateString = tvShow.getFirstAirDate();
        if (dateString != null) {
            int year = Integer.valueOf(dateString.split("-")[0]);
            Intent searchSimilarTvYearIntent = new Intent(this,
                    SearchResultActivity.class);
            searchSimilarTvYearIntent.putExtra(UxUtils.TAG_VALUE_KEY, year);
            searchSimilarTvYearIntent.putExtra(UxUtils.TAG_KEY, UxUtils.YEAR_TAG);
            searchSimilarTvYearIntent.putExtra(UxUtils.SEARCH_MODE, TmdbClient.TV_MODE);
            startActivity(searchSimilarTvYearIntent);
        }
    }

    private void runtimeClick(int runtime) {
        Intent searchSimilarTvRuntimeIntent = new Intent(this,
                SearchResultActivity.class);
        searchSimilarTvRuntimeIntent.putExtra(UxUtils.TAG_VALUE_KEY, runtime);
        searchSimilarTvRuntimeIntent.putExtra(UxUtils.TAG_KEY, UxUtils.RUNTIME_TAG);
        searchSimilarTvRuntimeIntent.putExtra(UxUtils.SEARCH_MODE, TmdbClient.TV_MODE);
        startActivity(searchSimilarTvRuntimeIntent);
    }

    private void genreClick(int genreId, String genreName) {
        Intent searchSimilarTvGenreIntent = new Intent(this,
                SearchResultActivity.class);
        searchSimilarTvGenreIntent.putExtra(UxUtils.TAG_VALUE_KEY, genreId);
        searchSimilarTvGenreIntent.putExtra(UxUtils.TAG_KEY, UxUtils.GENRE_TAG);
        searchSimilarTvGenreIntent.putExtra(UxUtils.SEARCH_MODE, TmdbClient.TV_MODE);
        searchSimilarTvGenreIntent.putExtra(UxUtils.EXTRA_STRING_VALUE_KEY, genreName);
        startActivity(searchSimilarTvGenreIntent);
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
