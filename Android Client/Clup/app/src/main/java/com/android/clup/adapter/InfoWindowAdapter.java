package com.android.clup.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.clup.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * This class styles the popup layout that appears when a user taps on a marker on the map.
 */
public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    @NonNull
    private final View parentView;

    public InfoWindowAdapter(@NonNull final Context context) {
        parentView = View.inflate(context, R.layout.item_info_window, null);
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull final Marker marker) {
        if (marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
            marker.showInfoWindow();
        }
        return null;
    }

    /**
     * Insert the information provided by the given {@link Marker} inside a custom layout.
     */
    @NonNull
    @Override
    public View getInfoWindow(@NonNull final Marker marker) {
        final TextView titleTextView = parentView.findViewById(R.id.title_info_window);
        final TextView addressInfoWindow = parentView.findViewById(R.id.address_info_window);

        titleTextView.setText(marker.getTitle());
        addressInfoWindow.setText(marker.getSnippet());

        return parentView;
    }
}
