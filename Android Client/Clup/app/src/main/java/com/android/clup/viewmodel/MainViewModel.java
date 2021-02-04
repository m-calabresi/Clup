package com.android.clup.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModel;

import com.android.clup.model.Model;
import com.android.clup.model.Preferences;
import com.android.clup.ui.Utils;

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
}
