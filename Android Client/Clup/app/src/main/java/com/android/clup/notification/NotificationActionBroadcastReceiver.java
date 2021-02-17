package com.android.clup.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.android.clup.api.MapsService;
import com.google.android.gms.maps.model.LatLng;

import static androidx.core.app.NotificationCompat.EXTRA_NOTIFICATION_ID;
import static com.android.clup.notification.NotificationService.EXTRA_RESERVATION_COORDINATES;

/**
 * This class is used to execute code when the user clicks on the action button inside the notification.
 */
public class NotificationActionBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(@NonNull final Context context, @NonNull final Intent intent) {
        final int notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, NotificationService.NOTIFICATION_ID_NOT_SET);

        // cancel the notification
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(notificationId);

        // pull up notification shade
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        // retrieve the interested coordinates
        final LatLng coords = intent.getParcelableExtra(EXTRA_RESERVATION_COORDINATES);

        // launch the google map navigation
        MapsService.launchNavigation(context, coords, true);
    }
}