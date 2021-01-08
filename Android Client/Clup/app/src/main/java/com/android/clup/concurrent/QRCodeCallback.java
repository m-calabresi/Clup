package com.android.clup.concurrent;

public interface QRCodeCallback<T> {
    void onComplete(Result<T> result);
}
