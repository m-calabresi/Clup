package com.android.clup.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * A date class holding date and time.
 */
public class Date implements Parcelable {
    private static final int DAYS_IN_A_WEEK = 7;
    public static final int BUSINESS_DAYS_IN_A_WEEK = 5;

    @NonNull
    public static final String monday = "monday";
    @NonNull
    public static final String tuesday = "tuesday";
    @NonNull
    public static final String wednesday = "wednesday";
    @NonNull
    public static final String thursday = "thursday";
    @NonNull
    public static final String friday = "friday";

    @NonNull
    public static final Creator<Date> CREATOR = new Creator<Date>() {
        @NonNull
        @Override
        public Date createFromParcel(@NonNull final Parcel in) {
            return new Date(in);
        }

        @NonNull
        @Override
        public Date[] newArray(final int size) {
            return new Date[size];
        }
    };

    @NonNull
    private final Calendar calendar;

    private Date(final int year, final int month, final int day, final int hours, final int minutes,
                 final int seconds, final int milliseconds) {
        this.calendar = Calendar.getInstance(Locale.getDefault());
        this.calendar.set(Calendar.YEAR, year);
        this.calendar.set(Calendar.MONTH, month - 1);
        this.calendar.set(Calendar.DAY_OF_MONTH, day);
        this.calendar.set(Calendar.HOUR_OF_DAY, hours);
        this.calendar.set(Calendar.MINUTE, minutes);
        this.calendar.set(Calendar.SECOND, seconds);
        this.calendar.set(Calendar.MILLISECOND, milliseconds);
    }

    protected Date(@NonNull final Parcel in) {
        this(in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt());
    }

    /**
     * Create a new {@link Date} object from the given string.
     * String must contain a valid date in the format {@code dd-MM-yyyy}.
     */
    @NonNull
    public static Date fromString(@NonNull final String dateString) {
        final Integer[] dateArray = Arrays.stream(dateString.split("-")).map(Integer::parseInt).toArray(Integer[]::new);
        return new Date(dateArray[2], dateArray[1], dateArray[0], 0, 0, 0, 0);
    }

    /**
     * Create a new {@link Date} object from the given string.
     * String must contain a valid date in the format {@code yyyy-MM-dd}.
     */
    @NonNull
    public static Date fromStringReverse(@NonNull final String dateString) {
        final LocalDate localdate =  LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ITALY));
        return Date.fromLocalDate(localdate);
    }

    /**
     * Create a new {@link Date} object from the given {@link LocalDate}.
     */
    @NonNull
    private static Date fromLocalDate(@NonNull final LocalDate localDate) {
        final String localDateString = localDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        return Date.fromString(localDateString);
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
        final SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM", Locale.getDefault());
        final String formatDate = sdf.format(calendar.getTime());
        return formatDate.substring(0, 1).toUpperCase() + formatDate.substring(1);
    }

    /**
     * Return a plain representation of the date following the convention: {@code dd-mm-yyyy}.
     * <p>
     * For example {@code 08-12-2021}.
     */
    @NonNull
    public String plain() {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    /**
     * Return a plain representation of the date following the convention: {@code yyyy-MM-dd}.
     * <p>
     * For example {@code 2021-12-08}.
     */
    @NonNull
    public String plainReversed() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    /**
     * Set the current date time to the given one.
     * Time must be in the format {@code hh:mm}.
     */
    public void setTime(@NonNull final String time) {
        final Integer[] timeArray = Arrays.stream(time.split(":")).map(Integer::parseInt).toArray(Integer[]::new);
        this.calendar.set(Calendar.HOUR_OF_DAY, timeArray[0]);
        this.calendar.set(Calendar.MINUTE, timeArray[1]);
    }

    /**
     * Return the current time in the format {@code HH:mm}.
     */
    @NonNull
    public String getTime() {
        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    /**
     * Return an integer representing the day of the week.
     * Days start from monday (0) and ends with sunday(6).
     */
    public int getDayOfWeek() {
        final int day = this.calendar.get(Calendar.DAY_OF_WEEK) -2;

        return (((day % DAYS_IN_A_WEEK) + DAYS_IN_A_WEEK) % DAYS_IN_A_WEEK);
    }

    /**
     * Returns the current time in milliseconds.
     */
    public long toMillis() {
        return this.calendar.getTimeInMillis();
    }

    /**
     * Take the given amount of time in {@code minutes}, convert them into  {@code milliseconds}
     * and return them.
     */
    public static long minutesToMillis(final int minutes) {
        return minutes * 60 * 1000;
    }

    /**
     * Return the current date in milliseconds.
     */
    public static long now() {
        return Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
        dest.writeInt(this.calendar.get(Calendar.YEAR));
        dest.writeInt(this.calendar.get(Calendar.MONTH) + 1);
        dest.writeInt(this.calendar.get(Calendar.DAY_OF_MONTH));
        dest.writeInt(this.calendar.get(Calendar.HOUR_OF_DAY));
        dest.writeInt(this.calendar.get(Calendar.MINUTE));
        dest.writeInt(this.calendar.get(Calendar.SECOND));
        dest.writeInt(this.calendar.get(Calendar.MILLISECOND));
    }

    /**
     * Return the next business week (5 days) starting from today (included).
     * A business week does not include weekends (saturday and sunday): those are skipped and
     * the next days will be returned instead.
     */
    @NonNull
    public static List<Date> getBusinessWeekFromToday() {
        final String todayString = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        return Date.getBusinessWeekFrom(todayString);
    }

    /**
     * Return the next business week (5 days) starting from the given date (included).
     * A business week does not include weekends (saturday and sunday): those are skipped and
     * the next days will be returned instead.
     * <p>
     * The given starting date must be in the format {@code dd-MM-yyyy}.
     */
    @NonNull
    public static List<Date> getBusinessWeekFrom(@NonNull final String startingDate) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate startingDateLocal = LocalDate.parse(startingDate, formatter);

        final int BUSINESS_WEEK_LENGTH = 5;
        final List<LocalDate> businessWeek = new ArrayList<>(BUSINESS_WEEK_LENGTH);

        // current day only needs weekend check
        startingDateLocal = getAdjustedDateFrom(startingDateLocal);
        businessWeek.add(startingDateLocal);

        // other business days need day increment and weekend check
        for (int i = 1; i < BUSINESS_WEEK_LENGTH; i++) {
            final LocalDate nextDate = getNextDateFrom(startingDateLocal);
            startingDateLocal = nextDate;

            businessWeek.add(nextDate);
        }
        return businessWeek.stream().map(Date::fromLocalDate).collect(Collectors.toList());
    }

    /**
     * Return the next day after {@code startingDate}, skipping weekends
     * (saturdays and sundays).
     */
    private static LocalDate getNextDateFrom(@NonNull final LocalDate startingDate) {
        final LocalDate nextDate = startingDate.plusDays(1);
        return getAdjustedDateFrom(nextDate);
    }

    private static LocalDate getAdjustedDateFrom(@NonNull final LocalDate localDate) {
        int weekendAdjustment = 0;

        if (localDate.getDayOfWeek() == DayOfWeek.SATURDAY)
            weekendAdjustment = 2;
        else if (localDate.getDayOfWeek() == DayOfWeek.SUNDAY)
            weekendAdjustment = 1;

        return localDate.plusDays(weekendAdjustment);
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if(obj == null)
            return false;
        if(!(obj instanceof Date))
            return false;
        final Date other = (Date) obj;
        return this.plain().equals(other.plain());
    }
}
