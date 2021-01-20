package com.android.clup.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModel;

import com.android.clup.api.QueueService;
import com.android.clup.concurrent.Callback;
import com.android.clup.concurrent.Result;

import net.glxn.qrgen.android.QRCode;

import java.security.InvalidParameterException;

public class MainViewModel extends ViewModel {
    public static final int TRANSITION_DELAY = 1000;
    private static final int QR_CODE_SIZE = 1024;

    @Nullable
    private Integer themeValue;
    @NonNull
    private static final String PREF_THEME_NAME = "theme_preference";

    @NonNull
    private final QueueService queueService;

    private String username;
    private String hour;
    private String status;
    private String uuid;

    public MainViewModel() {
        this.queueService = new QueueService();
    }

    public void setUserData(@NonNull final String username, @NonNull final String hour, @NonNull final String status) {
        this.username = username;
        this.hour = hour;
        this.status = status;
    }

    @SuppressWarnings("unchecked")
    public void getQrCode(final int onColor, final int offColor, @NonNull final Callback callback) {
        if (this.username == null || this.hour == null || this.status == null)
            throw new InvalidParameterException("Some parameters are null or empty, did you call 'setUserData'?");

        if (this.uuid == null) {
            this.queueService.getQueueUUID(this.username, this.hour, this.status, result -> {
                if (result instanceof Result.Success) {
                    this.uuid = ((Result.Success<String>) result).data; // cache qrCode to retrieve it faster

                    final Result qrCode = new Result.Success<>(generateQRCode(this.uuid, onColor, offColor));
                    callback.onComplete(qrCode);
                } else {
                    final String errorMsg = ((Result.Error) result).message;
                    final Result error = new Result.Error(errorMsg);
                    callback.onComplete(error);
                }
            });
            return;
        }
        final Bitmap cachedQrCode = generateQRCode(this.uuid, onColor, offColor);
        callback.onComplete(new Result.Success<>(cachedQrCode));
    }

    private Bitmap generateQRCode(@NonNull final String uuid, final int onColor, final int offColor) {
        // create qr-code bitmap & return it
        return QRCode.from(uuid).withSize(QR_CODE_SIZE, QR_CODE_SIZE).withColor(onColor, offColor).bitmap();
    }

    public void setThemePreference(@NonNull final Context context, final int mode) {
        this.themeValue = mode;

        final SharedPreferences preferences = context.getSharedPreferences(PREF_THEME_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREF_THEME_NAME, mode);
        editor.apply();

        AppCompatDelegate.setDefaultNightMode(mode);
    }

    public int getThemePreference(@NonNull final Context context) {
        if (this.themeValue == null) {
            final SharedPreferences preferences = context.getSharedPreferences(PREF_THEME_NAME, Context.MODE_PRIVATE);
            this.themeValue = preferences.getInt(PREF_THEME_NAME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        return this.themeValue;
    }

    public int mapToPosition(final int themeValue) {
        /*  -1 -> 2
         *   1 -> 0
         *   2 -> 1 */
        if (themeValue < 0)
            return 2;
        return themeValue - 1;
    }

    public int mapToTheme(final int position) {
        switch (position) {
            case 0:
                return AppCompatDelegate.MODE_NIGHT_NO;
            case 1:
                return AppCompatDelegate.MODE_NIGHT_YES;
            default:
                return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
    }
}
