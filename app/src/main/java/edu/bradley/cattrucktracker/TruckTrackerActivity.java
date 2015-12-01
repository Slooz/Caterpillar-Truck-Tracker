/*
 * Copyright 2015 Nathan Clark, Krzysztof Czelusniak, Michael Holwey, Dakota Leonard
 */

package edu.bradley.cattrucktracker;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class TruckTrackerActivity extends Activity implements GoogleApiClient.ConnectionCallbacks {
    private static final int LOCATION_REQUEST_MILLISECOND_INTERVAL = 0;
    private GoogleApiClient googleApiClient;

    @Override
    public void onConnected(Bundle connectionHint) {
        Intent intent = new Intent(this, TruckMovingMonitor.class);
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LOCATION_REQUEST_MILLISECOND_INTERVAL);
        PendingIntent pendingIntent = PendingIntent
                .getService(this, TruckMovingMonitor.LOCATION_REQUEST_REQUEST_CODE, intent, 0);
        LocationServices.FusedLocationApi
                .requestLocationUpdates(googleApiClient, locationRequest, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this).build();
        Intent intent = new Intent(this, DeviceAccelerationForceMonitor.class);
        startService(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }
}
