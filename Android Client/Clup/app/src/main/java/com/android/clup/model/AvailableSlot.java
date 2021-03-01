package com.android.clup.model;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * A portion of an {@link AvailableDay} characterized by a time and a list of enqueued customers.
 */
public class AvailableSlot {
    @NonNull
    private final String time;
    @NonNull
    private final List<String> enqueuedCustomersNames;

    public AvailableSlot(@NonNull final String time, @NonNull final List<String> enqueuedCustomersNames) {
        this.time = time;
        this.enqueuedCustomersNames = enqueuedCustomersNames;
    }

    @NonNull
    public String getTime() {
        return this.time;
    }

    @NonNull
    public List<String> getEnqueuedCustomersNames() {
        return this.enqueuedCustomersNames;
    }
}
