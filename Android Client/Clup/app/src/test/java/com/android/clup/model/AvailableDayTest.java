package com.android.clup.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AvailableDayTest {
    private static final String DATE_STR = "12-02-2021";
    private static final String TIME_1 = "12:00";
    private static final String TIME_2 = "18:30";


    private AvailableDay day;

    @BeforeEach
    void setUp() {
        final Date date = Date.fromString(DATE_STR);
        final List<String> times = Arrays.asList(TIME_1, TIME_2);

        this.day = new AvailableDay(date, times);
    }

    @Test
    void getDate() {
        assertEquals(DATE_STR, this.day.getDate().plain());
    }

    @Test
    void getTimes() {
        assertEquals(TIME_1, this.day.getTimes().get(0));
        assertEquals(TIME_2, this.day.getTimes().get(1));
    }
}