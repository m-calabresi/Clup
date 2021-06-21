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

    /**
     * Return the {@link AvailableSlot} corresponding to the given {@code time}.
     * The {@code time} parameter must be a valid time in the format {@code hh:mm}.
     */
    @NonNull
    public AvailableSlot getAvailableSlotByTime(@NonNull final String time) {
        for (final AvailableSlot availableSlot : this.availableSlots)
            if (availableSlot.getTime().equals(time))
                return availableSlot;

        throw new NoSuchElementException("No AvailableSlot found that matches the given time: " + time);
    }

    /**
     * Return the {@link AvailableSlot} corresponding to the given {@code hour}.
     * The {@code hour} parameter must be a valid integer between 0 and 24 in the format {@code hh}
     * or {@code h}.
     */
    @NonNull
    public AvailableSlot getAvailableSlotByHour(@NonNull final String hour) {
        for (final AvailableSlot availableSlot : this.availableSlots) {
            if (availableSlot.getHour().equals(hour))
                return availableSlot;
        }
        throw new NoSuchElementException("No AvailableSlot found that matches the given hour: " + hour);
    }
}
