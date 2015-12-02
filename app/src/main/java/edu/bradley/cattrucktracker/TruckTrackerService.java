/*
 * Copyright 2015 Nathan Clark, Krzysztof Czelusniak, Michael Holwey, Dakota Leonard
 */

package edu.bradley.cattrucktracker;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.LocationResult;

public class TruckTrackerService extends Service implements SensorEventListener {
    static final String TRUCK_STATE_BROADCAST_ACTION
            = TruckTrackerService.class.getPackage().getName() + "TRUCK_STATE";
    static final String TRUCK_STATE_EXTRA = "truckState";

    private SensorManager sensorManager;
    private TruckState truckState;
    private boolean truckLoaded;
    private Boolean truckMoving;
    private Boolean deviceAccelerating;

    @Override
    public void onCreate() {
        super.onCreate();

        truckState = TruckState.UNKNOWN;
        truckLoaded = false;

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor linearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        sensorManager
                .registerListener(this, linearAcceleration, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (LocationResult.hasResult(intent)) {
            LocationResult locationResult = LocationResult.extractResult(intent);
            Location location = locationResult.getLastLocation();

            truckMoving = location.hasSpeed();

            if (deviceAccelerating != null) {
                determineTruckState();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new TruckTrackerServiceBinder();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
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
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
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
                truckState = TruckState.UNLOADING;
                truckLoaded = false;
            } else {
                truckState = TruckState.LOADING;
                truckLoaded = true;
            }
        }

        if (oldTruckState != truckState) {
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

            Intent intent = new Intent(TRUCK_STATE_BROADCAST_ACTION)
                    .putExtra(TRUCK_STATE_EXTRA, truckState);

            localBroadcastManager.sendBroadcast(intent);
        }
    }

    enum TruckState {STOPPED, MOVING, LOADING, UNLOADING, UNKNOWN}

    class TruckTrackerServiceBinder extends Binder {
        TruckTrackerService truckTrackerService() {
            return TruckTrackerService.this;
        }
    }
}
