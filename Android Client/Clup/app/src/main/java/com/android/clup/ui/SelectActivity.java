package com.android.clup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.clup.R;
import com.android.clup.adapter.DayRecyclerViewAdapter;
import com.android.clup.concurrent.Result;
import com.android.clup.model.Shop;
import com.android.clup.viewmodel.SelectViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class SelectActivity extends AppCompatActivity {
    private SelectViewModel viewModel;

    private ExtendedFloatingActionButton doneButton;

    private final View.OnClickListener doneButtonOnClickListener = view -> {
        this.doneButton.setClickable(false);
        this.doneButton.setFocusable(false);
        Utils.showProgressBar(this.doneButton);

        this.viewModel.bookReservation(result -> {
            Utils.hideProgressBar(this.doneButton, getString(R.string.action_done));

            if (result instanceof Result.Success && ((Result.Success<Boolean>) result).data)
                switchToNextActivity();
            else
                showReservationError();
        });
    };
    private final Observer<Integer> doneButtonGroupIdLiveDataObserver = groupTag -> doneButton.setVisibility(View.VISIBLE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        this.viewModel = new ViewModelProvider(this).get(SelectViewModel.class);
        final Shop selectedShop = this.viewModel.getSelectedShop();

        final Toolbar toolbar = findViewById(R.id.select_toolbar);
        toolbar.setTitle(selectedShop.getName());
        setSupportActionBar(toolbar);

        final CardView cardView = findViewById(R.id.select_card_view);
        cardView.setBackgroundResource(R.drawable.rounded_view_background);

        final RecyclerView recyclerView = findViewById(R.id.select_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final DayRecyclerViewAdapter adapter = new DayRecyclerViewAdapter(this, this.viewModel);
        recyclerView.setAdapter(adapter);

        this.doneButton = findViewById(R.id.done_button);
        Utils.enableProgressButton(this.doneButton, this);
        this.doneButton.setOnClickListener(doneButtonOnClickListener);
        this.viewModel.getGroupIdLiveData().observe(this, this.doneButtonGroupIdLiveDataObserver);
    }

    /**
     * Switch to the next Activity.
     */
    private void switchToNextActivity() {
        new Handler(Looper.getMainLooper()).post(() -> {
            final Intent intent = new Intent(this, DetailsActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Shows an error snackbar.
     */
    private void showReservationError() {
        this.doneButton.post(() -> {
            this.doneButton.setClickable(true);
            this.doneButton.setFocusable(true);
            Utils.displayReservationErrorSnackBar(findViewById(R.id.layout_activity_select), this.doneButton);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.onBackActionPerformed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.onBackActionPerformed();
        this.finish();
        return true;
    }

    /**
     * Called every time a back action is performed (both from the navigation back button and from the back
     * arrow in the toolbar).
     * <p>
     * Close the current activity and go back to the previous one.
     */
    private void onBackActionPerformed() {
        this.viewModel.resetSelectedShopPosition();
        this.viewModel.resetSelectedDayPosition();
        this.viewModel.resetSelectedHourPosition();

        final Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
    }
}