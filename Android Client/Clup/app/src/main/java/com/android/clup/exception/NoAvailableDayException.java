package com.android.clup.exception;

import androidx.annotation.NonNull;

public class NoAvailableDayException extends Exception {
    public NoAvailableDayException(@NonNull final String message) {
        super(message);
    }
}
