package com.android.clup.notification;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NotificationServiceTest {
    @Test
    public void generateId() {
        final String from = "Shop Name";

        final int expectedId = 83104111;
        final int actualId = NotificationService.generateId(from);

        assertEquals(expectedId, actualId);
    }
}