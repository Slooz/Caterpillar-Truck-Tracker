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

import com.google.android.gms.location.LocationResult;

public class TruckTrackerService extends Service implements SensorEventListener {
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

            if (location.hasSpeed()) {
                truckMoving = location.getSpeed() > 0;

                if (deviceAccelerating != null) {
                    determineTruckState();
                }
            } else {
                truckMoving = null;
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

    class TruckTrackerServiceBinder extends Binder {
        TruckTrackerService truckTrackerService() {
            return TruckTrackerService.this;
        }
    }
}
