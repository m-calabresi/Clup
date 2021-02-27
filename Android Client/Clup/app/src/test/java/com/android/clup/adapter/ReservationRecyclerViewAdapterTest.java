package com.android.clup.adapter;

import com.android.clup.model.Date;
import com.android.clup.model.Reservation;
import com.google.android.gms.maps.model.LatLng;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReservationRecyclerViewAdapterTest {
    private ReservationRecyclerViewAdapter adapter;
    private int size;

    @BeforeEach
    void setup() {
        final OnListItemClickedCallback callback = position -> {
        };
        final String shopName = "Shop Name";
        final Date date = Date.fromString("12-02-2021");
        final String uuid = "345678uhgfe456yg";
        final LatLng coords = new LatLng(12.76543, 3.76543);

        final Reservation reservation = new Reservation(shopName, date, uuid, coords);

        final List<Reservation> reservations = Arrays.asList(reservation, reservation, reservation);

        this.size = reservations.size();
        this.adapter = new ReservationRecyclerViewAdapter(callback, reservations);
    }

    @Test
    void getItemViewType() {
        for (int i = 0; i < this.size; i++)
            assertEquals(1, this.adapter.getItemViewType(i));

        assertEquals(0, this.adapter.getItemViewType(this.size));
    }


    @Test
    void getItemCount() {
        assertEquals(this.size + 1, this.adapter.getItemCount());
    }
}