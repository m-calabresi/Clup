package com.android.clup;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.util.Objects;

public class PhoneInputActivity extends AppCompatActivity {
    @NonNull
    public static final String EXTRA_COMPLETE_PHONE_NUMBER = "com.android.clup.completePhoneNumber";

    private TextInputEditText prefixEditText;
    private TextInputEditText phoneNumberEditText;

    private Button continueButton;

    private boolean prefixFilled;
    private boolean phoneNumberFilled;

    private final View.OnClickListener continueOnClickListener = view -> {
        final String prefix = Objects.requireNonNull(prefixEditText.getText()).toString();
        final String phoneNumber = clearNumber(Objects.requireNonNull(Objects.requireNonNull(phoneNumberEditText.getText()).toString()));

        final String completePhoneNumber = getString(R.string.text_prefix) + prefix + phoneNumber;

        // TODO replace with verification procedure

        final Intent intent = new Intent(this, VerifyCodeActivity.class);
        intent.putExtra(EXTRA_COMPLETE_PHONE_NUMBER, completePhoneNumber);
        startActivity(intent);
        finish();
        overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_left);
    };

    private final TextWatcher prefixTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            prefixFilled = s.length() > 0;
            setContinueButtonVisibility();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private final TextWatcher phoneNumberTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            phoneNumberFilled = s.length() > 0;
            setContinueButtonVisibility();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_input);

        continueButton = findViewById(R.id.continue_button);
        continueButton.setOnClickListener(continueOnClickListener);

        final TextInputLayout prefixTextInput = findViewById(R.id.prefix_text_input);
        prefixEditText = prefixTextInput.findViewById(R.id.prefix_edit_text);
        prefixEditText.addTextChangedListener(prefixTextWatcher);

        final TextInputLayout phoneNumberTextInput = findViewById(R.id.auth_code_text_input);
        phoneNumberEditText = phoneNumberTextInput.findViewById(R.id.phone_number_edit_text);
        phoneNumberEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        phoneNumberEditText.addTextChangedListener(phoneNumberTextWatcher);

        init();
    }

    private void init() {
        prefixFilled = false;
        phoneNumberFilled = false;

        final String defaultPrefix = getDefaultPrefix();

        if (defaultPrefix == null) {
            prefixEditText.requestFocus();
        } else {
            prefixEditText.setText(defaultPrefix);
            phoneNumberEditText.requestFocus();
        }
    }

    @Nullable
    private String getDefaultPrefix() {
        final String locale = getResources().getConfiguration().locale.getCountry();

        if (locale.isEmpty())
            return null;

        final int regionCode = PhoneNumberUtil.getInstance().getCountryCodeForRegion(locale);
        return String.valueOf(regionCode);
    }

    @NonNull
    private String clearNumber(@NonNull final String phoneNumber) {
        // remove all spaces and non-numerical characters
        return phoneNumber.replaceAll("[^\\d]", "");
    }

    private void setContinueButtonVisibility() {
        if (prefixFilled && phoneNumberFilled) {
            if (continueButton.getVisibility() != View.VISIBLE)
                continueButton.setVisibility(View.VISIBLE);
        } else {
            if (continueButton.getVisibility() != View.INVISIBLE)
                continueButton.setVisibility(View.INVISIBLE);
        }
    }
}