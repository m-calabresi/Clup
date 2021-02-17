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
        this.model.resetSelectedShop();
        this.model.resetSelectedDay();
        this.model.resetSelectedTime();
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
     * Return the time of the reservation.
     */
    @NonNull
    public String getReservationTime() {
        return this.model.getSelectedReservation().getDate().getTime();
    }

    /**
     * Set the reservation to be displayed.
     */
    public void setSelectedReservation(@NonNull final Reservation reservation) {
        this.model.setSelectedReservation(reservation);
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

        switch (this.model.getSelectedReservation().getTimeNotice()) {
            case Reservation.TimeNotice.NOT_SET:
                iconId = R.drawable.ic_notification_not_set;
                actionId = R.string.action_notify_me;
                break;
            case Reservation.TimeNotice.DISABLED:
                iconId = R.drawable.ic_notification_off;
                actionId = R.string.action_notification_off;
                break;
            default:
                iconId = R.drawable.ic_notification_on;
                actionId = R.string.action_notification_on;
                break;
        }
        button.setCompoundDrawablesRelativeWithIntrinsicBounds(iconId, 0, 0, 0);
        button.setText(actionId);
    }

    /**
     * Toggles the notification status between on and off.
     * This method handles both the graphical part and the calls to the logical part.
     */
    public void toggleNotificationUi(@NonNull final Context context, @NonNull final Button button) {
        final int timeNotice = this.model.getSelectedReservation().getTimeNotice();

        if (timeNotice != Reservation.TimeNotice.NOT_SET && timeNotice != Reservation.TimeNotice.DISABLED) {
            conditionallyDisableNotifications(context);
            cancelNotification(context);

            @DrawableRes final int iconId = R.drawable.ic_notification_off;
            @StringRes final int actionId = R.string.action_notification_off;
            @StringRes final int snackbarTextId = R.string.text_notification_cancelled;

            toggleNotificationUi(context, button, iconId, actionId, snackbarTextId);
        } else {
            final int currentTimeNotice = this.model.getSelectedReservation().getTimeNotice();

            Utils.displayNotificationAlertDialog(context, currentTimeNotice, newTimeNotice -> {
                NotificationService.enableNotificationReceiver(context);
                scheduleNotification(context, newTimeNotice);

                @DrawableRes final int iconId = R.drawable.ic_notification_on;
                @StringRes final int actionId = R.string.action_notification_on;
                @StringRes final int snackbarTextId = R.string.text_notification_set;

                toggleNotificationUi(context, button, iconId, actionId, snackbarTextId);
            });
        }
    }

    /**
     * Toggle the notification-related UI components depending on the given parameters.
     */
    private void toggleNotificationUi(@NonNull final Context context, @NonNull final Button button,
                                      @DrawableRes final int iconId, @StringRes final int actionId,
                                      @StringRes final int snackBarTextId) {
        // set button icon and text
        button.setCompoundDrawablesRelativeWithIntrinsicBounds(iconId, 0, 0, 0);
        button.setText(actionId);

        // display confirmation snackbar
        final View parent = ((Activity) (context)).findViewById(R.id.layout_activity_details);
        Utils.displaySnackbar(parent, snackBarTextId);
    }

    /**
     * Schedule the notification for the selected reservation with the given time notice.
     */
    private void scheduleNotification(@NonNull final Context context, final int timeNotice) {
        // get the selected reservation
        final Reservation selectedReservation = this.model.getSelectedReservation();
        // update the notification information
        this.model.setSelectedReservationTimeNotice(timeNotice);

        // schedule the notification
        NotificationService.scheduleNotification(context, selectedReservation);
    }

    /**
     * Cancel the notification associated with the current reservation.
     */
    private void cancelNotification(@NonNull final Context context) {
        // get the selected reservation
        final Reservation reservation = this.model.getSelectedReservation();

        // cancel the notification
        NotificationService.cancelNotification(context, reservation);

        // update the notification information
        this.model.setSelectedReservationTimeNotice(Reservation.TimeNotice.DISABLED);
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
            if (reservation.getTimeNotice() != Reservation.TimeNotice.NOT_SET)
                return true;
        return false;
    }
}
