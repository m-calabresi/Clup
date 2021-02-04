package com.android.clup.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LifecycleOwner;

import com.android.clup.R;
import com.android.clup.concurrent.SimpleCallback;
import com.android.clup.model.Preferences;
import com.android.clup.model.Reservation;
import com.github.razir.progressbutton.ButtonTextAnimatorExtensionsKt;
import com.github.razir.progressbutton.DrawableButton;
import com.github.razir.progressbutton.DrawableButtonExtensionsKt;
import com.github.razir.progressbutton.ProgressButtonHolderKt;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.atomic.AtomicReference;

import kotlin.Unit;

public class Utils {
    private static final int DEFAULT_ANIMATION_DURATION = 200; // milliseconds

    private Utils() {

    }

    /**
     * Hide the soft-input from the user device.
     */
    public static void hideSoftInput(@NonNull final Activity activity) {
        // if keyboard is still open
        if (activity.getCurrentFocus() != null) {
            final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Shows the soft-input on the user device.
     */
    public static void showSoftInput(@NonNull final Activity activity, @NonNull final View view) {
        view.post(() -> {
            final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            view.requestFocus();
            inputMethodManager.showSoftInput(view, 0);
        });
    }

    /**
     * Enables the given button to expose a progress bar animation upon clicked.
     * Must be called before {@link #showProgressBar(Button)}
     */
    public static void enableProgressButton(@NonNull final Button button, @NonNull final LifecycleOwner lifecycleOwner) {
        ProgressButtonHolderKt.bindProgressButton(lifecycleOwner, button);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(button);
    }

    /**
     * Shows the progress bar animation on the given button upon clicked.
     * Must be called after {@link #enableProgressButton(Button, LifecycleOwner)}
     */
    public static void showProgressBar(@NonNull final Button button) {
        button.setClickable(false);

        // start spinning animation
        DrawableButtonExtensionsKt.showProgress(button, progressParams -> {
            final TypedValue typedValue = new TypedValue();
            button.getContext().getTheme().resolveAttribute(R.attr.colorOnPrimary, typedValue, true);
            final int progressColor = typedValue.data;

            progressParams.setProgressColor(progressColor);
            progressParams.setGravity(DrawableButton.GRAVITY_CENTER);
            return Unit.INSTANCE;
        });
    }

    /**
     * Ends the progress bar animation on the given device.
     * Must be called after {@link #showProgressBar(Button)}
     */
    public static void hideProgressBar(@NonNull final Button button, @NonNull final String newButtonText) {
        // stop spinning animation
        new Handler(Looper.getMainLooper()).post(() -> {
            DrawableButtonExtensionsKt.hideProgress(button, newButtonText);
            button.setClickable(true);
        });
    }

    @SuppressWarnings("SameParameterValue")
    private static void displayAlertDialog(@NonNull final Context context, @StringRes final int title,
                                           @StringRes final int message, @StringRes final int buttonText) {
        new Handler(Looper.getMainLooper()).post(() ->
                new MaterialAlertDialogBuilder(context, R.style.AppTheme_Clup_RoundedAlertDialog)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(buttonText, null)
                        .create()
                        .show());
    }

    /**
     * Displays an AlertDialog
     */
    @SuppressWarnings("SameParameterValue")
    private static void displayAlertDialog(@NonNull final Context context,
                                           @StringRes final int message, @StringRes final int buttonText) {
        new Handler(Looper.getMainLooper()).post(() ->
                new MaterialAlertDialogBuilder(context, R.style.AppTheme_Clup_RoundedAlertDialog)
                        .setMessage(message)
                        .setPositiveButton(buttonText, null)
                        .create()
                        .show());
    }

    /**
     * Displays an AlertDialog that notifies the user about a connection error.
     */
    public static void displayConnectionErrorDialog(@NonNull final Context context) {
        displayAlertDialog(context, R.string.title_connection_error_alert_message,
                R.string.text_connection_error_alert_message, R.string.action_ok);
    }

    /**
     * Displays the AlertDialog through which the user can choose the desired theme.
     */
    public static void displayThemesAlertDialog(@NonNull final Context context,
                                                @NonNull final SimpleCallback<Integer> callback) {
        final int selectedPosition = mapThemeToPosition(Preferences.getTheme());

        new Handler(Looper.getMainLooper()).post(() ->
                new MaterialAlertDialogBuilder(context, R.style.AppTheme_Clup_RoundedAlertDialog)
                        .setTitle(R.string.title_theme_alert)
                        .setSingleChoiceItems(R.array.themes_array, selectedPosition, (dialog, whichPosition) -> {
                            final int mode = Utils.mapPositionToTheme(whichPosition);
                            callback.onComplete(mode);
                            dialog.dismiss();
                        })
                        .create()
                        .show());
    }

    /**
     * Displays the AlertDialog through which the user can choose the time notice to be notified about
     * his current reservation.
     */
    public static void displayNotificationAlertDialog(@NonNull final Context context, final int currentTimeNotice,
                                                      @NonNull final SimpleCallback<Integer> callback) {
        final int selectedPosition = Utils.mapTimeNoticeToPosition(currentTimeNotice);
        final AtomicReference<Integer> newSelectedPosition = new AtomicReference<>(-1);

        new Handler(Looper.getMainLooper()).post(() ->
                new MaterialAlertDialogBuilder(context, R.style.AppTheme_Clup_RoundedAlertDialog)
                        .setTitle(R.string.title_notify_alert)
                        .setSingleChoiceItems(R.array.time_notices_array, selectedPosition, (dialog, whichPosition) -> {
                            // when user clicks on an item, save his choice
                            newSelectedPosition.set(whichPosition);
                        })
                        .setPositiveButton(R.string.action_done, (dialog, whichButton) -> {
                            // when the user clicks OK, the selected time notice is returned
                            final int selectedTimeNotice = Utils.mapPositionToTImeNotice(newSelectedPosition.get());
                            callback.onComplete(selectedTimeNotice);
                            dialog.dismiss();
                        })
                        .create()
                        .show());
    }

    /**
     * Displays an AlertDialog telling the user that there is no option to open maps navigation
     * on his device.
     */
    public static void displayMapsNotFoundError(@NonNull final Context context) {
        displayAlertDialog(context, R.string.text_maps_error_alert_dialog, R.string.action_ok);
    }

    /**
     * Displays an AlertDialog telling the user to enable location.
     */
    public static void displayLocationErrorDialog(@NonNull final Context context) {
        displayAlertDialog(context, R.string.text_location_error_alert_message, R.string.action_ok);
    }

    /**
     * Displays a SnackBar that will be attached to the given anchor view.
     */
    @SuppressWarnings("SameParameterValue")
    private static void displayErrorSnackbar(@NonNull final View parent, @Nullable final View anchorView,
                                             @StringRes final int text) {
        final TypedValue typedValue = new TypedValue();
        final Resources.Theme theme = parent.getContext().getTheme();

        theme.resolveAttribute(R.attr.colorError, typedValue, true);
        @ColorInt int colorError = typedValue.data;

        theme.resolveAttribute(R.attr.colorOnError, typedValue, true);
        @ColorInt int colorOnError = typedValue.data;

        Snackbar.make(parent, text, Snackbar.LENGTH_LONG)
                .setAnchorView(anchorView)
                .setBackgroundTint(colorError)
                .setTextColor(colorOnError)
                .show();
    }

    /**
     * Displays a SnackBar.
     */
    public static void displaySnackbar(@NonNull final View parent, @StringRes final int text) {
        Snackbar.make(parent, text, Snackbar.LENGTH_LONG)
                .show();
    }

    public static void displayShopsErrorSnackBar(@NonNull final View parent) {
        displayErrorSnackbar(parent, null, R.string.text_error_shops_snack_bar);
    }

    /**
     * Displays a SnackBar telling the user that a reservation error has occurred
     */
    public static void displayReservationErrorSnackBar(@NonNull final View parent, @NonNull final View anchorView) {
        displayErrorSnackbar(parent, anchorView, R.string.reservation_error_text);
    }

    /**
     * Maps a theme-preference value to the corresponding position in the AlertDialog.
     */
    private static int mapThemeToPosition(final int themeValue) {
        /*  -1 -> 2
         *   1 -> 0
         *   2 -> 1 */
        if (themeValue < 0)
            return 2;
        return themeValue - 1;
    }

    /**
     * Maps the position in the AlertDialog to the corresponding theme-preference.
     */
    private static int mapPositionToTheme(final int position) {
        switch (position) {
            case 0:
                return AppCompatDelegate.MODE_NIGHT_NO;
            case 1:
                return AppCompatDelegate.MODE_NIGHT_YES;
            default:
                return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
    }

    /**
     * Maps the given time notice to the corresponding position in the AlertDialog.
     */
    private static int mapTimeNoticeToPosition(final int timeNoticeValue) {
        switch (timeNoticeValue) {
            case Reservation.TimeNotice.FIFTEEN_MINUTES:
                return 0;
            case Reservation.TimeNotice.THIRTY_MINUTES:
                return 1;
            case Reservation.TimeNotice.ONE_HOUR:
                return 2;
            case Reservation.TimeNotice.TWO_HOURS:
                return 3;
            default:
                return -1;
        }
    }

    /**
     * Maps the given position to the corresponding time notice.
     */
    private static int mapPositionToTImeNotice(final int position) {
        switch (position) {
            case 0:
                return Reservation.TimeNotice.FIFTEEN_MINUTES;
            case 1:
                return Reservation.TimeNotice.THIRTY_MINUTES;
            case 2:
                return Reservation.TimeNotice.ONE_HOUR;
            case 3:
                return Reservation.TimeNotice.TWO_HOURS;
            default:
                throw new RuntimeException("User selected to return NOT_SET, did you handle the logic properly?");
        }
    }

    /**
     * Shows the given view.
     */
    public static void expandHeight(@NonNull final View view) {
        view.post(() -> view.setVisibility(View.VISIBLE));
    }

    /**
     * Hides the given view
     */
    public static void reduceHeight(@NonNull final View view) {
        view.post(() -> view.setVisibility(View.GONE));
    }

    /**
     * Animate the appearing of the given view.
     */
    public static void showView(@NonNull final View view) {
        view.post(() -> view.animate().scaleX(1).scaleY(1).setDuration(DEFAULT_ANIMATION_DURATION).start());
    }

    /**
     * Animate the disappearing of the given view.
     */
    public static void hideView(@NonNull final View view) {
        view.post(() -> view.animate().scaleX(0).scaleY(0).setDuration(DEFAULT_ANIMATION_DURATION).start());
    }

    /**
     * Sets the status bar to be fullscreen.
     */
    @SuppressWarnings("deprecation")
    public static void setFullScreenStatusBar(@NonNull final Activity activity) {
        int systemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
        systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

        activity.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
    }

    /**
     * Sets the height of the padding view to fill the status bar region when the bottom sheet
     * is fully expanded.
     */
    public static void setPadHeight(@NonNull final View parentView, @NonNull final View padView) {
        padView.post(() -> ViewCompat.setOnApplyWindowInsetsListener(parentView, (v, insets) -> {
            final int marginTop = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            final ViewGroup.LayoutParams layoutParams = padView.getLayoutParams();

            layoutParams.height = marginTop;
            padView.setLayoutParams(layoutParams);

            return WindowInsetsCompat.CONSUMED;
        }));
    }

    /**
     * Sets the top and start margin for the given view based on the status bar height.
     */
    public static void setTopStartMargins(@NonNull final View parentView, @NonNull final View view) {
        ViewCompat.setOnApplyWindowInsetsListener(parentView, (v, insets) -> {
            final int marginTop = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            final int marginStart = (int) parentView.getResources().getDimension(R.dimen.mini_fab_margin_start);

            final CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
            params.topMargin = marginTop;
            params.leftMargin = marginStart;
            params.rightMargin = marginStart;
            view.setLayoutParams(params);

            return WindowInsetsCompat.CONSUMED;
        });
    }
}
