package com.android.clup;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import static org.junit.Assert.*;

public class ApplicationContextTest {

    @Test
    public void get() {
        // Context of the app under test.
        final Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.android.clup", appContext.getPackageName());
        assertEquals("com.android.clup", ApplicationContext.get().getPackageName());
    }
}