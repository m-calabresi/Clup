package com.android.clup.api;

import androidx.annotation.NonNull;

import com.android.clup.concurrent.Callback;
import com.android.clup.concurrent.Result;
import com.android.clup.json.JsonParser;
import com.android.clup.model.Date;
import com.android.clup.model.Reservation;
import com.android.clup.model.Shop;

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
     * Return the {@code UUID} code associated to the Queue in which the Customer is being added.
     */
    public void getUuid(@NonNull final String username, @NonNull final String shopId,
                        @NonNull final Date date, @NonNull final String time,
                        @NonNull final Callback<String> callback) {
        this.executor.execute(() -> {
            final String queuePOSTRequestURL = API_AVAILABLE_SHOPS_URL + "queue/enter";

            final String jsonReservation =
                    JsonParser.toJsonReservation(username, time.split(":")[0], shopId, date.plainReversed());

            final Result<String> queuePOSTResponse =
                    RemoteConnection.postConnect(queuePOSTRequestURL, jsonReservation);

            Result<String> result;
            if (queuePOSTResponse instanceof Result.Success) {
                final String queueJSONResponse = ((Result.Success<String>) queuePOSTResponse).data;
                final String uuid = JsonParser.getUuid(queueJSONResponse);

                result = new Result.Success<>(uuid);
            } else
                result = queuePOSTResponse;

            callback.onComplete(result);
        });
    }

    /**
     * Return the list of {@link Shop}s available for a reservation.
     */
    public void getShops(@NonNull final Callback<List<Shop>> callback) {
        this.executor.execute(() -> {
            final String businessRequestURL = API_AVAILABLE_SHOPS_URL + "business/list";

            final Result<String> responseURL = RemoteConnection.connect(businessRequestURL);
            if (responseURL instanceof Result.Error) {
                final String errorMessage = "Unable to access business/list API";
                callback.onComplete(new Result.Error<>(errorMessage));
                return;
            }

            final String businessResponseJSON = ((Result.Success<String>) responseURL).data;

            final String queueRequestURL = API_AVAILABLE_SHOPS_URL + "queue/list";
            final Result<String> queueResponseURL = RemoteConnection.connect(queueRequestURL);

            if (queueResponseURL instanceof Result.Error) {
                final String errorMessage = "Unable to access queue/list API";
                callback.onComplete(new Result.Error<>(errorMessage));
                return;
            }

            final String queueResponseJSON = ((Result.Success<String>) queueResponseURL).data;

            final List<Shop> shops = JsonParser.getShops(businessResponseJSON, queueResponseJSON);
            final Result.Success<List<Shop>> result = new Result.Success<>(shops);
            callback.onComplete(result);
        });
    }

    /**
     * Invalidate the given {@link Reservation}. This means that the server side status
     * of the current reservation switches from {@code to-do} to {@code done}.
     */
    public void invalidateReservation(@NonNull final Reservation reservation) {
        this.executor.execute(() -> {
            final String requestURL = API_AVAILABLE_SHOPS_URL + "queue/update"
                    + "?uuid=" + reservation.getUuid();

            // no result caught: it is only an update call
            RemoteConnection.connect(requestURL);
        });
    }
}