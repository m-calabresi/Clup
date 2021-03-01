package com.android.clup.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.android.clup.model.Model;

public class InvalidateViewModel extends ViewModel {
    @NonNull
    private final Model model;

    public InvalidateViewModel() {
        this.model = Model.getInstance();
    }

    /**
     * Invalidates the current reservation by removing it from the local list. Then, this update
     * is propagated to the server-side business logic.
     */
    public void invalidateSelectedReservation() {
        this.model.removeReservation(this.model.getSelectedReservation());
        this.model.resetSelectedReservation();

        // TODO implement API call
    }
}
