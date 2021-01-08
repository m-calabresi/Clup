package com.android.clup.concurrent;

public interface AuthenticationCallback<T> {
    void onComplete(Result<T> result);
}
