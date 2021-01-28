package com.android.clup.api;

import androidx.annotation.NonNull;

public class InvalidResponseException extends Exception {
    @NonNull
    private final String message;

    public InvalidResponseException(@NonNull final String s) {
        super(s);
        this.message = s;
    }

    @NonNull
    @Override
    public String getMessage() {
        return this.message;
    }

    @NonNull
    @Override
    public String getLocalizedMessage() {
        return this.message;
    }
}
