package com.android.clup.api;

import androidx.annotation.NonNull;

import com.android.clup.concurrent.Callback;
import com.android.clup.concurrent.Result;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RemoteConnection {
    private static final String URL_TEST_CONNECTION = "http://clients3.google.com/generate_204";
    private static final int DEFAULT_TIMEOUT_DURATION = 3000;

    private RemoteConnection() {

    }

    /**
     * Sends to the specified {@code URL} an http/https request without parameters.
     *
     * @param requestUrl the {@code URL} to connect to.
     * @return the response to the request sent.
     */
    @NonNull
    static Result<String> connect(@NonNull final String requestUrl) {
        Result<String> result;
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;

        try {
            final URL url = new URL(requestUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            result = new Result.Success<>(toString(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
            result = new Result.Error<>(Objects.requireNonNull(e.getLocalizedMessage()));
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    result = new Result.Error<>(Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }

            if (httpURLConnection != null)
                httpURLConnection.disconnect();
        }
        return result;
    }

    /**
     * Send to the specified {@code requestUrl} an http/https message using {@code POST} method to
     * transmit {@code content}.
     *
     * @param requestUrl  the {@code URL} to connect to.
     * @param jsonPayload the content to be sent in {@code POST} to the target {@code URL}. This string must
     *                    be encoded in{@code Json} format.
     * @return the response to the request sent.
     */
    @NonNull
    public static Result<String> postConnect(@NonNull final String requestUrl, @NonNull final String jsonPayload) {
        Result<String> result;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            final URL url = new URL(requestUrl);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            outputStream = connection.getOutputStream();
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);

            inputStream = new BufferedInputStream(connection.getInputStream());
            result = new Result.Success<>(toString(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
            result = new Result.Error<>(Objects.requireNonNull(e.getLocalizedMessage()));
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    result = new Result.Error<>(Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    result = new Result.Error<>(Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }

            if (connection != null)
                connection.disconnect();
        }
        return result;
    }

    /**
     * Returns the String representation of the given InputStream.
     */
    @NonNull
    private static String toString(@NonNull final InputStream inputStream) {
        final Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        final String result = s.hasNext() ? s.next() : "";

        s.close();
        return result;
    }

    /**
     * Returns {@code true} if the devise is able to establish a remote https connection,
     * {@code false} otherwise.
     * This can be used to test whether the device is connected to the Internet.
     */
    public static void hasInternetAccess(@NonNull final Callback<Boolean> callback) {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            HttpURLConnection connection = null;
            Result<Boolean> result;
            try {
                HttpURLConnection.setFollowRedirects(false);
                connection = (HttpURLConnection) (new URL(URL_TEST_CONNECTION).openConnection());
                connection.setReadTimeout(DEFAULT_TIMEOUT_DURATION);
                connection.setConnectTimeout(DEFAULT_TIMEOUT_DURATION);
                result = new Result.Success<>(connection.getResponseCode() == 204 && connection.getContentLength() == 0);
            } catch (SocketTimeoutException e) {
                result = new Result.Success<>(false);
            } catch (IOException e) {
                e.printStackTrace();
                result = new Result.Error<>(Objects.requireNonNull(e.getLocalizedMessage()));
            } finally {
                if (connection != null)
                    connection.disconnect();
            }
            callback.onComplete(result);
        });
        executor.shutdown();
    }
}
