package com.android.clup.api;

import androidx.annotation.NonNull;

import com.android.clup.concurrent.Callback;
import com.android.clup.concurrent.Result;
import com.android.clup.json.JsonParser;
import com.android.clup.model.AvailableDay;
import com.android.clup.model.AvailableSlot;
import com.android.clup.model.Date;
import com.android.clup.model.Reservation;
import com.android.clup.model.Shop;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class QueueService {
    @NonNull
    private static final String API_AVAILABLE_SHOPS_URL = "https://mobile-project-4272.twil.io/";

    @NonNull
    private final Executor executor;

    public QueueService() {
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Return the UUID code associated to the Queue in which the user is being added.
     */
    public void getUuid(@NonNull final String username, @NonNull final String shopName, @NonNull final String date,
                        @NonNull final String time, @NonNull final Callback<String> callback) {
        this.executor.execute(() -> {
            // TODO replace with API call

            final String postRequestURL = API_AVAILABLE_SHOPS_URL + "queue/enter";
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("user_fullname", username);
                jsonObject.put("hour", time);
                jsonObject.put("status", "todo");
                jsonObject.put("businesses", shopName);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println("JSON" + jsonObject.toString());
            Result<String> response = RemoteConnection.postConnect(postRequestURL, jsonObject.toString());

            final Result<String> result = new Result.Success<>("12345yhgfr56ygfr56765433456uhgy7");
            callback.onComplete(result);
        });
    }

    /**
     * Return the list of shops available for a reservation.
     */
    public void getShops(@NonNull final Callback<List<Shop>> callback) {
        this.executor.execute(() -> {
            // TODO data format
            final String requestURL = API_AVAILABLE_SHOPS_URL + "business/list";
            final Result<String> responseURL = RemoteConnection.connect(requestURL);
            final String responseJSON = ((Result.Success<String>) responseURL).data;

            List<Shop> shopList = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(responseJSON);

                for (int i = 0; i < jsonArray.length(); i++) {
                    final JSONObject jsonObject = jsonArray.getJSONObject(i);
                    final JSONObject fieldsObject = jsonObject.getJSONObject("fields");
                    final JSONObject openingDaysObject = jsonObject.getJSONObject("opening_days");

                    /* Shop name parameter */
                    final String shopName = fieldsObject.getString("Name");

                    /* Shop coordinates parameter */
                    final String[] coordinates = fieldsObject.getString("coordinates").split(",");
                    final LatLng shopCoordinates = new LatLng(
                            Double.parseDouble(coordinates[0].trim()),
                            Double.parseDouble(coordinates[1].trim())
                    );

                    /* Shop Availability parameter */
                    final JSONArray monday = openingDaysObject.getJSONArray("monday");
                    final JSONArray tuesday = openingDaysObject.getJSONArray("tuesday");
                    final JSONArray wednesday = openingDaysObject.getJSONArray("wednesday");
                    final JSONArray thursday = openingDaysObject.getJSONArray("thursday");
                    final JSONArray friday = openingDaysObject.getJSONArray("friday");

                    List<AvailableSlot> mondaySlots = new ArrayList<>();
                    List<AvailableSlot> tuesdaySlots = new ArrayList<>();
                    List<AvailableSlot> wednesdaySlots = new ArrayList<>();
                    List<AvailableSlot> thursdaySlots = new ArrayList<>();
                    List<AvailableSlot> fridaySlots = new ArrayList<>();

                    for (int j = 0; j < 6; j++) {
                        mondaySlots.add(new AvailableSlot(monday.getString(j), Arrays.asList("Angela", "Giacomo")));
                        tuesdaySlots.add(new AvailableSlot(tuesday.getString(j), Arrays.asList("Elisa", "Emilia")));
                        wednesdaySlots.add(new AvailableSlot(wednesday.getString(j), Arrays.asList("Giulia", "Luca")));
                        thursdaySlots.add(new AvailableSlot(thursday.getString(j), Arrays.asList("Amara", "Lorenzo")));
                        fridaySlots.add(new AvailableSlot(friday.getString(j), Arrays.asList("Mario", "Gianni")));
                    }

//                        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
//                        Calendar obj = Calendar.getInstance();
//                        String str = formatter.format(obj.getTime());
//                        System.out.println("Current Date: "+str );
//
//                        final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
//                        final Date date = format.parse(curDate);
//                        final Calendar calendar = Calendar.getInstance();
//                        calendar.setTime(date);
//                        calendar.add(Calendar.DAY_OF_YEAR, 1);
//                        return format.format(calendar.getTime())

                    final Date date1 = Date.fromString("28-02-2021");
                    final Date date2 = Date.fromString("01-03-2021");
                    final Date date3 = Date.fromString("02-03-2021");
                    final Date date4 = Date.fromString("03-03-2021");
                    final Date date5 = Date.fromString("04-03-2021");

                    final List<AvailableDay> availableDays = new ArrayList<>();
                    availableDays.add(new AvailableDay(date1, mondaySlots));
                    availableDays.add(new AvailableDay(date2, tuesdaySlots));
                    availableDays.add(new AvailableDay(date3, wednesdaySlots));
                    availableDays.add(new AvailableDay(date4, thursdaySlots));
                    availableDays.add(new AvailableDay(date5, fridaySlots));

                    shopList.add(new Shop(shopName, shopCoordinates, availableDays));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            final Result.Success<List<Shop>> result = new Result.Success<>(shopList);
            callback.onComplete(result);
        });
    }

    public void invalidateReservation(@NonNull final Reservation reservation) {
        this.executor.execute(() -> {
            // TODO implement API call
            final String postRequestURL = API_AVAILABLE_SHOPS_URL + "queue/update";

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("uuid", "749bf370-33a5-4ca7-b7fa-a0c2775734d3");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Result<String> response = RemoteConnection.postConnect(postRequestURL, jsonObject.toString());
        });
    }
}
