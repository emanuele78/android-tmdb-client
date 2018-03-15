package com.example.android.popularmovies2.ux;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.model.Review;

import java.util.ArrayList;

/**
 * Created by Emanuele on 01/03/2018.
 */
public class ReviewViewPagerAdapter extends PagerAdapter {

    private ArrayList<Review> reviews;
    private LayoutInflater inflater;
    private String notAvailable;

    public ReviewViewPagerAdapter(ArrayList<Review> reviews, Context context, String notAvailable) {
        this.reviews = reviews;
        inflater = LayoutInflater.from(context);
        this.notAvailable = notAvailable;
    }

    @Override
    public int getCount() {
        if (reviews == null || reviews.isEmpty()) {
            return 1;
        } else {
            return reviews.size();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View layoutView = inflater.inflate(R.layout.item_review, container, false);
        TextView authorView = layoutView.findViewById(R.id.author_review_tv);
        TextView contentView = layoutView.findViewById(R.id.review_content_tv);
        if (reviews != null && !reviews.isEmpty()) {
            String author = reviews.get(position).getAuthor();
            String content = reviews.get(position).getContent();
            authorView.setText(author);
            contentView.setText(content);
        } else {
            authorView.setText(notAvailable);
        }
        container.addView(layoutView, 0);
        return layoutView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}