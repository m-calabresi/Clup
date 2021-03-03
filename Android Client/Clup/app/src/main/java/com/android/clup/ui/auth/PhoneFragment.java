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
import com.android.clup.ui.Utils;
import com.android.clup.viewmodel.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class PhoneFragment extends Fragment {
    @Nullable
    private AuthViewModel viewModel;

    @Nullable
    private Button nextButton;
    @Nullable
    private TextInputLayout phoneNumberTextInput;
    @Nullable
    private TextInputEditText prefixEditText;
    @Nullable
    private TextInputEditText phoneNumberEditText;

    @NonNull
    private final View.OnClickListener nextOnClickListener = view -> {
        Utils.hideSoftInput(requireActivity());
        Utils.startProgressBarAnimation(this.nextButton);

        this.viewModel.isConnectionAvailable(connectionResult -> {
            if (connectionResult instanceof Result.Success) {
                final boolean connectionAvailable = ((Result.Success<Boolean>) connectionResult).data;

                if (connectionAvailable) {
                    final String prefix = Objects.requireNonNull(this.prefixEditText.getText()).toString();
                    final String phoneNumber = Objects.requireNonNull(this.phoneNumberEditText.getText()).toString();
                    this.viewModel.setPhoneNumber(prefix + phoneNumber);

                    final String locale = this.viewModel.toLocale(Integer.parseInt(prefix));
                    this.viewModel.setLocale(locale);

                    this.viewModel.startVerify(verificationResult -> {
                        Utils.stopProgressBarAnimation(this.nextButton, getString(R.string.action_next));

                        if (verificationResult instanceof Result.Success) {
                            switchToNextFragment();
                        } else {
                            showErrorHint();
                            Utils.showSoftInput(requireActivity(), this.phoneNumberEditText);
                        }
                    });
                } else {
                    Utils.displayConnectionErrorDialog(requireContext());
                    Utils.stopProgressBarAnimation(this.nextButton, getString(R.string.action_next));
                }
            } else {
                Utils.displayConnectionErrorDialog(requireContext());
                Utils.stopProgressBarAnimation(this.nextButton, getString(R.string.action_next));
            }
        });
    };

    @NonNull
    private final Observer<Boolean> nextButtonObserver = buttonVisibility ->
            this.viewModel.handleInvisible(this.nextButton, buttonVisibility);

    @NonNull
    private final TextWatcher prefixTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(@NonNull final CharSequence s, final int start, final int count, final int after) {

        }

        @Override
        public void onTextChanged(@NonNull final CharSequence s, final int start, final int before, final int count) {
            viewModel.setPrefixStatus(s.length() > 0);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @NonNull
    private final TextWatcher phoneNumberTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(@NonNull final CharSequence s, final int start, final int count, final int after) {

        }

        @Override
        public void onTextChanged(@NonNull final CharSequence s, final int start, final int before, final int count) {
            final boolean textFilled = s.length() > 0;

            if (textFilled)
                phoneNumberTextInput.setError(null);

            viewModel.setPhoneNumberStatus(textFilled);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @SuppressWarnings({"unused", "RedundantSuppression"})
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
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_phone, container, false);

        Objects.requireNonNull(this.viewModel).getPhoneFragmentButtonVisibilityStatus().observe(getViewLifecycleOwner(), nextButtonObserver);

        this.nextButton = root.findViewById(R.id.next_button);
        this.nextButton.setOnClickListener(this.nextOnClickListener);
        Utils.enableProgressBarAnimation(this.nextButton, getViewLifecycleOwner());

        final TextInputLayout prefixTextInput = root.findViewById(R.id.prefix_text_input);
        this.prefixEditText = prefixTextInput.findViewById(R.id.prefix_edit_text);
        this.prefixEditText.addTextChangedListener(this.prefixTextWatcher);

        final int defaultCountryCode = this.viewModel.getDefaultCountryCode(requireActivity());
        this.prefixEditText.setText(String.valueOf(defaultCountryCode));

        this.phoneNumberTextInput = root.findViewById(R.id.phone_text_input);
        this.phoneNumberEditText = this.phoneNumberTextInput.findViewById(R.id.phone_edit_text);
        this.phoneNumberEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        this.phoneNumberEditText.addTextChangedListener(this.phoneNumberTextWatcher);
        Utils.showSoftInput(requireActivity(), this.phoneNumberEditText);

        return root;
    }

    @Override
    public void onResume() {
        Utils.showSoftInput(requireActivity(), Objects.requireNonNull(this.phoneNumberEditText));
        super.onResume();
    }

    @Override
    public void onPause() {
        Utils.hideSoftInput(requireActivity());
        super.onPause();
    }

    /**
     * Show an error hint associated to the textview the user is typing in.
     */
    private void showErrorHint() {
        Objects.requireNonNull(this.phoneNumberTextInput).post(() -> {
            final String errorMessage = getString(R.string.text_error_phone);
            this.phoneNumberTextInput.setError(errorMessage);
        });
    }

    /**
     * Switch to the next fragment.
     */
    private void switchToNextFragment() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Objects.requireNonNull(this.nextButton).setVisibility(View.INVISIBLE);
            Objects.requireNonNull(this.viewModel).switchTo(CodeFragment.class);
        });
    }
}
