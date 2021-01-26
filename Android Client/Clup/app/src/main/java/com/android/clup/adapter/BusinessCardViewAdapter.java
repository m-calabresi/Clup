package com.android.clup.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.clup.R;
import com.android.clup.model.Business;
import com.android.clup.viewmodel.MapViewModel;

import java.util.List;

public class BusinessCardViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final MapViewModel viewModel;
    private final List<Business> businesses;

    public static class BusinessViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView addressTextView;

        public BusinessViewHolder(@NonNull final View v) {
            super(v);
            this.nameTextView = v.findViewById(R.id.name_text_view);
            this.addressTextView = v.findViewById(R.id.address_text_view);
        }
    }

    public BusinessCardViewAdapter(@NonNull final MapViewModel viewModel, @NonNull final List<Business> businesses) {
        super();
        this.viewModel = viewModel;
        this.businesses = businesses;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View cardView = layoutInflater.inflate(R.layout.item_business, parent, false);

        return new BusinessViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final BusinessViewHolder viewHolder = (BusinessViewHolder) holder;
        final Business business = this.businesses.get(position);

        viewHolder.nameTextView.post(() -> {
            final String name = business.getName();
            viewHolder.nameTextView.setText(name);
        });

        viewHolder.addressTextView.post(() -> {
            final String address = this.viewModel.getAddressByCoordinates(business.getCoordinates());
            viewHolder.addressTextView.setText(address);
        });
    }

    @Override
    public int getItemCount() {
        return businesses.size();
    }
}
