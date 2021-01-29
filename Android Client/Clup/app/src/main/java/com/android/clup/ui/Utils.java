package com.android.clup.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LifecycleOwner;

import com.android.clup.R;
import com.android.clup.model.Preferences;
import com.github.razir.progressbutton.ButtonTextAnimatorExtensionsKt;
import com.github.razir.progressbutton.DrawableButton;
import com.github.razir.progressbutton.DrawableButtonExtensionsKt;
import com.github.razir.progressbutton.ProgressButtonHolderKt;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

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

    public static void displayConnectionErrorDialog(@NonNull final Context context) {
        new Handler(Looper.getMainLooper()).post(() ->
                new MaterialAlertDialogBuilder(context, R.style.AppTheme_Clup_RoundedAlertDialog)
                        .setTitle(R.string.title_connection_error_alert_message)
                        .setMessage(R.string.text_connection_error_alert_message)
                        .setPositiveButton(R.string.action_ok, null)
                        .create()
                        .show());
    }

    /**
     * Displays the AlertDialog through which the user can choose the desired theme.
     */
    public static void displayThemesAlertDialog(@NonNull final Context context) {
        final int selectedPosition = mapToPosition(Preferences.getTheme());

        new MaterialAlertDialogBuilder(context, R.style.AppTheme_Clup_RoundedAlertDialog)
                .setTitle(R.string.title_theme_alert)
                .setSingleChoiceItems(R.array.themes_array, selectedPosition, (dialog, which) -> {
                    final int mode = mapToTheme(which);
                    Preferences.setTheme(mode);
                    AppCompatDelegate.setDefaultNightMode(mode);
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    /**
     * Displays an AlertDialog telling the user to enable location.
     */
    public static void displayLocationErrorDialog(@NonNull final Context context) {
        new MaterialAlertDialogBuilder(context, R.style.AppTheme_Clup_RoundedAlertDialog)
                .setMessage(R.string.text_location_error_alert_message)
                .setPositiveButton(R.string.action_ok, null)
                .create()
                .show();
    }

    public static void displayReservationErrorSnackBar(@NonNull final View parent, @NonNull final View anchorView) {
        Snackbar.make(parent, R.string.reservation_error_text, Snackbar.LENGTH_LONG)
                .setAnchorView(anchorView)
                .show();
    }

    /**
     * Maps a theme-preference value to the corresponding position in the AlertDialog.
     */
    public static int mapToPosition(final int themeValue) {
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
    public static int mapToTheme(final int position) {
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
