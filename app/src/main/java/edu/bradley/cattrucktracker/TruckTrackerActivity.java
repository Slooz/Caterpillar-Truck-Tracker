/*
 * Copyright 2016 Nathan Clark, Krzysztof Czelusniak, Michael Holwey, Dakota Leonard
 */

package edu.bradley.cattrucktracker;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;

public class TruckTrackerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BackEnd backEnd = null;
        try {
            backEnd = new BackEnd();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        TruckState truckState = new TruckState(backEnd);

        GoogleApiClient.Builder googleApiClientBuilder = new GoogleApiClient.Builder(this);
        new Gps(googleApiClientBuilder, truckState, backEnd);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        new Accelerometer(sensorManager, truckState);

        finish();
    }
}
