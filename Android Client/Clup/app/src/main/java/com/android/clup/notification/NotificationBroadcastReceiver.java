package com.android.clup.notification;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import static androidx.core.app.NotificationCompat.EXTRA_NOTIFICATION_ID;
import static com.android.clup.notification.NotificationService.EXTRA_NOTIFICATION;
import static com.android.clup.notification.NotificationService.NOTIFICATION_ID_NOT_SET;

/**
 * This class is used to execute code when the scheduled notification time is elapsed.
 * It creates and displays the notification to the user
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(@NonNull final Context context, @NonNull final Intent intent) {
        // Retrieve the notification to be displayed
        final Notification notification = intent.getParcelableExtra(EXTRA_NOTIFICATION);
        final int notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, NOTIFICATION_ID_NOT_SET);

        // Display the notification
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, notification);
    }
}