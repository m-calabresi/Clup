package com.android.clup.ui;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.clup.R;
import com.android.clup.adapter.BusinessCardViewAdapter;
import com.android.clup.model.AvailableDay;
import com.android.clup.model.Business;
import com.android.clup.viewmodel.MapViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapViewModel viewModel;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;
    private GoogleMap map;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private TextView padView;
    private FloatingActionButton locationButton;
    private FloatingActionButton backButton;

    private final View.OnClickListener backButtonOnClickListener = view -> this.finish();
    private final View.OnClickListener locationButtonOnClickListener = view -> getLocationPermission();

    private static final int DEFAULT_ANIMATION_DURATION = 200; // milliseconds
    private final BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull final View bottomSheet, final int newState) {
            // hidesthe button immediately and waits for bottom sheet to collapse to show
            if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                hideView(backButton);
                hideView(locationButton);
                expandHeight(padView);
            } else {
                showView(backButton);
                showView(locationButton);
                reduceHeight(padView);
            }
        }

        @Override
        public void onSlide(@NonNull final View bottomSheet, final float slideOffset) {
        }
    };

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null)
            this.lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);

        setContentView(R.layout.activity_map);
        fullScreenStatusBar();

        this.viewModel = new ViewModelProvider(this).get(MapViewModel.class);

        // Construct a FusedLocationProviderClient.
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        this.locationButton = findViewById(R.id.location_button);
        this.locationButton.setOnClickListener(this.locationButtonOnClickListener);

        this.backButton = findViewById(R.id.back_button);
        this.backButton.setOnClickListener(this.backButtonOnClickListener);
        adjustTopStartMargins(this.backButton);

        this.padView = findViewById(R.id.pad_view);
        setPadHeight(padView);

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

        final BusinessCardViewAdapter adapter = new BusinessCardViewAdapter(this, businesses);
        recyclerView.setAdapter(adapter);

        final BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(recyclerView);
        bottomSheetBehavior.setFitToContents(false);
        bottomSheetBehavior.addBottomSheetCallback(this.bottomSheetCallback);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(@NonNull final Bundle outState) {
        if (this.map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    @SuppressWarnings("deprecation")
    private void fullScreenStatusBar() {
        int systemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
        systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

        getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
    }

    private void setPadHeight(@NonNull final View padView) {
        padView.post(() -> {
            final ViewGroup.LayoutParams layoutParams = padView.getLayoutParams();
            layoutParams.height = this.viewModel.getStatusBarHeight();
            System.out.println("HEIGHTTTTTTTTTTTTTTTT: " + layoutParams.height);
            padView.setLayoutParams(layoutParams);
        });
    }

    // set the height of the given view to the statusbar height
    private void expandHeight(@NonNull final View view) {
        view.post(() -> view.setVisibility(View.VISIBLE));
    }

    // hide the given view
    public void reduceHeight(@NonNull final View view) {
        view.post(() -> view.setVisibility(View.GONE));
    }

    private void adjustTopStartMargins(@NonNull final View view) {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layout_activity_map), (v, insets) -> {
            final int marginTop = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            final int marginStart = (int) getResources().getDimension(R.dimen.mini_fab_margin_start);

            final CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
            params.topMargin = marginTop;
            params.leftMargin = marginStart;
            params.rightMargin = marginStart;
            view.setLayoutParams(params);

            this.viewModel.setStatusBarHeight(marginTop);

            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void showView(@NonNull final View view) {
        view.post(() -> view.animate().scaleX(1).scaleY(1).setDuration(DEFAULT_ANIMATION_DURATION).start());
    }

    private void hideView(@NonNull final View view) {
        view.post(() -> view.animate().scaleX(0).scaleY(0).setDuration(DEFAULT_ANIMATION_DURATION).start());
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
        this.map = googleMap;
        this.map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));

        // Prompt the user for permission.
        getLocationPermission();
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (this.locationPermissionGranted) {
                final Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        this.lastKnownLocation = task.getResult();
                        if (this.lastKnownLocation != null) {
                            this.map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(this.lastKnownLocation.getLatitude(),
                                            this.lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            // Nothing to do here: check if location is enabled
                            final boolean isLocationEnabled =
                                    ((LocationManager) getSystemService(LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER);

                            if (!isLocationEnabled)
                                displayErrorAlertDialog();
                        }
                    } else {
                        // Current location is null, notify the user
                        Toast.makeText(this, R.string.location_error_text, Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            this.locationPermissionGranted = true;

            // Turn on the My Location layer and the related control on the map.
            updateLocationUI();

            // Get the current location of the device and set the position of the map.
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        this.locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.locationPermissionGranted = true;
            }
        }

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (this.map == null) {
            return;
        }
        try {
            if (this.locationPermissionGranted)
                this.map.setMyLocationEnabled(true);
            else {
                this.map.setMyLocationEnabled(false);
                this.lastKnownLocation = null;
            }
            this.map.getUiSettings().setMyLocationButtonEnabled(false);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void displayErrorAlertDialog() {
        new MaterialAlertDialogBuilder(this, R.style.AppTheme_Clup_RoundedAlertDialog)
                .setMessage(R.string.text_error_alert_message)
                .setPositiveButton(R.string.action_ok, null)
                .create()
                .show();
    }
}