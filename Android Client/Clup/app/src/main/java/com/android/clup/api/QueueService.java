package com.android.clup.api;

import androidx.annotation.NonNull;

import com.android.clup.concurrent.Callback;
import com.android.clup.concurrent.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class QueueService {
    @NonNull
    public static final String API_URL = "https://mobile-project-1605.twil.io/queue/enter";
    @NonNull
    private static final String TAG_USER_FULLNAME = "user_fullname";
    @NonNull
    private static final String TAG_HOUR = "hour";
    @NonNull
    private static final String TAG_STATUS = "status";
    @NonNull
    private static final String TAG_UUID = "uuid";

    private final Executor executor;

    public QueueService() {
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Returns the UUID code associated to the Queue in which the user is being added.
     *
     * @param username the complete name of the user to insert in the queue.
     * @param hour     the time at which the user can access the queue
     * @param status   TODO
     * @param callback the callback through which the caller will be notified once the queue
     *                 procedure is ended.
     */
    @SuppressWarnings("unchecked")
    public void getQueueUUID(@NonNull final String username, @NonNull final String hour,
                             @NonNull final String status, @NonNull final Callback callback) {
        executor.execute(() -> {
            Result result;
            try {
                final String payload = toJsonString(username, hour, status);
                final Result response = RemoteConnection.postConnect(API_URL, payload);

                if (response instanceof Result.Success) {
                    final String jsonResponse = ((Result.Success<String>) response).data;
                    final String uuid = getUuid(jsonResponse, username, hour, status);

                    result = new Result.Success<>(uuid);
                } else
                    result = new Result.Error("Invalid response"); // TODO replace with resource string
            } catch (InvalidResponseException e) {
                result = new Result.Error(e.getLocalizedMessage());
            }
            callback.onComplete(result);
        });
    }

    /**
     * Returns a Json String representation of the given information.
     *
     * @param username the complete name of the user
     * @param hour     the time at which the user can access the queue
     * @param status   TODO
     * @return a Json string representing the encoding of the given information.
     */
    @NonNull
    private static String toJsonString(@NonNull final String username, @NonNull final String hour,
                                       @NonNull final String status) {
        try {
            final JSONObject jsonContent = new JSONObject();
            jsonContent.put(TAG_USER_FULLNAME, username);
            jsonContent.put(TAG_HOUR, hour);
            jsonContent.put(TAG_STATUS, status);

            return jsonContent.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks whether the requests is legit and, in case, extracts the UUID code from the given
     * {@param jsonResponse} and returns it.
     *
     * @param jsonResponse the Json string containing the data to operate on.
     * @param username     the complete name of the user, used to legitimate the request.
     * @param hour         the hour at which the user can access the queue, used to legitimate the request.
     * @param status       TODO, used to legitimate the request.
     * @return the UUID code associate to the given response.
     * @throws InvalidResponseException if there is any mismatch between the data extracted from the
     *                                  response and the data provided.
     */
    @NonNull
    private String getUuid(@NonNull final String jsonResponse, @NonNull final String username,
                           @NonNull final String hour, @NonNull final String status)
            throws InvalidResponseException {
        try {
            final JSONObject jsonObject = new JSONObject(jsonResponse);

            final String receivedFullName = jsonObject.getString(TAG_USER_FULLNAME);
            final String receivedHour = jsonObject.getString(TAG_HOUR);
            final String receivedStatus = jsonObject.getString(TAG_STATUS);

            if (receivedFullName.equals(username) && receivedHour.equals(hour) && receivedStatus.equals(status))
                return jsonObject.getString(TAG_UUID);
            else
                throw new InvalidResponseException("One or more received parameters doesn't match with original ones");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
