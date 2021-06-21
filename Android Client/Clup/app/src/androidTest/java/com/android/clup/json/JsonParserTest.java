package com.android.clup.json;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;

import com.android.clup.model.AvailableDay;
import com.android.clup.model.AvailableSlot;
import com.android.clup.model.Date;
import com.android.clup.model.Reservation;
import com.android.clup.model.Shop;
import com.android.clup.ui.auth.AuthActivity;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JsonParserTest {
    // NOTE: change this string according to the file name in JsonParser class.
    @NonNull
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
        final String shopId = "1234567";
        final String shopName = "Shop Name";
        final Date date = Date.fromString("12-02-2021");
        date.setTime("12:00");
        final String uuid = "12345yhde51ed";
        final LatLng coords = new LatLng(12.76543, 8.543);
        final Reservation reservation = new Reservation(shopId, shopName, date, uuid, coords);

        final List<Reservation> reservations = Collections.singletonList(reservation);

        clearReservations();
        saveReservations(reservations);

        // load reservations from file
        final List<Reservation> retrievedReservations = JsonParser.loadReservations();

        // compare each reservation with the corresponding loaded one
        for (int i = 0; i < reservations.size(); i++) {
            final Reservation savedReservation = reservations.get(i);
            final Reservation retrievedReservation = retrievedReservations.get(i);

            assertEquals(savedReservation.getShopId(), retrievedReservation.getShopId());
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
        final String shopId = "1234567";
        final String shopName = "Shop Name";
        final Date date = Date.fromString("12-02-2021");
        date.setTime("12:00");
        final String uuid = "12345yhde577";
        final LatLng coords = new LatLng(12.76543, 8.543);
        final Reservation reservation = new Reservation(shopId, shopName, date, uuid, coords);

        final List<Reservation> reservations = Collections.singletonList(reservation);

        final String expectedJsonString = "{\"reservations\":[{\"id\":\""
                + shopId + "\",\"shopName\":\"" + shopName
                + "\",\"date\":\"" + date.plain() + "\",\"time\":\"" + date.getTime()
                + "\",\"uuid\":\"" + uuid + "\",\"coords\":{\"lat\":"
                + coords.latitude + ",\"lng\":" + coords.longitude + "},\"timeNotice\":-2,\"expired\":false}]}";

        clearReservations();
        saveReservations(reservations);

        // ensure that the string has been encoded successfully
        assertEquals(expectedJsonString, readJsonFile());

        final List<Reservation> emptyReservations = new ArrayList<>();

        clearReservations();
        saveReservations(emptyReservations);

        // ensure that empty list is encoded as empty file
        assertEquals("{}", readJsonFile());
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

    @Test
    public void getShops() {
        final String jsonShops =
                "[\n" +
                "    {\n" +
                "        \"id\": \"rec3XEqBb2EXeOkE9\",\n" +
                "        \"fields\": {\n" +
                "            \"coordinates\": \"45.4764299, 9.1986032\",\n" +
                "            \"Name\": \"Test Shop\"\n" +
                "        },\n" +
                "        \"opening_days\": {\n" +
                "            \"monday\": [\n" +
                "                \"8:00\",\n" +
                "                \"9:00\",\n" +
                "                \"10:00\",\n" +
                "                \"11:00\",\n" +
                "                \"12:00\",\n" +
                "                \"13:00\",\n" +
                "                \"14:00\",\n" +
                "                \"15:00\",\n" +
                "                \"16:00\",\n" +
                "                \"17:00\",\n" +
                "                \"18:00\"\n" +
                "            ],\n" +
                "            \"tuesday\": [\n" +
                "                \"8:00\",\n" +
                "                \"9:00\",\n" +
                "                \"10:00\",\n" +
                "                \"11:00\",\n" +
                "                \"12:00\",\n" +
                "                \"13:00\",\n" +
                "                \"14:00\",\n" +
                "                \"15:00\",\n" +
                "                \"16:00\",\n" +
                "                \"17:00\",\n" +
                "                \"18:00\"\n" +
                "            ],\n" +
                "            \"wednesday\": [\n" +
                "                \"8:00\",\n" +
                "                \"9:00\",\n" +
                "                \"10:00\",\n" +
                "                \"11:00\",\n" +
                "                \"12:00\",\n" +
                "                \"13:00\",\n" +
                "                \"14:00\",\n" +
                "                \"15:00\",\n" +
                "                \"16:00\",\n" +
                "                \"17:00\",\n" +
                "                \"18:00\"\n" +
                "            ],\n" +
                "            \"thursday\": [\n" +
                "                \"8:00\",\n" +
                "                \"9:00\",\n" +
                "                \"10:00\",\n" +
                "                \"11:00\",\n" +
                "                \"12:00\",\n" +
                "                \"13:00\",\n" +
                "                \"14:00\",\n" +
                "                \"15:00\",\n" +
                "                \"16:00\",\n" +
                "                \"17:00\",\n" +
                "                \"18:00\"\n" +
                "            ],\n" +
                "            \"friday\": [\n" +
                "                \"8:00\",\n" +
                "                \"9:00\",\n" +
                "                \"10:00\",\n" +
                "                \"11:00\",\n" +
                "                \"12:00\",\n" +
                "                \"13:00\",\n" +
                "                \"14:00\",\n" +
                "                \"15:00\",\n" +
                "                \"16:00\",\n" +
                "                \"17:00\",\n" +
                "                \"18:00\"\n" +
                "            ]\n" +
                "        }\n" +
                "    }\n" +
                "]";

        // queues functionality can't be tested due to its dynamically changing behavior.
        final String jsonQueues = "[]";
        // date cannot be tested due to its dynamically changing behavior.
        final Date dummyDate = Date.fromString("12-12-2012");
        // enqueued customers names cannot be tested due to its dependency on jsonQueues.
        final List<String> dummyEnqueuedCustomersNames = new ArrayList<>(0);

        final AvailableSlot slot1 = new AvailableSlot("8:00", dummyEnqueuedCustomersNames);
        final AvailableSlot slot2 = new AvailableSlot("9:00", dummyEnqueuedCustomersNames);
        final AvailableSlot slot3 = new AvailableSlot("10:00", dummyEnqueuedCustomersNames);
        final AvailableSlot slot4 = new AvailableSlot("11:00", dummyEnqueuedCustomersNames);
        final AvailableSlot slot5 = new AvailableSlot("12:00", dummyEnqueuedCustomersNames);
        final AvailableSlot slot6 = new AvailableSlot("13:00", dummyEnqueuedCustomersNames);
        final AvailableSlot slot7 = new AvailableSlot("14:00", dummyEnqueuedCustomersNames);
        final AvailableSlot slot8 = new AvailableSlot("15:00", dummyEnqueuedCustomersNames);
        final AvailableSlot slot9 = new AvailableSlot("16:00", dummyEnqueuedCustomersNames);
        final AvailableSlot slot10 = new AvailableSlot("17:00", dummyEnqueuedCustomersNames);
        final AvailableSlot slot11 = new AvailableSlot("18:00", dummyEnqueuedCustomersNames);

        final List<AvailableSlot> availableSlots = new ArrayList<>(11);
        availableSlots.add(slot1);
        availableSlots.add(slot2);
        availableSlots.add(slot3);
        availableSlots.add(slot4);
        availableSlots.add(slot5);
        availableSlots.add(slot6);
        availableSlots.add(slot7);
        availableSlots.add(slot8);
        availableSlots.add(slot9);
        availableSlots.add(slot10);
        availableSlots.add(slot11);

        final AvailableDay monday = new AvailableDay(dummyDate, availableSlots);
        final AvailableDay tuesday = new AvailableDay(dummyDate, availableSlots);
        final AvailableDay wednesday = new AvailableDay(dummyDate, availableSlots);
        final AvailableDay thursday = new AvailableDay(dummyDate, availableSlots);
        final AvailableDay friday = new AvailableDay(dummyDate, availableSlots);

        final List<AvailableDay> availableDays = new ArrayList<>(5);
        availableDays.add(monday);
        availableDays.add(tuesday);
        availableDays.add(wednesday);
        availableDays.add(thursday);
        availableDays.add(friday);

        final LatLng coords = new LatLng(45.4764299, 9.1986032);
        final Shop shop = new Shop("rec3XEqBb2EXeOkE9", "Test Shop", coords, availableDays);

        final List<Shop> expectedShops = new ArrayList<>(1);
        expectedShops.add(shop);

        final List<Shop> computedShops = JsonParser.getShops(jsonShops, jsonQueues);

        for (int i = 0; i < expectedShops.size(); i++) {
            final Shop expectedShop = expectedShops.get(i);
            final Shop computedShop = computedShops.get(i);

            assertEquals(expectedShop.getId(), computedShop.getId());
            assertEquals(expectedShop.getName(), computedShop.getName());
            assertEquals(expectedShop.getCoordinates(), computedShop.getCoordinates());

            final List<AvailableDay> expectedAvailableDays = expectedShop.getAvailableDays();
            final List<AvailableDay> computedAvailableDays = computedShop.getAvailableDays();

            for (int j = 0; j < expectedAvailableDays.size(); j++) {
                final AvailableDay expectedAvailableDay = expectedAvailableDays.get(j);
                final AvailableDay computedAvailableDay = computedAvailableDays.get(j);

                final List<AvailableSlot> expectedAvailableSlots = expectedAvailableDay.getAvailableSlots();
                final List<AvailableSlot> computedAvailableSlots = computedAvailableDay.getAvailableSlots();

                for (int k = 0; k < expectedAvailableSlots.size(); k++) {
                    final AvailableSlot expectedAvailableSlot = expectedAvailableSlots.get(k);
                    final AvailableSlot computedAvailableSlot = computedAvailableSlots.get(k);

                    assertEquals(expectedAvailableSlot.getTime(), computedAvailableSlot.getTime());
                    assertEquals(expectedAvailableSlot.getHour(), computedAvailableSlot.getHour());
                }
            }
        }

    }

    @Test
    public void getUuid() {
        final String jsonString =
                "{\n" +
                "    \"fields\": {\n" +
                "        \"uuid\": \"c19af6b7-3144-4aa9-a48b-eef10c2b561f\"\n" +
                "    }\n" +
                "}";

        final String expectedUuid = "c19af6b7-3144-4aa9-a48b-eef10c2b561f";
        final String retrievedUuid = JsonParser.getUuid(jsonString);

        assertEquals(expectedUuid, retrievedUuid);
    }

    @Test
    public void toJsonReservation() {
        final String userFullname = "mark brown";
        final String hour = "12";
        final String shopId = "1234567";
        final String date = "2021-12-12";

        final String expectedJsonString = "{\"user_fullname\":\"mark brown\",\"hour\":\"12\",\"status\":\"todo\",\"business\":\"1234567\",\"date\":\"2021-12-12\"}";
        final String retrievedJsonString = JsonParser.toJsonReservation(userFullname, hour, shopId, date);

        assertEquals(expectedJsonString, retrievedJsonString);
    }
}