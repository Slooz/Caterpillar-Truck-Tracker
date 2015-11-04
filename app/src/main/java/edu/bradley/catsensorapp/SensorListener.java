package edu.bradley.catsensorapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import edu.bradley.catsensorapp.csvdatatypes.Vector3;

/**
 * Created by dakotaleonard on 10/6/15.
 */
public class SensorListener implements SensorEventListener
{
    public int sensorType;
    public TimeSeriesSensorData series;
    public TimeSensorData.TractorState tracState = TimeSensorData.TractorState.STOPPED;

    public SensorListener(int sensorType)
    {
        this.sensorType = sensorType;
        series = new TimeSeriesSensorData();
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        series.StorePoint(new Vector3(event.values[0],event.values[1],event.values[2]), sensorType, tracState, System.currentTimeMillis());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
    }
}
