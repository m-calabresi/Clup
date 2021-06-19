package com.android.clup.api;

import androidx.annotation.NonNull;

import com.android.clup.concurrent.Callback;
import com.android.clup.concurrent.Result;
import com.android.clup.model.AvailableDay;
import com.android.clup.model.AvailableSlot;
import com.android.clup.model.Date;
import com.android.clup.model.Reservation;
import com.android.clup.model.Shop;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class QueueService {
    @NonNull
    private static final String API_AVAILABLE_SHOPS_URL = "https://mobile-project-4272.twil.io/";

    private enum status {
        todo, done
    }

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
            final String queuePOSTRequestURL = API_AVAILABLE_SHOPS_URL + "queue/enter";

            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("user_fullname", username);

                String queueEnterDate = LocalDate.parse(date,
                        DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ITALY)
                ).format(
                        DateTimeFormatter.ofPattern("MM-dd-yyyy", Locale.ITALY)
                );
                jsonObject.put("date", queueEnterDate);
                jsonObject.put("hour", time.split(":")[0]);
                jsonObject.put("status", status.todo);
                jsonObject.put("businesses", "rec3XEqBb2EXeOkE9");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            final Result<String> queuePOSTResponse =
                    RemoteConnection.postConnect(queuePOSTRequestURL, jsonObject.toString());

            if (queuePOSTResponse instanceof Result.Success) {
                final String queueJSONResponse = ((Result.Success<String>) queuePOSTResponse).data;

                try {
                    final JSONObject queueJSONObject = new JSONObject(queueJSONResponse);
                    final JSONObject fields = queueJSONObject.getJSONObject("fields");
                    final String uuid = fields.getString("uuid");
                    final Result<String> result = new Result.Success<>(uuid);
                    callback.onComplete(result);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (queuePOSTResponse instanceof  Result.Error) {
                final Result.Error<String> result = ((Result.Error<String>) queuePOSTResponse);
                callback.onComplete(result);
            }
        });
    }

    /**
     * Return the list of shops available for a reservation.
     */
    public void getShops(@NonNull final Callback<List<Shop>> callback) {
        this.executor.execute(() -> {
            // TODO data format & check response

            final String businessRequestURL = API_AVAILABLE_SHOPS_URL + "business/list";
            final Result<String> responseURL = RemoteConnection.connect(businessRequestURL);
            final String businessResponseJSON = ((Result.Success<String>) responseURL).data;

            final String queueRequestURL = API_AVAILABLE_SHOPS_URL + "queue/list";
            final Result<String> queueResponseURL = RemoteConnection.connect(queueRequestURL);
            final String queueResponseJSON = ((Result.Success<String>) queueResponseURL).data;

            List<Shop> shopList = new ArrayList<>();
            try {
                final JSONArray businessJsonArray = new JSONArray(businessResponseJSON);
                final JSONArray queueJsonArray = new JSONArray(queueResponseJSON);

                for (int i = 0; i < businessJsonArray.length(); i++) {
                    final JSONObject jsonObject = businessJsonArray.getJSONObject(i);
                    final String businessID = jsonObject.getString("id");
                    final JSONObject fieldsObject = jsonObject.getJSONObject("fields");
                    final JSONObject openingDaysObject = jsonObject.getJSONObject("opening_days");

                    /* Shop coordinates parameter */
                    final String[] coordinates = fieldsObject.getString("coordinates").split(",");
                    final LatLng shopCoordinates = new LatLng(
                            Double.parseDouble(coordinates[0].trim()),
                            Double.parseDouble(coordinates[1].trim())
                    );

                    /* Shop name parameter */
                    final String shopName = fieldsObject.getString("Name");

                    /* Shop createdTime parameter */
                    final String createdTime = jsonObject.getString("createdTime");

                    /* Shop Availability parameter */
                    final JSONArray monday = openingDaysObject.getJSONArray("monday");
                    final JSONArray tuesday = openingDaysObject.getJSONArray("tuesday");
                    final JSONArray wednesday = openingDaysObject.getJSONArray("wednesday");
                    final JSONArray thursday = openingDaysObject.getJSONArray("thursday");
                    final JSONArray friday = openingDaysObject.getJSONArray("friday");

                    final List<AvailableSlot> mondaySlots = new ArrayList<>();
                    final List<AvailableSlot> tuesdaySlots = new ArrayList<>();
                    final List<AvailableSlot> wednesdaySlots = new ArrayList<>();
                    final List<AvailableSlot> thursdaySlots = new ArrayList<>();
                    final List<AvailableSlot> fridaySlots = new ArrayList<>();

                    final List<String> mondayEnqueuedNames = new ArrayList<>();
                    final List<String> tuesdayEnqueuedNames = new ArrayList<>();
                    final List<String> wednesdayEnqueuedNames = new ArrayList<>();
                    final List<String> thursdayEnqueuedNames = new ArrayList<>();
                    final List<String> fridayEnqueuedNames = new ArrayList<>();

                    /* Parsing ISO dates (YYYY-MM-DDTHH:MM:SSZ)
                     * Date and time is separated with a capital T
                     * UTC time is defined with a capital letter Z
                     **/
                    final List<String> availableDates = new ArrayList<>();
                    for (int j = 0; j < 5; j++) {
                        LocalDateTime localDateTime = LocalDateTime.parse(createdTime,
                                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")).plusDays(j + 1);

                        long millis = localDateTime
                                .atZone(ZoneId.systemDefault())
                                .toInstant().toEpochMilli();
                        java.util.Date dateInMillis = new java.util.Date(millis);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        String businessDate = sdf.format(dateInMillis);

                        availableDates.add(businessDate);
//                        final List<QueueSort> queueSortList = getQueue(businessID, businessDate, queueJsonArray);
//                        mondayEnqueuedNames.add(getQueue(businessID, businessDate, queueJsonArray));

//                        if (!queueSortList.isEmpty()) {
//                            System.out.println("queueSortListDATE " + queueSortList.get(0).getDate());
//                            System.out.println("queueSortListTIMENAME " + queueSortList.get(0).getTimeName());
//                        }
                    }

                    for (int j = 0; j < monday.length(); j++) {
                        mondaySlots.add(new AvailableSlot(monday.getString(j), tuesdayEnqueuedNames));
                    }
                    for (int j = 0; j < tuesday.length(); j++) {
                        tuesdaySlots.add(new AvailableSlot(tuesday.getString(j), tuesdayEnqueuedNames));
                    }
                    for (int j = 0; j < wednesday.length(); j++) {
                        wednesdaySlots.add(new AvailableSlot(wednesday.getString(j), wednesdayEnqueuedNames));
                    }
                    for (int j = 0; j < thursday.length(); j++) {
                        thursdaySlots.add(new AvailableSlot(thursday.getString(j), thursdayEnqueuedNames));
                    }
                    for (int j = 0; j < friday.length(); j++) {
                        fridaySlots.add(new AvailableSlot(friday.getString(j), fridayEnqueuedNames));
                    }

                    final Map<String, List<String>> dateNameList = new LinkedHashMap<>();
                    for (int j = 0; j < 1; j++) {
                        dateNameList.put(availableDates.get(j), getNamesByDate(queueJsonArray, availableDates.get(j)));
                        dateNameList.put(availableDates.get(j + 1), getNamesByDate(queueJsonArray, availableDates.get(j + 1)));
                        dateNameList.put(availableDates.get(j + 2), getNamesByDate(queueJsonArray, availableDates.get(j + 2)));
                        dateNameList.put(availableDates.get(j + 3), getNamesByDate(queueJsonArray, availableDates.get(j + 3)));
                        dateNameList.put(availableDates.get(j + 4), getNamesByDate(queueJsonArray, availableDates.get(j + 4)));
                    }
                    //System.out.println(dateNameList);

//                    for (int j = 0; j < availableDates.size(); j++) {
//                        DayOfWeek dayOfWeek = LocalDate.parse(availableDates.get(j),
//                                DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ITALY))
//                                .getDayOfWeek();
//                        System.out.println();
//                        switch (dayOfWeek) {
//                            case MONDAY:
//                                for (int k = 0; k < monday.length(); k++) {
//                                    mondaySlots.add(new AvailableSlot(monday.getString(k),
//                                            getNames(queueJsonArray, availableDates.get(j),
//                                                    monday.getString(k))));
//                                    System.out.println(monday.getString(k));
//                                    System.out.println(availableDates.get(j));
//                                }
//                                break;
//                            case TUESDAY:
////                                System.out.println("Tuesday");
//                                break;
//                            case WEDNESDAY:
////                                System.out.println("Wednesday");
//                                break;
//                            case THURSDAY:
////                                System.out.println("Thursday");
//                                break;
//                            case FRIDAY:
////                                System.out.println("Friday");
//                                break;
//                        }
//                    }

                    final List<AvailableDay> availableDays = new ArrayList<AvailableDay>() {{
                        add(new AvailableDay(Date.fromString(availableDates.get(0)), mondaySlots));
                        add(new AvailableDay(Date.fromString(availableDates.get(1)), tuesdaySlots));
                        add(new AvailableDay(Date.fromString(availableDates.get(2)), wednesdaySlots));
                        add(new AvailableDay(Date.fromString(availableDates.get(3)), thursdaySlots));
                        add(new AvailableDay(Date.fromString(availableDates.get(4)), fridaySlots));
                    }};

                    shopList.add(new Shop(shopName, shopCoordinates, availableDays));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final Result.Success<List<Shop>> result = new Result.Success<>(shopList);
            callback.onComplete(result);
        });
    }

    private List<String> getQueue(@NonNull String businessID, String businessDate, @NonNull final JSONArray queueJsonArray) {
        final List<QueuedPeople> mondayQueuedPeople = new LinkedList<>();

        final QueueSort queueSort = new QueueSort();
        final List<QueueSort> queueSortList = new LinkedList<>();
        final List<String> names = new ArrayList<>();

        for (int i = 0; i < queueJsonArray.length(); i++) {
            final JSONObject jsonObject;

            try {
                jsonObject = queueJsonArray.getJSONObject(i);
                final JSONObject fieldsObject = jsonObject.getJSONObject("fields");
                final JSONArray businesses = fieldsObject.getJSONArray("businesses");

                String queueDate = LocalDate.parse(
                        fieldsObject.getString("date"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ITALY)
                ).format(
                        DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ITALY)
                );

                DayOfWeek dayOfWeek = LocalDate.parse(
                        fieldsObject.getString("date"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ITALY))
                        .getDayOfWeek();

                if (businesses.get(0).equals(businessID) &&
                        queueDate.equals(businessDate) &&
                        fieldsObject.getString("status").equals(status.todo.toString())) {

                    switch (dayOfWeek) {
                        case MONDAY:
                            System.out.println("Monday " + (fieldsObject.getString("hour")) +
                                    " " + queueDate +
                                    " " + fieldsObject.getString("user_fullname"));
                            mondayQueuedPeople.add(new QueuedPeople(queueDate,
                                    fieldsObject.getString("hour"), Arrays.asList(fieldsObject.getString("user_fullname"))));

                            break;
                        case TUESDAY:
                            System.out.println("Tuesday " + (fieldsObject.getString("hour")) +
                                    " " + queueDate +
                                    fieldsObject.getString("user_fullname"));
                            break;
                        case WEDNESDAY:
                            System.out.println("Wednesday");
                            break;
                        case THURSDAY:
                            System.out.println("Thursday");
                            break;
                        case FRIDAY:
                            System.out.println("Friday");
                            break;
                    }
                    names.add(fieldsObject.getString("user_fullname"));
                    // mondayEnqueuedNames.add(names.get(i));

//                    queueSort.setDate(queueDate);
//                    queueSort.putTimeName(fieldsObject.getString("hour"),
//                            fieldsObject.getString("user_fullname"));
//                    queueSortList.add(queueSort);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        for (int i = 0; i < mondayQueuedPeople.size(); i++) {
            System.out.println("LEGEND" + mondayQueuedPeople.get(i).getDate() +
                    mondayQueuedPeople.get(i).getTime() +
                    mondayQueuedPeople.get(i).getNames());
        }

        return names;
    }

    public List<String> getNamesByDate(@NonNull final JSONArray queueJsonArray, @NonNull final String businessDate) {
        List<String> hourList = new LinkedList<>();
        List<String> nameList = new LinkedList<>();

        for (int i = 0; i < queueJsonArray.length(); i++) {
            final JSONObject jsonObject;

            try {
                jsonObject = queueJsonArray.getJSONObject(i);
                final JSONObject fieldsObject = jsonObject.getJSONObject("fields");

                String queueDate = LocalDate.parse(
                        fieldsObject.getString("date"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ITALY)
                ).format(
                        DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ITALY)
                );

                final String queueHour = fieldsObject.getString("hour");
                final String queueName = fieldsObject.getString("user_fullname");

                if (queueDate.equals(businessDate)) {
                    nameList.add(queueName);
                    hourList.add(queueHour);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return nameList;
    }

    public String getNameByDateHour(@NonNull final JSONArray queueJsonArray,
                                    @NonNull final String businessDate,
                                    @NonNull final String businessHour) {

        for (int i = 0; i < queueJsonArray.length(); i++) {
            final JSONObject jsonObject;

            try {
                jsonObject = queueJsonArray.getJSONObject(i);
                final JSONObject fieldsObject = jsonObject.getJSONObject("fields");

                String queueDate = LocalDate.parse(
                        fieldsObject.getString("date"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ITALY)
                ).format(
                        DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ITALY)
                );

                final String queueHour = fieldsObject.getString("hour").concat(":00");
                final String queueName = fieldsObject.getString("user_fullname");

                if (queueDate.equals(businessDate) &&
                        queueHour.equals(businessHour)) {
                    return queueName;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    private class QueuedPeople {

        private String date = null;
        private String time = null;
        private List<String> names = new LinkedList<>();

        public QueuedPeople(String date, String time, List<String> names) {
            this.date = date;
            this.time = time;
            this.names = names;
        }

        public String getTime() {
            return time;
        }

        public String getDate() {
            return date;
        }

        public List<String> getNames() {
            return names;
        }

    }

    private class QueueSort {

        private String date = null;
        private DayOfWeek dayOfWeek = null;
        private Map<String, String> TimeName = new TreeMap<>();

        public DayOfWeek getDayOfWeek() {
            return dayOfWeek;
        }

        public void setDayOfWeek(DayOfWeek dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
        }

        public void setTimeName(Map<String, String> timeName) {
            TimeName = timeName;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void putTimeName(String time, String name) {
            TimeName.put(time, name);
        }

        public String getDate() {
            return date;
        }

        public Map<String, String> getTimeName() {
            return TimeName;
        }

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


