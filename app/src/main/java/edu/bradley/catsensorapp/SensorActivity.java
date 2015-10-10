package edu.bradley.catsensorapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SensorActivity extends ActionBarActivity
{
    Context context;
    SensorManager sensorManager;
    Sensor accel, mag;
    SensorListener accelLst, magLst;
    boolean recording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        accelLst = new SensorListener(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(recording)
            sensorManager.registerListener(accelLst, accel, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(recording)
            sensorManager.unregisterListener(accelLst);
    }

    public void startRecording(View view)
    {
        if(!recording)
        {
            System.out.println("STARTED");
            recording = true;
            sensorManager.registerListener(accelLst, accel, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Already Recording", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopRecording(View view)
    {
        if(recording)
        {
            System.out.println("STOPPED");
            recording = false;
            sensorManager.unregisterListener(accelLst);
            accelLst.series.writeToCSV("data_" + System.currentTimeMillis() + ".csv", getApplicationContext());
            accelLst.series.clear();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Not Currently Recording", Toast.LENGTH_SHORT).show();
        }
    }

    public void buttonStateChange(View view)
    {
        TimeSensorData.TractorState newState = TimeSensorData.TractorState.UNKNOWN;
        switch(view.getId())
        {
            case R.id.stoppedButton:
                newState = TimeSensorData.TractorState.STOPPED;
                break;
            case R.id.loadingButton:
                newState = TimeSensorData.TractorState.LOADING;
                break;
            case R.id.unloadingButton:
                newState = TimeSensorData.TractorState.UNLOADING;
                break;
            case R.id.movingButton:
                newState = TimeSensorData.TractorState.MOVING;
                break;
        }
        accelLst.tracState = newState;

        ((TextView)findViewById(R.id.stateTextView)).setText("Current State: " + TimeSensorData.getStateString(newState));
    }
}
