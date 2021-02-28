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

    private static final String shopName = "Shop Name";
    private static final Date date = Date.fromString(DATE_STRING);
    private static final String uuid = "1245ythgfr6yu";
    private static final LatLng coords = new LatLng(12.34567, 12.87654);

    @Before
    public void setUp() {
        date.setTime(TIME_STRING);
        this.reservation = new Reservation(shopName, date, uuid, coords);
    }

    @Test
    public void getShopName() {
        assertEquals(shopName, this.reservation.getShopName());
    }

    @Test
    public void getDate() {
        assertEquals(date, this.reservation.getDate());
    }

    @Test
    public void getUuid() {
        assertEquals(uuid, this.reservation.getUuid());
    }

    @Test
    public void getCoords() {
        assertEquals(coords, this.reservation.getCoords());
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
        final Reservation sameReservation = new Reservation(shopName, date, uuid, coords);

        // this comes at the same time of above reservations
        assertEquals(0, this.reservation.compareTo(thisReservation));
        assertEquals(0, this.reservation.compareTo(sameReservation));

        // preceding date and time
        final Date precedingDateAndTime = Date.fromString(BEFORE_DATE);
        precedingDateAndTime.setTime(BEFORE_TIME);
        final Reservation precedingDateAndTimeReservation = new Reservation(shopName, precedingDateAndTime, uuid, coords);

        // same date, preceding time
        final Date precedingTime = Date.fromString(DATE_STRING);
        precedingTime.setTime(BEFORE_TIME);
        final Reservation precedingTimeReservation = new Reservation(shopName, precedingTime, uuid, coords);

        //preceding date, same time
        final Date precedingDate = Date.fromString(BEFORE_DATE);
        precedingDate.setTime(TIME_STRING);
        final Reservation precedingDateReservation = new Reservation(shopName, precedingDate, uuid, coords);

        // this comes after above reservations
        assertEquals(1, this.reservation.compareTo(precedingDateAndTimeReservation));
        assertEquals(1, this.reservation.compareTo(precedingTimeReservation));
        assertEquals(1, this.reservation.compareTo(precedingDateReservation));

        // following date and time
        final Date followingDateAndTime = Date.fromString(AFTER_DATE);
        followingDateAndTime.setTime(AFTER_TIME);
        final Reservation followingDateAndTimeReservation = new Reservation(shopName, followingDateAndTime, uuid, coords);

        // same date, following time
        final Date followingTime = Date.fromString(DATE_STRING);
        followingTime.setTime(AFTER_TIME);
        final Reservation followingTimeReservation = new Reservation(shopName, followingTime, uuid, coords);

        //following date, same time
        final Date followingDate = Date.fromString(AFTER_DATE);
        followingDate.setTime(TIME_STRING);
        final Reservation followingDateReservation = new Reservation(shopName, followingDate, uuid, coords);

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