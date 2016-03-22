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

import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

public class TruckStateDeducer
        implements SensorEventListener, LocationListener, GoogleApiClient.ConnectionCallbacks {
    private final GoogleApiClient googleApiClient;
    private final HubProxy hubProxy;
    private final String serialNumber = "0";

    private TruckState truckState = TruckState.UNKNOWN;
    private boolean truckLoaded = false;
    private Boolean truckMoving;
    private Boolean deviceAccelerating;

    TruckStateDeducer(SensorManager sensorManager, GoogleApiClient.Builder googleApiClientBuilder)
            throws ExecutionException, InterruptedException {
        Sensor linearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager
                .registerListener(this, linearAcceleration, SensorManager.SENSOR_DELAY_FASTEST);

        googleApiClient = googleApiClientBuilder
                .addApi(LocationServices.API).addConnectionCallbacks(this).build();
        googleApiClient.connect();

        Platform.loadPlatformComponent(new AndroidPlatformComponent());
        HubConnection hubConnection
                = new HubConnection("http://bradley-capstone-app.azurewebsites.net");
        hubProxy = hubConnection.createHubProxy("SensorHub");
        SignalRFuture<Void> signalRFuture = hubConnection.start();
        signalRFuture.get();
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

            deviceAccelerating = absX >= 0.1 || absY >= 0.1 || absZ >= 0.1;

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
        Float speed = null;

        if (location.hasSpeed()) {
            speed = location.getSpeed();
            truckMoving = speed > 0;

            if (deviceAccelerating != null) {
                determineTruckState();
            }
        } else {
            truckMoving = null;
        }

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        hubProxy.invoke("PostGeo", serialNumber, latitude, longitude, speed);
    }

    private void determineTruckState() {
        TruckState oldTruckState = truckState;

        if (truckMoving) {
            truckState = TruckState.MOVING;
        } else if (truckState == TruckState.MOVING || truckState == TruckState.UNKNOWN) {
            truckState = TruckState.STOPPED;
        }

        if (truckState == TruckState.STOPPED && deviceAccelerating) {
            if (truckLoaded) {
                truckState = TruckState.STATIC_DUMP;
                truckLoaded = false;
            } else {
                truckState = TruckState.LOADING;
                truckLoaded = true;
            }
        }

        if (oldTruckState != truckState) {
            long currentTime = System.currentTimeMillis();
            hubProxy.invoke("PostStateChange", truckState, currentTime, serialNumber);
        }
    }

    enum TruckState {UNKNOWN, MOVING, STOPPED, LOADING, MOVING_DUMP, STATIC_DUMP}
}
