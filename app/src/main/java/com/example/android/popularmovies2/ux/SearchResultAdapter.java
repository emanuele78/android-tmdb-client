package com.example.android.popularmovies2.ux;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.model.Video;
import com.example.android.popularmovies2.network.NetworkUtils;
import com.example.android.popularmovies2.network.TmdbClient;

import java.util.ArrayList;

/**
 * Created by Emanuele Mazzante on 04/03/2018.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.RecyclerViewHolder> {

    private ArrayList<Video> mVideoList;
    private RecyclerViewHolder mViewHolder;
    private final ListItemClickListener mOnClickListener;
    private final String emptyString;
    private final @TmdbClient.WorkingMode
    String mode;

    public SearchResultAdapter(ListItemClickListener mOnClickListener, String emptyString, String mode) {
        this.mOnClickListener = mOnClickListener;
        this.emptyString = emptyString;
        this.mode = mode;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_search;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        mViewHolder = new RecyclerViewHolder(view);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(final SearchResultAdapter.RecyclerViewHolder holder, int position) {
        //video object can be movie or tv show
        Video video = mVideoList.get(position);
        //download poster
        String pic;
        if (mode.equals(TmdbClient.PEOPLE_MODE)) {
            pic = video.getProfilePath();
            NetworkUtils.downloadImage(holder.posterImageView, pic,
                    R.drawable.ic_person_shape, R.drawable.ic_person_shape);
        } else {
            pic = video.getPosterPath();
            NetworkUtils.downloadImage(holder.posterImageView, pic,
                    R.drawable.ic_video_camera, R.drawable.ic_video_camera);
        }
        String title = video.getTitle();
        if (title == null || title.isEmpty()) {
            title = video.getName();
        }
        if (title == null || title.isEmpty()) {
            title = emptyString;
        }
        holder.infoTextView.setText(title);
    }

    @Override
    public int getItemCount() {
        if (mVideoList == null) {
            return 0;
        } else {
            return mVideoList.size();
        }
    }

    public ArrayList<Video> getData() {
        return mVideoList;
    }

    public int getCurrentPosition() {
        if (mViewHolder != null) {
            return mViewHolder.getAdapterPosition();
        } else {
            return 0;
        }
    }

    public void setData(ArrayList<Video> newData) {
        mVideoList = newData;
        notifyDataSetChanged();
    }

    public void appendData(ArrayList<Video> newData) {
        if (newData != null || !newData.isEmpty()) {
            mVideoList.addAll(newData);
            notifyDataSetChanged();
        }
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView infoTextView;
        ImageView posterImageView;

        public RecyclerViewHolder(View view) {
            super(view);
            infoTextView = view.findViewById(R.id.info_tv);
            posterImageView = view.findViewById(R.id.poster_iv);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mOnClickListener.onListItemClick(position);
        }
    }

    public interface ListItemClickListener {

        void onListItemClick(int position);
    }
}