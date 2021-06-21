package com.android.clup.model;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReservationTest {
    private static final String DATE_STRING = "14-02-2021";
    private static final String TIME_STRING = "12:00";

    private Reservation reservation;

    private final String shopId = "1234567";
    private final String shopName = "Shop Name";
    private final Date date = Date.fromString(DATE_STRING);
    private final String uuid = "1245ythgfr6yu";
    private final LatLng coords = new LatLng(12.34567, 12.87654);

    @Before
    public void setUp() {
        date.setTime(TIME_STRING);
        this.reservation = new Reservation(this.shopId, this.shopName, this.date, this.uuid, this.coords);
    }

    @Test
    public void getShopId() {
        assertEquals(this.shopId, this.reservation.getShopId());
    }

    @Test
    public void getShopName() {
        assertEquals(this.shopName, this.reservation.getShopName());
    }

    @Test
    public void getDate() {
        assertEquals(this.date, this.reservation.getDate());
    }

    @Test
    public void getUuid() {
        assertEquals(this.uuid, this.reservation.getUuid());
    }

    @Test
    public void getCoords() {
        assertEquals(this.coords, this.reservation.getCoords());
    }

    @Test
    public void isExpired() {
        assertFalse(this.reservation.isExpired());
    }

    @Test
    public void setExpired() {
        assertFalse(this.reservation.isExpired());
        this.reservation.setExpired(true);
        assertTrue(this.reservation.isExpired());
    }

    @Test
    public void getTimeNotice() {
        assertEquals(Reservation.TimeNotice.NOT_SET, this.reservation.getTimeNotice());

        this.reservation.setTimeNotice(Reservation.TimeNotice.FIFTEEN_MINUTES);
        assertEquals(Reservation.TimeNotice.FIFTEEN_MINUTES, this.reservation.getTimeNotice());
    }

    @Test
    public void compareTo() {
        final String BEFORE_DATE = "13-02-2021";
        final String BEFORE_TIME = "11:00";
        final String AFTER_DATE = "15-02-2021";
        final String AFTER_TIME = "13:00";

        final Reservation thisReservation = this.reservation;
        final Reservation sameReservation = new Reservation(this.shopId, this.shopName, this.date, this.uuid, this.coords);

        // this comes at the same time of above reservations
        assertEquals(0, this.reservation.compareTo(thisReservation));
        assertEquals(0, this.reservation.compareTo(sameReservation));

        // preceding date and time
        final Date precedingDateAndTime = Date.fromString(BEFORE_DATE);
        precedingDateAndTime.setTime(BEFORE_TIME);
        final Reservation precedingDateAndTimeReservation = new Reservation(this.shopId, this.shopName, precedingDateAndTime, this.uuid, this.coords);

        // same date, preceding time
        final Date precedingTime = Date.fromString(DATE_STRING);
        precedingTime.setTime(BEFORE_TIME);
        final Reservation precedingTimeReservation = new Reservation(this.shopId, this.shopName, precedingTime, this.uuid, this.coords);

        //preceding date, same time
        final Date precedingDate = Date.fromString(BEFORE_DATE);
        precedingDate.setTime(TIME_STRING);
        final Reservation precedingDateReservation = new Reservation(this.shopId, this.shopName, precedingDate, this.uuid, this.coords);

        // this comes after above reservations
        assertEquals(1, this.reservation.compareTo(precedingDateAndTimeReservation));
        assertEquals(1, this.reservation.compareTo(precedingTimeReservation));
        assertEquals(1, this.reservation.compareTo(precedingDateReservation));

        // following date and time
        final Date followingDateAndTime = Date.fromString(AFTER_DATE);
        followingDateAndTime.setTime(AFTER_TIME);
        final Reservation followingDateAndTimeReservation = new Reservation(this.shopId, this.shopName, followingDateAndTime, this.uuid, this.coords);

        // same date, following time
        final Date followingTime = Date.fromString(DATE_STRING);
        followingTime.setTime(AFTER_TIME);
        final Reservation followingTimeReservation = new Reservation(this.shopId, this.shopName, followingTime, this.uuid, this.coords);

        //following date, same time
        final Date followingDate = Date.fromString(AFTER_DATE);
        followingDate.setTime(TIME_STRING);
        final Reservation followingDateReservation = new Reservation(this.shopId, this.shopName, followingDate, this.uuid, this.coords);

        // this comes before above reservations
        assertEquals(-1, this.reservation.compareTo(followingDateAndTimeReservation));
        assertEquals(-1, this.reservation.compareTo(followingTimeReservation));
        assertEquals(-1, this.reservation.compareTo(followingDateReservation));
    }

    @Test
    public void describeContents() {
        assertEquals(0, this.reservation.describeContents());
    }
}