package com.example.android.popularmovies2.ux;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies2.R;

import java.util.ArrayList;

/**
 * Created by Emanuele on 02/03/2018.
 */
public class TvShowRuntimeAdapter extends
        RecyclerView.Adapter<TvShowRuntimeAdapter.RecyclerViewHolder> {

    private ArrayList<String> lengths = new ArrayList<>();
    private OnRuntimeClick listener;

    public TvShowRuntimeAdapter(ArrayList<Integer> lengths, String emptyString,
                                OnRuntimeClick listener) {
        if (lengths == null || lengths.isEmpty()) {
            this.lengths.add(emptyString);
        } else {
            for (int i : lengths) {
                this.lengths.add(String.valueOf(i));
            }
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
        String length = lengths.get(position);
        holder.runtimeTextView.setText(length);
    }

    @Override
    public int getItemCount() {
        return lengths.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView runtimeTextView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            runtimeTextView = itemView.findViewById(R.id.generic_tv);
//			View view = itemView.findViewById(R.id.ripple_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int runtime = Integer.valueOf(lengths.get(getAdapterPosition()));
                listener.onClick(runtime);
            }
        }
    }

    public interface OnRuntimeClick {

        void onClick(int runtime);
    }
}
