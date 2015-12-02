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
import android.os.IBinder;

import com.google.android.gms.location.LocationResult;

public class TruckTrackerService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private TruckState truckState;
    private boolean truckMoving;
    private boolean deviceAccelerating;

    @Override
    public void onCreate() {
        super.onCreate();

        truckState = TruckState.UNKNOWN;

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

            determineTruckState();
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
        return null;
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

        determineTruckState();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void determineTruckState() {
        if (truckMoving) {
            truckState = TruckState.MOVING;
        }
        else if (!deviceAccelerating) {
            truckState = TruckState.STOPPED;
        }
    }

    private enum TruckState {STOPPED, MOVING, LOADING, UNLOADING, UNKNOWN}
}
