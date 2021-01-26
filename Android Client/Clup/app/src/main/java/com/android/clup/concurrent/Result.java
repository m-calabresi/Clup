package com.android.clup.concurrent;

public interface Result<T> {
    final class Success<T> implements Result<T> {
        public final T data;

        public Success(T data) {
            this.data = data;
        }
    }

    final class Error<T> implements Result<T> {
        public final String message;

        public Error(String message) {
            this.message = message;
        }
    }
}
