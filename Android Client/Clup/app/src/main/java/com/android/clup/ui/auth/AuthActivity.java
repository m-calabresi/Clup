package com.android.clup.ui.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.clup.R;
import com.android.clup.viewmodel.AuthViewModel;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Clup_Main);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        final AuthViewModel viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        viewModel.getNextFragmentLiveData().observe(this, this::replaceFragment);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, NameFragment.newInstance())
                    .commitNow();
        }
    }

    private void replaceFragment(@NonNull final Class<? extends Fragment> fragmentClass) {
        for (Fragment f : getSupportFragmentManager().getFragments()) {
            if (f.getClass().equals(fragmentClass)) {
                // f is the fragment already inserted (no need to crete a new one)
                replace(f);
                return;
            }
        }

        // no fragment found (need to create a new one)
        try {
            replace(fragmentClass.newInstance());
        } catch (@NonNull final IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private void replace(@NonNull final Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_enter, R.anim.slide_exit)
                .replace(R.id.container, fragment)
                .commitNow();
    }
}