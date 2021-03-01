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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.clup.R;
import com.android.clup.adapter.OnListItemClickedCallback;
import com.android.clup.adapter.ReservationRecyclerViewAdapter;
import com.android.clup.model.Preferences;
import com.android.clup.model.Reservation;
import com.android.clup.ui.auth.AuthActivity;
import com.android.clup.viewmodel.MainViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnListItemClickedCallback {
    private MainViewModel viewModel;
    private ReservationRecyclerViewAdapter adapter;

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
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        final String friendlyMessage = getResources().getString(R.string.text_friendly_message, this.viewModel.getFriendlyName());

        final TextView friendlyNameTextView = findViewById(R.id.friendly_name_text_view);
        friendlyNameTextView.setText(friendlyMessage);

        final CardView cardView = findViewById(R.id.main_card_view);
        cardView.setBackgroundResource(R.drawable.rounded_view_background);

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

    @Override
    public void onListItemClicked(int position) {
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

        setUpRecyclerView();

        if (this.adapter != null)
            this.adapter.notifyDataSetChanged();
    }

    private void setUpRecyclerView() {
        final List<Reservation> reservations = this.viewModel.getReservations();

        final ConstraintLayout emptyLayout = findViewById(R.id.empty_layout);
        final RecyclerView recyclerView = findViewById(R.id.main_recycler_view);

        if (reservations.isEmpty()) {
            // show empty layout
            emptyLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            // show reservations
            emptyLayout.setVisibility(View.GONE);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setVisibility(View.VISIBLE);

            this.adapter = new ReservationRecyclerViewAdapter(this, reservations);
            recyclerView.setAdapter(this.adapter);
        }
    }
}