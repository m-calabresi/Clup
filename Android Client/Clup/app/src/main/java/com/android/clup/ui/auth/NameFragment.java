package com.android.clup.ui.auth;

import android.os.Bundle;
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
import com.android.clup.ui.Utils;
import com.android.clup.viewmodel.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class NameFragment extends Fragment {
    @Nullable
    private AuthViewModel viewModel;

    @Nullable
    private Button nextButton;
    @Nullable
    private TextInputLayout nameInputLayout;
    @Nullable
    private TextInputEditText nameEditText;

    @NonNull
    private final View.OnClickListener nextButtonClickListener = view -> {
        final String username = Objects.requireNonNull(this.nameEditText.getText()).toString();

        // check whether the user inserted at least one name and one surname separated by a space
        if (this.viewModel.isValidUsername(username)) {
            this.viewModel.setUsername(username);
            this.viewModel.switchTo(PhoneFragment.class);
        } else {
            final String errorMessage = getString(R.string.text_error_fullname);
            this.nameInputLayout.setError(errorMessage);
        }
    };

    @NonNull
    private final Observer<Boolean> nextButtonObserver = buttonVisibility ->
            this.viewModel.handleInvisible(this.nextButton, buttonVisibility);

    @NonNull
    private final TextWatcher nameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(@NonNull final CharSequence s, final int start, final int count, final int after) {

        }

        @Override
        public void onTextChanged(@NonNull final CharSequence s, final int start, final int before, final int count) {
            final boolean buttonVisible = s.length() > 0;
            final String errorMessage = buttonVisible ? null : getString(R.string.text_error_name);

            viewModel.setNameFragmentButtonVisibilityStatus(buttonVisible);
            nameInputLayout.setError(errorMessage);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @NonNull
    public static NameFragment newInstance() {
        return new NameFragment();
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

        final View root = inflater.inflate(R.layout.fragment_name, container, false);

        Objects.requireNonNull(this.viewModel).getNameFragmentButtonVisibilityStatus().observe(getViewLifecycleOwner(), nextButtonObserver);

        this.nextButton = root.findViewById(R.id.next_button);
        this.nextButton.setOnClickListener(nextButtonClickListener);

        this.nameInputLayout = root.findViewById(R.id.name_text_input);
        this.nameEditText = this.nameInputLayout.findViewById(R.id.name_edit_text);
        this.nameEditText.addTextChangedListener(nameTextWatcher);
        Utils.showSoftInput(requireActivity(), this.nameEditText);

        return root;
    }

    @Override
    public void onResume() {
        Utils.showSoftInput(requireActivity(), Objects.requireNonNull(this.nameEditText));
        super.onResume();
    }

    @Override
    public void onPause() {
        Utils.hideSoftInput(requireActivity());
        super.onPause();
    }
}
