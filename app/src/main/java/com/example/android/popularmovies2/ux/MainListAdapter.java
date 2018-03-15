package com.example.android.popularmovies2.ux;
/**
 * Created by Emanuele on 21/02/2018.
 */
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

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by Emanuele on 20/02/2018.
 */
public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.RecyclerViewHolder> {

    private ArrayList<Video> mVideoList;
    private RecyclerViewHolder mViewHolder;
    private final ListItemClickListener mOnClickListener;
    private String mMode;
    private String mSort;

    public MainListAdapter(ListItemClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_main_list;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        mViewHolder = new RecyclerViewHolder(view);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(final MainListAdapter.RecyclerViewHolder holder, int position) {
        //video object can be movie or tv show
        Video video = mVideoList.get(position);
        Context context = holder.backgroundImageView.getContext();
        //showing release year
        loadReleaseYear(holder.yearTextView, video);
        //showing rating/popularity and small icon
        loadValuesAndIcons(holder.scoreTextView, video, holder);
        //download poster
        NetworkUtils.downloadImage(holder.posterImageView, video.getPosterPath(),
                R.drawable.ic_video_camera, R.drawable.ic_video_camera);
    }

    private void loadReleaseYear(TextView yearView, Video video) {
        String releaseDate;
        if (mMode.equals(TmdbClient.MOVIE_MODE)) {
            releaseDate = video.getReleaseDate();
        } else {
            releaseDate = video.getFirstAired();
        }
        if (releaseDate == null) {
            releaseDate = yearView.getContext().getResources().getString(R.string.not_available_short);
        } else {
            //only year from pattern yyyy-mm-dd
            releaseDate = releaseDate.split("-")[0];
        }
        yearView.setText(releaseDate);
    }

    private void loadValuesAndIcons(TextView scoreView, Video video,
                                    MainListAdapter.RecyclerViewHolder holder) {
        float value;
        int decimalPlace = 1;
        if (mSort.equals(TmdbClient.TOP_RATED_SORTING)) {
            value = video.getAverageVote();
            holder.smallIconImageView.setImageResource(R.drawable.ic_poster_star_detail);
        } else {
            //popularity need to be rounded
            value = BigDecimal.valueOf(video.getPopularity()).setScale(decimalPlace,
                    BigDecimal.ROUND_UP).floatValue();
            holder.smallIconImageView.setImageResource(R.drawable.ic_poster_tap_detail);
        }
        scoreView.setText(String.valueOf(value));
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

    public void setData(ArrayList<Video> newData, @TmdbClient.WorkingMode String mode,
                        @TmdbClient.SortMode String sort) {
        mVideoList = newData;
        mMode = mode;
        mSort = sort;
        notifyDataSetChanged();
    }

    public void appendData(ArrayList<Video> newData) {
        if (newData != null || !newData.isEmpty()) {
            mVideoList.addAll(newData);
            notifyDataSetChanged();
        }
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView yearTextView;
        TextView scoreTextView;
        ImageView posterImageView;
        ImageView backgroundImageView;
        ImageView smallIconImageView;

        public RecyclerViewHolder(View view) {
            super(view);
            yearTextView = view.findViewById(R.id.year_tv);
            scoreTextView = view.findViewById(R.id.score_tv);
            posterImageView = view.findViewById(R.id.poster_iv);
            backgroundImageView = view.findViewById(R.id.background_image);
            smallIconImageView = view.findViewById(R.id.small_icon_image);
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

