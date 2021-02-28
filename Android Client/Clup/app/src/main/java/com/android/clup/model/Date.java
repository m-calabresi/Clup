package com.android.clup.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

/**
 * A date class holding date and time.
 */
public class Date implements Parcelable {
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
     * Create a {@code Date} object from the given string.
     * String must contain a valid date in the format {@code dd-MM-yyyy}.
     */
    @NonNull
    public static Date fromString(@NonNull final String dateString) {
        final Integer[] dateArray = Arrays.stream(dateString.split("-")).map(Integer::parseInt).toArray(Integer[]::new);
        return new Date(dateArray[2], dateArray[1], dateArray[0], 0, 0, 0, 0);
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
     * Return a plain representation of the date  following the convention: {@code dd-mm-yyyy}.
     * <p>
     * For example {@code 08-12-2021}.
     */
    @NonNull
    public String plain() {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
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
}
