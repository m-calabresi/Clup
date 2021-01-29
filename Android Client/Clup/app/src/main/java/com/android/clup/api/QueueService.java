package com.android.clup.api;

import androidx.annotation.NonNull;

import com.android.clup.concurrent.Callback;
import com.android.clup.concurrent.Result;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class QueueService {
    @NonNull
    private final Executor executor;

    public QueueService() {
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Returns the UUID code associated to the Queue in which the user is being added.
     */
    public void getUuid(@NonNull final String username, @NonNull final String shopName, @NonNull final String date,
                        @NonNull final String hour, @NonNull final Callback<String> callback) {
        executor.execute(() -> {
            final Result<String> result = new Result.Success<>("12345yhgfr56ygfr56765433456uhgy7"); // TODO replace with API call
            callback.onComplete(result);
        });
    }
}
