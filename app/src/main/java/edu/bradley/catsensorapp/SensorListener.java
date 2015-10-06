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
    private TextView txt;

    public SensorListener(int sensorType, TextView txt)
    {
        this.sensorType = sensorType;
        this.txt = txt;
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        txt.setText(String.format("(%.3f,%.3f,%.3f", event.values[0], event.values[1], event.values[2]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}
