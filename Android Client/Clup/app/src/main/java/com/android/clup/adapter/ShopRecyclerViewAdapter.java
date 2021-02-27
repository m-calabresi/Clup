package com.android.clup.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.clup.R;
import com.android.clup.api.MapsService;
import com.android.clup.model.Shop;

import java.util.List;

public class ShopRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @NonNull
    private final OnListItemClickedCallback onListItemClickedCallback;

    @NonNull
    private final List<Shop> shops;

    public class ShopViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView addressTextView;

        public ShopViewHolder(@NonNull final View v) {
            super(v);

            final View.OnClickListener cardViewOnClickListener = view ->
                    onListItemClickedCallback.onRecyclerViewItemClicked(getBindingAdapterPosition());
            final CardView cardView = v.findViewById(R.id.shop_card_view);
            cardView.setOnClickListener(cardViewOnClickListener);

            this.nameTextView = v.findViewById(R.id.name_text_view);
            this.addressTextView = v.findViewById(R.id.address_text_view);
        }
    }

    public ShopRecyclerViewAdapter(@NonNull final OnListItemClickedCallback callback,
                                   @NonNull final List<Shop> shops) {
        super();
        this.onListItemClickedCallback = callback;
        this.shops = shops;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View cardView = layoutInflater.inflate(R.layout.item_shop, parent, false);

        return new ShopViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final ShopViewHolder viewHolder = (ShopViewHolder) holder;
        final Shop shop = this.shops.get(position);

        viewHolder.nameTextView.post(() -> {
            final String name = shop.getName();
            viewHolder.nameTextView.setText(name);
        });

        viewHolder.addressTextView.post(() -> {
            final String address = MapsService.getAddressByCoordinates(viewHolder.itemView.getContext(), shop.getCoordinates());
            viewHolder.addressTextView.setText(address);
        });
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }
}
