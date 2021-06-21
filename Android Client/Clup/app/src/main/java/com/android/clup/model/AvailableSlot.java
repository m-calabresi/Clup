package com.android.clup.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A portion of an {@link AvailableDay} characterized by a time and a list of enqueued customers.
 */
public class AvailableSlot {
    /**
     * The time of this slot in the format hh:mm.
     */
    @NonNull
    private final String time;
    /**
     * List o Customers in the Queue before the current active Customers.
     */
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

    /**
     * Return the hour portion of the {@code time} field for the current {@link AvailableSlot}.
     */
    @NonNull
    public String getHour() {
        return this.time.split(":")[0];
    }

    @NonNull
    public List<String> getEnqueuedCustomersNames() {
        return this.enqueuedCustomersNames;
    }

    /**
     * Sort the received Lists of {@link AvailableSlot}s according to the given {@link Date}s.
     * In particular, this method receives a list sorted from monday to friday and
     * returns a list sorted from {@code dates}' first day to {@code dates}' last day.
     */
    @NonNull
    public static List<List<AvailableSlot>> sortAvailableSlotsBy(@NonNull final List<List<AvailableSlot>> availableSlots, @NonNull final List<Date> dates) {
        final List<List<AvailableSlot>> sortedAvailableSlots = new ArrayList<>(availableSlots.size());

        int dateIndex = dates.get(0).getDayOfWeek(); // start from the the first date and get the day of week

        final int weekSlots = availableSlots.size();
        for (int i = 0; i < weekSlots; i++) {
            sortedAvailableSlots.add(availableSlots.get(dateIndex));
            dateIndex = (dateIndex + 1) % weekSlots; // get the next day circularly (after thursday again monday)
        }
        return sortedAvailableSlots;
    }
}
