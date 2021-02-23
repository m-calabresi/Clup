package com.android.clup.json;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;

import com.android.clup.model.Date;
import com.android.clup.model.Reservation;
import com.android.clup.ui.auth.AuthActivity;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JsonParserTest {
    // NOTE: change this string according to the file name in JsonParser class.
    private static final String filename = "reservations.json";

    private AuthActivity activity;

    @Before
    public void setUp() {
        try (final ActivityScenario<AuthActivity> scenario = ActivityScenario.launch(AuthActivity.class)) {
            scenario.onActivity(activity -> {
                this.activity = activity;

                // attempt to delete the file (if already exists)
                if (!activity.deleteFile(filename)) {
                    Log.w("JsonParserTest", "Failed to delete " + filename +
                            " from internal storage. This may be due to file not present or a proper error, check internal storage.");
                }
            });
        }
    }

    @NonNull
    private String readJsonFile() {
        try (final InputStream inputStream = activity.openFileInput(filename)) {
            try (final InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                try (final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    final StringBuilder stringBuilder = new StringBuilder();
                    String receiveString;

                    while ((receiveString = bufferedReader.readLine()) != null)
                        stringBuilder.append(receiveString);

                    return stringBuilder.toString();
                }
            }
        } catch (IOException e) {
            throw new AssertionError(e.getMessage());
        }
    }

    @Test
    public void loadReservations() {
        final String shopName = "Shop Name";
        final Date date = Date.fromString("12-02-2021");
        date.setTime("12:00");
        final String uuid = "12345yhde51ed";
        final LatLng coords = new LatLng(12.76543, 8.543);
        final Reservation reservation = new Reservation(shopName, date, uuid, coords);

        final List<Reservation> reservations = Collections.singletonList(reservation);

        clearReservations();
        saveReservations(reservations);

        // load reservations from file
        final List<Reservation> retrievedReservations = JsonParser.loadReservations();

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
    public void saveReservations() {
        final String shopName = "Shop Name";
        final Date date = Date.fromString("12-02-2021");
        date.setTime("12:00");
        final String uuid = "12345yhde577";
        final LatLng coords = new LatLng(12.76543, 8.543);
        final Reservation reservation = new Reservation(shopName, date, uuid, coords);

        final List<Reservation> reservations = Collections.singletonList(reservation);

        final String expectedJsonString = "{\"reservations\":[{\"shopName\":\"" + shopName
                + "\",\"date\":\"" + date.plain() + "\",\"time\":\"" + date.getTime()
                + "\",\"uuid\":\"" + uuid + "\",\"coords\":{\"lat\":"
                + coords.latitude + ",\"lng\":" + coords.longitude + "},\"timeNotice\":-2}]}";

        clearReservations();
        saveReservations(reservations);

        // ensure that the string has been encoded successfully
        assertEquals(expectedJsonString, readJsonFile());
    }

    @Test(expected = RuntimeException.class)
    public void initReservationsFile() {
        // file des not exists: exception thrown
        JsonParser.loadReservations();

        // file exists and is empty
        JsonParser.initReservationsFile();

        // give time to the executor to finish its job
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // check that parser successfully handles empty file
        assertEquals(0, JsonParser.loadReservations().size());

        // check that init procedure successfully created an empty file
        assertEquals("{}", readJsonFile());
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

    private void saveReservations(@NonNull final List<Reservation> reservations) {
        // save created reservations
        JsonParser.saveReservations(reservations);

        // give time to the executor to finish its job
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}