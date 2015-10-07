package edu.bradley.catsensorapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SensorActivity extends ActionBarActivity
{
    Context context;
    SensorManager sensorManager;
    Sensor accel, mag;
    SensorListener accelLst, magLst;
    //TODO TimeDataGraph accelGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        //TODO accelGraph = (TimeDataGraph)findViewById(R.id.accelGraph);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        accelLst = new SensorListener(Sensor.TYPE_ACCELEROMETER, null);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //sensorManager.registerListener(accelLst, accel, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(accelLst);
    }

    public void startRecording(View view)
    {
        System.out.println("STARTED");
        sensorManager.registerListener(accelLst, accel, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopRecording(View view)
    {
        System.out.println("STOPPED");
        sensorManager.unregisterListener(accelLst);
        accelLst.series.writeToCSV("data_"+System.currentTimeMillis()+".csv", getApplicationContext());
        accelLst.series.clear();
    }
}
