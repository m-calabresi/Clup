package com.android.clup.api;

import androidx.annotation.NonNull;

import com.android.clup.concurrent.Callback;
import com.android.clup.concurrent.Result;
import com.android.clup.model.AvailableDay;
import com.android.clup.model.Date;
import com.android.clup.model.Shop;
import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class QueueService {
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
        executor.execute(() -> {
            // TODO replace with API call

            final Result<String> result = new Result.Success<>("12345yhgfr56ygfr56765433456uhgy7");
            callback.onComplete(result);
        });
    }

    /**
     * Return the list of shops available for a reservation.
     */
    public void getShops(@NonNull final Callback<List<Shop>> callback) {
        executor.execute(() -> {
            // TODO replace with API call

            // dummy list
            final LatLng coords1 = new LatLng(45.4659, 9.1914);
            final LatLng coords2 = new LatLng(45.698342, 9.204998);

            final Date date1 = Date.fromString("11-02-2021");
            final Date date2 = Date.fromString("12-02-2021");
            final Date date3 = Date.fromString("13-02-2021");

            final AvailableDay availableDay1 = new AvailableDay(date1, Arrays.asList("12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"));
            final AvailableDay availableDay2 = new AvailableDay(date2, Arrays.asList("16:00", "17:00", "18:00", "19:00"));
            final AvailableDay availableDay3 = new AvailableDay(date3, Arrays.asList("15:00", "16:00", "17:00", "20:00"));

            final List<AvailableDay> availableDays = Arrays.asList(availableDay1, availableDay1, availableDay2, availableDay3, availableDay2, availableDay3, availableDay1, availableDay2, availableDay3, availableDay1, availableDay2, availableDay3);
            //final List<AvailableDay> availableDays = Arrays.asList(availableDay1, availableDay2, availableDay3);

            final Shop shop1 = new Shop("Local shop", coords1, availableDays);
            final Shop shop2 = new Shop("Supermarket", coords2, availableDays);

            //List<Shop> shops = Arrays.asList(shop1, shop2, shop1, shop2, shop1, shop2, shop1, shop2, shop1, shop2);
            final List<Shop> shops = Arrays.asList(shop1, shop2, shop1);

            final Result.Success<List<Shop>> result = new Result.Success<>(shops);
            callback.onComplete(result);
        });
    }
}
