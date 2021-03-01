package com.android.clup.model;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * An {@code AvailableDay} represents a day in which it is possible to book a reservation at
 * a certain shop for a certain time slot.
 */
public class AvailableDay {
    @NonNull
    private final Date date;
    @NonNull
    private final List<AvailableSlot> availableSlots;

    public AvailableDay(@NonNull final Date date, @NonNull final List<AvailableSlot> availableSlots) {
        this.date = date;
        this.availableSlots = availableSlots;
    }

    @NonNull
    public Date getDate() {
        return this.date;
    }

    @NonNull
    public List<AvailableSlot> getAvailableSlots() {
        return this.availableSlots;
    }
}
