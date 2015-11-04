package edu.bradley.catsensorapp;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import java.io.File;

import edu.bradley.catsensorapp.csvdatatypes.GPSData;

/**
 * Created by dakotaleonard on 11/3/15.
 */
public class GPSListener implements LocationListener
{
    TimeSeriesSensorData locationData;
    SensorActivity sensorActivity;

    public GPSListener(SensorActivity sensorActivity)
    {
        this.sensorActivity = sensorActivity;
        locationData = new TimeSeriesSensorData();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        if(sensorActivity.recording)
            locationData.StorePoint(new GPSData(location), sensorActivity.tracState, System.currentTimeMillis());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderEnabled(String provider)
    {

    }

    @Override
    public void onProviderDisabled(String provider)
    {

    }

    public void writeFiles(File csvFolder, File serialFolder, Context context)
    {
        //Write csv file
        try
        {
            locationData.writeToCSV(new File(csvFolder.getAbsolutePath() + File.separator + "GPS.csv"), context);
            locationData.writeSerial(new File(serialFolder.getAbsolutePath() + File.separator + "GPS.ser"), context);
        }catch(Exception e)
        {
            System.err.println("Failed to create file for GPS\n" + e.getMessage());
        }
        //Write serial file
    }
}
