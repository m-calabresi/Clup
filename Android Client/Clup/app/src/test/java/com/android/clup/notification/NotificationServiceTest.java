package com.android.clup.notification;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotificationServiceTest {
    @Test
    void generateId() {
        final String from = "Shop Name";

        final int expectedId = 83104111;
        final int actualId = NotificationService.generateId(from);

        assertEquals(expectedId, actualId);
    }
}