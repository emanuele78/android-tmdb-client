package com.example.android.popularmovies2.ux;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.data.PopularMoviesContract;
import com.example.android.popularmovies2.network.NetworkUtils;

/**
 * Created by Emanuele on 13/03/2018.
 */
public class FavoritesCursorAdapter extends
        RecyclerView.Adapter<FavoritesCursorAdapter.RecyclerViewHolder> {

    private Cursor cursor;
    private ListItemClickListener listener;

    public FavoritesCursorAdapter(ListItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_preferred;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        // Indices for the _id, description, and priority columns
        int pictureIndex = cursor.getColumnIndex(PopularMoviesContract.ItemEntry.COLUMN_POSTER);
        int titleIndex = cursor.getColumnIndex(PopularMoviesContract.ItemEntry.COLUMN_TITLE);
        int typeIndex = cursor.getColumnIndex(PopularMoviesContract.ItemEntry.COLUMN_ITEM_TYPE);
        cursor.moveToPosition(position);
        // Determine the values of the wanted data
        String picture = cursor.getString(pictureIndex);
        String title = cursor.getString(titleIndex);
        int type = cursor.getInt(typeIndex);
        if (type == PopularMoviesContract.ItemEntry.MOVIE_MEDIA_TYPE) {
            holder.typeImageView.setImageResource(R.drawable.ic_movie_s);
        } else {
            holder.typeImageView.setImageResource(R.drawable.ic_tv_s);
        }
        holder.titleTextView.setText(title);
        NetworkUtils.loadImage(holder.posterImageView, picture,
                R.drawable.ic_video_camera, R.drawable.ic_video_camera);
    }

    @Override
    public int getItemCount() {
        if (cursor != null) {
            return cursor.getCount();
        }
        return 0;
    }

    public void setData(Cursor cursor) {
        this.cursor = cursor;
        if (cursor != null) {
            notifyDataSetChanged();
        }
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView titleTextView;
        ImageView posterImageView;
        ImageView typeImageView;

        public RecyclerViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.info_tv);
            posterImageView = view.findViewById(R.id.poster_iv);
            typeImageView = view.findViewById(R.id.type_iv);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                int position = getAdapterPosition();
                cursor.moveToPosition(position);
                int itemIdIndex =
                        cursor.getColumnIndex(PopularMoviesContract.ItemEntry.COLUMN_ITEM_ID);
                int itemTypeIndex =
                        cursor.getColumnIndex(PopularMoviesContract.ItemEntry.COLUMN_ITEM_TYPE);
                int recordIndex =
                        cursor.getColumnIndex(PopularMoviesContract.ItemEntry._ID);
                int itemType = cursor.getInt(itemTypeIndex);
                int itemId = cursor.getInt(itemIdIndex);
                long record = cursor.getLong(recordIndex);
                listener.onListItemClick(itemId, itemType, record);
            }
        }
    }

    public interface ListItemClickListener {

        void onListItemClick(int itemId, int itemType, long recordId);
    }
}
