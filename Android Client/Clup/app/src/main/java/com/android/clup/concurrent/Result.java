package com.android.clup.concurrent;

import androidx.annotation.NonNull;

/**
 * A simple class implementing a result of some operation.
 */
public interface Result<T> {
    /**
     * A specific implementation of the {@link Result} class indicating a successful result.
     * This class contains a {@code data} value representing the result content.
     */
    final class Success<T> implements Result<T> {
        @NonNull
        public final T data;

        public Success(@NonNull final T data) {
            this.data = data;
        }
    }

    /**
     * A specific implementation of the {@link Result} class indicating an error result.
     * This class contains a {@code message} value representing the cause of the error.
     */
    final class Error<T> implements Result<T> {
        @NonNull
        public final String message;

        public Error(@NonNull final String message) {
            this.message = message;
        }
    }
}
