package com.android.clup.concurrent;

import androidx.annotation.NonNull;

public interface Callback<T> {
    void onComplete(@NonNull final Result<T> result);
}
