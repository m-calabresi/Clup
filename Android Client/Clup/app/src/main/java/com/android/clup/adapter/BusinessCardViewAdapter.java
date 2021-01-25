package com.android.clup.adapter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.clup.R;
import com.android.clup.model.Business;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class BusinessCardViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Business> businesses;
    private final Geocoder geocoder;

    public static class BusinessViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView addresseTextView;

        public BusinessViewHolder(@NonNull final View v) {
            super(v);
            this.nameTextView = v.findViewById(R.id.name_text_view);
            this.addresseTextView = v.findViewById(R.id.address_text_view);
        }
    }

    public BusinessCardViewAdapter(@NonNull final Context context, @NonNull final List<Business> businesses) {
        super();
        this.businesses = businesses;
        this.geocoder = new Geocoder(context, Locale.getDefault());
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

        viewHolder.addresseTextView.post(() -> {
            String address = "";
            String city = "";
            String state = "";

            try {
                final List<Address> addresses = this.geocoder.
                        getFromLocation(business.getCoordinates().latitude,
                                business.getCoordinates().longitude,
                                1);
                if (addresses != null && addresses.size() > 0) {
                    address = addresses.get(0).getAddressLine(0);
                    city = addresses.get(0).getLocality();
                    state = addresses.get(0).getAdminArea();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            String completeAddress = "";
            completeAddress += !address.equals("") ? address + ",\n" : "";
            completeAddress += !city.equals("") ? city + ", " : "";
            completeAddress += !state.equals("") ? state : "";
            // if no address, city and state provided, dummy string
            completeAddress = !completeAddress.equals("") ? completeAddress : "Unknown location"; // TODO replace with resource string

            viewHolder.addresseTextView.setText(completeAddress);
        });
    }

    @Override
    public int getItemCount() {
        return businesses.size();
    }
}
