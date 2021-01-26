package com.android.clup.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;

import com.android.clup.R;
import com.android.clup.model.AvailableDay;
import com.android.clup.model.Market;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class MapViewModel extends ViewModel {

    // The entry point to the Fused Location Provider.
    private final FusedLocationProviderClient fusedLocationProviderClient;
    private final Geocoder geocoder;

    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;
    private GoogleMap map;

    private static final int DEFAULT_ANIMATION_DURATION = 200; // milliseconds
    public static final float BOTTOM_SHEET_HALF_EXPANDED_RATIO = 0.4f;

    public MapViewModel(@NonNull final Activity activity) {
        // Construct a FusedLocationProviderClient.
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        this.geocoder = new Geocoder(activity, Locale.getDefault());
    }

    public void setGoogleMap(@NonNull final GoogleMap map) {
        this.map = map;
    }

    /**
     * Prompts the user for permission.
     */
    public void startLocationPermissionRequest(@NonNull final Activity activity) {
        if (this.map == null)
            throw new RuntimeException("this.map is null, did you call setGoogleMap()?");

        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            this.locationPermissionGranted = true;

            // Turn on the My Location layer and the related control on the map.
            updateLocationUI();

            // Get the current location of the device and set the position of the map.
            getDeviceLocation(activity);
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Continue the user permission request (to be called in OnMapReadyCallback#OnMapReady()).
     */
    public void continueLocationPermissionRequest(@NonNull final Activity activity, final int requestCode, @NonNull final int[] grantResults) {
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
        getDeviceLocation(activity);
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
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

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation(@NonNull final Activity activity) {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (this.locationPermissionGranted) {
                final Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(activity, task -> {
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
                                    ((LocationManager) activity.getSystemService(LOCATION_SERVICE))
                                            .isProviderEnabled(LocationManager.GPS_PROVIDER);

                            if (!isLocationEnabled)
                                displayLocationErrorDialog(activity);
                        }
                    } else {
                        // Current location is null, notify the user
                        Toast.makeText(activity, R.string.location_error_text, Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the address associated to the given coordinates (if exists), a placeholder string
     * otherwise.
     */
    @NonNull
    public String getAddressByCoordinates(@NonNull final LatLng coordinates) {
        String address = "";
        String city = "";
        String state = "";

        try {
            final List<Address> addresses = this.geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                // civic number or null
                final String throughFare = addresses.get(0).getThoroughfare();

                // street address or null
                final String subThroughFare = addresses.get(0).getSubThoroughfare();

                // "street address, civic number" or "street address" or ""
                address = throughFare != null ? throughFare : "";
                address += (subThroughFare != null && !address.equals("")) ? ", " + subThroughFare : "";

                // "city" or ""
                city = addresses.get(0).getLocality();
                city = city != null ? city : "";

                // "state" or ""
                state = addresses.get(0).getAdminArea();
                state = state != null ? state : "";
            }
        } catch (@NonNull final IOException e) {
            e.printStackTrace();
        }

        // "address,\ncity, state" or "city, state" or "address\ncity" or "address,\nstate" or "address,\n" or "city," or "state" or ""
        String completeAddress = "";
        completeAddress += !address.equals("") ? address + "\n" : "";
        completeAddress += !city.equals("") ? city + ", " : "";
        completeAddress += !state.equals("") ? state : "";

        // one of the above or "unknown location" string
        completeAddress = !completeAddress.equals("") ? completeAddress : "Unknown location"; // TODO replace with resource string
        return completeAddress;
    }

    /**
     * Displays an error alert dialog.
     */
    private void displayLocationErrorDialog(@NonNull final Context context) {
        new MaterialAlertDialogBuilder(context, R.style.AppTheme_Clup_RoundedAlertDialog)
                .setMessage(R.string.text_location_error_alert_message)
                .setPositiveButton(R.string.action_ok, null)
                .create()
                .show();
    }

    /**
     * Sets the status bar to be fullscreen.
     */
    @SuppressWarnings("deprecation")
    public void setFullScreenStatusBar(@NonNull final Activity activity) {
        int systemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
        systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

        activity.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
    }

    /**
     * Sets the height of the padding view to fill the status bar region when the bottom sheet
     * is fully expanded.
     */
    public void setPadHeight(@NonNull final View parentView, @NonNull final View padView) {
        padView.post(() -> ViewCompat.setOnApplyWindowInsetsListener(parentView, (v, insets) -> {
            final int marginTop = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            final ViewGroup.LayoutParams layoutParams = padView.getLayoutParams();

            layoutParams.height = marginTop;
            padView.setLayoutParams(layoutParams);

            return WindowInsetsCompat.CONSUMED;
        }));
    }

    /**
     * Sets the top and start margin for the given view based on the status bar height.
     */
    public void setTopStartMargins(@NonNull final View parentView, @NonNull final View view) {
        ViewCompat.setOnApplyWindowInsetsListener(parentView, (v, insets) -> {
            final int marginTop = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            final int marginStart = (int) parentView.getResources().getDimension(R.dimen.mini_fab_margin_start);

            final CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
            params.topMargin = marginTop;
            params.leftMargin = marginStart;
            params.rightMargin = marginStart;
            view.setLayoutParams(params);

            return WindowInsetsCompat.CONSUMED;
        });
    }

    /**
     * Shows the given view.
     */
    public void expandHeight(@NonNull final View view) {
        view.post(() -> view.setVisibility(View.VISIBLE));
    }

    /**
     * Hides the given view
     */
    public void reduceHeight(@NonNull final View view) {
        view.post(() -> view.setVisibility(View.GONE));
    }

    /**
     * Animate the appearing of the given view.
     */
    public void showView(@NonNull final View view) {
        view.post(() -> view.animate().scaleX(1).scaleY(1).setDuration(DEFAULT_ANIMATION_DURATION).start());
    }

    /**
     * Animate the disappearing of the given view.
     */
    public void hideView(@NonNull final View view) {
        view.post(() -> view.animate().scaleX(0).scaleY(0).setDuration(DEFAULT_ANIMATION_DURATION).start());
    }

    /**
     * Return the list of shops to be displayed in the UI.
     * TODO replace with API call
     */
    public List<Market> getMarkets() {
        // dummy list
        final LatLng coords1 = new LatLng(45.4659, 9.1914);
        final LatLng coords2 = new LatLng(1122.1, 1245.2);

        final AvailableDay availableDay1 = new AvailableDay("12-02-2020", Arrays.asList(12, 13, 14, 15));
        final AvailableDay availableDay2 = new AvailableDay("13-02-2020", Arrays.asList(16, 17, 18, 19));
        final AvailableDay availableDay3 = new AvailableDay("14-02-2020", Arrays.asList(15, 16, 17, 20));
        final List<AvailableDay> availableDays = Arrays.asList(availableDay1, availableDay2, availableDay3);

        final Market market1 = new Market("local shop", coords1, availableDays);
        final Market market2 = new Market("supermarket", coords2, availableDays);

        //return Arrays.asList(market1, market2, market1, market2, market1, market2, market1, market2, market1, market2);
        return Arrays.asList(market1, market2, market1);
    }
}
