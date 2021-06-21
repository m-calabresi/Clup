package com.android.clup.model;

import androidx.annotation.NonNull;

import com.android.clup.exception.NoAvailableDayException;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * A shop that is available to receive booking requests for reservations.
 */
public class Shop {
    /**
     * The id of the shop.
     */
    @NonNull
    private final String id;

    /**
     * The name of the shop.
     */
    @NonNull
    private final String name;
    /**
     * The coordinates of the shop.
     */
    @NonNull
    private final LatLng coordinates;
    /**
     * The list of available days associated to a shop.
     */
    @NonNull
    private final List<AvailableDay> availableDays;

    public Shop(@NonNull final String id, @NonNull final String name,
                @NonNull final LatLng coordinates, @NonNull final List<AvailableDay> availableDays) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.availableDays = availableDays;
    }

    @NonNull
    public String getId() {
        return this.id;
    }

    @NonNull
    public String getName() {
        return this.name;
    }

    @NonNull
    public LatLng getCoordinates() {
        return this.coordinates;
    }

    @NonNull
    public List<AvailableDay> getAvailableDays() {
        return this.availableDays;
    }

    /**
     * Return the {@link AvailableDay} corresponding to the given {@link Date}.
     */
    @NonNull
    public AvailableDay getAvailableDayByDate(@NonNull final Date date) throws NoAvailableDayException {
        for (final AvailableDay availableDay : this.availableDays) {
            if (availableDay.getDate().equals(date))
                return availableDay;
        }
        throw new NoAvailableDayException("No AvailableDay found that matches the given date: " + date.plain());
    }

    /**
     * Return the {@link Shop} corresponding to the given {@code id} from the given List of {@link Shop}s.
     */
    @NonNull
    public static Shop getById(@NonNull final List<Shop> shops, @NonNull final String id) {
        for (final Shop shop : shops) {
            if (shop.getId().equals(id))
                return shop;
        }
        throw new NoSuchElementException("No AvailableDay found that matches the given id: " + id);
    }
}
