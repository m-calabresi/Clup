package com.android.clup;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

/**
 * The application context. This class is used to retrieve the application context without the need
 * to ask it to an Activity.
 */
public class ApplicationContext extends Application {
    private static ApplicationContext instance;

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    /**
     * Returns the application context.
     */
    @NonNull
    public static Context get() {
        return instance.getApplicationContext();
    }
}
