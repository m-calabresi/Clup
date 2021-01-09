package com.android.clup.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.clup.R;
import com.android.clup.api.QRCodeService;
import com.android.clup.concurrent.Result;

import net.glxn.qrgen.android.QRCode;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView imageView = findViewById(R.id.imageView);

        // retrieved somewhere
        /*final String username = new String("alessandro brigandì".getBytes(), StandardCharsets.UTF_8);
        System.out.println(username);
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
        });*/

        final String uuid = "23457-898-764-3457";
        final Bitmap bitmap = QRCode.from(uuid).withSize(1024, 1024).bitmap();
        imageView.setImageBitmap(bitmap);
    }
}