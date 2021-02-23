package com.android.clup.viewmodel;

import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.clup.R;
import com.android.clup.api.QueueService;
import com.android.clup.concurrent.Callback;
import com.android.clup.concurrent.Result;
import com.android.clup.model.Date;
import com.android.clup.model.Model;
import com.android.clup.model.Reservation;
import com.android.clup.model.Shop;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class SelectViewModel extends ViewModel {
    @NonNull
    private final Model model;
    @NonNull
    private final MutableLiveData<Integer> groupTagLiveData;

    @NonNull
    private final QueueService queueService;

    public SelectViewModel() {
        this.model = Model.getInstance();
        this.groupTagLiveData = new MutableLiveData<>();
        this.queueService = new QueueService();
    }

    /**
     * Return the shop selected by the user.
     */
    @NonNull
    public Shop getSelectedShop() {
        return this.model.getSelectedShop();
    }

    /**
     * Reset the shop selected by the user.
     * This is done to allow the user to select another shop by going back in MapActivity and pick
     * another one.
     */
    public void resetSelectedShop() {
        this.model.resetSelectedShop();
    }

    /**
     * Store the day selected by the user.
     */
    public void setSelectedDay(final int position) {
        this.model.setSelectedDay(this.model.getSelectedShop().getAvailableDays().get(position));
    }

    /**
     * Reset the day selected by the user.
     */
    public void resetSelectedDay() {
        this.model.resetSelectedDay();
    }

    /**
     * Store the time selected by the user.
     */
    public void setSelectedTime(final int position) {
        this.model.setSelectedTime(this.model.getSelectedDay().getTimes().get(position));
    }

    /**
     * Reset the shop selected by the user.
     */
    public void resetSelectedTime() {
        this.model.resetSelectedTime();
    }

    /**
     * Utility method to fill a ChipGroup with chips that contains values specified by times.
     */
    public static void setTimeChips(@NonNull final ChipGroup parent, @NonNull final List<String> times) {
        parent.post(() -> {
            if (parent.getChildCount() > 0)
                parent.removeAllViews();

            final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

            for (int i = 0; i < times.size(); i++) {
                final String foodName = times.get(i);
                final Chip chip = (Chip) layoutInflater.inflate(R.layout.item_time, parent, false);
                chip.setText(foodName);

                parent.addView(chip);
            }
        });
    }

    /**
     * A MutableLiveData that reacts to chips selection.
     */
    @NonNull
    public MutableLiveData<Integer> getGroupTagLiveData() {
        return this.groupTagLiveData;
    }

    /**
     * Set the current value of MutableLiveData to the given group ID, all observers will be notified
     * that this group is now the only one containing a selected chip.
     */
    public void setGroupTagLiveData(@NonNull final Object groupTag) {
        this.groupTagLiveData.setValue((int) groupTag);
    }

    /**
     * Save the reservation made by the user, then notify the caller about the result.
     */
    public void bookReservation(@NonNull Callback<Boolean> callback) {
        final String shopName = this.model.getSelectedShop().getName();

        final Date date = this.model.getSelectedDay().getDate();
        final String time = this.model.getSelectedTime();
        date.setTime(time);

        final LatLng coords = this.model.getSelectedShop().getCoordinates();

        this.queueService.getUuid(this.model.getFullname(), shopName, date.plain(), time, result -> {
            Result<Boolean> bookResult;

            if (result instanceof Result.Success) {
                final String uuid = ((Result.Success<String>) result).data;

                final Reservation reservation = new Reservation(shopName, date, uuid, coords);
                this.model.addReservation(reservation);
                this.model.setSelectedReservation(reservation);

                bookResult = new Result.Success<>(true);
            } else {
                final String errorMsg = ((Result.Error<String>) result).message;
                bookResult = new Result.Error<>(errorMsg);
            }
            callback.onComplete(bookResult);
        });
    }
}
