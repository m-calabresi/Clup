package com.android.clup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.android.clup.model.Shop;
import com.android.clup.viewmodel.SelectViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class SelectActivity extends AppCompatActivity {
    private SelectViewModel viewModel;

    private ExtendedFloatingActionButton doneButton;

    private final View.OnClickListener doneButtonOnClickListener = view -> {
        final String date = this.viewModel.getSelectedDay().getFormatDate();
        final String hour = this.viewModel.getSelectedHour();

        final String message = date + ", " + hour;
        Log.i("AAA", message);
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
        this.doneButton.setOnClickListener(doneButtonOnClickListener);
        this.viewModel.getGroupIdLiveData().observe(this, this.doneButtonGroupIdLiveDataObserver);
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

    private void onBackActionPerformed() {
        this.viewModel.resetSelectedShopPosition();
        this.viewModel.resetSelectedDayPosition();
        this.viewModel.resetSelectedHourPosition();

        final Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
    }
}