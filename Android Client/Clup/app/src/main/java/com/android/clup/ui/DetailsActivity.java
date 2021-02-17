package com.android.clup.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.android.clup.R;
import com.android.clup.model.Reservation;
import com.android.clup.viewmodel.DetailsViewModel;

import static com.android.clup.notification.NotificationService.EXTRA_RESERVATION;

public class DetailsActivity extends AppCompatActivity {
    private DetailsViewModel viewModel;
    private Button notifyButton;

    private final View.OnClickListener notifyButtonOnClickListener = view -> this.viewModel.toggleNotificationUi(this, this.notifyButton);
    private final View.OnClickListener directionsButtonOnClickListener = view -> this.viewModel.navigateToSelectedShop(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        this.viewModel = new ViewModelProvider(this).get(DetailsViewModel.class);

        // enter the activity by clicking on the notification
        if (getIntent().hasExtra(EXTRA_RESERVATION)) {
            final Reservation reservation = getIntent().getParcelableExtra(EXTRA_RESERVATION);
            this.viewModel.setSelectedReservation(reservation);
        }

        final Toolbar toolbar = findViewById(R.id.details_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        final CardView cardView = findViewById(R.id.details_qr_card_view);
        this.viewModel.handleCardViewBackground(this, cardView);

        final int onColor = getResources().getColor(R.color.qr_code_on_color);
        final int offColor = getResources().getColor(R.color.qr_code_off_color);

        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final ImageView imageView = findViewById(R.id.qr_code_image_view);
        final Bitmap qrCode = this.viewModel.getReservationQrCode(onColor, offColor);
        imageView.setImageBitmap(qrCode);
        imageView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        final String shopName = this.viewModel.getReservationShopName();
        final TextView shopNameTextView = findViewById(R.id.shop_name_text_view);
        shopNameTextView.setText(shopName);

        final String date = this.viewModel.getReservationDate();
        final TextView dateTextView = findViewById(R.id.date_text_view);
        dateTextView.setText(date);

        final String time = this.viewModel.getReservationTime();
        final TextView timeTextView = findViewById(R.id.time_text_view);
        timeTextView.setText(time);

        final Button directionsButton = findViewById(R.id.directions_button);
        directionsButton.setOnClickListener(directionsButtonOnClickListener);

        this.notifyButton = findViewById(R.id.notify_button);
        this.viewModel.initButton(this.notifyButton);
        this.notifyButton.setOnClickListener(notifyButtonOnClickListener);
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
     * Called every time a back action is performed (both from the navigation back button and
     * from the back arrow in the toolbar).
     * <p>
     * Close the current activity and also the {@link MapActivity} that is still present in the
     * stack.
     */
    private void onBackActionPerformed() {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}