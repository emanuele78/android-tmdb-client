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

import java.util.ArrayList;

/**
 * Created by Emanuele on 09/03/2018.
 */
public class GenericLinkedPeopleAdapter extends
        RecyclerView.Adapter<GenericLinkedPeopleAdapter.RecyclerViewHolder> {

    private ArrayList<Video> mVideoList;
    private RecyclerViewHolder mViewHolder;
    private final ListItemClickListener mOnClickListener;
    private String notAvailable;

    public GenericLinkedPeopleAdapter(String notAvailable, ListItemClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
        this.notAvailable = notAvailable;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_search_small;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        mViewHolder = new RecyclerViewHolder(view);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        Video video = mVideoList.get(position);
        String title = video.getTitle();
        if (title == null || title.isEmpty()) {
            title = notAvailable;
        }
        holder.infoTextView.setText(title);
        String pic = video.getPosterPath();
        if (pic != null) {
            NetworkUtils.downloadImage(holder.picImageView, pic,
                    R.drawable.ic_video_camera, R.drawable.ic_video_camera);
        } else {
            holder.picImageView.setImageResource(R.drawable.ic_video_camera);
        }
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
        ImageView picImageView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            infoTextView = itemView.findViewById(R.id.info_tv);
            picImageView = itemView.findViewById(R.id.poster_iv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mOnClickListener.onListItemClick(mVideoList.get(position).getVideoId());
        }
    }

    public interface ListItemClickListener {

        void onListItemClick(int position);
    }
}


