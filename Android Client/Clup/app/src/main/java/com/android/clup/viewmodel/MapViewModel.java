package com.android.clup.viewmodel;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;

import com.android.clup.R;
import com.android.clup.model.AvailableDay;
import com.android.clup.model.Date;
import com.android.clup.model.Model;
import com.android.clup.model.Shop;
import com.android.clup.ui.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class MapViewModel extends ViewModel {
    private final Model model;

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

    public static final float BOTTOM_SHEET_HALF_EXPANDED_RATIO = 0.6f;

    public MapViewModel(@NonNull final Activity activity) {
        this.model = Model.getInstance();

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
            throw new RuntimeException("this.map is null, did you call 'setGoogleMap'?");

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
                                Utils.displayLocationErrorDialog(activity);
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
     * Returns the address associated to the given coordinates (if exists), a given placeholder string
     * otherwise.
     */
    @NonNull
    public String getAddressByCoordinates(@NonNull final LatLng coordinates, @NonNull final String altText) {
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
        completeAddress = !completeAddress.equals("") ? completeAddress : altText;
        return completeAddress;
    }

    /**
     * Return the list of shops to be displayed in the UI.
     * TODO replace with API call
     */
    public List<Shop> getShops() {
        if (this.model.getShops() == null) {
            // dummy list
            final LatLng coords1 = new LatLng(45.4659, 9.1914);
            final LatLng coords2 = new LatLng(1122.1, 1245.2);

            final Date date1 = Date.fromString("11-02-2020");
            final Date date2 = Date.fromString("12-02-2020");
            final Date date3 = Date.fromString("13-02-2020");

            final AvailableDay availableDay1 = new AvailableDay(date1, Arrays.asList("12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"));
            final AvailableDay availableDay2 = new AvailableDay(date2, Arrays.asList("16:00", "17:00", "18:00", "19:00"));
            final AvailableDay availableDay3 = new AvailableDay(date3, Arrays.asList("15:00", "16:00", "17:00", "20:00"));

            final List<AvailableDay> availableDays = Arrays.asList(availableDay1, availableDay1, availableDay2, availableDay3, availableDay2, availableDay3, availableDay1, availableDay2, availableDay3, availableDay1, availableDay2, availableDay3);
            //final List<AvailableDay> availableDays = Arrays.asList(availableDay1, availableDay2, availableDay3);

            final Shop shop1 = new Shop("Local shop", coords1, availableDays);
            final Shop shop2 = new Shop("Supermarket", coords2, availableDays);

            //List<Shop> shops = Arrays.asList(shop1, shop2, shop1, shop2, shop1, shop2, shop1, shop2, shop1, shop2);
            List<Shop> shops = Arrays.asList(shop1, shop2, shop1);
            this.model.setShops(shops);
        }
        return this.model.getShops();
    }

    /**
     * Store index corresponding to the shop selected by the user.
     */
    public void setSelectedShopPosition(final int position) {
        this.model.setSelectedShopIndex(position);
    }

    /**
     * If the bottom sheet is fully expanded, set it to half expansion. This allows for faster
     * bak action when the user decides to go back through multiple activities (back button will
     * otherwise be hidden).
     */
    public void handleExpansion(@NonNull final BottomSheetBehavior<View> bottomSheetBehavior) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        });
    }
}
