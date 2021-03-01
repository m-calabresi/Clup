package com.android.clup.model;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ShopTest {
    private Shop shop;
    private String shopName;
    private LatLng coords;
    private List<AvailableDay> availableDays;

    @Before
    public void setup() {
        this.shopName = "Shop Name";
        this.coords = new LatLng(12.7654, 5.76543);

        final Date date1 = Date.fromString("11-02-2021");
        final Date date2 = Date.fromString("12-02-2021");
        final Date date3 = Date.fromString("13-02-2021");

        final AvailableSlot availableSlot1 = new AvailableSlot("12:30", Arrays.asList("Marco", "Giacomo"));
        final AvailableSlot availableSlot2 = new AvailableSlot("13:30", Arrays.asList("Giovanni", "Aldo"));
        final AvailableSlot availableSlot3 = new AvailableSlot("14:30", Arrays.asList("Simone", "Aldo"));
        final AvailableSlot availableSlot4 = new AvailableSlot("15:30", Arrays.asList("Giovanni", "Aldo"));
        final AvailableSlot availableSlot5 = new AvailableSlot("16:30", Arrays.asList("Dino", "Alberto"));
        final AvailableSlot availableSlot6 = new AvailableSlot("17:30", Arrays.asList("Guido", "Aldo"));
        final AvailableSlot availableSlot7 = new AvailableSlot("18:30", Arrays.asList("Piero", "Luca"));

        final List<AvailableSlot> availableSlots1 = Arrays.asList(availableSlot1, availableSlot2, availableSlot3, availableSlot4, availableSlot5, availableSlot6, availableSlot7);
        final List<AvailableSlot> availableSlots2 = Arrays.asList(availableSlot1, availableSlot2, availableSlot3, availableSlot4);
        final List<AvailableSlot> availableSlots3 = Arrays.asList(availableSlot4, availableSlot5, availableSlot6, availableSlot7);

        final AvailableDay availableDay1 = new AvailableDay(date1, availableSlots1);
        final AvailableDay availableDay2 = new AvailableDay(date2, availableSlots2);
        final AvailableDay availableDay3 = new AvailableDay(date3, availableSlots3);

        this.availableDays = Arrays.asList(availableDay1, availableDay2, availableDay3);

        this.shop = new Shop(this.shopName, this.coords, this.availableDays);
    }

    @Test
    public void getName() {
        assertEquals(this.shopName, this.shop.getName());
    }

    @Test
    public void getCoordinates() {
        assertEquals(this.coords, this.shop.getCoordinates());
    }

    @Test
    public void getAvailableDays() {
        assertEquals(this.availableDays, this.shop.getAvailableDays());
    }
}