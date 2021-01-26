package com.android.clup.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.clup.R;
import com.android.clup.model.Market;
import com.android.clup.viewmodel.MapViewModel;

import java.util.List;

public class MarketCardViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final MapViewModel viewModel;
    private final List<Market> markets;

    public static class MarketViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView addressTextView;

        public MarketViewHolder(@NonNull final View v) {
            super(v);
            this.nameTextView = v.findViewById(R.id.name_text_view);
            this.addressTextView = v.findViewById(R.id.address_text_view);
        }
    }

    public MarketCardViewAdapter(@NonNull final MapViewModel viewModel, @NonNull final List<Market> markets) {
        super();
        this.viewModel = viewModel;
        this.markets = markets;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View cardView = layoutInflater.inflate(R.layout.item_market, parent, false);

        return new MarketViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final MarketViewHolder viewHolder = (MarketViewHolder) holder;
        final Market market = this.markets.get(position);

        viewHolder.nameTextView.post(() -> {
            final String name = market.getName();
            viewHolder.nameTextView.setText(name);
        });

        viewHolder.addressTextView.post(() -> {
            final String address = this.viewModel.getAddressByCoordinates(market.getCoordinates());
            viewHolder.addressTextView.setText(address);
        });
    }

    @Override
    public int getItemCount() {
        return markets.size();
    }
}
