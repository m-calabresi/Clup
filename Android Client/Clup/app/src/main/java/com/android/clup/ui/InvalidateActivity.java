package com.android.clup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.android.clup.R;
import com.android.clup.viewmodel.InvalidateViewModel;

/**
 * WARNING: this activity should not be here, it is here only for demonstration purposes. Its aim is
 * to show the correctness of the project infrastructure but, ideally, it should be implemented on
 * the machine outside the shop (see documentation for further details).
 * <p>
 * This activity is reachable from {@link DetailsActivity} by long pressing the qr-code ImageView.
 */
public class InvalidateActivity extends AppCompatActivity {
    private InvalidateViewModel viewModel;

    @NonNull
    private final View.OnClickListener registerButtonOnClickListener = view -> {
        this.viewModel.invalidateSelectedReservation();
        returnToMainActivity();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invalidate);

        this.viewModel = new ViewModelProvider(this).get(InvalidateViewModel.class);

        final Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(registerButtonOnClickListener);
    }

    private void returnToMainActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}