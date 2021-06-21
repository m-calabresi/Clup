package com.android.clup.model;

import androidx.annotation.NonNull;

import com.android.clup.exception.NoAvailableDayException;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

public class ShopTest {
    private Shop shop;
    private String shopId;
    private String shopName;
    private LatLng coords;
    private List<AvailableDay> availableDays;

    final Date RETRIEVAL_DATE = Date.fromString("12-02-2021");

    @Before
    public void setup() {
        this.shopId = "1234567";
        this.shopName = "Shop Name";
        this.coords = new LatLng(12.7654, 5.76543);

        final Date date1 = Date.fromString("11-02-2021");
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
        final AvailableDay availableDay2 = new AvailableDay(RETRIEVAL_DATE, availableSlots2);
        final AvailableDay availableDay3 = new AvailableDay(date3, availableSlots3);

        this.availableDays = Arrays.asList(availableDay1, availableDay2, availableDay3);

        this.shop = new Shop(this.shopId, this.shopName, this.coords, this.availableDays);
    }

    @Test
    public void getId() {
        assertEquals(this.shopId, this.shop.getId());
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

    @Test
    public void getAvailableDayByDate() {
        try {
            final AvailableDay retrievedAvailableDay = this.shop.getAvailableDayByDate(RETRIEVAL_DATE);
            final AvailableDay expectedAvailableDay = this.shop.getAvailableDays().get(1);

            assertEquals(expectedAvailableDay, retrievedAvailableDay);
        } catch (@NonNull final NoAvailableDayException e) {
            fail("Exception shouldn't be thrown for this section of the test.");
        }

        final Date exceptionDate = Date.fromString("12-12-2012");
        assertThrows(NoAvailableDayException.class, () -> this.shop.getAvailableDayByDate(exceptionDate));
    }

    @Test
    public void getById() {
        final String retrievalId = "0987654";

        final Shop shop1 = new Shop("1234567", "shop1", new LatLng(1, 1), new ArrayList<>());
        final Shop shop2 = new Shop(retrievalId, "shop2", new LatLng(1, 1), new ArrayList<>());
        final Shop shop3 = new Shop("3456543", "shop3", new LatLng(1, 1), new ArrayList<>());

        final List<Shop> shops = new ArrayList<>(3);
        shops.add(shop1);
        shops.add(shop2);
        shops.add(shop3);

        final Shop retrievedShop = Shop.getById(shops, retrievalId);

        assertEquals(shop2, retrievedShop);
    }
}