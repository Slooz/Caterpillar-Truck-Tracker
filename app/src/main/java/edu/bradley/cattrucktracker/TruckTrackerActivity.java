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

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        GoogleApiClient.Builder googleApiClient = new GoogleApiClient.Builder(this);
        try {
            new TruckStateDeducer(sensorManager, googleApiClient);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        finish();
    }
}
