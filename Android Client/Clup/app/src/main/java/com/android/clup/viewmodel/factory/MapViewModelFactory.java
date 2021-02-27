package com.android.clup.viewmodel.factory;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.android.clup.adapter.OnListItemClickedCallback;
import com.android.clup.viewmodel.MapViewModel;

import java.util.Objects;

public class MapViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    private final Activity activity;
    @NonNull
    final OnListItemClickedCallback recyclerViewItemClickedCallback;


    public MapViewModelFactory(@NonNull final Activity activity,
                               @NonNull final OnListItemClickedCallback recyclerViewItemClickedCallback) {
        this.activity = activity;
        this.recyclerViewItemClickedCallback = recyclerViewItemClickedCallback;
    }

    /**
     * Creates a new instance of {@link MapViewModel} such that it can accept as parameter the
     * parent {@code Activity} and the {@link OnListItemClickedCallback} callback.
     */
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull final Class<T> modelClass) {
        return Objects.requireNonNull(modelClass.cast(new MapViewModel(this.activity, this.recyclerViewItemClickedCallback)));
    }
}