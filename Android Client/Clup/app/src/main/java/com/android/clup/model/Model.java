package com.android.clup.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.clup.json.JsonParser;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public final class Model {
    private static class ModelHelper {
        @NonNull
        private static final Model INSTANCE = new Model();
    }

    //--- USER DATA ---
    /**
     * A friendly name to be displayed to the user.
     */
    @Nullable
    private String friendlyName;

    /**
     * The complete name of the user.
     */
    @Nullable
    private String fullname;

    //--- SHOPS DATA ---
    /**
     * The list of available shops that are available for booking.
     */
    @Nullable
    private List<Shop> shops;
    /**
     * The selected shop.
     */
    @Nullable
    private Shop selectedShop;
    /**
     * The selected day.
     */
    @Nullable
    private AvailableDay selectedDay;
    /**
     * The selected time.
     */
    @Nullable
    private String selectedTime;

    //--- RESERVATIONS DATA ---
    /**
     * The list of reservations made by the user.
     */
    @Nullable
    private List<Reservation> reservations;
    /**
     * The reservation the user is currently interacting with.
     */
    @Nullable
    private Reservation selectedReservation;

    private Model() {
        this.fullname = null;
        this.friendlyName = null;

        this.shops = null;
        this.selectedShop = null;
        this.selectedDay = null;
        this.selectedTime = null;

        this.reservations = null;
        this.selectedReservation = null;
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

    public void setSelectedShop(@NonNull final Shop shop) {
        this.selectedShop = shop;
    }

    @NonNull
    public Shop getSelectedShop() {
        if (this.selectedShop != null)
            return this.selectedShop;
        throw new NullPointerException("No item was set before calling this method, did you call 'setSelectedShop'?");
    }

    public void resetSelectedShop() {
        this.selectedShop = null;
    }

    public void setSelectedDay(@NonNull final AvailableDay day) {
        this.selectedDay = day;
    }

    @NonNull
    public AvailableDay getSelectedDay() {
        if (this.selectedDay != null)
            return this.selectedDay;
        throw new NullPointerException("No item was set before calling this method, did you call 'setSelectedDay'?");
    }

    public void resetSelectedDay() {
        this.selectedDay = null;
    }

    public void setSelectedTime(@NonNull final String time) {
        this.selectedTime = time;
    }

    @NonNull
    public String getSelectedTime() {
        if (this.selectedTime != null)
            return this.selectedTime;
        throw new NullPointerException("No item was set before calling this method, did you call 'setSelectedTime'?");
    }

    public void resetSelectedTime() {
        this.selectedTime = null;
    }

    @NonNull
    public List<Reservation> getReservations() {
        if (this.reservations == null) {
            this.reservations = JsonParser.loadReservations();
            removeExpiredReservations();
        }
        return this.reservations;
    }

    public void addReservation(@NonNull final Reservation reservation) {
        getReservations().add(reservation);
        finalizeReservations();
    }

    public void removeReservation(@NonNull final Reservation reservation) {
        getReservations().remove(reservation);
        finalizeReservations();
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

    public void setSelectedReservation(@NonNull final Reservation reservation) {
        this.selectedReservation = reservation;
    }

    @NonNull
    public Reservation getSelectedReservation() {
        if (this.selectedReservation != null)
            return this.selectedReservation;
        throw new NullPointerException("No value was set before calling this method, did you call 'setSelectedReservation'?");
    }

    public void resetSelectedReservation() {
        this.selectedReservation = null;
    }

    public void setSelectedReservationTimeNotice(final int timeNotice) {
        getSelectedReservation().setTimeNotice(timeNotice);
        finalizeReservations();
    }

    /**
     * Finalize reservations by sorting them (upcoming reservations first), then stores the sorted
     * reservations to local storage.
     */
    private void finalizeReservations() {
        Collections.sort(getReservations()); // sort reservations by date and time (upcoming reservations first)
        removeExpiredReservations();
        JsonParser.saveReservations(getReservations());
    }

    /**
     * Check for reservations that has expired and set the corresponding flag accordingly.
     * Check for expired reservations that have passed the {@code EXPIRE_TIME} and remove them from
     * the list.
     */
    private void removeExpiredReservations() {
        final long currentTime = Date.now();
        final ListIterator<Reservation> iterator = getReservations().listIterator();

        while (iterator.hasNext()) {
            final Reservation reservation = iterator.next();

            final long reservationDate = reservation.getDate().toMillis();
            final long expireDate = reservationDate + Reservation.EXPIRE_TIME;

            // reservation has expired but is still inside the EXPIRE_TIME
            if (currentTime > reservationDate && currentTime < expireDate)
                reservation.setExpired(true);
                // reservation has expired and has passed the EXPIRE_TIME
            else if (currentTime > expireDate)
                iterator.remove();
        }
    }
}
