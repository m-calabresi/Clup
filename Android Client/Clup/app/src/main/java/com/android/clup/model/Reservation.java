package com.android.clup.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.android.clup.R;
import com.google.android.gms.maps.model.LatLng;

/**
 * A class representing a reservation made by the user.
 */
public class Reservation implements Comparable<Reservation>, Parcelable {
    /**
     * The amount of time in advance (w.r.t. the appointment) the user wants to be notified.
     */
    public static class TimeNotice {
        /**
         * The user has never interacted with the reservation and no notice has been specified.
         */
        public static final int NOT_SET = -2;
        /**
         * The user has interacted with the reservation and he has chosen not to be notified.
         */
        public static final int DISABLED = -1;
        /**
         * The user has chosen to be notified 15 minutes in advance.
         */
        public static final int FIFTEEN_MINUTES = 15;
        /**
         * The user has chosen to be notified 30 minutes in advance.
         */
        public static final int THIRTY_MINUTES = 30;
        /**
         * The user has chosen to be notified 1 hour in advance.
         */
        public static final int ONE_HOUR = 60;
        /**
         * The user has chosen to be notified 2 hours in advance.
         */
        public static final int TWO_HOURS = 120;

        /**
         * Return the locale string representing the given integer time notice.
         */
        @NonNull
        public static String toTimeString(@NonNull final Context context, final int timeNotice) {
            @StringRes int timeNoticeId;

            switch (timeNotice) {
                case Reservation.TimeNotice.FIFTEEN_MINUTES:
                    timeNoticeId = R.string.time_fifteen_minutes;
                    break;
                case Reservation.TimeNotice.THIRTY_MINUTES:
                    timeNoticeId = R.string.time_thirty_minutes;
                    break;
                case Reservation.TimeNotice.ONE_HOUR:
                    timeNoticeId = R.string.time_one_hour;
                    break;
                case Reservation.TimeNotice.TWO_HOURS:
                    timeNoticeId = R.string.time_two_hours;
                    break;
                default:
                    throw new RuntimeException("Time notice for this reservation is set to NOT_SET, did you properly handle the notification logic?");
            }
            return context.getResources().getString(timeNoticeId);
        }
    }

    @NonNull
    public static final Parcelable.Creator<Reservation> CREATOR = new Parcelable.Creator<Reservation>() {
        public Reservation createFromParcel(@NonNull final Parcel in) {
            return new Reservation(in);
        }

        public Reservation[] newArray(final int size) {
            return new Reservation[size];
        }
    };

    /**
     * The name of the shop at which the user booked his reservation.
     */
    @NonNull
    private final String shopName;
    /**
     * The date at which the user booked his reservation.
     */
    @NonNull
    private final Date date;
    /**
     * The unique identifier associated to this reservation. It ill be used from the server side
     * logic to manage reservations and from the client application to build the corresponding QR code.
     */
    @NonNull
    private final String uuid;
    /**
     * The coordinates at which the store is located.
     */
    @NonNull
    private final LatLng coords;
    /**
     * The time in advance (w.r.t the reservation time) at which the user wants to be notified.
     *
     * @see TimeNotice
     */
    private int timeNotice;

    public Reservation(@NonNull final String shopName, @NonNull final Date date,
                       @NonNull final String uuid, @NonNull final LatLng coords,
                       final int timeNotice) {
        this.shopName = shopName;
        this.date = date;
        this.uuid = uuid;
        this.coords = coords;
        this.timeNotice = timeNotice;
    }

    public Reservation(@NonNull final String shopName, @NonNull final Date date,
                       @NonNull final String uuid, @NonNull final LatLng coords) {
        this(shopName, date, uuid, coords, TimeNotice.NOT_SET);
    }

    private Reservation(@NonNull final Parcel in) {
        this.shopName = in.readString();
        this.date = in.readParcelable(Date.class.getClassLoader());
        this.uuid = in.readString();
        this.coords = in.readParcelable(LatLng.class.getClassLoader());
        this.timeNotice = in.readInt();
    }

    @NonNull
    public String getShopName() {
        return this.shopName;
    }

    @NonNull
    public Date getDate() {
        return this.date;
    }

    @NonNull
    public String getUuid() {
        return this.uuid;
    }

    @NonNull
    public LatLng getCoords() {
        return this.coords;
    }

    void setTimeNotice(final int timeNotice) {
        this.timeNotice = timeNotice;
    }

    public int getTimeNotice() {
        return this.timeNotice;
    }

    /**
     * Compare the current reservation with the given one: a reservation comes before another
     * if its book date and time precedes the other's, a reservation comes after another if its book
     * date and time follows the other's, otherwise the two reservations are scheduled for the same
     * date and time.
     * <p>
     * This method returns -1 if the current reservation comes before the given one, 1 if the
     * current reservation comes after the other, 0 otherwise.
     */
    @Override
    public int compareTo(@NonNull final Reservation reservation) {
        return Long.compare(this.date.toMillis(), reservation.getDate().toMillis());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
        dest.writeString(this.shopName);
        dest.writeParcelable(this.date, flags);
        dest.writeString(this.uuid);
        dest.writeParcelable(this.coords, flags);
        dest.writeInt(this.timeNotice);
    }
}
