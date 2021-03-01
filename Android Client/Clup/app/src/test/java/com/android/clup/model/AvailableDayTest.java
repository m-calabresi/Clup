package com.android.clup.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AvailableDayTest {
    private static final String DATE_STR = "12-02-2021";

    private static final String TIME_1 = "12:00";
    private static final String TIME_2 = "18:30";

    private static final String NAME_1 = "Marco";
    private static final String NAME_2 = "Giovanni";


    private AvailableDay day;

    @Before
    public void setUp() {
        final Date date = Date.fromString(DATE_STR);
        final List<String> customers = Arrays.asList(NAME_1, NAME_2);

        final AvailableSlot availableSlot1 = new AvailableSlot(TIME_1, customers);
        final AvailableSlot availableSlot2 = new AvailableSlot(TIME_2, customers);
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
        assertEquals(NAME_1, this.day.getAvailableSlots().get(1).getEnqueuedCustomersNames().get(0));
        assertEquals(NAME_2, this.day.getAvailableSlots().get(1).getEnqueuedCustomersNames().get(1));
    }
}