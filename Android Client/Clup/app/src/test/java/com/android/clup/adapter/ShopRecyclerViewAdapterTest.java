package com.android.clup.adapter;

import com.android.clup.model.AvailableDay;
import com.android.clup.model.Date;
import com.android.clup.model.Shop;
import com.google.android.gms.maps.model.LatLng;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShopRecyclerViewAdapterTest {
    private ShopRecyclerViewAdapter adapter;
    private int size;

    @BeforeEach
    void setUp() {
        final OnRecyclerViewItemClickedCallback callback = position -> {
        };

        final LatLng coords = new LatLng(45.4659, 9.1914);
        final Date date = Date.fromString("11-02-2021");
        final AvailableDay availableDay = new AvailableDay(date, Arrays.asList("12:00", "13:00"));
        final List<AvailableDay> availableDays = Arrays.asList(availableDay, availableDay, availableDay);
        final Shop shop = new Shop("Local shop", coords, availableDays);
        final List<Shop> shops = Arrays.asList(shop, shop, shop);

        this.size = shops.size();
        this.adapter = new ShopRecyclerViewAdapter(callback, shops);
    }

    @Test
    void getItemCount() {
        assertEquals(this.size, this.adapter.getItemCount());
    }
}