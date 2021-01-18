package com.android.clup.concurrent;

public interface Result {
    final class Success<T> implements Result {
        public final T data;

        public Success(T data) {
            this.data = data;
        }
    }

    final class Error implements Result {
        public final String message;

        public Error(String message) {
            this.message = message;
        }
    }
}
