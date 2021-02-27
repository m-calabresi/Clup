package com.android.clup.api;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.android.clup.R;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MapsService {
    private static final String API_URL = "https://www.google.com/maps/dir/?api=1&destination=";

    private MapsService() {

    }

    /**
     * Launch an intent headed to google maps to display all available paths from the current user
     * position to the selected destination.
     * If unable to find the google maps app installed, an exception is raised.
     * Forced flag is used when the launch command is given from a BroadcastReceiver that doesn't
     * carry any parent activity
     */
    public static void launchNavigation(@NonNull final Context context, @NonNull final LatLng destination,
                                        final boolean force) throws ActivityNotFoundException {
        final String destinationString = destination.latitude + "," + destination.longitude;
        final String query = API_URL + destinationString;

        // Create a Uri from an intent string. Use the result to create an Intent.
        final Uri gmmIntentUri = Uri.parse(query);

        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        final Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");

        if (force)
            mapIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(mapIntent);
    }

    /**
     * Returns the address associated to the given coordinates (if exists), a given placeholder string
     * otherwise.
     */
    @NonNull
    public static String getAddressByCoordinates(@NonNull final Context context,
                                                 @NonNull final LatLng coordinates) {
        final Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        String address = "";
        String city = "";
        String state = "";

        try {
            final List<Address> addresses = geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1);
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
        final String altText = context.getString(R.string.text_unknown_location);

        completeAddress = !completeAddress.equals("") ? completeAddress : altText;
        return completeAddress;
    }
}
