package com.android.clup.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    @Nullable
    private Integer themeValue;
    @NonNull
    private static final String PREF_THEME_NAME = "theme_preference";

    public MainViewModel() {

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
