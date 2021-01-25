package com.android.clup.model;

import androidx.annotation.NonNull;

import java.util.List;

public class AvailableDay {
    @NonNull
    private final String date;
    @NonNull
    private final List<Integer> hours;

    public AvailableDay(@NonNull final String date, @NonNull final List<Integer> hours) {
        this.date = date;
        this.hours = hours;
    }

    @NonNull
    public String getDate() {
        return this.date;
    }

    @NonNull
    public List<Integer> getHours() {
        return this.hours;
    }
}
