package com.android.clup.api;

import androidx.annotation.NonNull;

import com.android.clup.concurrent.Callback;
import com.android.clup.concurrent.Result;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SMSAuthService {
    private static final String API_URL = "https://verify-2044-mdd3gm.twil.io/";
    public static final String DEFAULT_LOCALE = "us";

    private static SMSAuthService instance;
    private final Executor executor;

    private SMSAuthService() {
        this.executor = Executors.newSingleThreadExecutor();
    }

    public static SMSAuthService getInstance() {
        if (instance == null)
            instance = new SMSAuthService();
        return instance;
    }

    /**
     * Starts the verification procedure with the remote {@code API} by sending the {@code start-verify}
     * message.
     *
     * @param completePhoneNumber the complete phone number of the device to be authenticated.
     *                            This parameter should be in the form [country ISO][phone number].
     *                            eg. [1][5432211278]
     *                            Square brackets excluded, no number formatting (brackets, dashes, spaces ...).
     * @param smsLocale           the locale in which the authentication message will be formatted.
     * @param callback            the callback through which the caller will be notified once the verification
     *                            procedure is ended.
     */
    public void startVerify(@NonNull final String completePhoneNumber, @NonNull final String smsLocale,
                            @NonNull final Callback<String> callback) {
        this.executor.execute(() -> {
            final String authServiceUrl = API_URL + "start-verify";
            final String target = "to=%2B" + completePhoneNumber;
            final String channel = "channel=sms";
            final String locale = "locale=" + smsLocale;

            final String requestUrl = authServiceUrl + "?" + target + "&" + channel + "&" + locale;
            final Result<String> result = RemoteConnection.connect(requestUrl);

            callback.onComplete(result);
        });
    }

    /**
     * Concludes the verification procedure with the remote {@code API} by sending the {@code check-verify}
     * message.
     *
     * @param completePoneNumber the complete phone number of the device to be authenticated.
     *                           This parameter should be in the form [country ISO][phone number].
     *                           eg. [1][5432211278]
     * @param code               the authentication code received via SMS on the device to be authenticated.
     * @param callback           the callback through which the caller will be notified once the verification
     *                           procedure is ended.
     */
    public void checkVerify(@NonNull final String completePoneNumber, @NonNull final String code, @NonNull final Callback<String> callback) {
        this.executor.execute(() -> {
            final String authServiceUrl = API_URL + "check-verify";
            final String target = "to=%2B" + completePoneNumber;
            final String verificationCode = "verification_code=" + code;

            final String requestUrl = authServiceUrl + "?" + target + "&" + verificationCode;
            final Result<String> result = RemoteConnection.connect(requestUrl);

            callback.onComplete(result);
        });
    }
}
