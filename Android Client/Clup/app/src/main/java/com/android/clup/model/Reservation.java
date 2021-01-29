package com.android.clup.model;

import androidx.annotation.NonNull;

public class Reservation {
    @NonNull
    private final String shopName;
    @NonNull
    private final Date date;
    @NonNull
    private final String hour;
    @NonNull
    private final String uuid;

    public Reservation(@NonNull final String shopName, @NonNull final Date date, @NonNull final String hour, @NonNull final String uuid) {
        this.shopName = shopName;
        this.date = date;
        this.hour = hour;
        this.uuid = uuid;
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
}
