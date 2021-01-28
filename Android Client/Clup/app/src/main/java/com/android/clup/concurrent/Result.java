package com.android.clup.concurrent;

import androidx.annotation.NonNull;

public interface Result<T> {
    final class Success<T> implements Result<T> {
        public final T data;

        public Success(@NonNull final T data) {
            this.data = data;
        }
    }

    final class Error<T> implements Result<T> {
        public final String message;

        public Error(@NonNull final String message) {
            this.message = message;
        }
    }
}
