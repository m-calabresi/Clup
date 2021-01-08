package com.android.clup.api;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.android.clup.concurrent.QRCodeCallback;
import com.android.clup.concurrent.Result;

import net.glxn.qrgen.android.QRCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class QRCodeService {
    private static final int QR_CODE_SIZE = 1024;

    public static final String API_URL = "https://mobile-project-1605.twil.io/queue/enter";
    private final Executor executor;

    public QRCodeService() {
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void generateQRCode(@NonNull final String username, final int hours, @NonNull final String status,
                               @NonNull final QRCodeCallback<Bitmap> callback) {
        executor.execute(() -> {
            Result<Bitmap> result;
            try {
                final String payload = toJsonString(username, hours, status);
                final Result<String> response = RemoteConnection.postConnect(API_URL, payload);

                if (response instanceof Result.Success) {
                    final String jsonResponse = ((Result.Success<String>) response).data;
                    final String uuid = getUuid(jsonResponse, username, hours, status);

                    // create qr-code bitmap & return it
                    final Bitmap bitmap = QRCode.from(uuid).withSize(QR_CODE_SIZE, QR_CODE_SIZE).bitmap();
                    result = new Result.Success<>(bitmap);
                } else
                    result = new Result.Error<>("Invalid response"); // TODO replace with resource string
            } catch (InvalidResponseException e) {
                result = new Result.Error<>(e.getLocalizedMessage());
            }
            callback.onComplete(result);
        });
    }

    @NonNull
    private static String toJsonString(@NonNull final String username, final int hours, @NonNull final String status) {
        try {
            final JSONObject jsonContent = new JSONObject();
            jsonContent.put("user_fullname", username);

            jsonContent.put("hours", Integer.toString(hours));
            jsonContent.put("status", status);

            final JSONObject jsonBody = new JSONObject();
            jsonBody.put("body", jsonContent);

            return jsonBody.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    private String getUuid(@NonNull final String jsonResponse, @NonNull final String username,
                           final int hours, @NonNull final String status) throws InvalidResponseException {
        try {
            final JSONObject jsonObject = new JSONObject(jsonResponse);

            final String receivedFullName = jsonObject.getString("user_fullname");
            final String receivedHours = jsonObject.getString("hours");
            final String receivedStatus = jsonObject.getString("status");

            if (receivedFullName.equals(username)
                    && receivedHours.equals(Integer.toString(hours))
                    && receivedStatus.equals(status)) {

                return jsonObject.getString("uuid");
            } else
                throw new InvalidResponseException("One or more received parameters doesn't match with original ones");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
