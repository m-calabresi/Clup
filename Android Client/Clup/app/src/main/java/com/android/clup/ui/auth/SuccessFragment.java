package com.android.clup.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.clup.R;
import com.android.clup.ui.MainActivity;
import com.android.clup.viewmodel.AuthViewModel;

public class SuccessFragment extends Fragment {

    @NonNull
    public static SuccessFragment newInstance() {
        return new SuccessFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        final AuthViewModel viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        viewModel.finalizeAuth();

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_success, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            final Intent intent = new Intent(requireActivity(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }, AuthViewModel.TRANSITION_DELAY);
        super.onViewCreated(view, savedInstanceState);
    }
}
