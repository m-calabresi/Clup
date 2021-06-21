package com.android.clup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.clup.R;
import com.android.clup.adapter.OnListItemClickedCallback;
import com.android.clup.adapter.ShopRecyclerViewAdapter;
import com.android.clup.concurrent.Result;
import com.android.clup.model.Shop;
import com.android.clup.viewmodel.MapViewModel;
import com.android.clup.viewmodel.factory.MapViewModelFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, OnListItemClickedCallback {
    @Nullable
    private MapViewModel viewModel;

    @Nullable
    private TextView padView;
    @Nullable
    private FloatingActionButton locationButton;
    @Nullable
    private FloatingActionButton backButton;
    @Nullable
    private BottomSheetBehavior<View> bottomSheetBehavior;
    @Nullable
    private RecyclerView recyclerView;

    @NonNull
    private final View.OnClickListener backButtonOnClickListener = view -> this.finish();
    @NonNull
    private final View.OnClickListener locationButtonOnClickListener = view -> this.viewModel.startLocationPermissionRequest(this);

    @NonNull
    private final BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull final View bottomSheet, final int newState) {
            // hides the collapsed UI when bottom sheet is fully expanded
            if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                Utils.hideView(backButton);
                Utils.hideView(locationButton);
                Utils.expandHeight(padView);
            } else { // shows collapsed ui when bottom sheet reduces from fully expanded
                Utils.showView(backButton);
                Utils.showView(locationButton);
                Utils.reduceHeight(padView);
            }
        }

        @Override
        public void onSlide(@NonNull final View bottomSheet, final float slideOffset) {
        }
    };

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        this.viewModel = new ViewModelProvider(this, new MapViewModelFactory(this, this)).get(MapViewModel.class);
        Utils.setFullScreenStatusBar(this);

        final View mapActivityView = findViewById(R.id.layout_activity_map);

        this.locationButton = findViewById(R.id.location_button);
        this.locationButton.setOnClickListener(this.locationButtonOnClickListener);

        this.backButton = findViewById(R.id.back_button);
        this.backButton.setOnClickListener(this.backButtonOnClickListener);
        Utils.setTopStartMargins(mapActivityView, this.backButton);

        this.recyclerView = findViewById(R.id.map_recycler_view);
        this.recyclerView.setNestedScrollingEnabled(true);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (Utils.isPhone(this)) {
            this.padView = findViewById(R.id.pad_view);
            Utils.setPadHeight(mapActivityView, padView);

            this.bottomSheetBehavior = BottomSheetBehavior.from(this.recyclerView);
            this.bottomSheetBehavior.setFitToContents(false);
            this.bottomSheetBehavior.setHalfExpandedRatio(MapViewModel.BOTTOM_SHEET_HALF_EXPANDED_RATIO);
            this.bottomSheetBehavior.addBottomSheetCallback(this.bottomSheetCallback);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(@NonNull final GoogleMap googleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        Objects.requireNonNull(this.viewModel).setGoogleMap(this, googleMap);

        // Prompt the user for permission.
        this.viewModel.startLocationPermissionRequest(this);

        // TODO add loading bar in bottomSheet while waiting for list to be ready
        this.viewModel.getShops(result -> {
            if (result instanceof Result.Success) {
                final List<Shop> shops = ((Result.Success<List<Shop>>) result).data;

                // dispatch UI update to a UI thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    // display the shops list
                    final ShopRecyclerViewAdapter adapter = new ShopRecyclerViewAdapter(this, shops);

                    Objects.requireNonNull(this.recyclerView).setAdapter(adapter);

                    // add markers on the map
                    this.viewModel.addMarkers(this);
                });
            } else
                displayErrorSnackBar();
        });
    }

    /**
     * Handle the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Objects.requireNonNull(this.viewModel).continueLocationPermissionRequest(this, requestCode, grantResults);
    }

    /**
     * Handle the result of a recyclerview item being clicked.
     */
    @Override
    public void onListItemClicked(final int position) {
        Objects.requireNonNull(this.viewModel).setSelectedShop(position);
        final Intent intent = new Intent(this, SelectActivity.class);
        startActivity(intent);

        if (Utils.isPhone(this))
            this.viewModel.adjustExpansion(Objects.requireNonNull(this.bottomSheetBehavior));
    }

    /**
     * Display an error SnackBar.
     */
    private void displayErrorSnackBar() {
        new Handler(Looper.getMainLooper()).post(() ->
                Utils.displayShopsErrorSnackBar(findViewById(R.id.layout_activity_map)));
    }
}