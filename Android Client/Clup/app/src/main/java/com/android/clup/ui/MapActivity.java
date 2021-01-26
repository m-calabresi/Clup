package com.android.clup.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.clup.R;
import com.android.clup.adapter.BusinessCardViewAdapter;
import com.android.clup.model.AvailableDay;
import com.android.clup.model.Business;
import com.android.clup.viewmodel.MapViewModel;
import com.android.clup.viewmodel.MapViewModelFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapViewModel viewModel;

    private TextView padView;
    private FloatingActionButton locationButton;
    private FloatingActionButton backButton;

    private final View.OnClickListener backButtonOnClickListener = view -> this.finish();
    private final View.OnClickListener locationButtonOnClickListener = view -> this.viewModel.startLocationPermissionRequest(this);

    private final BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull final View bottomSheet, final int newState) {
            // hides the collapsed ui when bottom sheet is fully expanded
            if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                viewModel.hideView(backButton);
                viewModel.hideView(locationButton);
                viewModel.expandHeight(padView);
            } else { // shows collapsed ui when bottom sheet reduces from fully expanded
                viewModel.showView(backButton);
                viewModel.showView(locationButton);
                viewModel.reduceHeight(padView);
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

        this.viewModel = new ViewModelProvider(this, new MapViewModelFactory(this)).get(MapViewModel.class);
        this.viewModel.setFullScreenStatusBar(this);

        final View mapActivityView = findViewById(R.id.layout_activity_map);

        this.locationButton = findViewById(R.id.location_button);
        this.locationButton.setOnClickListener(this.locationButtonOnClickListener);

        this.backButton = findViewById(R.id.back_button);
        this.backButton.setOnClickListener(this.backButtonOnClickListener);
        this.viewModel.setTopStartMargins(mapActivityView, this.backButton);

        this.padView = findViewById(R.id.pad_view);
        this.viewModel.setPadHeight(mapActivityView, padView);

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // dummy list
        final LatLng coords1 = new LatLng(45.4659, 9.1914);
        final LatLng coords2 = new LatLng(1122.1, 1245.2);

        final AvailableDay availableDay1 = new AvailableDay("12-02-2020", Arrays.asList(12, 13, 14, 15));
        final AvailableDay availableDay2 = new AvailableDay("13-02-2020", Arrays.asList(16, 17, 18, 19));
        final AvailableDay availableDay3 = new AvailableDay("14-02-2020", Arrays.asList(15, 16, 17, 20));
        final List<AvailableDay> availableDays = Arrays.asList(availableDay1, availableDay2, availableDay3);

        final Business business1 = new Business("local shop", coords1, availableDays);
        final Business business2 = new Business("supermarket", coords2, availableDays);

        final List<Business> businesses = Arrays.asList(business1, business2, business1, business2, business1, business2, business1, business2, business1, business2);

        final BusinessCardViewAdapter adapter = new BusinessCardViewAdapter(this.viewModel, businesses);
        recyclerView.setAdapter(adapter);

        final BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(recyclerView);
        bottomSheetBehavior.setFitToContents(false);
        bottomSheetBehavior.setHalfExpandedRatio(MapViewModel.BOTTOM_SHEET_HALF_EXPANDED_RATIO);
        bottomSheetBehavior.addBottomSheetCallback(this.bottomSheetCallback);

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
        this.viewModel.setGoogleMap(googleMap);

        // Prompt the user for permission.
        this.viewModel.startLocationPermissionRequest(this);
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        this.viewModel.continueLocationPermissionRequest(this, requestCode, grantResults);
    }
}