package com.example.android.popularmovies2;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.popularmovies2.data.DbUtils;
import com.example.android.popularmovies2.data.PopularMoviesContract;
import com.example.android.popularmovies2.databinding.ActivityMovieDetailBinding;
import com.example.android.popularmovies2.model.Backdrop;
import com.example.android.popularmovies2.model.Country;
import com.example.android.popularmovies2.model.LinkedPerson;
import com.example.android.popularmovies2.model.Movie;
import com.example.android.popularmovies2.model.MovieTrailer;
import com.example.android.popularmovies2.model.Person;
import com.example.android.popularmovies2.model.Review;
import com.example.android.popularmovies2.network.NetworkUtils;
import com.example.android.popularmovies2.network.TmdbClient;
import com.example.android.popularmovies2.ux.BackdropViewPagerAdapter;
import com.example.android.popularmovies2.ux.CastRecyclerViewAdapter;
import com.example.android.popularmovies2.ux.CompaniesListAdapter;
import com.example.android.popularmovies2.ux.CrewRecyclerViewAdapter;
import com.example.android.popularmovies2.ux.GenreListAdapter;
import com.example.android.popularmovies2.ux.ReviewViewPagerAdapter;
import com.example.android.popularmovies2.ux.TrailerViewPagerAdapter;
import com.example.android.popularmovies2.ux.UxUtils;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity implements Callback<Movie>,
        DbUtils.AsyncDbOperations {

    private boolean savedAsPreferred = false;
    private static final int NO_MOVIE = -1;
    private Movie movie;
    private ActivityMovieDetailBinding binding;
    private String preferredContentLang;
    private SearchView searchView;
    private int movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        //finding reference
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);
        setSupportActionBar(binding.toolbar.bar);
        binding.toolbar.toolbarTitleTv.setText(R.string.movie_detail_title);
        preferredContentLang = UxUtils.getContentLangFromPreference(this);
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
        if (savedInstanceState != null && savedInstanceState.containsKey(Movie.MOVIE_BUNDLE_KEY)) {
            movie = savedInstanceState.getParcelable(Movie.MOVIE_BUNDLE_KEY);
            movieId = movie.getMovieId();
            preferredContentLang = savedInstanceState.getString(UxUtils.CONTENT_LANG_KEY);
            settingUpMovie();
            checkIfPreferred(movieId);
        } else {
            //getting intent with extra
            Intent intent = getIntent();
            //check for movie extra
            if (intent.hasExtra(Movie.MOVIE_PREFERRED_KEY)) {
                movie = intent.getParcelableExtra(Movie.MOVIE_PREFERRED_KEY);
                movieId = movie.getMovieId();
                binding.content.intro.preferredIv.setImageResource(R.drawable.ic_preferred);
                savedAsPreferred = true;
                settingUpMovie();
            } else {
                movieId = intent.getIntExtra(Movie.MOVIE_KEY, NO_MOVIE);
                if (movieId == NO_MOVIE) {
                    UxUtils.showToast(this, getString(R.string.unable_to_open_movie));
                    finish();
                    return;
                }
                doNetworkCall();
                checkIfPreferred(movieId);
            }
        }
    }

    private void removeCurrentItemAsPreferred() {
        DbUtils.removeItemFromDb(movie.getMovieId(), this, this);
    }

    private void saveCurrentItemAsPreferred() {
        if (movie == null) {
            UxUtils.showToast(this, getString(R.string.unable_to_save_as_preferred));
            return;
        }
        DbUtils.saveItemAsPreferred(movie, this, this);
    }

    private void checkIfPreferred(int movieId) {
        DbUtils.checkIfPreferred(PopularMoviesContract.ItemEntry.MOVIE_MEDIA_TYPE, movieId, this,
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
        String imageLang = getString(R.string.image_language,
                preferredContentLang.split("-")[0]);
        NetworkUtils.doNetworkCallForMovieDetail(
                movieId,
                preferredContentLang,
                imageLang,
                this);
    }

    @Override
    public void onResponse(Call<Movie> call, Response<Movie> response) {
        if (response.isSuccessful()) {
            movie = response.body();
            settingUpMovie();
        } else {
            UxUtils.showToast(MovieDetailActivity.this,
                    getString(R.string.unable_to_open_movie));
            finish();
        }
    }

    @Override
    public void onFailure(Call<Movie> call, Throwable t) {
        binding.loadingBar.setVisibility(View.INVISIBLE);
        UxUtils.showToast(MovieDetailActivity.this,
                getString(R.string.unable_to_open_movie));
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Movie.MOVIE_BUNDLE_KEY, movie);
        outState.putString(UxUtils.CONTENT_LANG_KEY, preferredContentLang);
    }

    private void settingUpMovie() {
        //setting genre recycler view
        String emptyString = getString(R.string.not_available_short);
        GenreListAdapter genreListAdapter = new GenreListAdapter(movie.getGenreList(), emptyString,
                new GenreListAdapter.OnGenreClick() {
                    @Override
                    public void onClick(int genreId, String genreName) {
                        genreClick(genreId, genreName);
                    }
                });
        LinearLayoutManager genreLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        binding.content.genreRv.setLayoutManager(genreLayoutManager);
        binding.content.genreRv.setAdapter(genreListAdapter);
        //setting backdrop view pager
        ArrayList<Backdrop> backdrops = movie.getBackdropImages().getBackdrops();
        BackdropViewPagerAdapter backdropViewPagerAdapter = new BackdropViewPagerAdapter(backdrops);
        binding.content.backdrop.backdropVp.setAdapter(backdropViewPagerAdapter);
        binding.content.backdrop.backdropTab.setupWithViewPager(binding.content.backdrop.backdropVp, true);
        //setting companies recycler view
        CompaniesListAdapter companiesListAdapter = new CompaniesListAdapter(movie.getCompanies(),
                emptyString, new CompaniesListAdapter.OnCompanyClick() {
            @Override
            public void onClick(int companyId, String companyName) {
                companyClick(companyId, companyName);
            }
        });
        LinearLayoutManager companiesLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.content.techInfo.companiesRv.setLayoutManager(companiesLayoutManager);
        binding.content.techInfo.companiesRv.setAdapter(companiesListAdapter);
        //setting crew recycler view
        CrewRecyclerViewAdapter crewRecyclerViewAdapter =
                new CrewRecyclerViewAdapter(movie.getCredits().getCrews(), emptyString,
                        new CrewRecyclerViewAdapter.OnCrewItemClick() {
                            @Override
                            public void onClick(int castId) {
                                openPersonDetail(castId);
                            }
                        });
        LinearLayoutManager crewLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        binding.content.crewRv.setLayoutManager(crewLayoutManager);
        binding.content.crewRv.setHasFixedSize(true);
        binding.content.crewRv.setAdapter(crewRecyclerViewAdapter);
        //setting cast recycler view
        CastRecyclerViewAdapter castRecyclerViewAdapter =
                new CastRecyclerViewAdapter(movie.getCredits().getCasts(), emptyString,
                        new CastRecyclerViewAdapter.OnCastItemClick() {
                            @Override
                            public void onClick(int crewId) {
                                openPersonDetail(crewId);
                            }
                        });
        LinearLayoutManager castLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        binding.content.castRv.setLayoutManager(castLayoutManager);
        binding.content.castRv.setHasFixedSize(true);
        binding.content.castRv.setAdapter(castRecyclerViewAdapter);
        //setting trailer view pager
        if (movie.getVideos() != null) {
            ArrayList<MovieTrailer> trailers = movie.getVideos().getTrailers();
            TrailerViewPagerAdapter trailerViewPagerAdapter =
                    new TrailerViewPagerAdapter(trailers, emptyString,
                            new TrailerViewPagerAdapter.OnTrailerClick() {
                                @Override
                                public void onClick(String key) {
                                    openTrailer(key);
                                }
                            }, new TrailerViewPagerAdapter.OnShareClick() {
                        @Override
                        public void onClick(String trailerKey) {
                            shareTrailer(trailerKey);
                        }
                    });
            binding.content.trailer.trailerVp.setAdapter(trailerViewPagerAdapter);
            binding.content.trailer.trailerTab.setupWithViewPager(binding.content.trailer.trailerVp,
                    true);
        }
        //setting review view pager
        if (movie.getReviews() != null) {
            ArrayList<Review> reviews = movie.getReviews().getReviews();
            ReviewViewPagerAdapter reviewViewPagerAdapter = new ReviewViewPagerAdapter(reviews,
                    this, emptyString);
            binding.content.review.reviewVp.setAdapter(reviewViewPagerAdapter);
            binding.content.review.reviewTab.setupWithViewPager(binding.content.review.reviewVp,
                    true);
        }
        //loading movie data
        loadMovieData();
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

    private void shareTrailer(String key) {
        if (key != null) {
            Uri builtUri = Uri.parse(NetworkUtils.YOUTUBE_BASE_URL).buildUpon()
                    .appendQueryParameter(NetworkUtils.YOUTUBE_QUERY_PARAMETER, key).build();
            String title = getString(R.string.share_with);
            ShareCompat.IntentBuilder
                    .from(this)
                    .setType(NetworkUtils.YOUTUBE_MIME_TYPE)
                    .setChooserTitle(title)
                    .setText(builtUri.toString())
                    .startChooser();
        } else {
            UxUtils.showToast(this, getString(R.string.unable_to_share_trailer));
        }
    }

    private void openTrailer(String key) {
        if (key != null) {
            Uri builtUri = Uri.parse(NetworkUtils.YOUTUBE_BASE_URL).buildUpon()
                    .appendQueryParameter(NetworkUtils.YOUTUBE_QUERY_PARAMETER, key).build();
            Intent intent = new Intent(Intent.ACTION_VIEW, builtUri);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else {
            UxUtils.showToast(this, getString(R.string.unable_to_open_trailer));
        }
    }

    private void openPersonDetail(int personId) {
        Intent openPersonIntent = new Intent(MovieDetailActivity.this,
                PersonDetailActivity.class);
        openPersonIntent.putExtra(Person.PERSON_KEY, personId);
        startActivity(openPersonIntent);
    }

    private void loadMovieData() {
        //title and original title
        setText(binding.content.intro.titleTv, UxUtils.getFormattedTile(movie.getTitle()));
        setText(binding.content.intro.originalTitleTv, movie.getOriginalTitle());
        //genre label
        setText(binding.content.genreLabel, getResources().getQuantityString(R.plurals.genre_label,
                movie.getGenreList().size()));
        //rating, popularity, release date, runtime
        setText(binding.content.generalInfo.ratingTv, movie.getVoteAverage());
        if (movie.getVoteCount() == 0) {
            setText(binding.content.generalInfo.voteCountTv, "");
        } else {
            String voteString = getString(R.string.votes_label, movie.getVoteCount());
            setText(binding.content.generalInfo.voteCountTv, voteString);
        }
        setText(binding.content.generalInfo.popularityTv, movie.getPopularityIndex());
        setText(binding.content.generalInfo.releaseDateTv, UxUtils.getFormattedDate(
                movie.getReleaseDate()));
        if (movie.getRuntime() == 0) {
            setText(binding.content.intro.lengthTv, "");
        } else {
            setText(binding.content.intro.lengthTv, UxUtils.getFormattedRuntime(movie.getRuntime(),
                    this));
        }
        //poster image
        NetworkUtils.downloadImage(binding.content.plot.posterIv, movie.getPosterPath(),
                R.drawable.ic_video_camera, R.drawable.ic_video_camera);
        //plot
        if (movie.getPlot() == null || movie.getPlot().isEmpty()) {
            binding.content.plot.plotEx.setText(getString(R.string.not_available_short));
        } else {
            binding.content.plot.plotEx.setText(movie.getPlot());
        }
        //technical data
        String budgetLabel = getString(R.string.budget_value_label,
                getString(R.string.budget_standard_currency));
        String revenueLabel = getString(R.string.revenue_value_label,
                getString(R.string.budget_standard_currency));
        setText(binding.content.techInfo.budgetLabel, budgetLabel);
        setText(binding.content.techInfo.revenueLabel, revenueLabel);
        if (movie.getBudget() == 0) {
            setText(binding.content.techInfo.budgetTv, "");
        } else {
            setText(binding.content.techInfo.budgetTv, String.format(Locale.getDefault(),
                    "%,d", movie.getBudget()));
        }
        if (movie.getRevenue() == 0) {
            setText(binding.content.techInfo.revenueTv, "");
        } else {
            setText(binding.content.techInfo.revenueTv, String.format(Locale.getDefault(),
                    "%,d", movie.getRevenue()));
        }
        setText(binding.content.techInfo.countryTv, movie.getCountries());
        //hide loading bar
        binding.loadingBar.setVisibility(View.INVISIBLE);
    }

    private void setText(TextView targetView, String text) {
        if (text == null || text.trim().isEmpty()) {
            targetView.setText(getString(R.string.not_available_short));
        } else {
            targetView.setText(text);
        }
    }

    private void setText(TextView targetView, float value) {
        String stringValue = String.format(Locale.getDefault(), "%.1f", value);
        if (stringValue.equals("0.0") || stringValue.equals("0,0")) {
            stringValue = null;
        }
        setText(targetView, stringValue);
    }

    private void setText(TextView targetView, ArrayList<Country> list) {
        if (list == null || list.isEmpty()) {
            targetView.setText(getString(R.string.not_available_short));
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                Country country = list.get(i);
                builder.append(country.getCountryCode());
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
        //setting search view
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
            case R.id.action_find_similar_movie:
                Intent searchSimilarMovieIntent = new Intent(this,
                        SearchResultActivity.class);
                searchSimilarMovieIntent.putExtra(UxUtils.TAG_VALUE_KEY, movie.getMovieId());
                searchSimilarMovieIntent.putExtra(UxUtils.TAG_KEY, UxUtils.SIMILAR_TAG);
                searchSimilarMovieIntent.putExtra(UxUtils.SEARCH_MODE, TmdbClient.MOVIE_MODE);
                searchSimilarMovieIntent.putExtra(UxUtils.EXTRA_STRING_VALUE_KEY, movie.getTitle());
                startActivity(searchSimilarMovieIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void goToHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void movieRuntimeClick(View view) {
        int runtime = movie.getRuntime();
        if (runtime != 0) {
            Intent searchSimilarMovieRuntimeIntent = new Intent(this,
                    SearchResultActivity.class);
            searchSimilarMovieRuntimeIntent.putExtra(UxUtils.TAG_VALUE_KEY, runtime);
            searchSimilarMovieRuntimeIntent.putExtra(UxUtils.TAG_KEY, UxUtils.RUNTIME_TAG);
            searchSimilarMovieRuntimeIntent.putExtra(UxUtils.SEARCH_MODE, TmdbClient.MOVIE_MODE);
            startActivity(searchSimilarMovieRuntimeIntent);
        }
    }

    public void movieRatingClick(View view) {
        int vote = (int) movie.getVoteAverage();
        if (vote != 0) {
            Intent searchSimilarMovieRatingIntent = new Intent(this,
                    SearchResultActivity.class);
            searchSimilarMovieRatingIntent.putExtra(UxUtils.TAG_VALUE_KEY, vote);
            searchSimilarMovieRatingIntent.putExtra(UxUtils.TAG_KEY, UxUtils.RATING_TAG);
            searchSimilarMovieRatingIntent.putExtra(UxUtils.SEARCH_MODE, TmdbClient.MOVIE_MODE);
            startActivity(searchSimilarMovieRatingIntent);
        }
    }

    public void movieYearClick(View view) {
        String dateString = movie.getReleaseDate();
        if (dateString != null) {
            int year = Integer.valueOf(dateString.split("-")[0]);
            Intent searchSimilarMovieYearIntent = new Intent(this,
                    SearchResultActivity.class);
            searchSimilarMovieYearIntent.putExtra(UxUtils.TAG_VALUE_KEY, year);
            searchSimilarMovieYearIntent.putExtra(UxUtils.TAG_KEY, UxUtils.YEAR_TAG);
            searchSimilarMovieYearIntent.putExtra(UxUtils.SEARCH_MODE, TmdbClient.MOVIE_MODE);
            startActivity(searchSimilarMovieYearIntent);
        }
    }

    private void genreClick(int genreId, String genreName) {
        Intent searchSimilarMovieGenreIntent = new Intent(this,
                SearchResultActivity.class);
        searchSimilarMovieGenreIntent.putExtra(UxUtils.TAG_VALUE_KEY, genreId);
        searchSimilarMovieGenreIntent.putExtra(UxUtils.TAG_KEY, UxUtils.GENRE_TAG);
        searchSimilarMovieGenreIntent.putExtra(UxUtils.SEARCH_MODE, TmdbClient.MOVIE_MODE);
        searchSimilarMovieGenreIntent.putExtra(UxUtils.EXTRA_STRING_VALUE_KEY, genreName);
        startActivity(searchSimilarMovieGenreIntent);
    }

    private void companyClick(int companyId, String companyName) {
        Intent searchSimilarMovieGenreIntent = new Intent(this,
                SearchResultActivity.class);
        searchSimilarMovieGenreIntent.putExtra(UxUtils.TAG_VALUE_KEY, companyId);
        searchSimilarMovieGenreIntent.putExtra(UxUtils.TAG_KEY, UxUtils.COMPANY_TAG);
        searchSimilarMovieGenreIntent.putExtra(UxUtils.SEARCH_MODE, TmdbClient.MOVIE_MODE);
        searchSimilarMovieGenreIntent.putExtra(UxUtils.EXTRA_STRING_VALUE_KEY, companyName);
        startActivity(searchSimilarMovieGenreIntent);
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
