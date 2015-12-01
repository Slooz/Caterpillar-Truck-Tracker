/*
 * Copyright 2015 Nathan Clark, Krzysztof Czelusniak, Michael Holwey, Dakota Leonard
 */

package edu.bradley.cattrucktracker;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationResult;

public class MovingMonitorService extends IntentService {
    static final int LOCATION_REQUEST_REQUEST_CODE = 0;

    public MovingMonitorService() {
        super(MovingMonitorService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LocationResult locationResult = LocationResult.extractResult(intent);
        if (locationResult != null) {
            Location location = locationResult.getLastLocation();
            boolean moving = location.hasSpeed();
        }
    }
}
