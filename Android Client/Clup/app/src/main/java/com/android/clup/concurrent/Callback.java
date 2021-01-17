package com.android.clup.concurrent;

public interface Callback<T> {
    void onComplete(Result<T> result);
}
