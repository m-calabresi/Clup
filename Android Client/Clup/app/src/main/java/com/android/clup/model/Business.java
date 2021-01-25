package com.android.clup.model;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Business {
    @NonNull
    private final String name;
    @NonNull
    private final LatLng coordinates;
    @NonNull
    private final List<AvailableDay> availableDays;

    public Business(@NonNull final String name, @NonNull final LatLng coordinates, @NonNull final List<AvailableDay> availableDays) {
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
