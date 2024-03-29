package com.android.clup.model;

import android.annotation.SuppressLint;
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
     * The amount of time in milliseconds during which an expired reservation remains visible to
     * the user (default is 1 hour).
     */
    public static final long EXPIRE_TIME = 60 * 60 * 1000;

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
         * The user has chosen to be notified now (for demo purposes only).
         */
        public static final int NOW = 0;
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
                case Reservation.TimeNotice.NOW:
                    timeNoticeId = R.string.time_now;
                    break;
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

        /**
         * Return the complete locale string representing the given integer time notice.
         */
        @NonNull
        public static String toCompleteTimeString(@NonNull final Context context, final int timeNotice) {
            @StringRes int timeNoticeId;

            switch (timeNotice) {
                case Reservation.TimeNotice.NOW:
                    timeNoticeId = R.string.array_item_now;
                    break;
                case Reservation.TimeNotice.FIFTEEN_MINUTES:
                    timeNoticeId = R.string.array_item_fifteen_minutes_before;
                    break;
                case Reservation.TimeNotice.THIRTY_MINUTES:
                    timeNoticeId = R.string.array_item_thirty_minutes_before;
                    break;
                case Reservation.TimeNotice.ONE_HOUR:
                    timeNoticeId = R.string.array_item_one_hour_before;
                    break;
                case Reservation.TimeNotice.TWO_HOURS:
                    timeNoticeId = R.string.array_item_two_hours_before;
                    break;
                default:
                    throw new RuntimeException("Time notice for this reservation is set to NOT_SET, did you properly handle the notification logic?");
            }
            return context.getResources().getString(timeNoticeId);
        }
    }

    @NonNull
    public static final Parcelable.Creator<Reservation> CREATOR = new Parcelable.Creator<Reservation>() {
        @NonNull
        public Reservation createFromParcel(@NonNull final Parcel in) {
            return new Reservation(in);
        }

        @NonNull
        public Reservation[] newArray(final int size) {
            return new Reservation[size];
        }
    };

    /**
     * The id of the shop at which the user booked his reservation.
     */
    @NonNull
    private final String shopId;

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

    /**
     * Whether the current reservation has expired or not. Expired reservations will still be visible
     * for a given amount of time to account for user delay.
     */
    private boolean expired;

    /**
     * The status of a {@link Reservation}.
     */
    public static class Status {
        /**
         * A {@link Reservation} in this status has been successfully booked but the Customer
         * isn't arrived to the store yet. This means that either the time hasn't yet come, or the
         * Customer is late / has lost his appointment and not enough time is passed and the system
         * hasn't automatically cancelled his reservation yet.
         */
        @NonNull
        public static final String TODO = "todo";
        /**
         * A {@link Reservation} with this status has been successfully booked and then closed.
         * This means that either the customer has successfully cleared his appointment, or he lost his
         * turn and the system cancelled his reservation automatically due to the time has expired.
         */
        @NonNull
        public static final String DONE = "done";
    }

    public Reservation(@NonNull final String shopId, @NonNull final String shopName,
                       @NonNull final Date date, @NonNull final String uuid,
                       @NonNull final LatLng coords, final int timeNotice) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.date = date;
        this.uuid = uuid;
        this.coords = coords;
        this.timeNotice = timeNotice;
        this.expired = false;
    }

    public Reservation(@NonNull final String shopId, @NonNull final String shopName, @NonNull final Date date,
                       @NonNull final String uuid, @NonNull final LatLng coords) {
        this(shopId, shopName, date, uuid, coords, TimeNotice.NOT_SET);
    }

    @SuppressLint("ParcelClassLoader")
    private Reservation(@NonNull final Parcel in) {
        this.shopId = in.readString();
        this.shopName = in.readString();
        this.date = in.readParcelable(Date.class.getClassLoader());
        this.uuid = in.readString();
        this.coords = in.readParcelable(LatLng.class.getClassLoader());
        this.timeNotice = in.readInt();
        this.expired = (Boolean) in.readValue(null);
    }

    @NonNull
    public String getShopId() {
        return this.shopId;
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

    public void setExpired(final boolean expired) {
        this.expired = expired;
    }

    public boolean isExpired() {
        return this.expired;
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
        dest.writeString(this.shopId);
        dest.writeString(this.shopName);
        dest.writeParcelable(this.date, flags);
        dest.writeString(this.uuid);
        dest.writeParcelable(this.coords, flags);
        dest.writeInt(this.timeNotice);
        dest.writeValue(this.expired);
    }
}
