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

        /*this.viewModel.setSelectedReservationPosition(0);
        final Intent intent = new Intent(this, DetailsActivity.class);
        startActivity(intent);*/

        // date: 11-03-2021, 12:00:00 expected millis: 1615460400000
        /*
        // THIS IS A TEST CASE
        int year = 2021;
        int month = 3;
        int day = 11;
        int hour = 12;
        int minute = 0;
        int second = 0;
        int millisecond = 0;

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, millisecond);

        Log.i("AAA", "date: " + calendar.getTimeInMillis());

        final int minutesDelay = 15;
        long millisDelay = 15 * 60 * 1000;

        Log.i("AAA", "delay: " + millisDelay);

        final long notificationDisplayTime = calendar.getTimeInMillis() - millisDelay;
        Log.i("AAA", "display time: " + notificationDisplayTime);*/
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
            this.viewModel.displayThemesAlertDialog(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}