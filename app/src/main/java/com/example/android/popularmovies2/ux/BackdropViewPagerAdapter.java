package com.example.android.popularmovies2.ux;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.model.Backdrop;
import com.example.android.popularmovies2.network.NetworkUtils;

import java.util.ArrayList;

/**
 * Created by Emanuele on 26/02/2018.
 */
public class BackdropViewPagerAdapter extends PagerAdapter {

    private static final int MAX_BACKDROP_IMAGES = 5;
    private static final int MIN_BACKDROP_IMAGES = 1;
    private static final int FIRST_INDEX = 0;
    private ArrayList<Backdrop> imagePaths;

    public BackdropViewPagerAdapter(ArrayList<Backdrop> imagePaths) {
        this.imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        if (imagePaths == null || imagePaths.isEmpty()) {
            return MIN_BACKDROP_IMAGES;
        } else if (imagePaths.size() > MAX_BACKDROP_IMAGES) {
            return MAX_BACKDROP_IMAGES;
        } else {
            return imagePaths.size();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View backdropLayout = inflater.inflate(R.layout.item_backdrop, container, false);
        if (imagePaths != null && !imagePaths.isEmpty()) {
            ImageView backdropImage = backdropLayout.findViewById(R.id.backdrop_iv);
            String path = imagePaths.get(position).getImagePath();
            NetworkUtils.downloadImage(backdropImage, path,
                    R.drawable.ic_video_camera, R.drawable.ic_video_camera);
        }
        container.addView(backdropLayout, FIRST_INDEX);
        return backdropLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
