package com.example.android.popularmovies2.ux;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.model.Author;
import com.example.android.popularmovies2.network.NetworkUtils;

import java.util.ArrayList;

/**
 * Created by Emanuele on 02/03/2018.
 */
public class AuthorRecyclerViewAdapter extends
        RecyclerView.Adapter<AuthorRecyclerViewAdapter.RecyclerViewHolder> {

    private final ArrayList<Author> authors;
    private final OnAuthorItemClick listener;

    public AuthorRecyclerViewAdapter(ArrayList<Author> authors, String notAvailable,
                                     OnAuthorItemClick listener) {
        if (authors == null || authors.isEmpty()) {
            this.authors = new ArrayList<>();
            this.authors.add(new Author(notAvailable));
            this.listener = null;
        } else {
            this.authors = authors;
            this.listener = listener;
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_person;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        Author author = authors.get(position);
        holder.originalNameTextView.setText(author.getAuthorName());
        String profilePic = author.getAuthorPic();
        if (profilePic != null) {
            NetworkUtils.downloadImage(holder.personImageView, profilePic,
                    R.drawable.ic_person_shape, R.drawable.ic_person_shape);
        } else {
            holder.personImageView.setImageResource(R.drawable.ic_person_shape);
        }
    }

    @Override
    public int getItemCount() {
        return authors.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView originalNameTextView;
        ImageView personImageView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            originalNameTextView = itemView.findViewById(R.id.original_name_tv);
            personImageView = itemView.findViewById(R.id.person_iv);
            TextView roleNameTextView = itemView.findViewById(R.id.role_name_tv);
            roleNameTextView.setVisibility(View.GONE);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onClick(authors.get(getAdapterPosition()).getAuthorId());
            }
        }
    }

    public interface OnAuthorItemClick {

        void onClick(int castId);
    }
}
