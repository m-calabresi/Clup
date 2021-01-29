package com.android.clup;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

public class ApplicationContext extends Application {
    private static ApplicationContext instance;

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    @NonNull
    public static Context get() {
        return instance.getApplicationContext();
    }
}
