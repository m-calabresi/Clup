package com.android.clup.ui.auth;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

public class CodeFragment extends Fragment {
    private AuthViewModel viewModel;

    private Button verifyButton;
    private Button retryButton;
    private TextInputEditText codeEditText;
    private TextInputLayout codeInputLayout;

    private String retryInText;
    private CountDownTimer countDownTimer;

    @NonNull
    private final View.OnClickListener nextButtonOnClickListener = view -> {
        this.retryButton.setEnabled(false);
        Utils.hideSoftInput(requireActivity());
        Utils.showProgressBar(this.verifyButton);

        final String code = Objects.requireNonNull(this.codeEditText.getText()).toString();

        this.viewModel.checkVerify(code, result -> {
            Utils.hideSoftInput(requireActivity());
            Utils.hideProgressBar(this.verifyButton, getString(R.string.action_verify));

            if (result instanceof Result.Success) {
                // final String message = ((Result.Success<String>) result).data;
                switchToNextFragment();
            } else {
                // final String error = ((Result.Error<String>) result).message;
                enableRetryButton();
                showErrorHint();
            }
        });
    };

    @NonNull
    private final Observer<Boolean> verifyButtonObserver = status ->
            this.viewModel.handleInvisible(this.verifyButton, status);

    @NonNull
    private final TextWatcher codeTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(@NonNull final CharSequence s, final int start, final int count, final int after) {

        }

        @Override
        public void onTextChanged(@NonNull final CharSequence s, final int start, final int before, final int count) {
            viewModel.setCodeFragmentButtonVisibilityStatus(s.length() > 0);
        }

        @Override
        public void afterTextChanged(@NonNull final Editable s) {

        }
    };

    @NonNull
    private final View.OnClickListener retryButtonOnClickListener = view -> {
        this.viewModel.startVerify(result -> {
            if (result instanceof Result.Error) {
                showErrorHint();
            }
        });
        this.retryButton.setEnabled(false);
        this.countDownTimer.start();
    };

    @NonNull
    public static CodeFragment newInstance() {
        return new CodeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        this.viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        this.countDownTimer = new CountDownTimer(AuthViewModel.COUNTDOWN_START, AuthViewModel.INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                final String text = retryInText + (millisUntilFinished / 1000);
                retryButton.setText(text);
            }

            @Override
            public void onFinish() {
                retryButton.setText(getString(R.string.action_retry));
                retryButton.setEnabled(true);
            }
        };
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_code, container, false);

        this.viewModel.getCodeFragmentButtonVisibilityStatus().observe(getViewLifecycleOwner(), this.verifyButtonObserver);

        this.retryInText = getString(R.string.action_retry_in);

        final TextView messageTextView = root.findViewById(R.id.text_code);
        final String message = getString(R.string.text_verify_code, this.viewModel.getPhoneNumber());
        messageTextView.setText(message);

        this.verifyButton = root.findViewById(R.id.verify_button);
        this.verifyButton.setOnClickListener(this.nextButtonOnClickListener);
        Utils.enableProgressButton(this.verifyButton, getViewLifecycleOwner());

        this.retryButton = root.findViewById(R.id.retry_button);
        this.retryButton.setOnClickListener(this.retryButtonOnClickListener);

        this.codeInputLayout = root.findViewById(R.id.code_text_input);
        this.codeEditText = this.codeInputLayout.findViewById(R.id.code_edit_text);
        this.codeEditText.addTextChangedListener(this.codeTextWatcher);
        Utils.showSoftInput(requireActivity(), this.codeEditText);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        this.countDownTimer.start();
        super.onViewCreated(view, savedInstanceState);
    }

    private void enableRetryButton() {
        this.retryButton.post(() -> this.retryButton.setEnabled(true));
    }

    private void showErrorHint() {
        this.codeInputLayout.post(() -> {
            final String errorMessage = getString(R.string.error_code);
            this.codeInputLayout.setError(errorMessage);
        });
    }

    private void switchToNextFragment() {
        new Handler(Looper.getMainLooper()).post(() -> {
            this.verifyButton.setVisibility(View.INVISIBLE);
            this.viewModel.switchTo(SuccessFragment.class);
        });
    }
}
