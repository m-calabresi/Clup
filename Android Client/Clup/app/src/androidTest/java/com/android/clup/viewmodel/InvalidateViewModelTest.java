package com.android.clup.viewmodel;

import androidx.annotation.NonNull;
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
        final String shopId1 = "1234567";
        final String shopId2 = "0987654";

        final String shopName1 = "shopName1";
        final String shopName2 = "shopName2";

        final Date date = Date.fromString("12-02-2121");
        date.setTime("12:30");

        final String uuid1 = "ji876tgf";
        final String uuid2 = "ji876tg4w";

        final LatLng coords = new LatLng(11.75, 6.7654);

        final Reservation reservation1 = new Reservation(shopId1, shopName1, date, uuid1, coords);
        final Reservation reservation2 = new Reservation(shopId2, shopName2, date, uuid2, coords);

        clearReservations();
        addReservation(reservation1);
        addReservation(reservation2);
        this.model.setSelectedReservation(reservation1);

        final int beforeSize = this.model.getReservations().size();

        this.viewModel.invalidateSelectedReservation();
        waitFor();

        final int afterSize = this.model.getReservations().size();

        assertEquals(beforeSize - 1, afterSize);
    }

    private void clearReservations() {
        // init json file & store created reservations
        JsonParser.initReservationsFile();
        waitFor();
    }

    private void addReservation(@NonNull final Reservation reservation) {
        this.model.addReservation(reservation);
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