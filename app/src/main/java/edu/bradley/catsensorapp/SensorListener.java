package edu.bradley.catsensorapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

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
        series.StorePoint(event.values, sensorType, tracState, System.currentTimeMillis());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
    }
}
