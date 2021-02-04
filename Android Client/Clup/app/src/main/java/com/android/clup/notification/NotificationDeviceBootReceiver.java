package com.android.clup.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.android.clup.model.Date;
import com.android.clup.model.Model;
import com.android.clup.model.Reservation;

import java.util.List;
import java.util.stream.Collectors;

import static com.android.clup.model.Reservation.NotificationStatus.ENABLED;

/**
 * This class is used to execute code after the device has been booted.
 * It is used to set again alarms for notifications (if any) that are discarded after the
 * system shuts down.
 * So if the user sets an alarm and then reboots the device, the notification is firstly discarded
 * by the OS and then re-enabled by this class.
 */
public class NotificationDeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(@NonNull final Context context, @NonNull final Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // resume alarms from previously scheduled reservations, if any
            final List<Reservation> reservations = Model.getInstance().getReservations();
            final List<Reservation> scheduledReservations = reservations.stream()
                    .filter(reservation -> reservation.getNotificationStatus() == ENABLED)
                    .collect(Collectors.toList());

            // schedule a new notification for each reservation that has a valid notification status
            for (Reservation reservation : scheduledReservations) {
                final int position = reservations.indexOf(reservation);
                final String shopName = reservation.getShopName();

                final int timeNotice = reservation.getTimeNotice();
                final String timeNoticeString = Reservation.TimeNotice.toTimeString(context, timeNotice);

                final double endTime = reservation.getDate().toMillis(reservation.getHour()); // time of the appointment
                final double beforeTime = Date.minutesToMillis(timeNotice); // amount of time before the appointment the user wants to be notified (eg. 15 min, 1h...)
                final double expireTime = endTime - beforeTime;

                NotificationService.scheduleNotification(context, position, shopName, timeNoticeString, expireTime);
            }
        }
    }
}
