package com.android.clup.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;

import com.android.clup.R;
import com.android.clup.adapter.InfoWindowAdapter;
import com.android.clup.adapter.OnListItemClickedCallback;
import com.android.clup.api.MapsService;
import com.android.clup.api.QueueService;
import com.android.clup.concurrent.Callback;
import com.android.clup.concurrent.Result;
import com.android.clup.model.Model;
import com.android.clup.model.Shop;
import com.android.clup.ui.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;
import java.util.Objects;

import static android.content.Context.LOCATION_SERVICE;

public class MapViewModel extends ViewModel {
    @NonNull
    private final Model model;

    private final OnListItemClickedCallback recyclerViewItemClickedCallback;

    // The entry point to the Fused Location Provider.
    @NonNull
    private final FusedLocationProviderClient fusedLocationProviderClient;

    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    @Nullable
    private Location lastKnownLocation;
    private GoogleMap map;

    @NonNull
    private final QueueService queueService;

    public static final float BOTTOM_SHEET_HALF_EXPANDED_RATIO = 0.6f;

    public MapViewModel(@NonNull final Activity activity, @NonNull final OnListItemClickedCallback recyclerViewItemClickedCallback) {
        this.model = Model.getInstance();

        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        this.recyclerViewItemClickedCallback = recyclerViewItemClickedCallback;

        this.queueService = new QueueService();
    }

    public void setGoogleMap(@NonNull final Context context, @NonNull final GoogleMap map) {
        this.map = map;
        this.map.getUiSettings().setCompassEnabled(false);
        this.map.setInfoWindowAdapter(new InfoWindowAdapter(context));

        // handles the click on the marker InfoView
        final GoogleMap.OnInfoWindowClickListener markerInfoWindowClickListener = marker -> {
            // retrieve the position of the clicked item
            final int position = (int) marker.getTag();
            recyclerViewItemClickedCallback.onRecyclerViewItemClicked(position);
        };
        this.map.setOnInfoWindowClickListener(markerInfoWindowClickListener);
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
                        Toast.makeText(activity, R.string.text_error_location, Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return the list of shops to be displayed in the UI.
     */
    public void getShops(@NonNull final Callback<List<Shop>> callback) {
        if (this.model.getShops() == null) {
            this.queueService.getShops(result -> {
                Result<List<Shop>> shopsResult;
                if (result instanceof Result.Success) {
                    final List<Shop> shops = ((Result.Success<List<Shop>>) result).data;
                    shopsResult = new Result.Success<>(shops);
                    this.model.setShops(shops);
                } else {
                    final String errorMessage = ((Result.Error<List<Shop>>) result).message;
                    shopsResult = new Result.Error<>(errorMessage);
                }
                callback.onComplete(shopsResult);
            });
        } else {
            final Result.Success<List<Shop>> cached = new Result.Success<>(this.model.getShops());
            callback.onComplete(cached);
        }
    }

    /**
     * Store the shop selected by the user.
     */
    public void setSelectedShop(final int position) {
        this.model.setSelectedShop(Objects.requireNonNull(this.model.getShops()).get(position));
    }

    /**
     * If the bottom sheet is fully expanded, set it to half expansion. This allows for faster
     * bak action when the user decides to go back through multiple activities (back button will
     * otherwise be hidden).
     */
    public void adjustExpansion(@NonNull final BottomSheetBehavior<View> bottomSheetBehavior) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        });
    }

    /**
     * Add markers on the map in the position specified by the retrieved shops.
     */
    public void addMarkers(@NonNull final Context context) {
        final int size = Objects.requireNonNull(this.model.getShops()).size();

        for (int position = 0; position < size; position++) {
            final Shop shop = this.model.getShops().get(position);

            final BitmapDescriptor icon = Utils.vectorToBitmap(context, R.drawable.marker_shop);
            final String title = shop.getName();
            final String snippet = MapsService.getAddressByCoordinates(context, shop.getCoordinates());

            final Marker marker = this.map.addMarker(new MarkerOptions()
                    .position(shop.getCoordinates())
                    .icon(icon)
                    .title(title)
                    .snippet(snippet));
            marker.setTag(position);
        }
    }
}
