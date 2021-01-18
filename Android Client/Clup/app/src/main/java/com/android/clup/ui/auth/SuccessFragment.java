package com.android.clup.ui.auth;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.clup.R;
import com.android.clup.ui.MainActivity;
import com.android.clup.viewmodel.MainViewModel;

public class SuccessFragment extends Fragment {

    @NonNull
    public static SuccessFragment newInstance() {
        return new SuccessFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // TODO BEFORE_FINAL_BUILD uncomment this
        //PackageManager packageManager = requireActivity().getPackageManager();

        // disable AuthActivity at startup
        //packageManager.setComponentEnabledSetting(new ComponentName(getActivity(), MainActivity.class),
        //        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        // enable MainActivity at startup
        //packageManager.setComponentEnabledSetting(new ComponentName(getActivity(), AuthActivity.class),
        //        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        // initiate the theme preference to follow_system (default behavior
        final MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.setThemePreference(requireContext(), AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
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
            final Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }, MainViewModel.TRANSITION_DELAY);
        super.onViewCreated(view, savedInstanceState);
    }
}
