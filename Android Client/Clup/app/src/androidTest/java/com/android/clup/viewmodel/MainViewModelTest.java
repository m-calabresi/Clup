package com.android.clup.viewmodel;

import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ActivityScenario;

import com.android.clup.json.JsonParser;
import com.android.clup.model.Date;
import com.android.clup.model.Model;
import com.android.clup.model.Reservation;
import com.android.clup.ui.auth.AuthActivity;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MainViewModelTest {
    private MainViewModel viewModel;

    @Before
    public void setUp() {
        try (final ActivityScenario<AuthActivity> scenario = ActivityScenario.launch(AuthActivity.class)) {
            scenario.onActivity(activity -> {
                this.viewModel = new ViewModelProvider(activity).get(MainViewModel.class);

                clearReservations();
            });
        }
    }

    @Test
    public void setSelectedReservation() {
        final String shopName = "Shop Name";
        final Date date = Date.fromString("12-02-2021");
        date.setTime("12:00");
        final String uuid = "15yhr5988uh";
        final LatLng coords = new LatLng(12.65432, 6.23456);

        final Reservation reservation = new Reservation(shopName, date, uuid, coords);

        Model.getInstance().addReservation(reservation);

        this.viewModel.setSelectedReservation(0);

        final Reservation selectedReservation = Model.getInstance().getSelectedReservation();

        assertEquals(shopName, selectedReservation.getShopName());
        assertEquals(date.plain(), selectedReservation.getDate().plain());
        assertEquals(date.getTime(), selectedReservation.getDate().getTime());
        assertEquals(uuid, selectedReservation.getUuid());
        assertEquals(coords.latitude, selectedReservation.getCoords().latitude, 0);
        assertEquals(coords.longitude, selectedReservation.getCoords().longitude, 0);
    }

    @Test
    public void getReservations() {
        final String shopName = "Shop Name";
        final Date date = Date.fromString("12-02-2021");
        date.setTime("15:20");
        final String uuid = "15yhr544r50";
        final LatLng coords = new LatLng(12.65432, 6.23456);

        final Reservation reservation = new Reservation(shopName, date, uuid, coords);

        Model.getInstance().addReservation(reservation);
        final List<Reservation> reservations = Model.getInstance().getReservations();
        final List<Reservation> retrievedReservations = this.viewModel.getReservations();

        // compare each reservation with the corresponding loaded one
        for (int i = 0; i < reservations.size(); i++) {
            final Reservation savedReservation = reservations.get(i);
            final Reservation retrievedReservation = retrievedReservations.get(i);

            assertEquals(savedReservation.getShopName(), retrievedReservation.getShopName());
            assertEquals(savedReservation.getDate().plain(), retrievedReservation.getDate().plain());
            assertEquals(savedReservation.getDate().getTime(), retrievedReservation.getDate().getTime());
            assertEquals(savedReservation.getUuid(), retrievedReservation.getUuid());
            assertEquals(savedReservation.getCoords().latitude, retrievedReservation.getCoords().latitude, 0);
            assertEquals(savedReservation.getCoords().longitude, retrievedReservation.getCoords().longitude, 0);
        }
    }

    @Test
    public void getFriendlyName() {
        Model.getInstance().setFriendlyName("FriendlyName");

        assertEquals(Model.getInstance().getFriendlyName(), this.viewModel.getFriendlyName());
    }

    private void clearReservations() {
        // clear reservations
        JsonParser.initReservationsFile();

        // give time to the executor to finish its job
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}