package com.android.clup.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AvailableSlotTest {
    private static final String TIME = "12:00";

    private static final String NAME_1 = "Marco";
    private static final String NAME_2 = "Giovanni";

    private AvailableSlot availableSlot;

    @Before
    public void setUp() {
        final List<String> customers = Arrays.asList(NAME_1, NAME_2);
        this.availableSlot = new AvailableSlot(TIME, customers);
    }

    @Test
    public void getTime() {
        assertEquals(TIME, this.availableSlot.getTime());
    }

    @Test
    public void getEnqueuedCustomersNames() {
        assertEquals(NAME_1, this.availableSlot.getEnqueuedCustomersNames().get(0));
        assertEquals(NAME_2, this.availableSlot.getEnqueuedCustomersNames().get(1));
    }
}