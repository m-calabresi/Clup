package com.android.clup.ui.auth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.clup.R;
import com.android.clup.concurrent.Result;
import com.android.clup.viewmodel.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class PhoneFragment extends Fragment {
    private AuthViewModel viewModel;

    private Button nextButton;
    private TextInputLayout phoneNumberTextInput;
    private TextInputEditText prefixEditText;
    private TextInputEditText phoneNumberEditText;

    @NonNull
    private final View.OnClickListener nextOnClickListener = view -> {
        this.viewModel.hideSoftInput(requireActivity());
        this.viewModel.showProgressBar(this.nextButton);

        final String prefix = Objects.requireNonNull(this.prefixEditText.getText()).toString();
        final String phoneNumber = Objects.requireNonNull(this.phoneNumberEditText.getText()).toString();
        this.viewModel.setPhoneNumber(prefix + phoneNumber);

        final String locale = this.viewModel.toLocale(Integer.parseInt(prefix));
        this.viewModel.setLocale(locale);

        this.viewModel.startVerify(result -> {
            this.viewModel.hideProgressBar(this.nextButton, getString(R.string.action_next));

            if (result instanceof Result.Success) {
                // final String message = ((Result.Success<String>) result).data;
                switchToNextFragment();
            } else {
                // final String error = ((Result.Error<String>) result).message;
                showErrorHint();
                this.viewModel.showSoftInput(requireActivity(), this.phoneNumberEditText);
            }
        });
    };

    @NonNull
    private final Observer<Boolean> nextButtonObserver = buttonVisibility ->
            this.viewModel.handleInvisible(this.nextButton, buttonVisibility);

    @NonNull
    private final TextWatcher prefixTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(@NonNull CharSequence s, int start, int before, int count) {
            viewModel.setPrefixStatus(s.length() > 0);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @NonNull
    private final TextWatcher phoneNumberTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(@NonNull CharSequence s, int start, int before, int count) {
            final boolean textFilled = s.length() > 0;

            if (textFilled)
                phoneNumberTextInput.setError(null);

            viewModel.setPhoneNumberStatus(textFilled);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @NonNull
    public static PhoneFragment newInstance() {
        return new PhoneFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        this.viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_phone, container, false);

        this.viewModel.getPhoneFragmentButtonVisibilityStatus().observe(getViewLifecycleOwner(), nextButtonObserver);

        this.nextButton = root.findViewById(R.id.next_button);
        this.nextButton.setOnClickListener(this.nextOnClickListener);
        this.viewModel.enableProgressButton(this.nextButton, getViewLifecycleOwner());

        final TextInputLayout prefixTextInput = root.findViewById(R.id.prefix_text_input);
        this.prefixEditText = prefixTextInput.findViewById(R.id.prefix_edit_text);
        this.prefixEditText.addTextChangedListener(this.prefixTextWatcher);

        final int defaultCountryCode = this.viewModel.getDefaultCountryCode(requireActivity());
        this.prefixEditText.setText(String.valueOf(defaultCountryCode));

        this.phoneNumberTextInput = root.findViewById(R.id.phone_text_input);
        this.phoneNumberEditText = this.phoneNumberTextInput.findViewById(R.id.phone_edit_text);
        this.phoneNumberEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        this.phoneNumberEditText.addTextChangedListener(this.phoneNumberTextWatcher);
        this.viewModel.showSoftInput(requireActivity(), this.phoneNumberEditText);

        return root;
    }

    @Override
    public void onResume() {
        this.viewModel.showSoftInput(requireActivity(), this.phoneNumberEditText);
        super.onResume();
    }

    @Override
    public void onPause() {
        this.viewModel.hideSoftInput(requireActivity());
        super.onPause();
    }

    private void showErrorHint() {
        runOnUIThread(() -> {
            final String errorMessage = getString(R.string.phone_error);
            phoneNumberTextInput.setError(errorMessage);
        });
    }

    private void switchToNextFragment() {
        runOnUIThread(() -> {
            this.nextButton.setVisibility(View.INVISIBLE);
            this.viewModel.switchTo(CodeFragment.class);
        });
    }

    private void runOnUIThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }
}
