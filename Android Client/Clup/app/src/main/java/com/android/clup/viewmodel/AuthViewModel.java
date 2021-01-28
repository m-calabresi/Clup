package com.android.clup.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.clup.R;
import com.android.clup.api.RemoteConnection;
import com.android.clup.api.SMSAuthService;
import com.android.clup.concurrent.Callback;
import com.github.razir.progressbutton.ButtonTextAnimatorExtensionsKt;
import com.github.razir.progressbutton.DrawableButton;
import com.github.razir.progressbutton.DrawableButtonExtensionsKt;
import com.github.razir.progressbutton.ProgressButtonHolderKt;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import kotlin.Unit;

public class AuthViewModel extends ViewModel {
    @NonNull
    private final MutableLiveData<Class<? extends Fragment>> nextFragmentLiveData;

    private final SMSAuthService authService;

    private String username; // complete name of the user
    private String phoneNumber; // phone name of the user in format [prefix without '+'][phone number]
    private String locale; // language chosen by the user in form [\c\c] (eg. US)

    // NameFragment fields
    @NonNull
    private final MutableLiveData<Boolean> nameFragmentButtonVisibilityStatus;

    // PhoneFragment fields
    @NonNull
    private final MutableLiveData<Boolean> prefixStatus;
    @NonNull
    private final MutableLiveData<Boolean> phoneNumberStatus;
    @NonNull
    private final MediatorLiveData<Boolean> phoneFragmentButtonVisibilityStatus;

    // CodeFragment fields
    @NonNull
    private final MutableLiveData<Boolean> codeFragmentButtonVisibilityStatus;
    public static final long COUNTDOWN_START = 10000;
    public static final long INTERVAL = 1000;

    public AuthViewModel() {
        this.nextFragmentLiveData = new MutableLiveData<>();
        this.authService = SMSAuthService.getInstance();

        this.nameFragmentButtonVisibilityStatus = new MutableLiveData<>(false);

        this.prefixStatus = new MutableLiveData<>(false);
        this.phoneNumberStatus = new MutableLiveData<>(false);
        this.phoneFragmentButtonVisibilityStatus = new MediatorLiveData<>();

        // button in PhoneFragment should be visible only if both prefix and phone-number fields are filled
        this.phoneFragmentButtonVisibilityStatus.addSource(this.prefixStatus, status -> {
            if (this.phoneNumberStatus.getValue() != null)
                this.phoneFragmentButtonVisibilityStatus.setValue(status && this.phoneNumberStatus.getValue());
        });
        this.phoneFragmentButtonVisibilityStatus.addSource(this.phoneNumberStatus, status -> {
            if (this.prefixStatus.getValue() != null)
                this.phoneFragmentButtonVisibilityStatus.setValue(status && this.prefixStatus.getValue());
        });

        this.codeFragmentButtonVisibilityStatus = new MutableLiveData<>(false);
    }

    /**
     * Returns the LiveData that observes the next fragment change.
     */
    @NonNull
    public MutableLiveData<Class<? extends Fragment>> getNextFragmentLiveData() {
        return this.nextFragmentLiveData;
    }

    /**
     * Switch from the current Fragment to the given one.
     */
    public void switchTo(@NonNull final Class<? extends Fragment> fragmentClass) {
        this.nextFragmentLiveData.setValue(fragmentClass);
    }

    public void setUsername(@NonNull final String username) {
        this.username = username;
    }

    @Nullable
    public String getUsername() {
        return this.username;
    }

    public void setPhoneNumber(@NonNull final String phoneNumber) {
        this.phoneNumber = clearNumber(phoneNumber);
    }

    @Nullable
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setLocale(@NonNull final String locale) {
        this.locale = locale;
    }

    /**
     * Check the given username to validate it.
     */
    public boolean isValidUsername(@NonNull final String username) {
        return username.trim().contains(" ");
    }

    /**
     * Returns the locale associated to the given country-code.
     */
    @NonNull
    public String toLocale(int countryCode) {
        String locale = PhoneNumberUtil.getInstance().getRegionCodeForCountryCode(countryCode);

        if (locale == null || locale.isEmpty() || locale.equalsIgnoreCase("zz"))
            locale = SMSAuthService.DEFAULT_LOCALE;
        return locale;
    }

    @Nullable
    public String getLocale() {
        return this.locale;
    }

    /**
     * Whether the button in NameFragment should be visible or not.
     */
    public void setNameFragmentButtonVisibilityStatus(final boolean status) {
        this.nameFragmentButtonVisibilityStatus.setValue(status);
    }

    /**
     * Get the NameFragment button visibility.
     */
    @NonNull
    public MutableLiveData<Boolean> getNameFragmentButtonVisibilityStatus() {
        return this.nameFragmentButtonVisibilityStatus;
    }

    /**
     * Whether the prefix field in PhoneFragment is filled or not.
     */
    public void setPrefixStatus(final boolean status) {
        this.prefixStatus.setValue(status);
    }

    /**
     * Whether the phone-number field in PhoneFragment is filled or not.
     */
    public void setPhoneNumberStatus(final boolean status) {
        this.phoneNumberStatus.setValue(status);
    }

    /**
     * Get the PhoneFragment button visibility.
     */
    @NonNull
    public MediatorLiveData<Boolean> getPhoneFragmentButtonVisibilityStatus() {
        return this.phoneFragmentButtonVisibilityStatus;
    }

    /**
     * Whether the button in CodeFragment should be visible or not.
     */
    public void setCodeFragmentButtonVisibilityStatus(final boolean status) {
        this.codeFragmentButtonVisibilityStatus.setValue(status);
    }

    /**
     * Get the CodeFragment button visibility.
     */
    @NonNull
    public MutableLiveData<Boolean> getCodeFragmentButtonVisibilityStatus() {
        return this.codeFragmentButtonVisibilityStatus;
    }

    /**
     * Check whether the device has access to the Internet or not.
     */
    public void isConnectionAvailable(@NonNull final Callback<Boolean> callback) {
        RemoteConnection.hasInternetAccess(callback);
    }

    public void displayConnectionErrorDialog(@NonNull final Context context) {
        new Handler(Looper.getMainLooper()).post(() ->
                new MaterialAlertDialogBuilder(context, R.style.AppTheme_Clup_RoundedAlertDialog)
                        .setTitle(R.string.title_connection_error_alert_message)
                        .setMessage(R.string.text_connection_error_alert_message)
                        .setPositiveButton(R.string.action_ok, null)
                        .create()
                        .show());
    }

    /**
     * Start the phone-number verification procedure, notify the user once it has finished.
     * After this method has been called, if the procedure is successful, the authentication SMS
     * should appear on the user device.
     */
    public void startVerify(@NonNull final Callback<String> callback) {
        if (this.phoneNumber == null || this.phoneNumber.isEmpty())
            throw new NullPointerException("'phoneNumber' is null or empty, did you call 'setPhoneNumber'?");
        if (this.locale == null || this.locale.isEmpty())
            throw new NullPointerException("'locale' is null or empty, did you call 'setLocale'?");

        authService.startVerify(this.phoneNumber, this.locale, callback);
    }

    /**
     * End the phone-number verification procedure, notify the user once it has finished.
     * After this method has been called, if the procedure is successful, the user device should
     * be correctly authenticated.
     */
    public void checkVerify(@NonNull final String code, @NonNull final Callback<String> callback) {
        this.authService.checkVerify(this.phoneNumber, code, callback);
    }

    /**
     * Returns the country-code associated to the user device.
     */
    public int getDefaultCountryCode(@NonNull final Activity activity) {
        String locale = activity.getResources().getConfiguration().locale.getCountry();

        if (locale.isEmpty())
            locale = SMSAuthService.DEFAULT_LOCALE.toUpperCase();

        return PhoneNumberUtil.getInstance().getCountryCodeForRegion(locale);
    }

    /**
     * Remove all spaces and non-numerical characters from the given phone-number.
     */
    @NonNull
    private String clearNumber(@NonNull final String phoneNumber) {
        return phoneNumber.replaceAll("[^\\d]", "");
    }

    /**
     * Handle the visibility of the given component.
     */
    public void handleInvisible(@NonNull final View view, boolean visibility) {
        if (visibility)
            view.setVisibility(View.VISIBLE);
        else
            view.setVisibility(View.INVISIBLE);
    }

    /**
     * Handle the visibility of the given component. If necessary the view is destroyed.
     */
    public void handleGone(@NonNull final View view, boolean visibility) {
        if (visibility)
            view.setVisibility(View.VISIBLE);
        else
            view.setVisibility(View.GONE);
    }

    /**
     * Hide the soft-input from the user device.
     */
    public void hideSoftInput(@NonNull final Activity activity) {
        // if keyboard is still open
        if (activity.getCurrentFocus() != null) {
            final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Shows the soft-input on the user device.
     */
    public void showSoftInput(@NonNull final Activity activity, @NonNull final View view) {
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
    public void enableProgressButton(@NonNull final Button button, @NonNull final LifecycleOwner lifecycleOwner) {
        ProgressButtonHolderKt.bindProgressButton(lifecycleOwner, button);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(button);
    }

    /**
     * Shows the progress bar animation on the given button upon clicked.
     * Must be called after {@link #enableProgressButton(Button, LifecycleOwner)}
     */
    public void showProgressBar(@NonNull final Button button) {
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
    public void hideProgressBar(@NonNull final Button button, @NonNull final String newButtonText) {
        // stop spinning animation
        new Handler(Looper.getMainLooper()).post(() -> {
            DrawableButtonExtensionsKt.hideProgress(button, newButtonText);
            button.setClickable(true);
        });
    }
}
