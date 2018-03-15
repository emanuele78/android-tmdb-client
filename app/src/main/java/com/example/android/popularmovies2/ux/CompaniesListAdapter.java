package com.example.android.popularmovies2.ux;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.model.ProductionCompany;

import java.util.ArrayList;

/**
 * Created by Emanuele on 27/02/2018.
 */
public class CompaniesListAdapter extends
        RecyclerView.Adapter<CompaniesListAdapter.RecyclerViewHolder> {

    private ArrayList<ProductionCompany> companies;
    private OnCompanyClick listener;

    public CompaniesListAdapter(ArrayList<ProductionCompany> companies,
                                String emptyString, OnCompanyClick listener) {
        if (companies == null || companies.isEmpty()) {
            this.companies = new ArrayList<>();
            this.companies.add(new ProductionCompany(emptyString));
        } else {
            this.companies = companies;
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
        ProductionCompany company = companies.get(position);
        holder.genreTextView.setText(company.getProductionCompanyName());
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView genreTextView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            genreTextView = itemView.findViewById(R.id.generic_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int index = getAdapterPosition();
                listener.onClick(companies.get(index).getProductionCompanyId(),
                        companies.get(index).getProductionCompanyName());
            }
        }
    }

    public interface OnCompanyClick {

        void onClick(int companyId, String companyName);
    }
}
