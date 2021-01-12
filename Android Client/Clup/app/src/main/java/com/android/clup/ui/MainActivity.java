package com.android.clup.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.android.clup.R;
import com.android.clup.api.QRCodeService;
import com.android.clup.concurrent.Result;
import com.android.clup.viewmodel.MainViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Clup);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == R.id.action_settings_theme) {
            final int itemPosition = this.viewModel.mapToPosition(this.viewModel.getThemePreference(this));
            displayThemesAlertDialog(itemPosition);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayThemesAlertDialog(final int selectedPosition) {
        new MaterialAlertDialogBuilder(this, R.style.AppTheme_RoundedAlertDialog)
                .setTitle(R.string.title_theme_alert)
                .setSingleChoiceItems(R.array.themes_array, selectedPosition, (dialog, which) -> {
                    final int mode = this.viewModel.mapToTheme(which);
                    this.viewModel.setThemePreference(this, mode);
                    dialog.dismiss();
                })
                .create()
                .show();
    }
}