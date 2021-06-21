package com.android.clup.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
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
    public void getHour() {
        final String hour = TIME.split(":")[0];
        assertEquals(hour, this.availableSlot.getHour());
    }

    @Test
    public void getEnqueuedCustomersNames() {
        assertEquals(NAME_1, this.availableSlot.getEnqueuedCustomersNames().get(0));
        assertEquals(NAME_2, this.availableSlot.getEnqueuedCustomersNames().get(1));
    }

    @Test
    public void sortAvailableSlotsBy() {
        final String time1 = "12:00";
        final String time2 = "14:00";
        final String time3 = "16:00";
        final String time4 = "18:00";

        final String customer1 = "customer1";
        final String customer2 = "customer2";
        final String customer3 = "customer3";
        final String customer4 = "customer4";
        final String customer5 = "customer5";
        final String customer6 = "customer6";
        final String customer7 = "customer7";
        final String customer8 = "customer8";

        final List<String> enqueuedCustomersNames1 = new ArrayList<>(2);
        enqueuedCustomersNames1.add(customer1);
        enqueuedCustomersNames1.add(customer2);

        final List<String> enqueuedCustomersNames2 = new ArrayList<>(2);
        enqueuedCustomersNames1.add(customer3);
        enqueuedCustomersNames1.add(customer4);

        final List<String> enqueuedCustomersNames3 = new ArrayList<>(2);
        enqueuedCustomersNames1.add(customer5);
        enqueuedCustomersNames1.add(customer6);

        final List<String> enqueuedCustomersNames4 = new ArrayList<>(2);
        enqueuedCustomersNames1.add(customer7);
        enqueuedCustomersNames1.add(customer8);

        final AvailableSlot availableSlot1 = new AvailableSlot(time1, enqueuedCustomersNames1);
        final AvailableSlot availableSlot2 = new AvailableSlot(time2, enqueuedCustomersNames2);
        final AvailableSlot availableSlot3 = new AvailableSlot(time3, enqueuedCustomersNames3);
        final AvailableSlot availableSlot4 = new AvailableSlot(time4, enqueuedCustomersNames4);

        final List<AvailableSlot> mondayAvailableSlots = new ArrayList<>(2);
        mondayAvailableSlots.add(availableSlot1);
        mondayAvailableSlots.add(availableSlot2);

        final List<AvailableSlot> tuesdayAvailableSlots = new ArrayList<>(2);
        tuesdayAvailableSlots.add(availableSlot3);
        tuesdayAvailableSlots.add(availableSlot4);

        // weekSlots from monday to tuesday
        final List<List<AvailableSlot>> weekSlots = new ArrayList<>(2);
        weekSlots.add(mondayAvailableSlots);
        weekSlots.add(tuesdayAvailableSlots);

        final List<Date> dates = new ArrayList<>(1);
        dates.add(Date.fromString("22-06-2021")); //must be a tuesday

        // order weekSlots from tuesday to monday
        final List<List<AvailableSlot>> sortedWeekSlots = AvailableSlot.sortAvailableSlotsBy(weekSlots, dates);

        // expected order: from tuesday to monday
        final List<List<AvailableSlot>> expectedSortedWeekSlots = new ArrayList<>(2);
        expectedSortedWeekSlots.add(tuesdayAvailableSlots);
        expectedSortedWeekSlots.add(mondayAvailableSlots);

        for(int i = 0; i < expectedSortedWeekSlots.size(); i++) {
            final List<AvailableSlot> expectedSortedDaySlots = expectedSortedWeekSlots.get(i);
            final List<AvailableSlot> sortedDaySlots = sortedWeekSlots.get(i);

            for(int j = 0; j < expectedSortedDaySlots.size(); j++) {
                final AvailableSlot expectedAvailableDay = expectedSortedDaySlots.get(j);
                final AvailableSlot availableDay = sortedDaySlots.get(j);

                assertEquals(expectedAvailableDay.getTime(), availableDay.getTime());
                assertEquals(expectedAvailableDay.getHour(), availableDay.getHour());

                final List<String> expectedEnqueuedCustomersNames = expectedAvailableDay.getEnqueuedCustomersNames();
                final List<String> enqueuedCustomersNames = availableDay.getEnqueuedCustomersNames();

                for(int k = 0; k < expectedEnqueuedCustomersNames.size(); k++) {
                    final String expectedCustomerName = expectedEnqueuedCustomersNames.get(k);
                    final String customerName = enqueuedCustomersNames.get(k);

                    assertEquals(expectedCustomerName, customerName);
                }
            }
        }
    }
}