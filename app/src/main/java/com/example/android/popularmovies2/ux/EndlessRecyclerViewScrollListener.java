package com.example.android.popularmovies2.ux;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Emanuele on 22/02/2018.
 */
public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    //credits for the following code go to
    // https://github.com/codepath/android_guides/wiki/Endless-Scrolling-with-AdapterViews-and-RecyclerView
    // (modified from the original version)
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int mVisibleThreshold = 4;
    // The current offset index of data you have loaded
    private int mCurrentPage = 0;
    // The total number of items in the dataset after the last load
    private int mPreviousItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private final int mStartingPageIndex;
    RecyclerView.LayoutManager mLayoutManager;
    private boolean enabled = true;

    public EndlessRecyclerViewScrollListener(RecyclerView.LayoutManager layoutManager, int startingPageIndex, int currentPage) {
        this.mLayoutManager = layoutManager;
        if (layoutManager instanceof GridLayoutManager) {
            mVisibleThreshold = mVisibleThreshold * ((GridLayoutManager) layoutManager).getSpanCount();
        }
        this.mStartingPageIndex = startingPageIndex;
        this.mCurrentPage = currentPage;
    }

    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        int lastVisibleItemPosition = 0;
        int actualItemCount = mLayoutManager.getItemCount();
        if (mLayoutManager instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        } else {
            lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        }
        onScrolling(lastVisibleItemPosition);
        // check for reset
        if (actualItemCount < mPreviousItemCount) {
            this.mCurrentPage = mStartingPageIndex;
            this.mPreviousItemCount = actualItemCount;
            if (actualItemCount == 0) {
                this.loading = true;
            }
        }
        if (loading && (actualItemCount > mPreviousItemCount)) {
            loading = false;
            mPreviousItemCount = actualItemCount;
        }
        if (enabled && !loading && (lastVisibleItemPosition + mVisibleThreshold) > actualItemCount) {
            mCurrentPage++;
            onLoadMore(mCurrentPage, actualItemCount, view);
            loading = true;
        }
    }

    public void resetState() {
        this.mCurrentPage = mStartingPageIndex;
        this.mPreviousItemCount = 0;
        this.loading = true;
    }

    public void resetLoading() {
        this.loading = false;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public abstract void onLoadMore(int page, int totalItemsCount, RecyclerView view);
    public abstract void onScrolling(int lastPosition);
}
