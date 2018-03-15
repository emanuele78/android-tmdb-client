package com.example.android.popularmovies2.ux;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.model.OnItemClick;
import com.example.android.popularmovies2.model.Video;
import com.example.android.popularmovies2.network.NetworkUtils;
import com.example.android.popularmovies2.network.TmdbClient;

import java.util.ArrayList;

/**
 * Created by Emanuele on 06/03/2018.
 */
public class GenericRecyclerViewAdapter extends
        RecyclerView.Adapter<GenericRecyclerViewAdapter.RecyclerViewHolder> {

    private String loadMoreLabel;
    private final String notAvailable;
    private ArrayList<Video> videos = new ArrayList<>();
    private final OnItemClick listener;
    @TmdbClient.WorkingMode
    private String mode;

    public GenericRecyclerViewAdapter(String mode, String notAvailable, OnItemClick listener) {
        this.notAvailable = notAvailable;
        this.mode = mode;
        this.listener = listener;
        //fake item for layout
        videos.add(new Video(notAvailable));
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_search_small;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        Video video = videos.get(position);
        String pic;
        switch (mode) {
            case TmdbClient.PEOPLE_MODE:
                String name = video.getName();
                if (name == null || name.isEmpty()) {
                    name = notAvailable;
                }
                holder.infoTextView.setText(name);
                pic = video.getProfilePath();
                if (pic != null) {
                    NetworkUtils.downloadImage(holder.picImageView, pic,
                            R.drawable.ic_person_shape, R.drawable.ic_person_shape);
                } else {
                    if (name.equals(loadMoreLabel)) {
                        holder.picImageView.setImageResource(R.drawable.ic_more);
                    } else {
                        holder.picImageView.setImageResource(R.drawable.ic_person_shape);
                    }
                }
                break;
            case TmdbClient.TV_MODE:
            case TmdbClient.MOVIE_MODE:
                String title = video.getTitle();
                if (title == null || title.isEmpty()) {
                    title = notAvailable;
                }
                holder.infoTextView.setText(title);
                pic = video.getPosterPath();
                if (pic != null) {
                    NetworkUtils.downloadImage(holder.picImageView, pic,
                            R.drawable.ic_video_camera, R.drawable.ic_video_camera);
                } else {
                    if (title.equals(loadMoreLabel)) {
                        holder.picImageView.setImageResource(R.drawable.ic_more);
                    } else {
                        holder.picImageView.setImageResource(R.drawable.ic_video_camera);
                    }
                }
        }
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public ArrayList<Video> getData() {
        return this.videos;
    }

    public void setData(ArrayList<Video> videos, String loadMoreLabel) {
        if (videos == null || videos.isEmpty()) {
            this.videos = new ArrayList<>();
            this.videos.add(new Video(notAvailable));
        } else {
            this.videos = videos;
        }
        this.loadMoreLabel = loadMoreLabel;
        notifyDataSetChanged();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView infoTextView;
        ImageView picImageView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            infoTextView = itemView.findViewById(R.id.info_tv);
            picImageView = itemView.findViewById(R.id.poster_iv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onClick(videos.get(getAdapterPosition()).getVideoId());
            }
        }
    }
}

