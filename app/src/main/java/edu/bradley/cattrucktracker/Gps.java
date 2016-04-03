/*
 * Copyright 2016 Nathan Clark, Krzysztof Czelusniak, Michael Holwey, Dakota Leonard
 */

package edu.bradley.cattrucktracker;

import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

class Gps implements LocationListener, GoogleApiClient.ConnectionCallbacks {
    private final GoogleApiClient googleApiClient;
    private final TruckState truckState;
    private final BackEnd backEnd;

    Gps(GoogleApiClient.Builder googleApiClientBuilder, TruckState truckState, BackEnd backEnd) {
        googleApiClientBuilder
                = googleApiClientBuilder.addApi(LocationServices.API).addConnectionCallbacks(this);
        googleApiClient = googleApiClientBuilder.build();
        googleApiClient.connect();

        this.truckState = truckState;
        this.backEnd = backEnd;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(0);

        LocationServices.FusedLocationApi
                .requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onLocationChanged(Location location) {
        Boolean truckMoving = null;
        Float speed = null;

        if (location.hasSpeed()) {
            speed = location.getSpeed();
            truckMoving = speed > 0;
        }

        truckState.setTruckMovingStateAndUpdate(truckMoving);

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        backEnd.sendTruckLocationAndSpeed(latitude, longitude, speed);
    }
}
