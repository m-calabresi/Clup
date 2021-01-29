package com.android.clup.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.android.clup.R;
import com.android.clup.viewmodel.DetailsViewModel;

public class DetailsActivity extends AppCompatActivity {
    private DetailsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final ImageView imageView = findViewById(R.id.imageView);

        final int onColor = getResources().getColor(R.color.qr_code_on_color);
        final int offColor = getResources().getColor(R.color.qr_code_off_color);

        this.viewModel = new ViewModelProvider(this).get(DetailsViewModel.class);
        final Bitmap qrCode = this.viewModel.generateQRCode(onColor, offColor);

        progressBar.setVisibility(View.GONE);
        imageView.setImageBitmap(qrCode);
    }
}