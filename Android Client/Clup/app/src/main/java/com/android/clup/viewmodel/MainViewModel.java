package com.android.clup.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModel;

import com.android.clup.model.Model;
import com.android.clup.model.Preferences;
import com.android.clup.model.Reservation;
import com.android.clup.ui.Utils;

import java.util.List;

public class MainViewModel extends ViewModel {
    @NonNull
    private final Model model;

    public MainViewModel() {
        this.model = Model.getInstance();
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

    /**
     * Set the selected reservation.
     */
    public void setSelectedReservation(final int position) {
        this.model.setSelectedReservation(this.model.getReservations().get(position));
    }

    /**
     * Return the list of reservation the user has booked.
     */
    @NonNull
    public List<Reservation> getReservations() {
        return this.model.getReservations();
    }

    /**
     * Return the friendly name of the user.
     */
    @NonNull
    public String getFriendlyName() {
        return this.model.getFriendlyName();
    }
}
