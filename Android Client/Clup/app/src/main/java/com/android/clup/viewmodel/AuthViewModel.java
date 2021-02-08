package com.android.clup.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.clup.api.RemoteConnection;
import com.android.clup.api.SMSAuthService;
import com.android.clup.concurrent.Callback;
import com.android.clup.json.JsonParser;
import com.android.clup.model.Model;
import com.android.clup.model.Preferences;
import com.android.clup.notification.NotificationService;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

public class AuthViewModel extends ViewModel {
    @NonNull
    private final Model model;

    @NonNull
    private final MutableLiveData<Class<? extends Fragment>> nextFragmentLiveData;

    private final SMSAuthService authService;

    // NameFragment fields
    @NonNull
    private final MutableLiveData<Boolean> nameFragmentButtonVisibilityStatus;

    // PhoneFragment fields
    private String phoneNumber; // phone name of the user in format [prefix without '+'][phone number]
    private String locale; // language chosen by the user in form [\c\c] (eg. US)

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

    // SuccessFragment fields
    public static final int TRANSITION_DELAY = 1500;

    public AuthViewModel() {
        this.model = Model.getInstance();

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
        this.model.setFullname(username);
        this.model.setFriendlyName(username.split(" ")[0]);
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
     * A username is valid only if:
     * <p>
     * ^                start of line
     * [a-zA-Z -]{2,}   will except a name with at least two characters including -
     * \s               will look for white space between name and surname
     * [a-zA-Z]+        needs at least 1 character
     * \'?-?            possibility of **'** or **-** for double barreled and hyphenated surnames
     * [a-zA-Z]{2,}     will except a name with at least two characters
     * \s?              possibility of another whitespace
     * ([a-zA-Z]+)?     possibility of a second surname
     */
    public boolean isValidUsername(@NonNull final String username) {
        return username.trim().matches("^([a-zA-Z -]{2,}\\s[a-zA-z]+'?-?[a-zA-Z]{2,}\\s?([a-zA-Z]+)?)");
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
     * Finalize the authentication by preparing the environment for the MainActivity.
     * In particular, initialize the json file that will store reservations and set the default app theme.
     */
    public void finalizeAuth(@NonNull final Context context) {
        // disable AuthActivity: first time has been executed
        Preferences.setFirstTime(false);

        // initialize reservations file
        JsonParser.initReservationsFile();

        // initialize theme preference
        Preferences.setTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        // initialize notification channel
        NotificationService.createNotificationChannel(context);
    }

    /**
     * Return the last theme selected by the user or the default one (if user didn't set any theme).
     */
    public int getTheme() {
        return Preferences.getTheme();
    }
}
