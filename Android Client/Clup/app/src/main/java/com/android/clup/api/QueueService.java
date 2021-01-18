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

    public void getQueueUUID(@NonNull final String username, @NonNull final String hour,
                             @NonNull final String status, @NonNull final Callback<String> callback) {
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
