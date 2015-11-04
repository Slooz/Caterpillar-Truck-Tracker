package edu.bradley.catsensorapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class SensorActivity extends ActionBarActivity
{
    SensorManager sensorManager;
    LocationManager locManager;

    Vector3SensorListener v3Listener;
    GPSListener gpsListener;
    Criteria criteria;

    boolean recording = false;
    Sensor sensors[];
    TextView recordingTextView;

    public TimeSensorData.TractorState tracState;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        tracState = TimeSensorData.TractorState.STOPPED;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        v3Listener = new Vector3SensorListener(this);
        gpsListener = new GPSListener(this);

        //Setup sensors and listener for that sensor
        sensors = new Sensor[5];
        sensors[0] = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensors[1] = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensors[2] = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensors[3] = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensors[4] = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        for(Sensor s : sensors)
        {
            v3Listener.addSensor(s);
        }

        recordingTextView = (TextView)findViewById(R.id.recordingText);

        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
    }

    private void registerListeners()
    {
        for(Sensor s : sensors)
            sensorManager.registerListener(v3Listener, s, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterListeners()
    {
        for(Sensor s : sensors)
            sensorManager.registerListener(v3Listener, s, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(recording)
            registerListeners();
        locManager.requestLocationUpdates(locManager.getBestProvider(criteria, true), 0, 0, gpsListener);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(recording)
            unregisterListeners();
        locManager.removeUpdates(gpsListener);
    }

    public void startRecording(View view)
    {
        if(!recording)
        {
            recording = true;
            recordingTextView.setText("Recording");
            registerListeners();

            Toast.makeText(getApplicationContext(), "Recording...", Toast.LENGTH_SHORT).show();
            System.out.println("STARTED");
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
            recordingTextView.setText("Not Recording");
            recording = false;
            unregisterListeners();

            //Create folders
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df.setTimeZone(tz);
            String curTimeStamp = df.format(new Date());

            Toast.makeText(getApplicationContext(), "Saving...", Toast.LENGTH_SHORT).show();

            File rootDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + File.separator + curTimeStamp);
            rootDir.mkdir();
            File csvDir = new File(rootDir.getAbsolutePath() + File.separator + "csv");
            csvDir.mkdir();
            File serialDir = new File(rootDir.getAbsolutePath() + File.separator + "serial");
            serialDir.mkdir();

            //Write with listeners
            v3Listener.writeFiles(csvDir, serialDir, getApplicationContext());
            gpsListener.writeFiles(csvDir, serialDir, getApplicationContext());

            //Delete data
            for(TimeSeriesSensorData s : v3Listener.series)
            {
                s.clear();
            }

            Toast.makeText(getApplicationContext(), "Saved file to documents", Toast.LENGTH_LONG).show();
            gpsListener.locationData.clear();
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

        synchronized (tracState)
        {
            tracState = newState;
        }
        ((TextView)findViewById(R.id.stateTextView)).setText("Current State: " + TimeSensorData.getStateString(newState));
    }
}
