/*
 * Copyright 2015 Nathan Clark, Krzysztof Czelusniak, Michael Holwey, Dakota Leonard
 */

package edu.bradley.cattrucktracker;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class TruckTrackerActivity extends Activity implements GoogleApiClient.ConnectionCallbacks {
    private GoogleApiClient googleApiClient;
    private Intent truckTrackerServiceIntent;
    private TruckTrackerService truckTrackerService;
    private final ServiceConnection truckTrackerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            truckTrackerService
                    = ((TruckTrackerService.TruckTrackerServiceBinder) service)
                    .truckTrackerService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public void onConnected(Bundle connectionHint) {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(0);

        PendingIntent pendingIntent
                = PendingIntent.getService(this, 0, truckTrackerServiceIntent, 0);

        LocationServices.FusedLocationApi
                .requestLocationUpdates(googleApiClient, locationRequest, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, TruckTrackerService.class);
        startService(intent);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this).build();

        truckTrackerServiceIntent = new Intent(this, TruckTrackerService.class);
    }

    @Override
    protected void onStart() {
        super.onStart();

        googleApiClient.connect();

        bindService(truckTrackerServiceIntent, truckTrackerServiceConnection, 0);
    }

    @Override
    protected void onStop() {
        unbindService(truckTrackerServiceConnection);

        googleApiClient.disconnect();

        super.onStop();
    }
}
