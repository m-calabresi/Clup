package com.android.clup.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.clup.R;
import com.android.clup.api.QRCodeService;
import com.android.clup.concurrent.Result;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView imageView = findViewById(R.id.imageView);

        // retrieved somewhere
        final String username = "alessandro brigandì";
        final int hours = 11;
        final String status = "todo";

        final QRCodeService qrCodeService = new QRCodeService();
        qrCodeService.generateQRCode(username, hours, status, result -> {
            if (result instanceof Result.Success) {
                final Bitmap qrCode = ((Result.Success<Bitmap>) result).data;
                imageView.setImageBitmap(qrCode);
            } else {
                // error checking
            }
        });
    }
}