package com.example.android.popularmovies2.ux;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Emanuele on 21/02/2018.
 */
public class GridSpacingDecoration extends RecyclerView.ItemDecoration {

    //credits for the following code go to
    //https://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing
    private int spanCount;
    private int spacing;
    private boolean includeEdge;

    /**
     * @param spanCount   columns number
     * @param spacing     total space available
     * @param includeEdge
     */
    public GridSpacingDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = position % spanCount;
        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount;
            outRect.right = (column + 1) * spacing / spanCount;
            if (position < spanCount) {
                outRect.top = spacing;
            }
            outRect.bottom = spacing;
        } else {
            outRect.left = column * spacing / spanCount;
            outRect.right = spacing - (column + 1) * spacing / spanCount;
            if (position >= spanCount) {
                outRect.top = spacing;
            }
        }
    }
}
