package com.android.clup.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.android.clup.R;
import com.google.android.gms.maps.model.LatLng;

/**
 * A class representing a reservation made by the user.
 */
public class Reservation {
    /**
     * The status of the notification.
     */
    public static class NotificationStatus {
        /**
         * The user has never interact with the notification UI.
         */
        public static final int NOT_SET = -1;
        /**
         * The user has interacted with the notification UI and chosen to disable notifications for
         * a specific reservation.
         */
        public static final int DISABLED = 0;
        /**
         * The user has interacted with the notification UI and chosen to enable notifications for
         * a specific reservation.
         */
        public static final int ENABLED = 1;
    }

    /**
     * The amount of time in advance (w.r.t. the appointment) the user wants to be notified.
     */
    public static class TimeNotice {
        /**
         * The user has never interacted with the reservation and no notice has been specified or
         * the user has chosen not to be notified.
         */
        public static final int NOT_SET = -1;
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
     * The hour at which the user booked his reservation.
     */
    @NonNull
    private final String hour;
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
     * The status of the notifications for the current reservation.
     *
     * @see NotificationStatus
     */
    private int notificationStatus;
    /**
     * The time in advance (w.r.t the reservation time) at which the user wants to be notified.
     *
     * @see TimeNotice
     */
    private int timeNotice;

    public Reservation(@NonNull final String shopName, @NonNull final Date date,
                       @NonNull final String hour, @NonNull final String uuid,
                       @NonNull final LatLng coords, final int notificationStatus, final int timeNotice) {
        this.shopName = shopName;
        this.date = date;
        this.hour = hour;
        this.uuid = uuid;
        this.coords = coords;
        this.notificationStatus = notificationStatus;
        this.timeNotice = timeNotice;
    }

    public Reservation(@NonNull final String shopName, @NonNull final Date date,
                       @NonNull final String hour, @NonNull final String uuid,
                       @NonNull final LatLng coords) {
        this(shopName, date, hour, uuid, coords, NotificationStatus.NOT_SET, TimeNotice.NOT_SET);
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
    public String getHour() {
        return this.hour;
    }

    @NonNull
    public String getUuid() {
        return this.uuid;
    }

    @NonNull
    public LatLng getCoords() {
        return this.coords;
    }

    void setNotificationStatus(final int notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public int getNotificationStatus() {
        return this.notificationStatus;
    }

    void setTimeNotice(final int timeNotice) {
        this.timeNotice = timeNotice;
    }

    public int getTimeNotice() {
        return this.timeNotice;
    }
}
