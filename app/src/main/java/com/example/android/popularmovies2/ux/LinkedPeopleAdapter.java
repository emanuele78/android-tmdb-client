package com.example.android.popularmovies2.ux;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.model.LinkedPerson;
import com.example.android.popularmovies2.network.NetworkUtils;

import java.util.ArrayList;

/**
 * Created by Emanuele on 09/03/2018.
 */
public class LinkedPeopleAdapter extends
        RecyclerView.Adapter<LinkedPeopleAdapter.RecyclerViewHolder> {

    private ArrayList<LinkedPerson> people;
    private final OnPersonItemClick listener;

    public LinkedPeopleAdapter(ArrayList<LinkedPerson> people, OnPersonItemClick listener) {
        this.people = people;
        this.listener = listener;
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
        LinkedPerson person = people.get(position);
        holder.nameTextView.setText(person.getName());
        String profilePic = person.getProfilePic();
        if (profilePic != null) {
            NetworkUtils.downloadImage(holder.profileImageView, profilePic,
                    R.drawable.ic_person_shape, R.drawable.ic_person_shape);
        } else {
            holder.profileImageView.setImageResource(R.drawable.ic_person_shape);
        }
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    public void setNewData(ArrayList<LinkedPerson> people) {
        this.people = people;
        notifyDataSetChanged();
    }

    public ArrayList<LinkedPerson> getData() {
        return people;
    }

    public void remove(int position) {
        int personId = people.get(position).getPersonId();
        people.remove(position);
        notifyItemRemoved(position);
        listener.onRemove(personId);
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nameTextView;
        ImageView profileImageView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.info_tv);
            profileImageView = itemView.findViewById(R.id.poster_iv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onClick(people.get(getAdapterPosition()).getPersonId(), getAdapterPosition());
            }
        }
    }

    public interface OnPersonItemClick {

        void onClick(int personId, int position);
        void onRemove(int personId);
    }
}