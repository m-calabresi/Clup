package com.android.clup.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.android.clup.R;
import com.android.clup.model.Date;
import com.android.clup.model.Reservation;
import com.android.clup.ui.DetailsActivity;

import java.util.ArrayList;
import java.util.List;

import static androidx.core.app.NotificationCompat.EXTRA_NOTIFICATION_ID;

/**
 * This class is responsible for building, scheduling and displaying notifications.
 */
public class NotificationService {
    @NonNull
    private static final String PACKAGE_NAME = "com.android.Clup";
    @NonNull
    private static final String CHANNEL_ID = PACKAGE_NAME + ".ChannelId";
    @NonNull
    private static final String ACTION_DIRECTIONS = PACKAGE_NAME + ".ACTION_DIRECTIONS";
    @NonNull
    private static final String NOTIFICATION_GROUP_KEY = PACKAGE_NAME + ".RESERVATIONS_GROUP";

    @NonNull
    public static final String EXTRA_RESERVATION = PACKAGE_NAME + ".EXTRA_RESERVATION";
    @NonNull
    public static final String EXTRA_RESERVATION_COORDINATES = EXTRA_RESERVATION + ".COORDINATES";
    @NonNull
    public static final String EXTRA_NOTIFICATION = PACKAGE_NAME + ".EXTRA_NOTIFICATION";
    public static final int NOTIFICATION_ID_NOT_SET = -1;

    private NotificationService() {

    }

    /**
     * Create the NotificationChannel, but only on API 26+ because
     * the NotificationChannel class is new and not in the support library.
     * <p>
     * Should be called at app startup and it is safe to call it multiple times.
     */
    public static void createNotificationChannel(@NonNull final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final CharSequence channelName = context.getString(R.string.notification_channel_name);
            final String channelDescription = context.getString(R.string.notification_channel_description);
            final int importance = NotificationManager.IMPORTANCE_HIGH;

            final NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDescription);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            final NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Immediately shows a notification with action button.
     *
     * @param context        the application context from which the notification starts.
     * @param notificationId a unique id for the current notification. It must be the same only if
     *                       the notification to display is related to the same reservation.
     * @param reservation    the reservation upon which the notification is built.
     */
    @NonNull
    private static Notification buildNotification(@NonNull final Context context, final int notificationId,
                                                  @NonNull final Reservation reservation) {
        // Create an explicit intent to launch DetailsActivity when the notification is clicked
        final Intent intent = new Intent(context, DetailsActivity.class);
        // Specify the reservation interested by the notification. It will be used by DetailsActivity
        // to display related content
        intent.putExtra(EXTRA_RESERVATION, reservation);

        // Create the TaskStackBuilder and add the intent, which inflates the back stack.
        // This allows the user to go back to the previous activity even if it was not present before
        final TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent);

        // Get the PendingIntent containing the entire back stack
        final PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the intent that handles the click on the notification button. It will trigger
        // the BroadcastReceiver and execute the related code.
        final Intent directionsIntent = new Intent(context, NotificationActionBroadcastReceiver.class);
        directionsIntent.setAction(ACTION_DIRECTIONS + System.currentTimeMillis()); // millis are required in order to force the update of a new action (that otherwise is being cached)
        directionsIntent.putExtra(EXTRA_NOTIFICATION_ID, notificationId);
        directionsIntent.putExtra(EXTRA_RESERVATION_COORDINATES, reservation.getCoords());
        final PendingIntent directionsPendingIntent = PendingIntent.getBroadcast(context, 0, directionsIntent, 0);

        final CharSequence textTitle = context.getString(R.string.notification_title, reservation.getShopName());
        final String timeNotice = Reservation.TimeNotice.toTimeString(context, reservation.getTimeNotice());
        final CharSequence textContent = context.getString(R.string.notification_content, timeNotice);
        final CharSequence actionName = context.getString(R.string.action_directions);
        @ColorInt final int notificationAccentColor = context.getResources().getColor(R.color.notification_accent_color);

        // Build the actual notification
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(notificationAccentColor)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setGroup(NOTIFICATION_GROUP_KEY)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_directions, actionName, directionsPendingIntent);

        return builder.build();
    }

    /**
     * Schedule a notification to be show after the given amount of time.
     * All parameters MUST be immutable and deterministic, otherwise the cancelling procedure can't be performed.
     *
     * @param context     the application context from which the notification starts.
     * @param reservation the reservation upon which the notification is built.
     */
    public static void scheduleNotification(@NonNull final Context context,
                                            @NonNull final Reservation reservation) {
        // time of the appointment
        final long endTime = reservation.getDate().toMillis();
        // amount of time before the appointment the user wants to be notified (eg. 15 min, 1h...)
        final long beforeTime = Date.minutesToMillis(reservation.getTimeNotice());
        // time at which the notification should appear
        final long expireTime = endTime - beforeTime;

        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final PendingIntent alarmIntent = NotificationService.buildPendingIntent(context, reservation);
        alarmManager.set(AlarmManager.RTC_WAKEUP, expireTime, alarmIntent);
    }

    /**
     * Cancel the notification associate to the PendingIntent created with the given information.
     * If the PendingIntent created does not correspond exactly to the PendingIntent used to schedule
     * the notification, the cancel operation is ignored.
     *
     * @param context     the application context from which the notification starts.
     * @param reservation the reservation corresponding to the notification ot be cancelled.
     */
    public static void cancelNotification(@NonNull final Context context,
                                          @NonNull final Reservation reservation) {
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final PendingIntent alarmIntent = NotificationService.buildPendingIntent(context, reservation);

        alarmManager.cancel(alarmIntent);
    }

    /**
     * Build a PendingIntent with given information.
     *
     * @param context     the application context from which the notification starts.
     * @param reservation the reservation upon which the notification is built.
     * @return the PendingIntent.
     */
    private static PendingIntent buildPendingIntent(@NonNull final Context context,
                                                    @NonNull final Reservation reservation) {
        final int notificationId = NotificationService.generateId(reservation.getShopName());

        final Notification notification = NotificationService.buildNotification(context, notificationId, reservation);
        final Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        intent.putExtra(EXTRA_NOTIFICATION, notification);
        intent.putExtra(EXTRA_NOTIFICATION_ID, notificationId);
        return PendingIntent.getBroadcast(context, notificationId, intent, 0);
    }

    /**
     * Disables the receiver in manifest, preventing it from being notified after device boots.
     * This method can be called when no notifications are scheduled and therefore there is no need
     * to listen for device restart to restore them.
     */
    public static void disableNotificationReceiver(@NonNull final Context context) {
        NotificationService.setNotificationReceiverStatus(context,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
    }

    /**
     * Enables the receiver in manifest, allowing it to be notified after device boots.
     * This method can be called when at least one notification is scheduled, in order for the
     * receiver to be notified of a possible reboot of the device and reschedule the pending
     * notifications.
     */
    public static void enableNotificationReceiver(@NonNull final Context context) {
        NotificationService.setNotificationReceiverStatus(context,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
    }

    /**
     * Set the status of the receiver in the manifest to the given status.
     */
    private static void setNotificationReceiverStatus(@NonNull final Context context, final int status) {
        final ComponentName receiver = new ComponentName(context, NotificationDeviceBootBroadcastReceiver.class);
        final PackageManager packageManager = context.getPackageManager();

        packageManager.setComponentEnabledSetting(receiver,
                status,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * Generate an identifier associated to the given string.
     * The identifier is created by selecting the first 9 values concatenation of ASCII values of each character.
     */
    public static int generateId(@NonNull final String string) {
        final List<Integer> ints = new ArrayList<>(string.length());

        for (char c : string.toCharArray())
            ints.add((int) c);

        final String id = ints.stream().map(c -> c + "").reduce((c, d) -> c + d).orElse("");
        return Integer.parseInt(id.substring(0, 8));
    }
}
