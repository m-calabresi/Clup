package com.android.clup.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.android.clup.R;
import com.android.clup.api.QRCodeService;
import com.android.clup.concurrent.Result;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Clup);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView imageView = findViewById(R.id.imageView);
        final ProgressBar progressBar = findViewById(R.id.progressBar);

        // retrieved somewhere
        final String username = "alessandro brigandì";
        final String hours = "11";
        final String status = "todo";

        final QRCodeService qrCodeService = new QRCodeService();
        qrCodeService.generateQRCode(username, hours, status, result -> {
            if (result instanceof Result.Success) {
                imageView.post(() -> {
                    final Bitmap qrCode = ((Result.Success<Bitmap>) result).data;
                    imageView.setImageBitmap(qrCode);
                    imageView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                });
            } else {
                // error checking
            }
        });
    }
}