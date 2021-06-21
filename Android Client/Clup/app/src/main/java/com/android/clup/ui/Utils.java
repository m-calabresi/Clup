package com.android.clup.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LifecycleOwner;

import com.android.clup.R;
import com.android.clup.adapter.TimeLineAdapter;
import com.android.clup.concurrent.SimpleCallback;
import com.android.clup.model.Preferences;
import com.android.clup.model.Reservation;
import com.github.razir.progressbutton.ButtonTextAnimatorExtensionsKt;
import com.github.razir.progressbutton.DrawableButton;
import com.github.razir.progressbutton.DrawableButtonExtensionsKt;
import com.github.razir.progressbutton.ProgressButtonHolderKt;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import kotlin.Unit;

/**
 * Utility class used to quickly retrieve useful methods that can be executed on the UI thread.
 */
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
     * Show the soft-input on the user device.
     */
    public static void showSoftInput(@NonNull final Activity activity, @NonNull final View view) {
        view.post(() -> {
            final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            view.requestFocus();
            inputMethodManager.showSoftInput(view, 0);
        });
    }

    /**
     * Enable the given button to expose a progress bar animation upon clicked.
     * Must be called before {@link #startProgressBarAnimation(Button)}.
     */
    public static void enableProgressBarAnimation(@NonNull final Button button, @NonNull final LifecycleOwner lifecycleOwner) {
        ProgressButtonHolderKt.bindProgressButton(lifecycleOwner, button);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(button);
    }

    /**
     * Start the progress bar animation on the given button upon clicked.
     * Must be called after {@link #enableProgressBarAnimation(Button, LifecycleOwner)}.
     */
    public static void startProgressBarAnimation(@NonNull final Button button) {
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
     * End the progress bar animation on the given device.
     * Must be called after {@link #startProgressBarAnimation(Button)}.
     */
    public static void stopProgressBarAnimation(@NonNull final Button button, @NonNull final String newButtonText) {
        // stop spinning animation
        new Handler(Looper.getMainLooper()).post(() -> {
            DrawableButtonExtensionsKt.hideProgress(button, newButtonText);
            button.setClickable(true);
        });
    }

    /**
     * Displays an AlertDialog.
     */
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
     * Display an AlertDialog.
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
     * Display an AlertDialog that notifies the user about a connection error.
     */
    public static void displayConnectionErrorDialog(@NonNull final Context context) {
        displayAlertDialog(context, R.string.title_error_connection_alert,
                R.string.text_error_connection_alert, R.string.action_ok);
    }

    /**
     * Display the AlertDialog through which the user can choose the desired theme.
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
     * Display the AlertDialog through which the user can choose the time notice to be notified about
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
                            final int selectedTimeNotice = Utils.mapPositionToTimeNotice(newSelectedPosition.get());
                            callback.onComplete(selectedTimeNotice);
                            dialog.dismiss();
                        })
                        .create()
                        .show());
    }

    /**
     * Display an AlertDialog telling the user that there is no option to open maps navigation
     * on his device.
     */
    public static void displayMapsNotFoundError(@NonNull final Context context) {
        displayAlertDialog(context, R.string.text_error_alert_maps, R.string.action_ok);
    }

    /**
     * Display an AlertDialog telling the user to enable location.
     */
    public static void displayLocationErrorDialog(@NonNull final Context context) {
        displayAlertDialog(context, R.string.text_error_alert_location, R.string.action_ok);
    }

    /**
     * Display a SnackBar that will be attached to the given anchor view.
     */
    @SuppressLint("ShowToast")
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
     * Display a SnackBar.
     */
    public static void displaySnackbar(@NonNull final View parent, @StringRes final int text) {
        Snackbar.make(parent, text, Snackbar.LENGTH_LONG)
                .show();
    }

    public static void displayShopsErrorSnackBar(@NonNull final View parent) {
        displayErrorSnackbar(parent, null, R.string.text_error_snackbar_shop);
    }

    /**
     * Display a SnackBar telling the user that a reservation error has occurred.
     */
    public static void displayReservationErrorSnackBar(@NonNull final View parent, @NonNull final View anchorView) {
        displayErrorSnackbar(parent, anchorView, R.string.text_error_reservation);
    }

    /**
     * Map a theme-preference value to the corresponding position in the AlertDialog.
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
     * Map the position in the AlertDialog to the corresponding theme-preference.
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
     * Map the given time notice to the corresponding position in the AlertDialog.
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
     * Map the given position to the corresponding time notice.
     */
    private static int mapPositionToTimeNotice(final int position) {
        switch (position) {
            case 0:
                return Reservation.TimeNotice.NOW;
            case 1:
                return Reservation.TimeNotice.FIFTEEN_MINUTES;
            case 2:
                return Reservation.TimeNotice.THIRTY_MINUTES;
            case 3:
                return Reservation.TimeNotice.ONE_HOUR;
            case 4:
                return Reservation.TimeNotice.TWO_HOURS;
            default:
                throw new RuntimeException("User selected to return NOT_SET, did you handle the logic properly?");
        }
    }

    /**
     * Show the given view.
     */
    public static void expandHeight(@NonNull final View view) {
        view.post(() -> view.setVisibility(View.VISIBLE));
    }

    /**
     * Hide the given view.
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
     * Set the status bar to be fullscreen.
     */
    public static void setFullScreenStatusBar(@NonNull final Activity activity) {
        int systemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
        systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

        activity.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
    }

    /**
     * Set the height of the padding view to fill the status bar region when the bottom sheet
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
     * Set the top and start margin for the given view based on the status bar height.
     */
    public static void setTopStartMargins(@NonNull final View parentView, @NonNull final View view) {
        ViewCompat.setOnApplyWindowInsetsListener(parentView, (v, insets) -> {
            final int marginTop = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            final int marginStart = (int) parentView.getResources().getDimension(R.dimen.mini_fab_margin_start);

            if (!Utils.isPhone(parentView.getContext())) {
                final ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) view.getLayoutParams();
                params.topMargin = marginTop;
                params.leftMargin = marginStart;
                params.rightMargin = marginStart;
                view.setLayoutParams(params);
            } else {
                final CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
                params.topMargin = marginTop;
                params.leftMargin = marginStart;
                params.rightMargin = marginStart;
                view.setLayoutParams(params);
            }

            return WindowInsetsCompat.CONSUMED;
        });
    }

    /**
     * Convert the given resource id (corresponding to a vector drawable resource) into a {@code BitmapDescriptor}
     * entity.
     */
    @NonNull
    public static BitmapDescriptor vectorToBitmap(@NonNull final Context context, @DrawableRes int id) {
        final Drawable vectorDrawable = ResourcesCompat.getDrawable(context.getResources(), id, null);
        final Bitmap bitmap = Bitmap.createBitmap(Objects.requireNonNull(vectorDrawable).getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);

        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * Fill a {@link ChipGroup} with chips that contains values specified by {@code values}.
     */
    public static void setTimeChips(@NonNull final ChipGroup parent, @NonNull final List<String> values) {
        parent.post(() -> {
            if (parent.getChildCount() > 0)
                parent.removeAllViews();

            final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

            for (int i = 0; i < values.size(); i++) {
                final String value = values.get(i);
                final Chip chip = (Chip) layoutInflater.inflate(R.layout.item_chip, parent, false);
                chip.setText(value);

                parent.addView(chip);
            }
        });
    }

    /**
     * Highlight end line of the {@code TimeLineView} of the given {@code TimeLineViewHolder}.
     */
    public static void highlightEndLine(@NonNull final TimeLineAdapter.TimeLineViewHolder viewHolder) {
        final int lineColor = viewHolder.itemView.getContext().getResources().getColor(R.color.timeline_active_line_color);
        viewHolder.timelineView.setEndLineColor(lineColor, viewHolder.getItemViewType());
    }

    /**
     * Removes the highlighting at the end of the {@code TimeLineView} of the given {@code TimeLineViewHolder}.
     */
    public static void unHighlightEndLine(@NonNull final TimeLineAdapter.TimeLineViewHolder viewHolder) {
        final int lineColor = viewHolder.itemView.getContext().getResources().getColor(R.color.normal_text_color);
        viewHolder.timelineView.setEndLineColor(lineColor, viewHolder.getItemViewType());
    }

    /**
     * Highlights the start line and marker of the {@code TimeLineTextView} and the
     * background/text color of the {@code TextView} of the given {@code TimeLineViewHolder}.
     */
    public static void highlightAll(@NonNull final TimeLineAdapter.TimeLineViewHolder viewHolder) {
        final int lineColor = viewHolder.itemView.getContext().getResources().getColor(R.color.timeline_active_line_color);
        final int backgroundColor = viewHolder.itemView.getContext().getResources().getColor(R.color.selected_background_color);
        final int textColor = viewHolder.itemView.getContext().getResources().getColor(R.color.selected_text_color);

        final Drawable marker = AppCompatResources.getDrawable(viewHolder.itemView.getContext(), R.drawable.marker_timeline_active);

        viewHolder.timelineView.setStartLineColor(lineColor, viewHolder.getItemViewType());
        viewHolder.timelineView.setMarker(marker, viewHolder.getItemViewType());

        viewHolder.textView.setTextColor(textColor);

        viewHolder.cardView.setCardBackgroundColor(backgroundColor);
    }

    /**
     * Whether the current device is a phone or not.
     */
    public static boolean isPhone(@NonNull final Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) < Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * Whether the current device is in portrait mode or not.
     */
    public static boolean isPortrait(@NonNull final Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }
}
