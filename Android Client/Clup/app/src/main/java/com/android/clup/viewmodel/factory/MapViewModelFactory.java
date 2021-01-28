package com.android.clup.viewmodel.factory;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.android.clup.viewmodel.MapViewModel;

import java.util.Objects;

public class MapViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    private final Activity activity;


    public MapViewModelFactory(@NonNull final Activity activity) {
        this.activity = activity;
    }

    /**
     * Creates a new instance of {@link MapViewModel} such that it can accept the Activity parameter.
     */
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull final Class<T> modelClass) {
        return Objects.requireNonNull(modelClass.cast(new MapViewModel(this.activity)));
    }
}