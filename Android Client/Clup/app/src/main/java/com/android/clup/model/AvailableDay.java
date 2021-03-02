package com.android.clup.model;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.NoSuchElementException;

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

    @NonNull
    public AvailableSlot getAvailableSlot(@NonNull final String time) {
        for (final AvailableSlot availableSlot : this.availableSlots)
            if (availableSlot.getTime().equals(time))
                return availableSlot;

        throw new NoSuchElementException("No AvailableSlot found that matches the given time: " + time);
    }
}
