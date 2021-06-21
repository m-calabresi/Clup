package com.android.clup.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class AvailableDayTest {
    private static final String DATE_STR = "12-02-2021";

    private static final String TIME_1 = "12:00";
    private static final String TIME_2 = "18:30";
    private static final String TIME_ERROR = "17:00";

    private static final String NAME_1 = "Marco";
    private static final String NAME_2 = "Giovanni";
    private static final String NAME_3 = "Martina";
    private static final String NAME_4 = "Anna";


    private AvailableDay day;

    @Before
    public void setUp() {
        final Date date = Date.fromString(DATE_STR);
        final List<String> customers1 = Arrays.asList(NAME_1, NAME_2);
        final List<String> customers2 = Arrays.asList(NAME_3, NAME_4);

        final AvailableSlot availableSlot1 = new AvailableSlot(TIME_1, customers1);
        final AvailableSlot availableSlot2 = new AvailableSlot(TIME_2, customers2);
        final List<AvailableSlot> availableSlots = Arrays.asList(availableSlot1, availableSlot2);

        this.day = new AvailableDay(date, availableSlots);
    }

    @Test
    public void getDate() {
        assertEquals(DATE_STR, this.day.getDate().plain());
    }

    @Test
    public void getAvailableSlots() {
        assertEquals(TIME_1, this.day.getAvailableSlots().get(0).getTime());
        assertEquals(NAME_1, this.day.getAvailableSlots().get(0).getEnqueuedCustomersNames().get(0));
        assertEquals(NAME_2, this.day.getAvailableSlots().get(0).getEnqueuedCustomersNames().get(1));

        assertEquals(TIME_2, this.day.getAvailableSlots().get(1).getTime());
        assertEquals(NAME_3, this.day.getAvailableSlots().get(1).getEnqueuedCustomersNames().get(0));
        assertEquals(NAME_4, this.day.getAvailableSlots().get(1).getEnqueuedCustomersNames().get(1));
    }

    @Test
    public void getAvailableSlot() {
        final AvailableSlot availableSlot = this.day.getAvailableSlotByTime(TIME_1);

        assertEquals(availableSlot.getTime(), TIME_1);
        assertEquals(availableSlot.getEnqueuedCustomersNames().get(0), NAME_1);
        assertEquals(availableSlot.getEnqueuedCustomersNames().get(1), NAME_2);

        assertThrows(NoSuchElementException.class, () -> this.day.getAvailableSlotByTime(TIME_ERROR));
    }

    @Test
    public void getAvailableSlotByHour() {
        final String hour = TIME_1.split(":")[0]; // 12

        final AvailableSlot expectedAvailableSlot = this.day.getAvailableSlots().get(0);
        final AvailableSlot retrievedAvailableSlot = this.day.getAvailableSlotByHour(hour);

        assertEquals(expectedAvailableSlot.getHour(), retrievedAvailableSlot.getHour());
        assertEquals(expectedAvailableSlot.getTime(), retrievedAvailableSlot.getTime());

        final List<String> expectedEnqueuedCustomersNames = expectedAvailableSlot.getEnqueuedCustomersNames();
        final List<String> retrievedEnqueuedCustomersNames = retrievedAvailableSlot.getEnqueuedCustomersNames();

        for(int i = 0; i < expectedEnqueuedCustomersNames.size(); i++)
            assertEquals(expectedEnqueuedCustomersNames.get(i), retrievedEnqueuedCustomersNames.get(i));
    }
}