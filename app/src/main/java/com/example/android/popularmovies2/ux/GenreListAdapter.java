package com.example.android.popularmovies2.ux;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.model.Genre;

import java.util.ArrayList;

/**
 * Created by Emanuele on 26/02/2018.
 */
public class GenreListAdapter extends RecyclerView.Adapter<GenreListAdapter.RecyclerViewHolder> {

    private ArrayList<Genre> genres;
    private OnGenreClick listener;

    public GenreListAdapter(ArrayList<Genre> genres, String emptyString, OnGenreClick listener) {
        if (genres == null || genres.isEmpty()) {
            this.genres = new ArrayList<>();
            this.genres.add(new Genre(emptyString));
        } else {
            this.genres = genres;
            this.listener = listener;
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_generic;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        Genre genre = genres.get(position);
        holder.genericTextView.setText(genre.getGenreName());
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView genericTextView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            genericTextView = itemView.findViewById(R.id.generic_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onClick(genres.get(getAdapterPosition()).getGenreId(),
                        genres.get(getAdapterPosition()).getGenreName());
            }
        }
    }

    public interface OnGenreClick {

        void onClick(int genreId, String genreName);
    }
}
