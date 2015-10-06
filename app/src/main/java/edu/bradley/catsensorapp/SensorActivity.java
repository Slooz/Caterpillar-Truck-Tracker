package edu.bradley.catsensorapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SensorActivity extends ActionBarActivity
{

    TextView accelView, magView;
    Context context;
    SensorManager sensorManager;
    Sensor accel, mag;
    SensorListener accelLst, magLst;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        accelView = (TextView)findViewById(R.id.accelView);
        magView = (TextView)findViewById(R.id.magView);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        accelLst = new SensorListener(Sensor.TYPE_ACCELEROMETER, accelView);
        magLst = new SensorListener(Sensor.TYPE_MAGNETIC_FIELD, magView);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(accelLst, accel, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(magLst, mag, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(accelLst);
        sensorManager.unregisterListener(magLst);
    }
}
