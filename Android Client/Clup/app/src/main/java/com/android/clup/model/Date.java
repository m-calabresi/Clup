package com.android.clup.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

/**
 * A date class holding date and hour.
 */
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

    /**
     * Create a {@code Date} object from the given string.
     * String must contain a valid date in the format {@code dd-mm-yyyy}
     */
    @NonNull
    public static Date fromString(@NonNull final String dateString) {
        final Integer[] dateArray = Arrays.stream(dateString.split("-")).map(Integer::parseInt).toArray(Integer[]::new);
        return new Date(dateArray[0], dateArray[1], dateArray[2]);
    }

    /**
     * Return a formatted representation of the date following the convention: {@code EEEE, d MMMM}.
     * <p>
     * For example {@code Monday, 8 february}.
     * <p>
     * First letter is uppercase, content is displayed according to the current device locale.
     */
    @NonNull
    public String formatted() {
        if (this.formatDate == null) {
            final String formatDate = this.sdf.format(calendar.getTime());
            this.formatDate = formatDate.substring(0, 1).toUpperCase() + formatDate.substring(1);
        }
        return this.formatDate;
    }

    /**
     * Return a plain representation of the date  following the convention: {@code dd-mm-yyyy}.
     * <p>
     * For example {@code 08-12-2021}.
     */
    @NonNull
    public String plain() {
        return this.plainDate;
    }

    /**
     * Take as input an hour and outputs the complete time in millis.
     * Hour must be in the format {@code hh:mm}.
     * <p>
     * Complete time is calculated by setting to 0 seconds and milliseconds, reusing the current date
     * and adding the given hour.
     */
    public double toMillis(@NonNull final String hour) {
        final Integer[] timeArray = Arrays.stream(hour.split(":")).map(Integer::parseInt).toArray(Integer[]::new);
        this.calendar.set(Calendar.HOUR_OF_DAY, timeArray[0]);
        this.calendar.set(Calendar.MINUTE, timeArray[1]);
        this.calendar.set(Calendar.SECOND, 0);
        this.calendar.set(Calendar.MILLISECOND, 0);

        return this.calendar.getTimeInMillis();
    }

    /**
     * Take the given amount of time in {@code minutes}, convert them into  {@code milliseconds}
     * and return them.
     */
    public static double minutesToMillis(final int minutes) {
        return minutes * 60 * 1000;
    }
}
