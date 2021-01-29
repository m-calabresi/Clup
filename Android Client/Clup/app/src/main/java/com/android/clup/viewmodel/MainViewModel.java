package com.android.clup.viewmodel;

import androidx.lifecycle.ViewModel;

import com.android.clup.model.Preferences;

public class MainViewModel extends ViewModel {


    public MainViewModel() {

    }

    /**
     * Return the last theme selected by the user or the default one (if user didn't set any theme).
     */
    public int getTheme() {
        return Preferences.getTheme();
    }
}
