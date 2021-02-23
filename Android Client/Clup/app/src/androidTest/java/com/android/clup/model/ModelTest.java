package com.android.clup.model;

import com.android.clup.json.JsonParser;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ModelTest {
    private Model model;

    @Before
    public void setUp() {
        this.model = Model.getInstance();
    }

    @Test
    public void addReservation() {
        final String shopName = "Shop Name";
        final Date date = Date.fromString("12-02-2021");
        date.setTime("12:00");
        final String uuid = "15yhr5988uh";
        final LatLng coords = new LatLng(12.65432, 6.23456);

        final Reservation reservation = new Reservation(shopName, date, uuid, coords);

        clearReservations();
        this.model.addReservation(reservation);
        final Reservation retrievedReservation = this.model.getReservations().get(0);

        assertEquals(shopName, retrievedReservation.getShopName());
        assertEquals(date.plain(), retrievedReservation.getDate().plain());
        assertEquals(date.getTime(), retrievedReservation.getDate().getTime());
        assertEquals(uuid, retrievedReservation.getUuid());
        assertEquals(coords.latitude, retrievedReservation.getCoords().latitude, 0);
        assertEquals(coords.longitude, retrievedReservation.getCoords().longitude, 0);
    }

    @Test
    public void setFriendlyName() {
        final String friendlyName = "MyFriendlyNameHere";

        this.model.setFriendlyName(friendlyName);

        assertEquals(friendlyName, Preferences.getFriendlyName());
    }

    @Test
    public void setFullname() {
        final String fullName = "MyFullNameHere";

        this.model.setFullname(fullName);

        assertEquals(fullName, Preferences.getFullname());
    }

    @Test
    public void setSelectedReservationTimeNotice() {
        final int timeNotice = Reservation.TimeNotice.FIFTEEN_MINUTES;

        final String shopName = "Shop Name";
        final Date date = Date.fromString("12-02-2021");
        date.setTime("12:00");
        final String uuid = "15yhr5988uh";
        final LatLng coords = new LatLng(12.65432, 6.23456);

        final Reservation reservation = new Reservation(shopName, date, uuid, coords);

        clearReservations();
        this.model.addReservation(reservation);
        this.model.setSelectedReservation(reservation);
        this.model.setSelectedReservationTimeNotice(timeNotice);

        assertEquals(timeNotice, reservation.getTimeNotice());
    }

    private void clearReservations() {
        JsonParser.initReservationsFile();

        // give time to the executor to finish its job
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}