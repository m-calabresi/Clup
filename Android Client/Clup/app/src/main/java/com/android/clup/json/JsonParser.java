package com.android.clup.json;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.clup.ApplicationContext;
import com.android.clup.exception.NoAvailableDayException;
import com.android.clup.model.AvailableDay;
import com.android.clup.model.AvailableSlot;
import com.android.clup.model.Date;
import com.android.clup.model.Reservation;
import com.android.clup.model.Shop;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JsonParser {
    @NonNull
    private static final String RESERVATIONS_FILE_NAME = "reservations.json";
    @NonNull
    private static final String RESERVATIONS_ARRAY_NAME = "reservations";
    @NonNull
    private static final String SHOP_ID = "id";
    @NonNull
    private static final String SHOP_NAME = "shopName";
    @NonNull
    private static final String SHOP_NAME_ALT = "Name";
    @NonNull
    private static final String DATE_NAME = "date";
    @NonNull
    private static final String HOUR = "hour";
    @NonNull
    private static final String TIME_NAME = "time";
    @NonNull
    private static final String UUID_NAME = "uuid";
    @NonNull
    private static final String COORDS_NAME = "coords";
    @NonNull
    private static final String COORDINATES_NAME = "coordinates";
    @NonNull
    private static final String LAT_NAME = "lat";
    @NonNull
    private static final String LNG_NAME = "lng";
    @NonNull
    private static final String TIME_NOTICE = "timeNotice";
    @NonNull
    private static final String EXPIRED = "expired";
    @NonNull
    private static final String FIELDS = "fields";
    @NonNull
    private static final String OPENING_DAYS = "opening_days";
    @NonNull
    private static final String BUSINESSES = "businesses";
    @NonNull
    private static final String BUSINESS = "business";
    @NonNull
    private static final String USER_FULLNAME = "user_fullname";
    @NonNull
    private static final String STATUS = "status";

    @NonNull
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    /*
     * -------- PUBLIC INTERFACE --------
     */

    /**
     * Load the reservation list from the JSON file and returns it.
     */
    @NonNull
    public static List<Reservation> loadReservations() {
        try {
            final JSONObject jsonObject = JsonParser.loadJsonFromFile(ApplicationContext.get(), RESERVATIONS_FILE_NAME);
            final JSONArray reservationsArray = jsonObject.getJSONArray(RESERVATIONS_ARRAY_NAME);
            return JsonParser.toReservationsList(reservationsArray);
        } catch (JSONException e) {
            // first time the app launches: no data inside JSON file
            return new ArrayList<>();
        }
    }

    /**
     * Save the given reservation list to a JSON file (overwriting the content already inside).
     */
    public static void saveReservations(@NonNull final List<Reservation> reservations) {
        if (!reservations.isEmpty()) {
            executor.execute(() -> {
                final JSONObject jsonReservations = JsonParser.toJsonReservations(reservations);
                JsonParser.storeJsonObject(ApplicationContext.get(), jsonReservations, RESERVATIONS_FILE_NAME);
            });
        } else
            JsonParser.initReservationsFile();
    }

    /**
     * Create a new JSON file and initializes it to be correct for parsing and reading.
     */
    public static void initReservationsFile() {
        executor.execute(() -> {
            final JSONObject emptyJsonObject = JsonParser.toJsonObject("{}");
            JsonParser.storeJsonObject(ApplicationContext.get(), emptyJsonObject, RESERVATIONS_FILE_NAME);
        });
    }

    /**
     * Add the Customers given by the {@code jsonQueues} to their respective {@code enqueuedCustomers}
     * List in the appropriate {@link Shop} taken from the given List.
     */
    private static void addQueues(@NonNull final List<Shop> shops, @NonNull final String jsonQueues) {
        final JSONArray queueJsonArray = JsonParser.toJsonArray(jsonQueues);

        for (int i = 0; i < queueJsonArray.length(); i++) {
            final JSONObject jsonQueue = JsonParser.getJSONObject(queueJsonArray, i);
            JsonParser.addQueue(shops, jsonQueue);
        }
    }

    /**
     * Add the Customer given by the {@code jsonQueue} to its respective {@code enqueuedCustomers}
     * List in the appropriate {@link Shop} taken from the given List.
     */
    private static void addQueue(@NonNull final List<Shop> shops, @NonNull final JSONObject jsonQueue) {
        try {
            final JSONObject fieldsObject = jsonQueue.getJSONObject(FIELDS);
            final String businessId = fieldsObject.getJSONArray(BUSINESSES).getString(0);

            final String status = fieldsObject.getString(STATUS);

            // only pending reservations have to be shown
            if(status.equals(Reservation.Status.DONE))
                return;

            final String hour = fieldsObject.getString(HOUR);
            final String customerName = fieldsObject.getString(USER_FULLNAME);

            final String dateString = fieldsObject.getString(DATE_NAME);
            final Date date = Date.fromStringReverse(dateString);

            // insert other customer's reservations in the shop list
            try {
                final AvailableDay availableDay = Shop.getById(shops, businessId).getAvailableDayByDate(date);
                availableDay.getAvailableSlotByHour(hour).getEnqueuedCustomersNames().add(customerName);
            } catch (@NonNull final NoAvailableDayException ignored) {
                // the reservation to be added refers to a day which is not in the visualization period (next 5 business days): ignore it
            }
        } catch (@NonNull final JSONException e) {
            throw new RuntimeException("Unable add JSONObject to Customers Queue: " + e.getLocalizedMessage());
        }
    }

    /*
     * -------- CONVERSION FROM JSON FORMAT --------
     */

    /**
     * Convert the given JSON Array into a list of reservations.
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
     * Convert the given JSON Object into a {@link Reservation}.
     */
    @NonNull
    private static Reservation toReservation(@NonNull final JSONObject jsonReservation) {
        try {
            final String shopId = jsonReservation.getString(SHOP_ID);
            final String shopName = jsonReservation.getString(SHOP_NAME);
            final Date date = Date.fromString(jsonReservation.getString(DATE_NAME));
            final String time = jsonReservation.getString(TIME_NAME);
            final String uuid = jsonReservation.getString(UUID_NAME);
            final LatLng coords = JsonParser.toCoords(jsonReservation.getJSONObject(COORDS_NAME));
            final int timeNotice = jsonReservation.getInt(TIME_NOTICE);
            final boolean expired = jsonReservation.getBoolean(EXPIRED);

            date.setTime(time);
            final Reservation reservation = new Reservation(shopId, shopName, date, uuid, coords, timeNotice);
            reservation.setExpired(expired);

            return reservation;
        } catch (JSONException e) {
            throw new RuntimeException("Unable to convert JSONObject to Reservation: " + e.getLocalizedMessage());
        }
    }

    /**
     * Convert the given JSON Object into a coordinate object.
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

    /**
     * Create a List of {@link Shop}s from the given JSON string.
     * Shops are created with an empty {@code enqueuedCustomersList} from the {@code jsonShops} String
     * and then filled with the appropriate {@code enqueuedCustomers} Lists, taken from {@code jsonQueues}.
     */
    @NonNull
    public static List<Shop> getShops(@NonNull final String jsonShops, @NonNull final String jsonQueues) {
        final JSONArray businessJsonArray = JsonParser.toJsonArray(jsonShops);
        final List<Shop> shops = new ArrayList<>(businessJsonArray.length());

        // create empty shop list
        for (int i = 0; i < businessJsonArray.length(); i++) {
            final Shop shop = JsonParser.getShop(JsonParser.getJSONObject(businessJsonArray, i));
            shops.add(shop);
        }

        // add other enqueued customers
        JsonParser.addQueues(shops, jsonQueues);
        return shops;
    }

    /**
     * Return a List of {@code Shop}s from the given JSON Object.
     */
    @NonNull
    private static Shop getShop(@NonNull final JSONObject jsonShop) {
        try {
            // shop id
            final String businessID = jsonShop.getString(SHOP_ID);

            final JSONObject fieldsObject = jsonShop.getJSONObject(FIELDS);

            // shop coordinates
            final double[] coordinates = Arrays
                    .stream(fieldsObject.getString(COORDINATES_NAME)
                            .split(","))
                    .map(String::trim)
                    .mapToDouble(Double::parseDouble)
                    .toArray();
            final LatLng shopCoordinates = new LatLng(coordinates[0], coordinates[1]);

            // shop name
            final String shopName = fieldsObject.getString(SHOP_NAME_ALT);

            final JSONObject openingDaysObject = jsonShop.getJSONObject(OPENING_DAYS);

            // available days
            final List<AvailableDay> availableDays = JsonParser.getAvailableDays(openingDaysObject);

            // shop
            return new Shop(businessID, shopName, shopCoordinates, availableDays);
        } catch (JSONException e) {
            throw new RuntimeException("Unable to retrieve Shop from JSONObject: " + e.getLocalizedMessage());
        }
    }

    /**
     * Return a List of {@link AvailableDay}s from the given JSON Object.
     */
    @NonNull
    private static List<AvailableDay> getAvailableDays(@NonNull final JSONObject openingDaysObject) {
        // available slots
        final List<AvailableSlot> mondaySlots = JsonParser.getAvailableSlots(openingDaysObject, Date.monday);
        final List<AvailableSlot> tuesdaySlots = JsonParser.getAvailableSlots(openingDaysObject, Date.tuesday);
        final List<AvailableSlot> wednesdaySlots = JsonParser.getAvailableSlots(openingDaysObject, Date.wednesday);
        final List<AvailableSlot> thursdaySlots = JsonParser.getAvailableSlots(openingDaysObject, Date.thursday);
        final List<AvailableSlot> fridaySlots = JsonParser.getAvailableSlots(openingDaysObject, Date.friday);

        final List<List<AvailableSlot>> weekAvailableSlots = new ArrayList<>(Date.BUSINESS_DAYS_IN_A_WEEK);
        weekAvailableSlots.add(mondaySlots);
        weekAvailableSlots.add(tuesdaySlots);
        weekAvailableSlots.add(wednesdaySlots);
        weekAvailableSlots.add(thursdaySlots);
        weekAvailableSlots.add(fridaySlots);

        final List<Date> businessWeek = Date.getBusinessWeekFromToday();

        final List<List<AvailableSlot>> sortedWeekAvailableSlots = AvailableSlot.sortAvailableSlotsBy(weekAvailableSlots, businessWeek);

        // available days
        final List<AvailableDay> availableDays = new ArrayList<>(Date.BUSINESS_DAYS_IN_A_WEEK);

        for (int i = 0; i < Date.BUSINESS_DAYS_IN_A_WEEK; i++) {
            final AvailableDay availableDay = new AvailableDay(businessWeek.get(i), sortedWeekAvailableSlots.get(i));
            availableDays.add(availableDay);
        }
        return availableDays;
    }

    /**
     * Return the List of {@link AvailableSlot}s corresponding to the given {@code day}
     * from the given JSON Object.
     */
    @NonNull
    private static List<AvailableSlot> getAvailableSlots(@NonNull final JSONObject openingDays, @NonNull final String day) {
        try {
            final JSONArray timesArray = openingDays.getJSONArray(day);
            final List<AvailableSlot> availableSlots = new ArrayList<>(timesArray.length());

            for (int i = 0; i < timesArray.length(); i++) {
                final String time = timesArray.getString(i);
                final List<String> enqueuedCustomers = new ArrayList<>(); // customers will be inserted later

                final AvailableSlot availableSlot = new AvailableSlot(time, enqueuedCustomers);
                availableSlots.add(availableSlot);
            }

            return availableSlots;
        } catch (@NonNull final JSONException e) {
            throw new RuntimeException("Unable to retrieve AvailableSlots from JSONObject: " + e.getLocalizedMessage());
        }
    }

    @NonNull
    public static String getUuid(@NonNull final String jsonString) {
        try {
            final JSONObject queueJSONObject = JsonParser.toJsonObject(jsonString);
            final JSONObject fields = queueJSONObject.getJSONObject(FIELDS);

            return fields.getString(UUID_NAME);
        } catch (@NonNull final JSONException e) {
            throw new RuntimeException("Unable to retrieve UUID from JSON String: " + e.getLocalizedMessage());
        }
    }

    /*
     * -------- CONVERSION TO JSON FORMAT --------
     */

    /**
     * Convert the given {@link Reservation} parameters to the corresponding JSON String.
     */
    @NonNull
    public static String toJsonReservation(@NonNull final String username, @NonNull final String hour,
                                           @NonNull final String shopId, @NonNull final String date) {
        try {
            final JSONObject jsonObject = new JSONObject();

            jsonObject.put(USER_FULLNAME, username);
            jsonObject.put(HOUR, hour);
            jsonObject.put(STATUS, Reservation.Status.TODO);
            jsonObject.put(BUSINESS, shopId);
            jsonObject.put(DATE_NAME, date);

            return jsonObject.toString();
        } catch (@NonNull final JSONException e) {
            throw new RuntimeException("Unable to convert Reservation to JSON String: " + e.getLocalizedMessage());
        }
    }

    /**
     * Convert the given {@link Reservation}s into the corresponding JSON representation.
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
     * Convert the given {@link Reservation} into the corresponding JSON representation.
     */
    @NonNull
    private static JSONObject toJsonReservation(@NonNull final Reservation reservation) {
        try {
            final JSONObject jsonReservation = new JSONObject();

            jsonReservation.put(SHOP_ID, reservation.getShopId());
            jsonReservation.put(SHOP_NAME, reservation.getShopName());
            jsonReservation.put(DATE_NAME, reservation.getDate().plain());
            jsonReservation.put(TIME_NAME, reservation.getDate().getTime());
            jsonReservation.put(UUID_NAME, reservation.getUuid());
            jsonReservation.put(COORDS_NAME, JsonParser.toJsonCoords(reservation.getCoords()));
            jsonReservation.put(TIME_NOTICE, reservation.getTimeNotice());
            jsonReservation.put(EXPIRED, reservation.isExpired());

            return jsonReservation;
        } catch (JSONException e) {
            throw new RuntimeException("Unable to convert Reservation to JSONObject: " + e.getLocalizedMessage());
        }
    }

    /**
     * Convert the given coordinates into the corresponding JSON representation.
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

    /**
     * Return the element of {@code jsonArray} at the specified {@code index} position.
     */
    @NonNull
    private static JSONObject getJSONObject(@NonNull final JSONArray jsonArray, final int index) {
        try {
            return jsonArray.getJSONObject(index);
        } catch (@NonNull final JSONException e) {
            throw new RuntimeException("Unable to retrieve JSONObject from JSONArray: " + e.getLocalizedMessage());
        }
    }

    /*
     * -------- I/O FILE OPERATIONS --------
     */

    /**
     * Save the given JSON Object into the chosen file.
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
     * Load the data in the given JSON file and return a JSON Object holding those data.
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
     * Convert a string encoded in JSON format to a proper JSON Object.
     */
    @NonNull
    private static JSONObject toJsonObject(@NonNull final String jsonString) {
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            throw new RuntimeException("Unable to convert String to JSONObject: " + e.getLocalizedMessage());
        }
    }

    /**
     * Convert a string encoded in JSON format to a proper JSON Array.
     */
    @NonNull
    private static JSONArray toJsonArray(@NonNull final String jsonString) {
        try {
            return new JSONArray(jsonString);
        } catch (JSONException e) {
            throw new RuntimeException("Unable to convert String to JSONOArray: " + e.getLocalizedMessage());
        }
    }
}

