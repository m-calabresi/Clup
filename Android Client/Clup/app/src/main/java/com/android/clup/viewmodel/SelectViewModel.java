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
    private final MutableLiveData<Integer> groupIdLiveData;

    @NonNull
    private final QueueService queueService;

    public SelectViewModel() {
        this.model = Model.getInstance();
        this.groupIdLiveData = new MutableLiveData<>();
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
     * Reset the index corresponding to the shop selected by the user.
     * This is done to allow the user to select another shop by going back in MapActivity and pick
     * another shop.
     */
    public void resetSelectedShopPosition() {
        this.model.resetSelectedShopIndex();
    }

    /**
     * Store index corresponding to the day selected by the user.
     */
    public void setSelectedDayPosition(final int position) {
        this.model.setSelectedDayIndex(position);
    }

    /**
     * Reset the index corresponding to the day selected by the user.
     */
    public void resetSelectedDayPosition() {
        this.model.resetSelectedDayIndex();
    }

    /**
     * Store index corresponding to the hour selected by the user.
     */
    public void setSelectedHourPosition(final int position) {
        this.model.setSelectedHourIndex(position);
    }

    /**
     * Reset the index corresponding to the shop selected by the user.
     */
    public void resetSelectedHourPosition() {
        this.model.resetSelectedHourIndex();
    }

    /**
     * Utility method to fill a ChipGroup with chips that contains values specified by hours.
     */
    public static void setHourChips(@NonNull final ChipGroup parent, @NonNull final List<String> hours) {
        parent.post(() -> {
            if (parent.getChildCount() > 0)
                parent.removeAllViews();

            final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

            for (int i = 0; i < hours.size(); i++) {
                final String foodName = hours.get(i);
                final Chip chip = (Chip) layoutInflater.inflate(R.layout.item_hour, parent, false);
                chip.setText(foodName);

                parent.addView(chip);
            }
        });
    }

    /**
     * A MutableLiveData that reacts to chips selection.
     */
    @NonNull
    public MutableLiveData<Integer> getGroupIdLiveData() {
        return this.groupIdLiveData;
    }

    /**
     * Set the current value of MutableLiveData to the given group ID, all observers will be notified
     * that this group is now the only one containing a selected chip.
     */
    public void setGroupTagLiveData(@NonNull final Object groupTag) {
        this.groupIdLiveData.setValue((int) groupTag);
    }

    /**
     * Save the reservation made by the user, then notify the caller about the result.
     */
    public void bookReservation(@NonNull Callback<Boolean> callback) {
        final String shopName = this.model.getSelectedShop().getName();
        final Date date = this.model.getSelectedDay().getDate();
        final String hour = this.model.getSelectedHour();
        final LatLng coords = this.model.getSelectedShop().getCoordinates();

        this.queueService.getUuid(this.model.getFullname(), shopName, date.plain(), hour, result -> {
            Result<Boolean> bookResult;

            if (result instanceof Result.Success) {
                final String uuid = ((Result.Success<String>) result).data;

                final Reservation reservation = new Reservation(shopName, date, hour, uuid, coords);
                this.model.addReservation(reservation);
                // last reservation added is put in the last position of the list
                this.model.setSelectedReservationIndex(this.model.getReservations().size() - 1);

                bookResult = new Result.Success<>(true);
            } else {
                final String errorMsg = ((Result.Error<String>) result).message;
                bookResult = new Result.Error<>(errorMsg);
            }
            callback.onComplete(bookResult);
        });
    }
}
