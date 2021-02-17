package com.android.clup.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.android.clup.model.Model;
import com.android.clup.model.Reservation;

import java.util.List;
import java.util.stream.Collectors;

import static com.android.clup.model.Reservation.TimeNotice.DISABLED;
import static com.android.clup.model.Reservation.TimeNotice.NOT_SET;

/**
 * This class is used to execute code after the device has been booted.
 * It is used to set again alarms for notifications (if any) that are discarded after the
 * system shuts down.
 * So if the user sets an alarm and then reboots the device, the notification is firstly discarded
 * by the OS and then re-enabled by this class.
 */
public class NotificationDeviceBootBroadcastReceiver extends BroadcastReceiver {

    /**
     * Resume alarms from previously scheduled reservations, if any.
     */
    @Override
    public void onReceive(@NonNull final Context context, @NonNull final Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            // find reservations for which a notification was previously scheduled
            final List<Reservation> reservations = Model.getInstance().getReservations();
            final List<Reservation> scheduledReservations = reservations.stream()
                    .filter(reservation -> reservation.getTimeNotice() != NOT_SET
                            && reservation.getTimeNotice() != DISABLED)
                    .collect(Collectors.toList());

            // schedule a new notification for each reservation that has a valid notification status
            for (Reservation reservation : scheduledReservations)
                NotificationService.scheduleNotification(context, reservation);
        }
    }
}
