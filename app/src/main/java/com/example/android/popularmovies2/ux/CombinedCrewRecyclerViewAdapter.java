package com.example.android.popularmovies2.ux;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.model.CombinedCreditCrew;
import com.example.android.popularmovies2.network.NetworkUtils;

import java.util.ArrayList;

/**
 * Created by Emanuele on 02/03/2018.
 */
public class CombinedCrewRecyclerViewAdapter extends
        RecyclerView.Adapter<CombinedCrewRecyclerViewAdapter.RecyclerViewHolder> {

    private final ArrayList<CombinedCreditCrew> crews;
    private final OnMediaCrewItemClick listener;

    public CombinedCrewRecyclerViewAdapter(ArrayList<CombinedCreditCrew> crews,
                                           String notAvailable, OnMediaCrewItemClick listener) {
        if (crews == null || crews.isEmpty()) {
            this.crews = new ArrayList<>();
            this.crews.add(new CombinedCreditCrew(notAvailable));
            this.listener = null;
        } else {
            this.crews = crews;
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
        CombinedCreditCrew crew = crews.get(position);
        holder.originalNameTextView.setText(crew.getJob());
        holder.roleNameTextView.setText(crew.getTitle());
        String posterPath = crew.getPosterPath();
        if (posterPath != null) {
            NetworkUtils.downloadImage(holder.personImageView, posterPath,
                    R.drawable.ic_person_shape, R.drawable.ic_person_shape);
        } else {
            holder.personImageView.setImageResource(R.drawable.ic_person_shape);
        }
    }

    @Override
    public int getItemCount() {
        return crews.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView originalNameTextView;
        TextView roleNameTextView;
        ImageView personImageView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            originalNameTextView = itemView.findViewById(R.id.original_name_tv);
            roleNameTextView = itemView.findViewById(R.id.role_name_tv);
            personImageView = itemView.findViewById(R.id.person_iv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int mediaId = crews.get(getAdapterPosition()).getMediaId();
                String mediaType = crews.get(getAdapterPosition()).getMediaType();
                listener.onClick(mediaId, mediaType);
            }
        }
    }

    public interface OnMediaCrewItemClick {

        void onClick(int mediaId, String mediaType);
    }
}
