package com.android.clup.viewmodel;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModel;

import com.android.clup.R;
import com.android.clup.api.MapsService;
import com.android.clup.model.Date;
import com.android.clup.model.Model;
import com.android.clup.model.Reservation;
import com.android.clup.notification.NotificationService;
import com.android.clup.ui.Utils;

import net.glxn.qrgen.android.QRCode;

public class DetailsViewModel extends ViewModel {
    private static final int QR_CODE_SIZE = 1024;

    @NonNull
    private final Model model;

    public DetailsViewModel() {
        this.model = Model.getInstance();

        // reset unused fields
        this.model.resetSelectedShopIndex();
        this.model.resetSelectedDayIndex();
        this.model.resetSelectedHourIndex();
    }

    /**
     * Generates the qr-code associated to the given uuid using the specified color-scheme.
     */
    @NonNull
    public Bitmap getReservationQrCode(final int onColor, final int offColor) {
        final String uuid = this.model.getSelectedReservation().getUuid();
        return QRCode.from(uuid).withSize(QR_CODE_SIZE, QR_CODE_SIZE).withColor(onColor, offColor).bitmap();
    }

    /**
     * Return the name of the shop to which the reservation has been made.
     */
    @NonNull
    public String getReservationShopName() {
        return this.model.getSelectedReservation().getShopName();
    }

    /**
     * Return the formatted date of the reservation.
     */
    @NonNull
    public String getReservationDate() {
        return this.model.getSelectedReservation().getDate().formatted();
    }

    /**
     * Return the hour of the reservation.
     */
    @NonNull
    public String getReservationHour() {
        return this.model.getSelectedReservation().getHour();
    }

    /**
     * Set the reservation index to retrieve the correct reservation to be displayed.
     */
    public void setSelectedReservationPosition(final int position) {
        this.model.setSelectedReservationIndex(position);
    }

    /**
     * Set the CardView background as top-rounded-corners-only if the device is not a tablet and
     * it is in portrait mode.
     */
    public void handleCardViewBackground(@NonNull final Context context, @NonNull final CardView cardView) {
        final boolean isTablet = (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        final boolean isPortrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        if (!isTablet && isPortrait) {
            cardView.setBackgroundResource(R.drawable.rounded_view_background);
        }
    }

    /**
     * Navigates to the selected shop using google maps if available, otherwise a prompt is displayed
     * to the user.
     */
    public void navigateToSelectedShop(@NonNull final Activity activity) {
        try {
            MapsService.launchNavigation(activity, this.model.getSelectedReservation().getCoords(), false);
        } catch (ActivityNotFoundException e) {
            Utils.displayMapsNotFoundError(activity);
        }
    }

    /**
     * Initialize icon and text of the given button to the drawable specified by the value returned by
     * the model.
     */
    public void initButton(@NonNull final Button button) {
        @DrawableRes int iconId;
        @StringRes int actionId;

        switch (this.model.getSelectedReservation().getNotificationStatus()) {
            case Reservation.NotificationStatus.ENABLED:
                iconId = R.drawable.ic_notification_on;
                actionId = R.string.action_notification_on;
                break;
            case Reservation.NotificationStatus.DISABLED:
                iconId = R.drawable.ic_notification_off;
                actionId = R.string.action_notification_off;
                break;
            default:
                iconId = R.drawable.ic_notification_unassigned;
                actionId = R.string.action_notify_me;
                break;
        }
        button.setCompoundDrawablesRelativeWithIntrinsicBounds(iconId, 0, 0, 0);
        button.setText(actionId);
    }

    /**
     * Toggles the notification status between on and off.
     * THis method handles both the graphical part and the calls to the logical part.
     */
    public void toggleNotification(@NonNull final Context context, @NonNull final Button button) {

        if (this.model.getSelectedReservation().getNotificationStatus() == Reservation.NotificationStatus.ENABLED) {
            conditionallyDisableNotifications(context);
            cancelNotification(context);

            @DrawableRes final int iconId = R.drawable.ic_notification_off;
            @StringRes final int actionId = R.string.action_notification_off;
            final int notificationStatus = Reservation.NotificationStatus.DISABLED;
            final int timeNotice = Reservation.TimeNotice.NOT_SET;
            @StringRes final int snackbarTextId = R.string.text_notification_cancelled;

            toggleNotification(context, button, iconId, actionId, notificationStatus, timeNotice, snackbarTextId);
        } else {
            final int currentTimeNotice = this.model.getSelectedReservation().getTimeNotice();

            Utils.displayNotificationAlertDialog(context, currentTimeNotice, newTimeNotice -> {

                NotificationService.enableNotificationReceiver(context);
                scheduleNotification(context, newTimeNotice);

                @DrawableRes final int iconId = R.drawable.ic_notification_on;
                @StringRes final int actionId = R.string.action_notification_on;
                final int notificationStatus = Reservation.NotificationStatus.ENABLED;
                @StringRes final int snackbarTextId = R.string.text_notification_set;

                toggleNotification(context, button, iconId, actionId, notificationStatus, newTimeNotice, snackbarTextId);
            });
        }
    }

    /**
     * Toggle the notification-related UI components depending on the given parameters.
     */
    private void toggleNotification(@NonNull final Context context, @NonNull final Button button,
                                    @DrawableRes final int iconId, @StringRes final int actionId,
                                    final int notificationStatus, final int timeNotice,
                                    @StringRes final int snackBarTextId) {
        // set button icon and text
        button.setCompoundDrawablesRelativeWithIntrinsicBounds(iconId, 0, 0, 0);
        button.setText(actionId);

        // display confirmation snackbar
        final View parent = ((Activity) (context)).findViewById(R.id.layout_activity_details);
        Utils.displaySnackbar(parent, snackBarTextId);

        // update the notification information
        this.model.setSelectedReservationNotificationInfo(notificationStatus, timeNotice);
    }

    /**
     * Schedule the notification for the selected reservation with the given time notice.
     */
    private void scheduleNotification(@NonNull final Context context, final int timeNotice) {
        // gather the information to schedule the notification
        final Reservation selectedReservation = this.model.getSelectedReservation();

        final int position = this.model.getSelectedReservationIndex();
        final String shopName = selectedReservation.getShopName();
        final String timeNoticeString = Reservation.TimeNotice.toTimeString(context, timeNotice);

        final double endTime = selectedReservation.getDate().toMillis(selectedReservation.getHour()); // time of the appointment
        final double beforeTime = Date.minutesToMillis(timeNotice); // amount of time before the appointment the user wants to be notified (eg. 15 min, 1h...)
        final double expireTime = endTime - beforeTime;

        // schedule the notification
        NotificationService.scheduleNotification(context, position, shopName, timeNoticeString, expireTime);
    }

    /**
     * Cancel the notification associated with the current reservation.
     */
    private void cancelNotification(@NonNull final Context context) {
        // gather the information to schedule the notification
        final Reservation reservation = this.model.getSelectedReservation();

        final int position = this.model.getSelectedReservationIndex();
        final String shopName = reservation.getShopName();
        final String timeNotice = Reservation.TimeNotice.toTimeString(context, reservation.getTimeNotice());

        // cancel the notification
        NotificationService.cancelNotification(context, position, shopName, timeNotice);
    }

    /**
     * Decides whether to toggle notification service completely off.
     * This can happen only if all reservations doesn't have a notification set.
     */
    private void conditionallyDisableNotifications(@NonNull final Context context) {
        // when notification is switched off on a particular reservation, only if all of the notifications are off, then I can disable the receiver
        if (!isAnyNotificationSet())
            NotificationService.disableNotificationReceiver(context);
    }

    /**
     * Check whether exists at least one reservation that has a notification set and, if so, returns
     * true.
     */
    private boolean isAnyNotificationSet() {
        for (Reservation reservation : this.model.getReservations())
            if (reservation.getTimeNotice() != Reservation.TimeNotice.NOT_SET) {
                return true;
            }
        return false;
    }
}
