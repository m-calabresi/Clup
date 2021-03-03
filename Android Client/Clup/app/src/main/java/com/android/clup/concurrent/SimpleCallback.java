package com.android.clup.concurrent;

import androidx.annotation.NonNull;

/**
 * A simple interface implementing a callback provided by a caller and used by a callee
 * to notify the caller.
 */
public interface SimpleCallback<T> {
    /**
     * Notify the caller that the requested operation has been completed and return the raw result
     * associated to the performed operation.
     * <p>
     * The returned result is not wrapped inside a {@link Result}
     * object allowing for simple wrapping/unwrapping of data.
     */
    void onComplete(@NonNull final T result);
}
