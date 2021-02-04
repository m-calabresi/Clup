package com.android.clup.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.clup.json.JsonParser;

import java.util.List;
import java.util.Objects;

public final class Model {
    private static class ModelHelper {
        @NonNull
        private static final Model INSTANCE = new Model();
    }

    public static final int INVALID_INDEX = -1;

    //--- USER DATA ---
    private String friendlyName;
    private String fullname;

    //--- SHOPS DATA ---
    private List<Shop> shops;
    private int selectedShopIndex;
    private int selectedDayIndex;
    private int selectedHourIndex;

    //--- RESERVATIONS DATA ---
    private List<Reservation> reservations;
    private int selectedReservationIndex;

    private Model() {
        this.fullname = null;
        this.friendlyName = null;

        this.shops = null;
        this.selectedShopIndex = INVALID_INDEX;
        this.selectedDayIndex = INVALID_INDEX;
        this.selectedHourIndex = INVALID_INDEX;

        this.reservations = null;
        this.selectedReservationIndex = INVALID_INDEX;
    }

    @NonNull
    public static Model getInstance() {
        return ModelHelper.INSTANCE;
    }

    public void setShops(@NonNull final List<Shop> shops) {
        this.shops = shops;
    }

    @Nullable
    public List<Shop> getShops() {
        return this.shops;
    }

    public void setSelectedShopIndex(final int selectedShopIndex) {
        this.selectedShopIndex = selectedShopIndex;
    }

    @NonNull
    public Shop getSelectedShop() {
        if (this.selectedShopIndex != INVALID_INDEX)
            return Objects.requireNonNull(this.shops).get(this.selectedShopIndex);
        throw new NullPointerException("No index was set before calling this method, did you call 'setSelectedShopIndex'?");
    }

    public void resetSelectedShopIndex() {
        this.selectedShopIndex = INVALID_INDEX;
    }

    public void setSelectedDayIndex(final int selectedDayIndex) {
        this.selectedDayIndex = selectedDayIndex;
    }

    @NonNull
    public AvailableDay getSelectedDay() {
        if (this.selectedDayIndex != INVALID_INDEX)
            return getSelectedShop().getAvailableDays().get(this.selectedDayIndex);
        throw new NullPointerException("No index was set before calling this method, did you call 'setSelectedDayIndex'?");
    }

    public void resetSelectedDayIndex() {
        this.selectedDayIndex = INVALID_INDEX;
    }

    public void setSelectedHourIndex(final int selectedHourIndex) {
        this.selectedHourIndex = selectedHourIndex;
    }

    @NonNull
    public String getSelectedHour() {
        if (this.selectedHourIndex != INVALID_INDEX)
            return getSelectedDay().getHours().get(this.selectedHourIndex);
        throw new NullPointerException("No index was set before calling this method, did you call 'setSelectedHourIndex'?");
    }

    public void resetSelectedHourIndex() {
        this.selectedHourIndex = INVALID_INDEX;
    }

    @NonNull
    public List<Reservation> getReservations() {
        if (this.reservations == null)
            this.reservations = JsonParser.loadReservations();
        return this.reservations;
    }

    public void addReservation(@NonNull final Reservation reservation) {
        getReservations().add(reservation);
        JsonParser.saveReservations(this.reservations);
    }

    public void setFriendlyName(@NonNull final String friendlyName) {
        this.friendlyName = friendlyName;
        Preferences.setFriendlyName(this.friendlyName);
    }

    @NonNull
    public String getFriendlyName() {
        if (this.friendlyName == null)
            this.friendlyName = Preferences.getFriendlyName();
        return this.friendlyName;
    }

    public void setFullname(@NonNull final String fullname) {
        this.fullname = fullname;
        Preferences.setFullname(fullname);
    }

    @NonNull
    public String getFullname() {
        if (this.fullname == null)
            this.fullname = Preferences.getFullname();
        return this.fullname;
    }

    public void setSelectedReservationIndex(final int selectedReservationIndex) {
        this.selectedReservationIndex = selectedReservationIndex;
    }

    public int getSelectedReservationIndex() {
        if (this.selectedReservationIndex != INVALID_INDEX)
            return this.selectedReservationIndex;
        throw new NullPointerException("No index was set before calling this method, did you call 'setSelectedReservationIndex'?");
    }

    public Reservation getSelectedReservation() {
        return getReservations().get(getSelectedReservationIndex());
    }

    public void setSelectedReservationNotificationInfo(final int notificationStatus, final int timeNotice) {
        getSelectedReservation().setNotificationStatus(notificationStatus);
        getSelectedReservation().setTimeNotice(timeNotice);
        JsonParser.saveReservations(this.reservations);
    }
}
