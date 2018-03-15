package com.example.android.popularmovies2.ux;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.model.Cast;
import com.example.android.popularmovies2.network.NetworkUtils;

import java.util.ArrayList;

/**
 * Created by Emanuele on 27/02/2018.
 */
public class CastRecyclerViewAdapter extends
        RecyclerView.Adapter<CastRecyclerViewAdapter.RecyclerViewHolder> {

    private final ArrayList<Cast> casts;
    private final OnCastItemClick listener;

    public CastRecyclerViewAdapter(ArrayList<Cast> casts, String notAvailable,
                                   OnCastItemClick listener) {
        if (casts == null || casts.isEmpty()) {
            this.casts = new ArrayList<>();
            this.casts.add(new Cast(notAvailable));
            this.listener = null;
        } else {
            this.casts = casts;
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
        Cast cast = casts.get(position);
        holder.originalNameTextView.setText(cast.getName());
        holder.roleNameTextView.setText(cast.getCharacter());
        String profilePic = cast.getProfilePic();
        if (profilePic != null) {
            NetworkUtils.downloadImage(holder.personImageView, profilePic,
                    R.drawable.ic_person_shape, R.drawable.ic_person_shape);
        } else {
            holder.personImageView.setImageResource(R.drawable.ic_person_shape);
        }
    }

    @Override
    public int getItemCount() {
        return casts.size();
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
                listener.onClick(casts.get(getAdapterPosition()).getPersonId());
            }
        }
    }

    public interface OnCastItemClick {

        void onClick(int castId);
    }
}
