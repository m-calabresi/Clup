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
import com.android.clup.api.SMSAuthService;
import com.android.clup.concurrent.Callback;
import com.github.razir.progressbutton.ButtonTextAnimatorExtensionsKt;
import com.github.razir.progressbutton.DrawableButton;
import com.github.razir.progressbutton.DrawableButtonExtensionsKt;
import com.github.razir.progressbutton.ProgressButtonHolderKt;
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

    @NonNull
    public MutableLiveData<Class<? extends Fragment>> getNextFragmentLiveData() {
        return this.nextFragmentLiveData;
    }

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

    public void setNameFragmentButtonVisibilityStatus(final boolean status) {
        this.nameFragmentButtonVisibilityStatus.setValue(status);
    }

    @NonNull
    public MutableLiveData<Boolean> getNameFragmentButtonVisibilityStatus() {
        return this.nameFragmentButtonVisibilityStatus;
    }

    public void setPrefixStatus(final boolean status) {
        this.prefixStatus.setValue(status);
    }

    public void setPhoneNumberStatus(final boolean status) {
        this.phoneNumberStatus.setValue(status);
    }

    @NonNull
    public MediatorLiveData<Boolean> getPhoneFragmentButtonVisibilityStatus() {
        return this.phoneFragmentButtonVisibilityStatus;
    }

    public void setCodeFragmentButtonVisibilityStatus(final boolean status) {
        this.codeFragmentButtonVisibilityStatus.setValue(status);
    }

    @NonNull
    public MutableLiveData<Boolean> getCodeFragmentButtonVisibilityStatus() {
        return this.codeFragmentButtonVisibilityStatus;
    }

    public void startVerify(@NonNull final Callback<String> callback) {
        if (this.phoneNumber == null || this.phoneNumber.isEmpty())
            throw new NullPointerException("'phoneNumber' is null or empty, did you call 'setPhoneNumber'?");
        if (this.locale == null || this.locale.isEmpty())
            throw new NullPointerException("'locale' is null or empty, did you call 'setLocale'?");

        authService.startVerify(this.phoneNumber, this.locale, callback);
    }

    public void checkVerify(@NonNull final String code, @NonNull final Callback<String> callback) {
        this.authService.checkVerify(this.phoneNumber, code, callback);
    }

    public int getDefaultCountryCode(@NonNull final Activity activity) {
        String locale = activity.getResources().getConfiguration().locale.getCountry();

        if (locale.isEmpty())
            locale = SMSAuthService.DEFAULT_LOCALE.toUpperCase();

        return PhoneNumberUtil.getInstance().getCountryCodeForRegion(locale);
    }

    @NonNull
    private String clearNumber(@NonNull final String phoneNumber) {
        // remove all spaces and non-numerical characters
        return phoneNumber.replaceAll("[^\\d]", "");
    }

    public void handleInvisible(@NonNull final View view, boolean visibility) {
        if (visibility) {
            if (view.getVisibility() != View.VISIBLE)
                view.setVisibility(View.VISIBLE);
        } else {
            if (view.getVisibility() != View.INVISIBLE)
                view.setVisibility(View.INVISIBLE);
        }
    }

    public void handleGone(@NonNull final View view, boolean visibility) {
        if (visibility) {
            if (view.getVisibility() != View.VISIBLE)
                view.setVisibility(View.VISIBLE);
        } else {
            if (view.getVisibility() != View.GONE)
                view.setVisibility(View.GONE);
        }
    }

    public void hideSoftInput(@NonNull final Activity activity) {
        // if keyboard is still open
        if (activity.getCurrentFocus() != null) {
            final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void showSoftInput(@NonNull final Activity activity, @NonNull final View view) {
        view.post(() -> {
            final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            view.requestFocus();
            inputMethodManager.showSoftInput(view, 0);
        });
    }

    public void enableProgressButton(@NonNull final Button button, @NonNull final LifecycleOwner lifecycleOwner) {
        ProgressButtonHolderKt.bindProgressButton(lifecycleOwner, button);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(button);
    }

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

    public void hideProgressBar(@NonNull final Button button, @NonNull final String newButtonText) {
        // stop spinning animation
        new Handler(Looper.getMainLooper()).post(() -> {
            DrawableButtonExtensionsKt.hideProgress(button, newButtonText);
            button.setClickable(true);
        });
    }
}
