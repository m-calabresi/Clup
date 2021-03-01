package com.android.clup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.android.clup.R;
import com.android.clup.adapter.SectionsPagerAdapter;
import com.android.clup.concurrent.Result;
import com.android.clup.model.Shop;
import com.android.clup.viewmodel.SelectViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SelectActivity extends AppCompatActivity {
    private SelectViewModel viewModel;

    private ExtendedFloatingActionButton doneButton;

    private final View.OnClickListener doneButtonOnClickListener = view -> {
        this.doneButton.setClickable(false);
        this.doneButton.setFocusable(false);
        Utils.startProgressBarAnimation(this.doneButton);

        this.viewModel.bookReservation(result -> {
            Utils.stopProgressBarAnimation(this.doneButton, getString(R.string.action_done));

            if (result instanceof Result.Success && ((Result.Success<Boolean>) result).data)
                switchToNextActivity();
            else
                showReservationError();
        });
    };

    private final Observer<Boolean> visibilityObserver = visible -> {
        if (visible)
            this.doneButton.show();
        else
            this.doneButton.hide();
    };

    private final ViewPager2.OnPageChangeCallback onPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(final int position) {
            super.onPageSelected(position);

            // set the selected day based on the current page
            viewModel.setSelectedDay(position);
        }

        @Override
        public void onPageScrollStateChanged(final int state) {
            super.onPageScrollStateChanged(state);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        this.viewModel = new ViewModelProvider(this).get(SelectViewModel.class);
        final Shop selectedShop = this.viewModel.getSelectedShop();

        final Toolbar toolbar = findViewById(R.id.select_toolbar);
        toolbar.setTitle(selectedShop.getName());
        setSupportActionBar(toolbar);

        final TabLayout tabLayout = findViewById(R.id.select_tab_layout);

        final ViewPager2 viewPager = findViewById(R.id.select_view_pager);
        viewPager.setAdapter(new SectionsPagerAdapter(this));
        viewPager.registerOnPageChangeCallback(this.onPageChangeCallback);

        // called for every tab during initialization, set up each tab individually
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            final String date = this.viewModel.getSelectedShop().getAvailableDays().get(position).getDate().formatted();
            tab.setText(date);
        }).attach();

        this.doneButton = findViewById(R.id.done_button);
        Utils.enableProgressBarAnimation(this.doneButton, this);
        this.doneButton.setOnClickListener(doneButtonOnClickListener);
        this.doneButton.setAnimateShowBeforeLayout(true);

        this.viewModel.getVisibilityStatusLiveData().observe(this, this.visibilityObserver);
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
        this.viewModel.resetSelectedShop();
        this.viewModel.resetSelectedDay();
        this.viewModel.resetSelectedTime();

        final Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
    }
}