package com.android.clup.viewmodel;

import androidx.lifecycle.ViewModel;

public class MapViewModel extends ViewModel {
    private int statusBarHeight;

    public MapViewModel() {

    }

    public void setStatusBarHeight(final int statusBarHeight) {
        this.statusBarHeight = statusBarHeight;
    }

    public int getStatusBarHeight() {
        return this.statusBarHeight;
    }
}
