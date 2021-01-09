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
import androidx.fragment.app.Fragment;

import com.android.clup.R;
import com.android.clup.ui.MainActivity;

public class SuccessFragment extends Fragment {
    private static final int TRANSITION_DELAY = 1000;

    @NonNull
    public static NameFragment newInstance() {
        return new NameFragment();
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

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_success, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            final Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }, TRANSITION_DELAY);
        super.onViewCreated(view, savedInstanceState);
    }
}
