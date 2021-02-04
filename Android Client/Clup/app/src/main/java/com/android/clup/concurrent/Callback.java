package com.android.clup.concurrent;

import androidx.annotation.NonNull;

/**
 * A simple interface implementing a callback provided by a caller and used by a callee
 * to notify the caller.
 */
public interface Callback<T> {
    /**
     * Notify the caller that the requested operation has been completed and return the result
     * associated to the performed operation.
     * <p>
     * The returned result is wrapped inside the generic {@link Result} class, allowing for a richer
     * error handling.
     */
    void onComplete(@NonNull final Result<T> result);
}
