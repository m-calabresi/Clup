package com.android.clup.model;

import androidx.annotation.NonNull;

import java.util.List;

public class AvailableDay {
    @NonNull
    private final Date date;
    @NonNull
    private final List<String> hours;

    public AvailableDay(@NonNull final Date date, @NonNull final List<String> hours) {
        this.date = date;
        this.hours = hours;
    }

    @NonNull
    public String getFormatDate() {
        return this.date.getFormatDate();
    }

    @NonNull
    public List<String> getHours() {
        return this.hours;
    }
}
