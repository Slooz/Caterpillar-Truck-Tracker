package edu.bradley.catsensorapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import edu.bradley.catsensorapp.csvdatatypes.GPSData;
import edu.bradley.catsensorapp.csvdatatypes.Vector3;

public class DataGatheringService extends Service
{
    private Vector3 accelData;
    private GPSData gpsData;


    public DataGatheringService()
    {
        accelData = new Vector3(0,0,0);
        gpsData = new
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
