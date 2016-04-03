/*
 * Copyright 2016 Nathan Clark, Krzysztof Czelusniak, Michael Holwey, Dakota Leonard
 */

package edu.bradley.cattrucktracker;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

class Accelerometer implements SensorEventListener {
    private final TruckState truckState;

    Accelerometer(SensorManager sensorManager, TruckState truckState) {
        Sensor linearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager
                .registerListener(this, linearAcceleration, SensorManager.SENSOR_DELAY_FASTEST);

        this.truckState = truckState;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Boolean deviceAccelerating = null;

        if (event.accuracy == SensorManager.SENSOR_STATUS_ACCURACY_HIGH) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float absX = Math.abs(x);
            float absY = Math.abs(y);
            float absZ = Math.abs(z);

            deviceAccelerating = absX >= 0.25 || absY >= 0.25 || absZ >= 0.25;
        }

        truckState.setDeviceAcceleratingStateAndUpdate(deviceAccelerating);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
