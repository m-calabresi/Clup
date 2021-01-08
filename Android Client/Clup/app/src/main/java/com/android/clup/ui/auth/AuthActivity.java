package com.android.clup.ui.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.android.clup.R;
import com.android.clup.viewmodel.AuthViewModel;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    private void replaceFragment(@NonNull final Class<? extends Fragment> fragment) {
        try {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_enter, R.anim.slide_exit)
                    .replace(R.id.container, fragment.newInstance())
                    .commitNow();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}