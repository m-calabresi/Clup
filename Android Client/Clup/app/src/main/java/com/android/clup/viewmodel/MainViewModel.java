package com.android.clup.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModel;

import com.android.clup.R;
import com.android.clup.api.QueueService;
import com.android.clup.concurrent.Callback;
import com.android.clup.concurrent.Result;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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

    /**
     * Sets the user data that will be used to create the corresponding qr-code.
     */
    public void setUserData(@NonNull final String username, @NonNull final String hour, @NonNull final String status) {
        this.username = username;
        this.hour = hour;
        this.status = status;
    }

    /**
     * Request asynchronously the data used to create the corresponding qr-code and notify the user
     * when a valid qr-code has been created.
     */
    public void getQrCode(final int onColor, final int offColor, @NonNull final Callback<Bitmap> callback) {
        if (this.username == null || this.hour == null || this.status == null)
            throw new InvalidParameterException("Some parameters are null or empty, did you call 'setUserData'?");

        if (this.uuid == null) {
            this.queueService.getQueueUUID(this.username, this.hour, this.status, result -> {
                if (result instanceof Result.Success) {
                    this.uuid = ((Result.Success<String>) result).data; // cache qrCode to retrieve it faster

                    final Result<Bitmap> qrCode = new Result.Success<>(generateQRCode(this.uuid, onColor, offColor));
                    callback.onComplete(qrCode);
                } else {
                    final String errorMsg = ((Result.Error<String>) result).message;
                    final Result<Bitmap> error = new Result.Error<>(errorMsg);
                    callback.onComplete(error);
                }
            });
            return;
        }
        final Bitmap cachedQrCode = generateQRCode(this.uuid, onColor, offColor);
        callback.onComplete(new Result.Success<>(cachedQrCode));
    }

    /**
     * Generates the qr-code associated to the given uuid using the specified color-scheme.
     */
    private Bitmap generateQRCode(@NonNull final String uuid, final int onColor, final int offColor) {
        // create qr-code bitmap & return it
        return QRCode.from(uuid).withSize(QR_CODE_SIZE, QR_CODE_SIZE).withColor(onColor, offColor).bitmap();
    }

    /**
     * Sets the default theme for the application.
     */
    public void setDefaultTheme(@NonNull final Context context) {
        final int mode = getThemePreference(context);
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    /**
     * Stores the given theme-preference.
     */
    public void setThemePreference(@NonNull final Context context, final int mode) {
        this.themeValue = mode;

        final SharedPreferences preferences = context.getSharedPreferences(PREF_THEME_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREF_THEME_NAME, mode);
        editor.apply();

        AppCompatDelegate.setDefaultNightMode(mode);
    }

    /**
     * Returns the stored theme.preference.
     */
    private int getThemePreference(@NonNull final Context context) {
        if (this.themeValue == null) {
            final SharedPreferences preferences = context.getSharedPreferences(PREF_THEME_NAME, Context.MODE_PRIVATE);
            this.themeValue = preferences.getInt(PREF_THEME_NAME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        return this.themeValue;
    }

    /**
     * Maps a theme-preference value to the corresponding position in the AlertDialog.
     */
    private int mapToPosition(final int themeValue) {
        /*  -1 -> 2
         *   1 -> 0
         *   2 -> 1 */
        if (themeValue < 0)
            return 2;
        return themeValue - 1;
    }

    /**
     * Maps the position in the AlertDialog to the corresponding theme-preference.
     */
    private int mapToTheme(final int position) {
        switch (position) {
            case 0:
                return AppCompatDelegate.MODE_NIGHT_NO;
            case 1:
                return AppCompatDelegate.MODE_NIGHT_YES;
            default:
                return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
    }

    /**
     * Displays the AlertDialog through which the user can choose the desired theme.
     */
    public void displayThemesAlertDialog(@NonNull final Context context) {
        final int selectedPosition = mapToPosition(getThemePreference(context));

        new MaterialAlertDialogBuilder(context, R.style.AppTheme_Clup_RoundedAlertDialog)
                .setTitle(R.string.title_theme_alert)
                .setSingleChoiceItems(R.array.themes_array, selectedPosition, (dialog, which) -> {
                    final int mode = mapToTheme(which);
                    setThemePreference(context, mode);
                    dialog.dismiss();
                })
                .create()
                .show();
    }
}
