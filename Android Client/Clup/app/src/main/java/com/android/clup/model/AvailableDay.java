package com.android.clup.model;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * An available day represents a day in which it is possible to book a reservation at a certain shop.
 */
public class AvailableDay {
    @NonNull
    private final Date date;
    @NonNull
    private final List<String> times;

    public AvailableDay(@NonNull final Date date, @NonNull final List<String> times) {
        this.date = date;
        this.times = times;
    }

    @NonNull
    public Date getDate() {
        return this.date;
    }

    @NonNull
    public List<String> getTimes() {
        return this.times;
    }
}