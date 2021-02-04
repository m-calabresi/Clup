package com.android.clup.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.android.clup.R;
import com.google.android.gms.maps.model.LatLng;

public class Reservation {
    public static class NotificationStatus {
        public static final int NOT_SET = -1;
        public static final int DISABLED = 0;
        public static final int ENABLED = 1;
    }

    public static class TimeNotice {
        public static final int NOT_SET = -1;
        public static final int FIFTEEN_MINUTES = 15;
        public static final int THIRTY_MINUTES = 30;
        public static final int ONE_HOUR = 60;
        public static final int TWO_HOURS = 120;

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
    private final String shopName;
    @NonNull
    private final Date date;
    @NonNull
    private final String hour;
    @NonNull
    private final String uuid;
    @NonNull
    private final LatLng coords;
    private int notificationStatus;
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
