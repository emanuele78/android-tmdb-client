package com.example.android.popularmovies2.ux;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.model.MovieTrailer;
import com.example.android.popularmovies2.network.NetworkUtils;

import java.util.ArrayList;

/**
 * Created by Emanuele on 27/02/2018.
 */
public class TrailerViewPagerAdapter extends PagerAdapter {

    private ArrayList<MovieTrailer> trailers;
    private final OnTrailerClick trailerListener;
    private final OnShareClick shareListener;
    private String notAvailable;

    public TrailerViewPagerAdapter(ArrayList<MovieTrailer> trailers, String notAvailable,
                                   OnTrailerClick trailerListener, OnShareClick shareListener) {
        this.trailers = trailers;
        this.notAvailable = notAvailable;
        this.trailerListener = trailerListener;
        this.shareListener = shareListener;
    }

    @Override
    public int getCount() {
        if (trailers == null || trailers.isEmpty()) {
            return 1;
        } else {
            return trailers.size();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View layoutView = inflater.inflate(R.layout.item_trailer, container, false);
        ImageView playView = layoutView.findViewById(R.id.play_iv);
        ImageView shareView = layoutView.findViewById(R.id.share_iv);
        View view = layoutView.findViewById(R.id.ripple_view);
        TextView trailerDescription = layoutView.findViewById(R.id.trailer_name_tv);
        if (trailers != null && !trailers.isEmpty()) {
            playView.setVisibility(View.VISIBLE);
            shareView.setVisibility(View.VISIBLE);
            ImageView trailerImage = layoutView.findViewById(R.id.trailer_iv);
            String key = trailers.get(position).getTrailerKey();
            NetworkUtils.downloadYoutubeImage(trailerImage, key,
                    R.drawable.ic_video_camera, R.drawable.ic_video_camera);
            trailerDescription.setText(trailers.get(position).getTrailerName());
        } else {
            playView.setVisibility(View.INVISIBLE);
            shareView.setVisibility(View.INVISIBLE);
            trailerDescription.setText(notAvailable);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = getTrailerKey(position);
                if (key != null) {
                    trailerListener.onClick(key);
                }
            }
        });
        shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = getTrailerKey(position);
                if (key != null) {
                    shareListener.onClick(key);
                }
            }
        });
        container.addView(layoutView, 0);
        return layoutView;
    }

    private String getTrailerKey(int index) {
        if (trailers != null && !trailers.isEmpty()) {
            MovieTrailer trailer = trailers.get(index);
            return trailer.getTrailerKey();
        } else {
            return null;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public interface OnTrailerClick {

        void onClick(String trailerKey);
    }

    public interface OnShareClick {

        void onClick(String trailerKey);
    }
}
