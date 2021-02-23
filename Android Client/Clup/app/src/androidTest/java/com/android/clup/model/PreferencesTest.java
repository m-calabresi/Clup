package com.android.clup.model;

import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.android.clup.ApplicationContext;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PreferencesTest {
    // NOTE: prefNames are defined in Preferences.java, if they are modified these tests will not work.

    @Test(expected = NullPointerException.class)
    public void setFriendlyName() {
        final String prefName = "com.android.Clup.FriendlyName";
        final String friendlyName = "Friendly Name";

        // clear shared preference
        ApplicationContext.get().getSharedPreferences(prefName, Context.MODE_PRIVATE).edit().clear().apply();

        // check for non defined preference to throw an exception
        Preferences.getFriendlyName();

        // set the preference correctly
        Preferences.setFriendlyName(friendlyName);

        assertEquals(friendlyName, Preferences.getFriendlyName());
    }

    @Test(expected = NullPointerException.class)
    public void setFullname() {
        final String prefName = "com.android.Clup.Fullname";
        final String fullName = "MyFullName";

        // clear shared preference
        ApplicationContext.get().getSharedPreferences(prefName, Context.MODE_PRIVATE).edit().clear().apply();

        // check for non defined preference to throw an exception
        Preferences.getFullname();

        // set the preference correctly
        Preferences.setFullname(fullName);

        assertEquals(fullName, Preferences.getFullname());
    }

    @Test
    public void setTheme() {
        final String prefName = "com.android.Clup.Theme";
        final int theme = 1;

        // clear shared preference
        ApplicationContext.get().getSharedPreferences(prefName, Context.MODE_PRIVATE).edit().clear().apply();

        // check for non defined preference to return the default value
        assertEquals(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, Preferences.getTheme());

        // set the preference correctly
        Preferences.setTheme(theme);

        assertEquals(theme, Preferences.getTheme());
    }

    @Test
    public void setFirstTime() {
        final String prefName = "com.android.Clup.FirstTime";
        final boolean firstTime = false;

        // clear shared preference
        ApplicationContext.get().getSharedPreferences(prefName, Context.MODE_PRIVATE).edit().clear().apply();

        // check for non defined preference to return the default value
        assertTrue(Preferences.isFirstTime());

        // set the preference correctly
        Preferences.setFirstTime(firstTime);

        assertFalse(Preferences.isFirstTime());
    }
}