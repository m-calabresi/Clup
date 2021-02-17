package com.android.clup.model;

import com.google.android.gms.maps.model.LatLng;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShopTest {
    private Shop shop;
    private String shopName;
    private LatLng coords;
    private List<AvailableDay> availableDays;

    @BeforeEach
    void setup() {
        this.shopName = "Shop Name";
        this.coords = new LatLng(12.7654, 5.76543);

        final Date date1 = Date.fromString("11-02-2021");
        final Date date2 = Date.fromString("12-02-2021");
        final Date date3 = Date.fromString("13-02-2021");

        final AvailableDay availableDay1 = new AvailableDay(date1, Arrays.asList("12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"));
        final AvailableDay availableDay2 = new AvailableDay(date2, Arrays.asList("16:00", "17:00", "18:00", "19:00"));
        final AvailableDay availableDay3 = new AvailableDay(date3, Arrays.asList("15:00", "16:00", "17:00", "20:00"));

        this.availableDays = Arrays.asList(availableDay1, availableDay2, availableDay3);

        this.shop = new Shop(this.shopName, this.coords, this.availableDays);
    }

    @Test
    void getName() {
        assertEquals(this.shopName, this.shop.getName());
    }

    @Test
    void getCoordinates() {
        assertEquals(this.coords, this.shop.getCoordinates());
    }

    @Test
    void getAvailableDays() {
        assertEquals(this.availableDays, this.shop.getAvailableDays());
    }
}