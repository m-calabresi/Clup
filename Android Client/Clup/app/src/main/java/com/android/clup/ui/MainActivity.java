package com.android.clup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.android.clup.R;
import com.android.clup.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel;

    private final View.OnClickListener bookButtonOnClickListener = view -> {
        final Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Clup_Main);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        this.viewModel.setDefaultTheme(this);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //final ImageView imageView = findViewById(R.id.imageView);
        //final ProgressBar progressBar = findViewById(R.id.progressBar);
        //progressBar.setVisibility(View.GONE);
        final Button bookButton = findViewById(R.id.book_button);
        bookButton.setOnClickListener(bookButtonOnClickListener);

        // retrieved somewhere
        /*final String username = "alessandro brigandì";
        final String hours = "11";
        final String status = "todo";

        this.viewModel.setUserData(username, hours, status);

        final int onColor = getResources().getColor(R.color.qr_code_on_color);
        final int offColor = getResources().getColor(R.color.qr_code_off_color);

        this.viewModel.getQrCode(onColor, offColor, result -> {
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
        });*/
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
}