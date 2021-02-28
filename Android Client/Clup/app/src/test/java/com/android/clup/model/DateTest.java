package com.android.clup.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class DateTest {
    // WARNING: FORMATTED_DATE may depend on the testing device locale, adjust this string accordingly
    private static final String FORMATTED_DATE = "Sunday, 14 February";
    private static final String PLAIN_DATE = "14-02-2021";
    private static final int YEAR = 2021;
    private static final int MONTH = 2;
    private static final int DAY = 14;

    private static final String TIME = "12:00";
    private static final int HOURS = 12;
    private static final int MINUTES = 0;

    private Date date;

    @Before
    public void setUp() {
        this.date = Date.fromString(PLAIN_DATE);
    }

    @Test
    public void formatted() {
        assertEquals(FORMATTED_DATE, this.date.formatted());
    }

    @Test
    public void plain() {
        assertEquals(PLAIN_DATE, this.date.plain());
    }

    @Test
    public void getTime() {
        this.date.setTime(TIME);
        assertEquals(TIME, this.date.getTime());
    }

    @Test
    public void toMillis() {
        final int SECONDS = 0;
        final int MILLISECONDS = 0;

        final Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(Calendar.YEAR, DateTest.YEAR);
        calendar.set(Calendar.MONTH, DateTest.MONTH - 1);
        calendar.set(Calendar.DAY_OF_MONTH, DateTest.DAY);
        calendar.set(Calendar.HOUR_OF_DAY, DateTest.HOURS);
        calendar.set(Calendar.MINUTE, DateTest.MINUTES);
        calendar.set(Calendar.SECOND, SECONDS);
        calendar.set(Calendar.MILLISECOND, MILLISECONDS);

        this.date.setTime(TIME);

        assertEquals(calendar.getTimeInMillis(), this.date.toMillis());
    }

    @Test
    public void minutesToMillis() {
        final int minutes = 35;
        final int millis = minutes * 60 * 1000;

        assertEquals(millis, Date.minutesToMillis(minutes));
    }

    @Test
    public void now() {
        assertEquals(Calendar.getInstance().getTimeInMillis(), Date.now(), 10);
    }

    @Test
    public void describeContents() {
        assertEquals(0, this.date.describeContents());
    }
}