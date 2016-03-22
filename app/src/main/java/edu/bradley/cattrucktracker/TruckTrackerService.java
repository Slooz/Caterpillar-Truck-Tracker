/*
 * Copyright 2016 Nathan Clark, Krzysztof Czelusniak, Michael Holwey, Dakota Leonard
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
    private loadingStatus curLoadStatus;
    private boolean truckLoaded;
    private Boolean truckMoving;
    private float xPeak, yPeak, zPeak, xThresh,yThresh,zThresh, loadingTimer, loadingTimerLimit;
    private Boolean deviceAccelerating;
    private boolean isLoading;

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
        xThresh = .1f;
        yThresh = .1f;
        zThresh = .1f;
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
        return null;
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
            if(deviceAccelerating)
            {
                xPeak = absX;
                yPeak = absY;
                zPeak = absZ;
            }
            else
            {
                xPeak = 0f;
                yPeak = 0f;
                zPeak = 0f;
            }
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
        if(!truckMoving)
        {
            switch (curLoadStatus)
            {
                case loading:
                    truckState = TruckState.LOADING;
                    break;
                case unloading:
                    truckState= TruckState.UNLOADING;
                    break;
                case none:
                    truckState = TruckState.STOPPED;
                    break;
                default:
                    truckState = TruckState.UNKNOWN;
                    break;
            }
        }
        else if(truckMoving)
        {
            switch (curLoadStatus)
            {
                case loading:
                    truckState = TruckState.UNKNOWN;
                    break;
                case unloading:
                    truckState = TruckState.MOVING_DUMP;
                    break;
                case none:
                    truckState = TruckState.MOVING;
                    break;
                default:
                    truckState = TruckState.UNKNOWN;
                    break;
            }
        }

        switch(truckState)
        {
            case TruckState.STOPPED:
                if(yPeak > yThresh)
                {
                    if(xPeak > xThresh && zPeak >zThresh)
                        curLoadStatus = loadingStatus.unloading;
                    else
                    {
                        curLoadStatus = loadingStatus.loading;
                        loadingTimer = System.currentTimeMillis() + loadingTimerLimit;
                    }

                }

                break;
            case TruckState.LOADING:
                if(loadingTimer >= System.currentTimeMillis())
                {
                   // curLoadStatus = loadingStatus.none;
                }
                else if( yPeak > yThresh)
                {
                    loadingTimer = System.currentTimeMillis() + loadingTimerLimit;
                }
                break;
            case TruckState.MOVING:
                //cannot accurately determine if unloading or loading
                break;
            case TruckState.UNLOADING:
                //if determined to be unloading, wait until moving to change state, which will default it to moving dump which will resolve to be moving.
                break;
            case TruckState.MOVING_DUMP:
                //cannot determine if still unloading so default to moving
                truckState = TruckState.MOVING;
                break;
            case TruckState.UNKNOWN:
                truckState = TruckState.MOVING;
                break;

        }
/*
        //end my code
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
        }*/
    }
    enum loadingStatus {loading,unloading,none}
    enum TruckState {STOPPED, MOVING, LOADING, UNLOADING, MOVING_DUMP, UNKNOWN}
}
