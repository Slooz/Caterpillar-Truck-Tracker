package edu.bradley.catsensorapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

/**
 * Created by dakotaleonard on 10/6/15.
 */
public class SensorListener implements SensorEventListener
{
    public int sensorType;
    public TimeDataGraph graph;
    public TimeSeriesSensorData series;
    public TimeSensorData.TractorState tracState = TimeSensorData.TractorState.UNKNOWN;

    public SensorListener(int sensorType,  TimeDataGraph graph)
    {
        this.sensorType = sensorType;
        series = new TimeSeriesSensorData();
        //TODO this.graph = graph;
        //TODO graph.attachDataSeries(series);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        series.StorePoint(event.values, sensorType, tracState);
        //TODO graph.invalidate();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}
