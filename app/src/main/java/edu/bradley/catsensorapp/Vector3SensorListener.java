package edu.bradley.catsensorapp;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import edu.bradley.catsensorapp.csvdatatypes.Vector3;

/**
 * Created by dakotaleonard on 10/6/15.
 */
public class Vector3SensorListener implements SensorEventListener
{
    public List<TimeSeriesSensorData> series;
    public int curAccuracy;

    SensorActivity sensorActivity;

    public Vector3SensorListener(SensorActivity sensorActivity)
    {
        this.sensorActivity = sensorActivity;
        series = new ArrayList<TimeSeriesSensorData>();
        curAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM;
    }

    public void addSensor(Sensor sensor)
    {
        series.add(new TimeSeriesSensorData(sensor));
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        for(TimeSeriesSensorData seriesData : series)
        {
            if(seriesData.sensor.equals(event.sensor.getName()))
            {
                synchronized (sensorActivity.tracState)
                {
                    seriesData.StorePoint(new Vector3(event.values[0], event.values[1], event.values[2]), sensorActivity.tracState, System.currentTimeMillis());
                }
            }
        }
    }

    public void writeFiles(File csvFolder, File serialFolder, Context context)
    {
        //CSVs
        for(TimeSeriesSensorData seriesData : series)
        {
            try
            {
                seriesData.writeToCSV(new File(csvFolder.getAbsolutePath() + File.separator + seriesData.sensor + ".csv"), context);
                seriesData.writeSerial(new File(serialFolder.getAbsolutePath() + File.separator + seriesData.sensor + ".ser"), context);
            }catch(Exception e)
            {
                System.err.println("Failed to create file for " + seriesData.sensor +" \n" + e.getMessage());
            }
        }

        //Serialized
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        curAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM;
    }
}
