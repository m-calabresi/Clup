package com.android.clup.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Date {
    private final Calendar calendar;
    private final SimpleDateFormat sdf;

    private String formatDate;

    public Date(final int day, final int month, final int year) {
        this.calendar = Calendar.getInstance(Locale.getDefault());
        this.calendar.set(year, month, day);
        this.sdf = new SimpleDateFormat("EEEE d, MMMM", Locale.getDefault());
    }

    public String getFormatDate() {
        if (this.formatDate == null) {
            final String formatDate = this.sdf.format(calendar.getTime());
            this.formatDate = formatDate.substring(0, 1).toUpperCase() + formatDate.substring(1);
        }
        return this.formatDate;
    }
}
