package com.android.clup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class VerifyCodeActivity extends AppCompatActivity {
    private TextView verifyTextView;
    private Button verifyButton;

    private boolean authCodeFilled;

    private final TextWatcher authCodeTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            authCodeFilled = s.length() > 0;
            setVerifyButtonVisibility();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private final View.OnClickListener verifyButtonClickListener = view -> {
        // TODO complete authentication procedure
        Toast.makeText(this, "Authentication succeeded", Toast.LENGTH_LONG).show();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        verifyTextView = findViewById(R.id.text_verify);

        verifyButton = findViewById(R.id.verify_button);
        verifyButton.setOnClickListener(verifyButtonClickListener);

        final TextInputLayout authCodeTextInput = findViewById(R.id.auth_code_text_input);
        final TextInputEditText authCodeEditText = authCodeTextInput.findViewById(R.id.phone_number_edit_text);
        authCodeEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        authCodeEditText.addTextChangedListener(authCodeTextWatcher);
        authCodeEditText.requestFocus();

        init();
    }

    private void init() {
        authCodeFilled = false;

        final String completePhoneNumber = getIntent().getStringExtra(PhoneInputActivity.EXTRA_COMPLETE_PHONE_NUMBER);
        assert completePhoneNumber != null;

        final String authMessage = getString(R.string.text_verify_code, completePhoneNumber);
        verifyTextView.setText(authMessage);
    }

    private void setVerifyButtonVisibility() {
        if (authCodeFilled) {
            if (verifyButton.getVisibility() != View.VISIBLE)
                verifyButton.setVisibility(View.VISIBLE);
        } else {
            if (verifyButton.getVisibility() != View.INVISIBLE)
                verifyButton.setVisibility(View.INVISIBLE);
        }
    }
}