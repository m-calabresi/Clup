package com.android.clup.viewmodel;

import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ActivityScenario;

import com.android.clup.json.JsonParser;
import com.android.clup.model.Date;
import com.android.clup.model.Model;
import com.android.clup.model.Reservation;
import com.android.clup.ui.InvalidateActivity;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InvalidateViewModelTest {
    private Model model;

    private InvalidateViewModel viewModel;

    @Before
    public void setUp() {
        try (final ActivityScenario<InvalidateActivity> scenario = ActivityScenario.launch(InvalidateActivity.class)) {
            scenario.onActivity(activity -> {
                this.model = Model.getInstance();
                this.viewModel = new ViewModelProvider(activity).get(InvalidateViewModel.class);
            });
        }
    }

    @Test
    public void invalidateCurrentReservation() {
        final String shopName1 = "shopName1";
        final String shopName2 = "shopName2";

        final Date date = Date.fromString("12-02-2021");
        date.setTime("12:30");

        final String uuid1 = "ji876tgf";
        final String uuid2 = "ji876tg4w";

        final LatLng coords = new LatLng(11.75, 6.7654);

        final Reservation reservation1 = new Reservation(shopName1, date, uuid1, coords);
        final Reservation reservation2 = new Reservation(shopName2, date, uuid2, coords);

        clearReservations();
        this.model.addReservation(reservation1);
        this.model.addReservation(reservation2);
        this.model.setSelectedReservation(reservation1);

        final int beforeSize = this.model.getReservations().size();

        this.viewModel.invalidateSelectedReservation();

        final int afterSize = this.model.getReservations().size();

        assertEquals(beforeSize - 1, afterSize);
    }

    private void clearReservations() {
        // init json file & store created reservations
        JsonParser.initReservationsFile();

        // give time to the executor to finish its job
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}