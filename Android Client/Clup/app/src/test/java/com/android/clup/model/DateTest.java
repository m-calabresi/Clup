package com.android.clup.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DateTest {
    // WARNING: FORMATTED_DATE may depend on the testing device locale, adjust this string accordingly
    private static final String FORMATTED_DATE = "Sunday, 14 February";
    private static final String PLAIN_DATE = "14-02-2021";
    private static final String PLAIN_REVERSE_DATE = "2021-02-14";
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
    public void fromStringReverse() {
        final Date date = Date.fromStringReverse(PLAIN_REVERSE_DATE);
        assertEquals(this.date, date);
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
    public void plainReverse() {
        assertEquals(PLAIN_REVERSE_DATE, this.date.plainReversed());
    }

    @Test
    public void getTime() {
        this.date.setTime(TIME);
        assertEquals(TIME, this.date.getTime());
    }

    @Test
    public void getDayOfWeek() {
        final int monday = 0;
        final int tuesday = 1;
        final int wednesday = 2;
        final int thursday = 3;
        final int friday = 4;
        final int saturday = 5;
        final int sunday = 6;

        final Date mondayDate = Date.fromString("21-06-2021"); // monday
        final Date tuesdayDate = Date.fromString("22-06-2021"); // tuesday
        final Date wednesdayDate = Date.fromString("23-06-2021"); // wednesday
        final Date thursdayDate = Date.fromString("24-06-2021"); // thursday
        final Date fridayDate = Date.fromString("25-06-2021"); // friday
        final Date saturdayDate = Date.fromString("26-06-2021"); // saturday
        final Date sundayDate = Date.fromString("27-06-2021"); // sunday

        assertEquals(monday, mondayDate.getDayOfWeek());
        assertEquals(tuesday, tuesdayDate.getDayOfWeek());
        assertEquals(wednesday, wednesdayDate.getDayOfWeek());
        assertEquals(thursday, thursdayDate.getDayOfWeek());
        assertEquals(friday, fridayDate.getDayOfWeek());
        assertEquals(saturday, saturdayDate.getDayOfWeek());
        assertEquals(sunday, sundayDate.getDayOfWeek());
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

    @Test
    public void getBusinessWeekFrom() {
        // #1: monday to friday
        final List<Date> businessWeek1 = Date.getBusinessWeekFrom("14-06-2021");
        final String[] expectedBusinessWeek1 = {"14-06-2021", "15-06-2021", "16-06-2021", "17-06-2021", "18-06-2021"};

        for (int i = 0; i < businessWeek1.size(); i++) {
            assertEquals(expectedBusinessWeek1[i], businessWeek1.get(i).plain());
        }

        // #2: tuesday to saturday (monday)
        final List<Date> businessWeek2 = Date.getBusinessWeekFrom("15-06-2021");
        final String[] expectedBusinessWeek2 = {"15-06-2021", "16-06-2021", "17-06-2021", "18-06-2021", "21-06-2021"};

        for (int i = 0; i < businessWeek2.size(); i++) {
            assertEquals(expectedBusinessWeek2[i], businessWeek2.get(i).plain());
        }

        // #3: wednesday to sunday (tuesday)
        final List<Date> businessWeek3 = Date.getBusinessWeekFrom("16-06-2021");
        final String[] expectedBusinessWeek3 = {"16-06-2021", "17-06-2021", "18-06-2021", "21-06-2021", "22-06-2021"};

        for (int i = 0; i < businessWeek3.size(); i++) {
            assertEquals(expectedBusinessWeek3[i], businessWeek3.get(i).plain());
        }

        // #4: thursday to monday (wednesday)
        final List<Date> businessWeek4 = Date.getBusinessWeekFrom("17-06-2021");
        final String[] expectedBusinessWeek4 = {"17-06-2021", "18-06-2021", "21-06-2021", "22-06-2021", "23-06-2021"};

        for (int i = 0; i < businessWeek4.size(); i++) {
            assertEquals(expectedBusinessWeek4[i], businessWeek4.get(i).plain());
        }

        // #5: friday to tuesday (thursday)
        final List<Date> businessWeek5 = Date.getBusinessWeekFrom("18-06-2021");
        final String[] expectedBusinessWeek5 = {"18-06-2021", "21-06-2021", "22-06-2021", "23-06-2021", "24-06-2021"};

        for (int i = 0; i < businessWeek5.size(); i++) {
            assertEquals(expectedBusinessWeek5[i], businessWeek5.get(i).plain());
        }

        // #6: saturday to wednesday (friday)
        final List<Date> businessWeek6 = Date.getBusinessWeekFrom("19-06-2021");
        final String[] expectedBusinessWeek6 = {"21-06-2021", "22-06-2021", "23-06-2021", "24-06-2021", "25-06-2021"};

        for (int i = 0; i < businessWeek6.size(); i++) {
            assertEquals(expectedBusinessWeek6[i], businessWeek6.get(i).plain());
        }

        // #7: sunday to thursday (friday)
        final List<Date> businessWeek7 = Date.getBusinessWeekFrom("20-06-2021");
        final String[] expectedBusinessWeek7 = {"21-06-2021", "22-06-2021", "23-06-2021", "24-06-2021", "25-06-2021"};

        for (int i = 0; i < businessWeek7.size(); i++) {
            assertEquals(expectedBusinessWeek7[i], businessWeek7.get(i).plain());
        }
    }

    @Test
    public void equals() {
        final Object nonDate = new Object();
        final Date differentDate = Date.fromString("12-12-2021");
        final Date equalDate = Date.fromString(PLAIN_DATE);
        final Date sameDate = this.date;

        assertNotEquals(this.date, null);
        assertNotEquals(this.date, nonDate);
        assertNotEquals(this.date, differentDate);
        assertEquals(this.date, equalDate);
        assertEquals(this.date, sameDate);
        assertEquals(this.date, this.date);
    }
}