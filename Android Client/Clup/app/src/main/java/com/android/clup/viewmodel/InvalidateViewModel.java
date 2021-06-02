package com.android.clup.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.android.clup.api.QueueService;
import com.android.clup.model.Model;
import com.android.clup.model.Reservation;

public class InvalidateViewModel extends ViewModel {
    @NonNull
    private final Model model;

    @NonNull
    private final QueueService queueService;

    public InvalidateViewModel() {
        this.model = Model.getInstance();
        this.queueService = new QueueService();
    }

    /**
     * Invalidates the current reservation by removing it from the local list. Then, this update
     * is propagated to the server-side business logic.
     */
    public void invalidateSelectedReservation() {
        final Reservation selectedReservation = this.model.getSelectedReservation();

        this.model.removeReservation(selectedReservation);
        this.model.resetSelectedReservation();

        this.queueService.invalidateReservation(selectedReservation);
    }
}
