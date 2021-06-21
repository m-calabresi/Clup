package com.android.clup.viewmodel;

import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ActivityScenario;

import com.android.clup.model.Date;
import com.android.clup.model.Model;
import com.android.clup.model.Reservation;
import com.android.clup.ui.auth.AuthActivity;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DetailsViewModelTest {
    private DetailsViewModel viewModel;

    @Before
    public void setUp() {
        try (final ActivityScenario<AuthActivity> scenario = ActivityScenario.launch(AuthActivity.class)) {
            scenario.onActivity(activity ->
                    this.viewModel = new ViewModelProvider(activity).get(DetailsViewModel.class));
        }
    }

    @Test
    public void getReservationShopName() {
        final String shopId = "1234567";
        final String shopName = "Shop Name";
        final Date date = Date.fromString("12-02-2021");
        date.setTime("12:00");
        final String uuid = "15yhr578";
        final LatLng coords = new LatLng(12.65432, 6.23456);

        final Reservation reservation = new Reservation(shopId, shopName, date, uuid, coords);

        this.viewModel.setSelectedReservation(reservation);

        assertEquals(shopName, this.viewModel.getReservationShopName());
    }

    @Test
    public void getReservationDate() {
        final String shopId = "1234567";
        final String shopName = "Shop Name";
        final Date date = Date.fromString("12-02-2021");
        date.setTime("12:00");
        final String uuid = "15yhr598";
        final LatLng coords = new LatLng(12.65432, 6.23456);

        final Reservation reservation = new Reservation(shopId, shopName, date, uuid, coords);

        this.viewModel.setSelectedReservation(reservation);

        assertEquals(date.formatted(), this.viewModel.getReservationDate());
    }

    @Test
    public void getReservationTime() {
        final String shopId = "1234567";
        final String shopName = "Shop Name";
        final Date date = Date.fromString("12-02-2021");
        date.setTime("12:00");
        final String uuid = "15yhr54rf";
        final LatLng coords = new LatLng(12.65432, 6.23456);

        final Reservation reservation = new Reservation(shopId, shopName, date, uuid, coords);

        this.viewModel.setSelectedReservation(reservation);

        assertEquals(date.getTime(), this.viewModel.getReservationTime());
    }

    @Test
    public void isReservationExpired() {
        final String shopId = "1234567";
        final String shopName = "Shop Name";
        final Date date = Date.fromString("12-02-2021");
        date.setTime("12:00");
        final String uuid = "15yhr54rf";
        final LatLng coords = new LatLng(12.65432, 6.23456);

        final Reservation reservation = new Reservation(shopId, shopName, date, uuid, coords);

        this.viewModel.setSelectedReservation(reservation);

        assertFalse(this.viewModel.isReservationExpired());

        reservation.setExpired(true);

        assertTrue(this.viewModel.isReservationExpired());
    }

    @Test
    public void setSelectedReservation() {
        final String shopId = "1234567";
        final String shopName = "Shop Name";
        final Date date = Date.fromString("12-02-2021");
        date.setTime("12:00");
        final String uuid = "15yhr56tg";
        final LatLng coords = new LatLng(12.65432, 6.23456);

        final Reservation reservation = new Reservation(shopId, shopName, date, uuid, coords);

        this.viewModel.setSelectedReservation(reservation);

        final Reservation selectedReservation = Model.getInstance().getSelectedReservation();

        assertEquals(shopId, selectedReservation.getShopId());
        assertEquals(shopName, selectedReservation.getShopName());
        assertEquals(date.plain(), selectedReservation.getDate().plain());
        assertEquals(date.getTime(), selectedReservation.getDate().getTime());
        assertEquals(uuid, selectedReservation.getUuid());
        assertEquals(coords.latitude, selectedReservation.getCoords().latitude, 0);
        assertEquals(coords.longitude, selectedReservation.getCoords().longitude, 0);
    }
}