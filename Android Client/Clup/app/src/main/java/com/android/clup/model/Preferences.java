package com.android.clup.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.clup.ApplicationContext;

/**
 * A helper class used to load and store shared preferences.
 */
public class Preferences {
    @NonNull
    private static final String PREF_FRIENDLY_NAME = "com.android.Clup.FriendlyName";
    @NonNull
    private static final String PREF_FULL_NAME = "com.android.Clup.Fullname";
    @NonNull
    private static final String PREF_THEME = "com.android.Clup.Theme";
    @NonNull
    private static final String PREF_FIRST_TIME_NAME = "com.android.Clup.FirstTime";

    private Preferences() {

    }

    public static void setFriendlyName(@NonNull final String friendlyName) {
        setStringPreference(PREF_FRIENDLY_NAME, friendlyName);
    }

    @NonNull
    public static String getFriendlyName() {
        return getStringPreference(PREF_FRIENDLY_NAME);
    }

    public static void setFullname(@NonNull final String fullname) {
        setStringPreference(PREF_FULL_NAME, fullname);
    }

    @NonNull
    public static String getFullname() {
        return getStringPreference(PREF_FULL_NAME);
    }

    public static void setTheme(final int mode) {
        setIntPreference(PREF_THEME, mode);
    }

    public static int getTheme() {
        return getIntPreference(PREF_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public static void setFirstTime(final boolean isFirstTime) {
        setBooleanPreference(PREF_FIRST_TIME_NAME, isFirstTime);
    }

    public static boolean isFirstTime() {
        return getBooleanPreference(PREF_FIRST_TIME_NAME, true);
    }

    /**
     * Set the given string preference associated to the corresponding name.
     */
    private static void setStringPreference(@NonNull final String name, @NonNull final String value) {
        final SharedPreferences mPreferences = ApplicationContext.get().getSharedPreferences(name, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(name, value);
        editor.apply();
    }

    /**
     * Return the string preference associated to the specified name.
     */
    @NonNull
    private static String getStringPreference(@NonNull final String name) {
        final SharedPreferences mPreferences = ApplicationContext.get().getSharedPreferences(name, Context.MODE_PRIVATE);
        final String value = mPreferences.getString(name, null);

        if (value != null)
            return value;
        throw new NullPointerException("No preference found that matches the given name: " + name);
    }

    /**
     * Set the given integer preference associated to the corresponding name.
     */
    @SuppressWarnings("SameParameterValue")
    private static void setIntPreference(@NonNull final String name, final int value) {
        final SharedPreferences mPreferences = ApplicationContext.get().getSharedPreferences(name, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(name, value);
        editor.apply();
    }

    /**
     * Return the integer preference associated to the specified name.
     */
    @SuppressWarnings("SameParameterValue")
    private static int getIntPreference(@NonNull final String name, final int defaultValue) {
        final SharedPreferences mPreferences = ApplicationContext.get().getSharedPreferences(name, Context.MODE_PRIVATE);
        return mPreferences.getInt(name, defaultValue);
    }

    /**
     * Set the given boolean preference associated to the corresponding name.
     */
    @SuppressWarnings("SameParameterValue")
    private static void setBooleanPreference(@NonNull final String name, final boolean value) {
        final SharedPreferences mPreferences = ApplicationContext.get().getSharedPreferences(name, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(name, value);
        editor.apply();
    }

    /**
     * Return the boolean preference associated to the specified name.
     */
    @SuppressWarnings("SameParameterValue")
    private static boolean getBooleanPreference(@NonNull final String name, final boolean defaultValue) {
        final SharedPreferences mPreferences = ApplicationContext.get().getSharedPreferences(name, Context.MODE_PRIVATE);
        return mPreferences.getBoolean(name, defaultValue);
    }
}
