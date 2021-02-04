package com.android.clup.model;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * A shop that is available to receive booking requests for reservations.
 */
public class Shop {
    /**
     * The name of the shop.
     */
    @NonNull
    private final String name;
    /**
     * The coordinates of the shop.
     */
    @NonNull
    private final LatLng coordinates;
    /**
     * The list of available days associated to a shop.
     */
    @NonNull
    private final List<AvailableDay> availableDays;

    public Shop(@NonNull final String name, @NonNull final LatLng coordinates, @NonNull final List<AvailableDay> availableDays) {
        this.name = name;
        this.coordinates = coordinates;
        this.availableDays = availableDays;
    }

    @NonNull
    public String getName() {
        return this.name;
    }

    @NonNull
    public LatLng getCoordinates() {
        return this.coordinates;
    }

    @NonNull
    public List<AvailableDay> getAvailableDays() {
        return this.availableDays;
    }
}
