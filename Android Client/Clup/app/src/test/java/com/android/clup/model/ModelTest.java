package com.android.clup.model;

import com.google.android.gms.maps.model.LatLng;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModelTest {
    private Model model;

    @BeforeEach
    void setUp() {
        this.model = Model.getInstance();
    }

    @Test
    void getShops() {
        assertNull(this.model.getShops());

        // dummy list
        final LatLng coords1 = new LatLng(45.4659, 9.1914);
        final LatLng coords2 = new LatLng(45.698342, 9.204998);

        final Date date1 = Date.fromString("11-02-2021");
        final Date date2 = Date.fromString("12-02-2021");
        final Date date3 = Date.fromString("13-02-2021");

        final AvailableDay availableDay1 = new AvailableDay(date1, Arrays.asList("12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"));
        final AvailableDay availableDay2 = new AvailableDay(date2, Arrays.asList("16:00", "17:00", "18:00", "19:00"));
        final AvailableDay availableDay3 = new AvailableDay(date3, Arrays.asList("15:00", "16:00", "17:00", "20:00"));

        final List<AvailableDay> availableDays = Arrays.asList(availableDay1, availableDay2, availableDay3);

        final Shop shop1 = new Shop("Local shop", coords1, availableDays);
        final Shop shop2 = new Shop("Supermarket", coords2, availableDays);

        final List<Shop> shops = Arrays.asList(shop1, shop2, shop1);

        this.model.setShops(shops);
        assertEquals(shops, this.model.getShops());
    }

    @Test
    void getSelectedShop() {
        assertThrows(NullPointerException.class, () -> this.model.getSelectedShop());

        // dummy shop
        final LatLng coords1 = new LatLng(45.4659, 9.1914);

        final Date date1 = Date.fromString("11-02-2021");
        final Date date2 = Date.fromString("12-02-2021");
        final Date date3 = Date.fromString("13-02-2021");

        final AvailableDay availableDay1 = new AvailableDay(date1, Arrays.asList("12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"));
        final AvailableDay availableDay2 = new AvailableDay(date2, Arrays.asList("16:00", "17:00", "18:00", "19:00"));
        final AvailableDay availableDay3 = new AvailableDay(date3, Arrays.asList("15:00", "16:00", "17:00", "20:00"));

        final List<AvailableDay> availableDays = Arrays.asList(availableDay1, availableDay2, availableDay3);

        final Shop shop1 = new Shop("Local shop", coords1, availableDays);

        this.model.setSelectedShop(shop1);

        assertNotNull(this.model.getSelectedShop());
        assertEquals(shop1, this.model.getSelectedShop());
    }

    @Test
    void resetSelectedShop() {
        this.model.resetSelectedShop();

        assertThrows(NullPointerException.class, () -> this.model.getSelectedShop());
    }

    @Test
    void getSelectedDay() {
        assertThrows(NullPointerException.class, () -> this.model.getSelectedDay());

        final Date date1 = Date.fromString("11-02-2021");
        final AvailableDay availableDay1 = new AvailableDay(date1, Arrays.asList("12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"));

        this.model.setSelectedDay(availableDay1);

        assertNotNull(this.model.getSelectedDay());
        assertEquals(availableDay1, this.model.getSelectedDay());
    }

    @Test
    void resetSelectedDay() {
        this.model.resetSelectedDay();
        assertThrows(NullPointerException.class, () -> this.model.getSelectedDay());
    }

    @Test
    void getSelectedTime() {
        final String time = "12:00";

        assertThrows(NullPointerException.class, () -> this.model.getSelectedTime());

        this.model.setSelectedTime(time);
        assertEquals(time, this.model.getSelectedTime());
    }

    @Test
    void resetSelectedTime() {
        this.model.resetSelectedTime();
        assertThrows(NullPointerException.class, () -> this.model.getSelectedTime());
    }

    @Test
    void getSelectedReservation() {
        assertThrows(NullPointerException.class, () -> this.model.getSelectedReservation());

        final String shopName = "Shop Name";
        final Date date = Date.fromString("14-02-2021");
        final String time = "12:00";
        final LatLng coords = new LatLng(12.34567, 12.87654);

        final Reservation reservation = new Reservation(shopName, date, time, coords);

        this.model.setSelectedReservation(reservation);

        assertEquals(reservation, this.model.getSelectedReservation());
    }
}