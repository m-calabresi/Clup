package com.android.clup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.android.clup.R;
import com.android.clup.model.Preferences;
import com.android.clup.ui.auth.AuthActivity;
import com.android.clup.viewmodel.MainViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel;

    private final View.OnClickListener bookButtonOnClickListener = view -> {
        final Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Clup_Main);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check first time procedure
        if (Preferences.isFirstTime()) {
            final Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
            finish();
        }

        this.viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        AppCompatDelegate.setDefaultNightMode(Preferences.getTheme());

        final Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        final ExtendedFloatingActionButton bookButton = findViewById(R.id.book_button);
        bookButton.setOnClickListener(bookButtonOnClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == R.id.action_settings_theme) {
            Utils.displayThemesAlertDialog(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}