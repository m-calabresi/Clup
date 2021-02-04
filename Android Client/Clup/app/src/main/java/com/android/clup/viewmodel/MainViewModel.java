package com.android.clup.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModel;

import com.android.clup.model.Model;
import com.android.clup.model.Preferences;
import com.android.clup.model.Reservation;
import com.android.clup.ui.Utils;

public class MainViewModel extends ViewModel {
    private final Model model;

    public MainViewModel() {
        this.model = Model.getInstance();
    }

    /**
     * Return the last theme selected by the user or the default one (if user didn't set any theme).
     */
    public int getTheme() {
        return Preferences.getTheme();
    }

    public void setSelectedReservationPosition(final int position) {
        this.model.setSelectedReservationIndex(position);
    }

    public Reservation getSelectedReservation() {
        return this.model.getSelectedReservation();
    }

    /**
     * Displays the theme alert dialog and handles the user choice.
     */
    public void displayThemesAlertDialog(@NonNull final Context context) {
        Utils.displayThemesAlertDialog(context, mode -> {
            Preferences.setTheme(mode);
            AppCompatDelegate.setDefaultNightMode(mode);
        });
    }
}
