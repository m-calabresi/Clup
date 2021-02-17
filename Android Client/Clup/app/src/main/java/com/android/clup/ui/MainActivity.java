package com.android.clup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.clup.R;
import com.android.clup.adapter.OnRecyclerViewItemClickedCallback;
import com.android.clup.adapter.ReservationRecyclerViewAdapter;
import com.android.clup.model.Preferences;
import com.android.clup.ui.auth.AuthActivity;
import com.android.clup.viewmodel.MainViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class MainActivity extends AppCompatActivity implements OnRecyclerViewItemClickedCallback {
    private MainViewModel viewModel;
    private ReservationRecyclerViewAdapter adapter;

    private final View.OnClickListener bookButtonOnClickListener = view -> {
        final Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);

        /*
        // date: 11-03-2021, 12:00:00 expected millis: 1615460400000

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

        final int minutesDelay = 15;
        long millisDelay = minutesDelay * 60 * 1000;

        final long notificationDisplayTime = calendar.getTimeInMillis() - millisDelay;

        // same with custom date
        Date date = Date.fromString("11-03-2021");
        date.setTime("12:00");

        final long notificationDateDisplayTime = date.toMillis() - millisDelay;

        Log.i("AAA", "expected date: " + calendar.getTimeInMillis());
        Log.i("AAA", "  actual date: " + date.toMillis());

        Log.i("AAA", "expected display time: " + notificationDisplayTime);
        Log.i("AAA", "  actual display time: " + notificationDateDisplayTime);

        Log.i("AAA", "expected time: 12:00");
        Log.i("AAA", "  actual time: " + date.getTime());

        Log.i("AAA", "expected plain date: 11-03-2021");
        Log.i("AAA", "  actual plain date: " + date.plain());

        Log.i("AAA", "expected format date: Thursday, 11 march");
        Log.i("AAA", "  actual format date: " + date.formatted());
        */
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
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        final String friendlyMessage = getResources().getString(R.string.text_friendly_message, this.viewModel.getFriendlyName());

        final TextView friendlyNameTextView = findViewById(R.id.friendly_name_text_view);
        friendlyNameTextView.setText(friendlyMessage);

        final CardView cardView = findViewById(R.id.main_card_view);
        cardView.setBackgroundResource(R.drawable.rounded_view_background);

        final ExtendedFloatingActionButton bookButton = findViewById(R.id.book_button);
        bookButton.setOnClickListener(bookButtonOnClickListener);

        final RecyclerView recyclerView = findViewById(R.id.main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        this.adapter = new ReservationRecyclerViewAdapter(this, this.viewModel.getReservations());
        recyclerView.setAdapter(this.adapter);
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

    @Override
    public void onRecyclerViewItemClicked(int position) {
        this.viewModel.setSelectedReservation(position);
        startDetailsActivity();
    }

    private void startDetailsActivity() {
        final Intent intent = new Intent(this, DetailsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.adapter.notifyDataSetChanged();
    }
}