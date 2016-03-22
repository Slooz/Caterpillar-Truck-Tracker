/*
 * Copyright 2016 Nathan Clark, Krzysztof Czelusniak, Michael Holwey, Dakota Leonard
 */

package edu.bradley.cattrucktracker;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class TruckStateDeducer
        implements SensorEventListener, LocationListener, GoogleApiClient.ConnectionCallbacks {
    private TruckState truckState;
    private boolean truckLoaded;
    private Boolean truckMoving;
    private Boolean deviceAccelerating;
    private GoogleApiClient googleApiClient;

    TruckStateDeducer(SensorManager sensorManager, GoogleApiClient.Builder googleApiClientBuilder) {
        truckState = TruckState.UNKNOWN;
        truckLoaded = false;

        Sensor linearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager
                .registerListener(this, linearAcceleration, SensorManager.SENSOR_DELAY_FASTEST);

        googleApiClient = googleApiClientBuilder
                .addApi(LocationServices.API).addConnectionCallbacks(this).build();
        googleApiClient.connect();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.accuracy == SensorManager.SENSOR_STATUS_ACCURACY_HIGH) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float absX = Math.abs(x);
            float absY = Math.abs(y);
            float absZ = Math.abs(z);

            deviceAccelerating = absX >= 0.5 || absY >= 0.5 || absZ >= 0.5;

            if (truckMoving != null) {
                determineTruckState();
            }
        } else {
            deviceAccelerating = null;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
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
        if (location.hasSpeed()) {
            truckMoving = location.getSpeed() > 0;

            if (deviceAccelerating != null) {
                determineTruckState();
            }
        } else {
            truckMoving = null;
        }
    }

    private void determineTruckState() {
        if (truckMoving) {
            truckState = TruckState.MOVING;
        } else if (truckState == TruckState.MOVING || truckState == TruckState.UNKNOWN) {
            truckState = TruckState.STOPPED;
        }

        if (truckState == TruckState.STOPPED && deviceAccelerating) {
            if (truckLoaded) {
                truckState = TruckState.UNLOADING;
                truckLoaded = false;
            } else {
                truckState = TruckState.LOADING;
                truckLoaded = true;
            }
        }
    }

    enum TruckState {STOPPED, MOVING, LOADING, UNLOADING, UNKNOWN}
}
