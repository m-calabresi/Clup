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

    private final View.OnClickListener nextButtonOnClickListener = view -> {
        this.viewModel.hideSoftInput(requireActivity());
        this.viewModel.showProgressBar(this.verifyButton);

        final String code = Objects.requireNonNull(this.codeEditText.getText()).toString();

        this.viewModel.checkVerify(code, result -> {
            this.viewModel.hideSoftInput(requireActivity());
            this.viewModel.hideProgressBar(this.verifyButton, getString(R.string.action_verify));

            if (result instanceof Result.Success) {
                // final String message = ((Result.Success<String>) result).data;
                switchToNextFragment();
            } else {
                // final String error = ((Result.Error<String>) result).message;
                showErrorHint();
            }
        });
    };

    private final Observer<Boolean> verifyButtonObserver = status ->
            this.viewModel.handleInvisible(this.verifyButton, status);

    private final TextWatcher codeTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            viewModel.setCodeFragmentButtonVisibilityStatus(s.length() > 0);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

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
        this.countDownTimer = new CountDownTimer(AuthViewModel.countDown, AuthViewModel.interval) {
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_code, container, false);

        this.viewModel.getCodeFragmentButtonVisibilityStatus().observe(getViewLifecycleOwner(), this.verifyButtonObserver);

        this.retryInText = getString(R.string.action_retry_in);

        final TextView messageTextView = root.findViewById(R.id.text_code);
        final String message = getString(R.string.text_verify_code, this.viewModel.getPhoneNumber());
        messageTextView.setText(message);

        this.verifyButton = root.findViewById(R.id.verify_button);
        this.verifyButton.setOnClickListener(this.nextButtonOnClickListener);
        this.viewModel.enableProgressButton(this.verifyButton, getViewLifecycleOwner());

        this.retryButton = root.findViewById(R.id.retry_button);
        this.retryButton.setOnClickListener(this.retryButtonOnClickListener);

        this.codeInputLayout = root.findViewById(R.id.code_text_input);
        this.codeEditText = this.codeInputLayout.findViewById(R.id.code_edit_text);
        this.codeEditText.addTextChangedListener(this.codeTextWatcher);
        this.viewModel.showSoftInput(requireActivity(), this.codeEditText);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.countDownTimer.start();
        super.onViewCreated(view, savedInstanceState);
    }

    private void showErrorHint() {
        runOnUIThread(() -> {
            final String errorMessage = getString(R.string.code_error);
            this.codeInputLayout.setError(errorMessage);
        });
    }

    private void switchToNextFragment() {
        runOnUIThread(() -> {
            this.verifyButton.setVisibility(View.INVISIBLE);
            this.viewModel.switchTo(SuccessFragment.class);

            // TODO after this, username, phoneNumber and locale stored in viewModel will be lost, consider saving to local storage/cloud storage
        });
    }

    private void runOnUIThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }
}
