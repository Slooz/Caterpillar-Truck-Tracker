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
import android.os.IBinder;

public class AccelerationForceMonitorService extends Service implements SensorEventListener {
    private SensorManager sensorManager;

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor linearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager
                .registerListener(this, linearAcceleration, SensorManager.SENSOR_DELAY_FASTEST);
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
        boolean deviceAccelerating = Math.abs(x) >= 0.5 || Math.abs(y) >= 0.5 || Math.abs(z) >= 0.5;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
