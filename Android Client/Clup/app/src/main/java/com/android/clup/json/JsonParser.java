package com.android.clup.json;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.clup.ApplicationContext;
import com.android.clup.model.Date;
import com.android.clup.model.Reservation;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JsonParser {
    @NonNull
    private static final String RESERVATIONS_FILE_NAME = "reservations.json";
    @NonNull
    private static final String RESERVATIONS_ARRAY_NAME = "reservations";
    @NonNull
    private static final String SHOP_NAME = "shopName";
    @NonNull
    private static final String DATE_NAME = "date";
    @NonNull
    private static final String TIME_NAME = "time";
    @NonNull
    private static final String UUID_NAME = "uuid";
    @NonNull
    private static final String COORDS_NAME = "coords";
    @NonNull
    private static final String LAT_NAME = "lat";
    @NonNull
    private static final String LNG_NAME = "lng";
    @NonNull
    private static final String TIME_NOTICE = "timeNotice";

    @NonNull
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    /*
     * -------- PUBLIC INTERFACE --------
     */

    /**
     * Load the reservation list from the json file and returns it.
     */
    @NonNull
    public static List<Reservation> loadReservations() {
        try {
            final JSONObject jsonObject = JsonParser.loadJsonFromFile(ApplicationContext.get(), RESERVATIONS_FILE_NAME);
            final JSONArray reservationsArray = jsonObject.getJSONArray(RESERVATIONS_ARRAY_NAME);
            return JsonParser.toReservationsList(reservationsArray);
        } catch (JSONException e) {
            // first time the app launches: no data inside json file
            return new ArrayList<>();
        }
    }

    /**
     * Save the given reservation list to a json file (overwriting the content already inside).
     */
    public static void saveReservations(@NonNull final List<Reservation> reservations) {
        executor.execute(() -> {
            final JSONObject jsonReservations = JsonParser.toJsonReservations(reservations);
            JsonParser.storeJsonObject(ApplicationContext.get(), jsonReservations, RESERVATIONS_FILE_NAME);
        });
    }

    /**
     * Create a new json file and initializes it to be correct for parsing and reading.
     */
    public static void initReservationsFile() {
        executor.execute(() -> {
            final JSONObject emptyJsonObject = JsonParser.toJsonObject("{}");
            JsonParser.storeJsonObject(ApplicationContext.get(), emptyJsonObject, RESERVATIONS_FILE_NAME);
        });
    }

    /*
     * -------- CONVERSION FROM JSON FORMAT --------
     */

    /**
     * Convert the given json array into a list of reservations.
     */
    @NonNull
    private static List<Reservation> toReservationsList(@NonNull final JSONArray jsonArray) {
        try {
            final List<Reservation> reservations = new ArrayList<>(jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {
                final JSONObject jsonReservation = jsonArray.getJSONObject(i);
                final Reservation reservation = JsonParser.toReservation(jsonReservation);
                reservations.add(reservation);
            }
            return reservations;
        } catch (JSONException e) {
            throw new RuntimeException("Unable to convert JSONObject to List<Reservation>: " + e.getLocalizedMessage());
        }
    }

    /**
     * Convert the given json object into a reservation.
     */
    @NonNull
    private static Reservation toReservation(@NonNull final JSONObject jsonReservation) {
        try {
            final String shopName = jsonReservation.getString(SHOP_NAME);
            final Date date = Date.fromString(jsonReservation.getString(DATE_NAME));
            final String time = jsonReservation.getString(TIME_NAME);
            final String uuid = jsonReservation.getString(UUID_NAME);
            final LatLng coords = JsonParser.toCoords(jsonReservation.getJSONObject(COORDS_NAME));
            final int timeNotice = jsonReservation.getInt(TIME_NOTICE);

            date.setTime(time);
            return new Reservation(shopName, date, uuid, coords, timeNotice);
        } catch (JSONException e) {
            throw new RuntimeException("Unable to convert JSONObject to Reservation: " + e.getLocalizedMessage());
        }
    }

    /**
     * Convert the given json object into a coordinate object.
     */
    @NonNull
    private static LatLng toCoords(@NonNull final JSONObject coordsArray) {
        try {
            final double lat = coordsArray.getDouble(LAT_NAME);
            final double lng = coordsArray.getDouble(LNG_NAME);
            return new LatLng(lat, lng);
        } catch (JSONException e) {
            throw new RuntimeException("Unable to convert JSONObject to LatLng: " + e.getLocalizedMessage());
        }
    }

    /*
     * -------- CONVERSION TO JSON FORMAT --------
     */

    /**
     * Convert the given reservations into the corresponding json representation.
     */
    @NonNull
    private static JSONObject toJsonReservations(@NonNull final List<Reservation> reservations) {
        try {
            final JSONObject jsonObject = new JSONObject();
            final JSONArray jsonArray = new JSONArray();

            for (final Reservation reservation : reservations) {
                final JSONObject jsonReservation = JsonParser.toJsonReservation(reservation);
                jsonArray.put(jsonReservation);
            }

            jsonObject.put(RESERVATIONS_ARRAY_NAME, jsonArray);
            return jsonObject;
        } catch (JSONException e) {
            throw new RuntimeException("Unable to convert List<Reservation> to JSONObject: " + e.getLocalizedMessage());
        }
    }

    /**
     * Convert the given reservation into the corresponding json representation.
     */
    @NonNull
    private static JSONObject toJsonReservation(@NonNull final Reservation reservation) {
        try {
            final JSONObject jsonReservation = new JSONObject();

            jsonReservation.put(SHOP_NAME, reservation.getShopName());
            jsonReservation.put(DATE_NAME, reservation.getDate().plain());
            jsonReservation.put(TIME_NAME, reservation.getDate().getTime());
            jsonReservation.put(UUID_NAME, reservation.getUuid());
            jsonReservation.put(COORDS_NAME, JsonParser.toJsonCoords(reservation.getCoords()));
            jsonReservation.put(TIME_NOTICE, reservation.getTimeNotice());

            return jsonReservation;
        } catch (JSONException e) {
            throw new RuntimeException("Unable to convert Reservation to JSONObject: " + e.getLocalizedMessage());
        }
    }

    /**
     * Convert the given coordinates into the corresponding json representation.
     */
    @NonNull
    private static JSONObject toJsonCoords(@NonNull final LatLng coords) {
        try {
            final JSONObject jsonCoords = new JSONObject();
            jsonCoords.put(LAT_NAME, coords.latitude);
            jsonCoords.put(LNG_NAME, coords.longitude);
            return jsonCoords;
        } catch (JSONException e) {
            throw new RuntimeException("Unable to convert LatLng to JSONObject: " + e.getLocalizedMessage());
        }
    }

    /*
     * -------- I/O FILE OPERATIONS --------
     */

    /**
     * Save the given json object into the chosen file.
     */
    @SuppressWarnings("SameParameterValue")
    private static void storeJsonObject(@NonNull final Context context, @NonNull final JSONObject jsonObject, @NonNull final String fileName) {
        try (final FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            final String fileContents = jsonObject.toString();
            fos.write(fileContents.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Unable to store JSONObject to file: " + e.getLocalizedMessage());
        }
    }

    /**
     * Load the data in the given json file and return a json object holding those data.
     */
    @NonNull
    @SuppressWarnings("SameParameterValue")
    private static JSONObject loadJsonFromFile(@NonNull final Context context, @NonNull final String fileName) {
        try (final InputStream inputStream = context.openFileInput(fileName)) {
            try (final InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                try (final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    final StringBuilder stringBuilder = new StringBuilder();
                    String receiveString;

                    while ((receiveString = bufferedReader.readLine()) != null)
                        stringBuilder.append(receiveString);

                    final String jsonString = stringBuilder.toString();
                    return JsonParser.toJsonObject(jsonString);
                }
            }
        } catch (@NonNull final IOException e) {
            throw new RuntimeException("Unable to load JSONObject from file: " + e.getLocalizedMessage());
        }
    }

    /**
     * Convert a string encoded in json format to a proper json object.
     */
    @NonNull
    private static JSONObject toJsonObject(@NonNull final String jsonString) {
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            throw new RuntimeException("Unable to convert String to JSONObject: " + e.getLocalizedMessage());
        }
    }
}
