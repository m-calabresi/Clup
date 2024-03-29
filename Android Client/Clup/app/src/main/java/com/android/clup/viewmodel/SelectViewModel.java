package com.android.clup.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.clup.api.QueueService;
import com.android.clup.concurrent.Callback;
import com.android.clup.concurrent.Result;
import com.android.clup.exception.NoAvailableDayException;
import com.android.clup.model.AvailableDay;
import com.android.clup.model.AvailableSlot;
import com.android.clup.model.Date;
import com.android.clup.model.Model;
import com.android.clup.model.Reservation;
import com.android.clup.model.Shop;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Objects;

public class SelectViewModel extends ViewModel {
    @NonNull
    private final Model model;
    @NonNull
    private final QueueService queueService;

    /**
     * {@code LiveData} responsible for the visibility status of some {@code View}.
     */
    @NonNull
    private final MutableLiveData<Boolean> visibilityStatusLiveData;

    public SelectViewModel() {
        this.model = Model.getInstance();
        this.queueService = new QueueService();

        this.visibilityStatusLiveData = new MutableLiveData<>();
    }

    /**
     * Return the visibility status to be observed by the interested {@code View}s.
     */
    @NonNull
    public LiveData<Boolean> getVisibilityStatusLiveData() {
        return this.visibilityStatusLiveData;
    }

    /**
     * Trigger an update to the visibility status of all interested {@code View}s.
     */
    public void setVisibilityStatusLiveData(final boolean status) {
        this.visibilityStatusLiveData.setValue(status);
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
        final AvailableDay availableDay = this.model.getSelectedShop().getAvailableDays().get(position);
        this.model.setSelectedDay(availableDay);
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
    public void setSelectedTime(@NonNull final String time) {
        this.model.setSelectedTime(time);
    }

    /**
     * Reset the shop selected by the user.
     */
    public void resetSelectedTime() {
        this.model.resetSelectedTime();
    }

    /**
     * Save the reservation made by the user, then notify the caller about the result.
     */
    public void bookReservation(@NonNull Callback<Boolean> callback) {
        final String shopId = this.model.getSelectedShop().getId();
        final String shopName = this.model.getSelectedShop().getName();

        final String userFullName = this.model.getFullname();

        final Date date = this.model.getSelectedDay().getDate();
        final String time = this.model.getSelectedTime();
        date.setTime(time);

        final LatLng coords = this.model.getSelectedShop().getCoordinates();

        final List<Shop> shops = Objects.requireNonNull(this.model.getShops());

        // prevent the user from booking another reservation for the same day
        try {
            final AvailableDay availableDay = Shop.getById(shops, shopId).getAvailableDayByDate(date);

            for (final AvailableSlot availableSlot : availableDay.getAvailableSlots()) {
                if (availableSlot.getEnqueuedCustomersNames().contains(userFullName)) {
                    final Result.Success<Boolean> result = new Result.Success<>(false);
                    callback.onComplete(result);
                    return;
                }
            }
        } catch (@NonNull final NoAvailableDayException e) {
            final String errorMessage = "Unable to book reservation, the requested day (" + date.formatted() + ") is not available for shop " + shopName + ". Cause: " + e.getLocalizedMessage();
            final Result.Error<Boolean> result = new Result.Error<>(errorMessage);
            callback.onComplete(result);
            return;
        }

        // no duplicate reservations, finalize the reservation
        this.queueService.getUuid(userFullName, shopId, date, time, result -> {
            Result<Boolean> bookResult;

            if (result instanceof Result.Success) {
                final String uuid = ((Result.Success<String>) result).data;

                final Reservation reservation = new Reservation(shopId, shopName, date, uuid, coords);
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
