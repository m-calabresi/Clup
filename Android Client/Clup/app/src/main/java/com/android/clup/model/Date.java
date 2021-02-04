package com.android.clup.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class Date {
    @NonNull
    private final Calendar calendar;
    @NonNull
    private final SimpleDateFormat sdf;

    @Nullable
    private String formatDate;
    @NonNull
    private final String plainDate;

    private Date(final int day, final int month, final int year) {
        this.calendar = Calendar.getInstance(Locale.getDefault());
        this.calendar.set(Calendar.YEAR, year);
        this.calendar.set(Calendar.MONTH, month - 1);
        this.calendar.set(Calendar.DAY_OF_MONTH, day);

        this.sdf = new SimpleDateFormat("EEEE, d MMMM", Locale.getDefault());

        this.plainDate = day + "-" + month + "-" + year;
    }

    @NonNull
    public static Date fromString(@NonNull final String dateString) {
        final Integer[] dateArray = Arrays.stream(dateString.split("-")).map(Integer::parseInt).toArray(Integer[]::new);
        return new Date(dateArray[0], dateArray[1], dateArray[2]);
    }

    @NonNull
    public String formatted() {
        if (this.formatDate == null) {
            final String formatDate = this.sdf.format(calendar.getTime());
            this.formatDate = formatDate.substring(0, 1).toUpperCase() + formatDate.substring(1);
        }
        return this.formatDate;
    }

    @NonNull
    public String plain() {
        return this.plainDate;
    }

    /**
     * Takes as input an hour and outputs the complete time in millis.
     * Hour must be in the format hh:mm.
     */
    public double toMillis(@NonNull final String hour) {
        final Integer[] timeArray = Arrays.stream(hour.split(":")).map(Integer::parseInt).toArray(Integer[]::new);
        this.calendar.set(Calendar.HOUR_OF_DAY, timeArray[0]);
        this.calendar.set(Calendar.MINUTE, timeArray[1]);
        this.calendar.set(Calendar.SECOND, 0);
        this.calendar.set(Calendar.MILLISECOND, 0);

        return this.calendar.getTimeInMillis();
    }

    public static double minutesToMillis(final int minutes) {
        return minutes * 60 * 1000;
    }
}
