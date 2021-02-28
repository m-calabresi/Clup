package com.android.clup.model;

import androidx.annotation.NonNull;

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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(expected = IndexOutOfBoundsException.class)
    public void addReservation() {
        final String shopName = "Shop Name";
        final Date date = Date.fromString("12-02-2021");
        date.setTime("12:00");
        final String uuid = "15yhr5988uh";
        final LatLng coords = new LatLng(12.65432, 6.23456);

        final Reservation reservation = new Reservation(shopName, date, uuid, coords);

        clearReservations();
        addReservation(reservation);

        this.model.getReservations().get(0);
    }

    @Test
    public void removeReservation() {
        final String shopName1 = "shopName1";
        final String shopName2 = "shopName2";

        final Date date = Date.fromString("12-02-2121");
        date.setTime("12:30");

        final String uuid1 = "ji876tgf";
        final String uuid2 = "ji876tg4w";

        final LatLng coords = new LatLng(11.75, 6.7654);

        final Reservation reservation1 = new Reservation(shopName1, date, uuid1, coords);
        final Reservation reservation2 = new Reservation(shopName2, date, uuid2, coords);

        clearReservations();
        addReservation(reservation1);
        addReservation(reservation2);

        final int beforeLength = this.model.getReservations().size();

        removeReservation(reservation1);

        final Reservation retrievedReservation = this.model.getReservations().get(0);
        final int afterLength = this.model.getReservations().size();

        assertEquals(beforeLength - 1, afterLength);

        assertEquals(shopName2, retrievedReservation.getShopName());
        assertEquals(date.plain(), retrievedReservation.getDate().plain());
        assertEquals(date.getTime(), retrievedReservation.getDate().getTime());
        assertEquals(uuid2, retrievedReservation.getUuid());
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
        addReservation(reservation);
        this.model.setSelectedReservation(reservation);
        this.model.setSelectedReservationTimeNotice(timeNotice);

        assertEquals(timeNotice, reservation.getTimeNotice());
    }

    private void clearReservations() {
        JsonParser.initReservationsFile();
        waitFor();
    }

    private void addReservation(@NonNull final Reservation reservation) {
        this.model.addReservation(reservation);
        waitFor();
    }

    private void removeReservation(@NonNull final Reservation reservation) {
        this.model.removeReservation(reservation);
        waitFor();
    }

    private void waitFor() {
        // give time to the executor to finish its job
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}