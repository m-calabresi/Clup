package com.android.clup.model;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

public class ModelTest {
    private Model model;

    @Before
    public void setUp() {
        this.model = Model.getInstance();
    }

    @Test
    public void getShops() {
        assertNull(this.model.getShops());

        // dummy list
        final LatLng coords1 = new LatLng(45.4659, 9.1914);
        final LatLng coords2 = new LatLng(45.698342, 9.204998);

        final Date date1 = Date.fromString("28-02-2021");
        final Date date2 = Date.fromString("02-07-2021");
        final Date date3 = Date.fromString("03-07-2021");

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

        final List<AvailableDay> availableDays = Arrays.asList(availableDay1, availableDay2, availableDay3);

        final Shop shop1 = new Shop("Local shop", coords1, availableDays);
        final Shop shop2 = new Shop("Supermarket", coords2, availableDays);

        final List<Shop> shops = Arrays.asList(shop1, shop2, shop1);

        this.model.setShops(shops);
        assertEquals(shops, this.model.getShops());
    }

    @Test
    public void getSelectedShop() {
        assertThrows(NullPointerException.class, () -> this.model.getSelectedShop());

        // dummy list
        final LatLng coords1 = new LatLng(45.4659, 9.1914);

        final Date date1 = Date.fromString("28-02-2021");
        final Date date2 = Date.fromString("02-07-2021");
        final Date date3 = Date.fromString("03-07-2021");

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

        final List<AvailableDay> availableDays = Arrays.asList(availableDay1, availableDay2, availableDay3);

        final Shop shop1 = new Shop("Local shop", coords1, availableDays);

        this.model.setSelectedShop(shop1);

        assertNotNull(this.model.getSelectedShop());
        assertEquals(shop1, this.model.getSelectedShop());
    }

    @Test
    public void resetSelectedShop() {
        this.model.resetSelectedShop();

        assertThrows(NullPointerException.class, () -> this.model.getSelectedShop());
    }

    @Test
    public void getSelectedDay() {
        assertThrows(NullPointerException.class, () -> this.model.getSelectedDay());

        final Date date1 = Date.fromString("11-02-2021");

        final AvailableSlot availableSlot1 = new AvailableSlot("12:30", Arrays.asList("Marco", "Giacomo"));
        final AvailableSlot availableSlot2 = new AvailableSlot("13:30", Arrays.asList("Giovanni", "Aldo"));
        final AvailableSlot availableSlot3 = new AvailableSlot("14:30", Arrays.asList("Simone", "Matteo"));

        final List<AvailableSlot> availableSlots1 = Arrays.asList(availableSlot1, availableSlot2, availableSlot3);

        final AvailableDay availableDay1 = new AvailableDay(date1, availableSlots1);

        this.model.setSelectedDay(availableDay1);

        assertNotNull(this.model.getSelectedDay());
        assertEquals(availableDay1, this.model.getSelectedDay());
    }

    @Test(expected = NullPointerException.class)
    public void resetSelectedDay() {
        this.model.resetSelectedDay();
        this.model.getSelectedDay();
    }

    @Test
    public void getSelectedTime() {
        final String time = "12:00";

        assertThrows(NullPointerException.class, () -> this.model.getSelectedTime());

        this.model.setSelectedTime(time);
        assertEquals(time, this.model.getSelectedTime());
    }

    @Test
    public void resetSelectedTime() {
        this.model.resetSelectedTime();
        assertThrows(NullPointerException.class, () -> this.model.getSelectedTime());
    }

    @Test
    public void getSelectedReservation() {
        assertThrows(NullPointerException.class, () -> this.model.getSelectedReservation());

        final String shopName = "Shop Name";
        final Date date = Date.fromString("14-02-2021");
        final String time = "12:00";
        final LatLng coords = new LatLng(12.34567, 12.87654);

        final Reservation reservation = new Reservation(shopName, date, time, coords);

        this.model.setSelectedReservation(reservation);

        assertEquals(reservation, this.model.getSelectedReservation());
    }

    @Test
    public void resetSelectedReservation() {
        this.model.resetSelectedReservation();
        assertThrows(NullPointerException.class, () -> this.model.getSelectedReservation());
    }
}