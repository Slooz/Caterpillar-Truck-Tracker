/*
 * Copyright 2015 Nathan Clark, Krzysztof Czelusniak, Michael Holwey, Dakota Leonard
 */

package edu.bradley.cattrucktracker;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class TruckTrackerActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, SeekBar.OnSeekBarChangeListener {
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TruckTrackerService.TruckState truckState = (TruckTrackerService.TruckState) intent
                    .getSerializableExtra(TruckTrackerService.TRUCK_STATE_EXTRA);
            TextView textView = (TextView)findViewById(R.id.stateValueLabel);
            TruckTrackerActivity.truckState = truckState;
            textView.setText(truckState.name());
        }
    };
    private GoogleApiClient googleApiClient;
    private LocalBroadcastManager localBroadcastManager;
    private Intent truckTrackerServiceIntent;
    private TruckTrackerService truckTrackerService;
    private static TruckTrackerService.TruckState truckState;
    private final ServiceConnection truckTrackerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            truckTrackerService
                    = ((TruckTrackerService.TruckTrackerServiceBinder) service)
                    .truckTrackerService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onConnected(Bundle connectionHint) {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(0);

        PendingIntent pendingIntent
                = PendingIntent.getService(this, 0, truckTrackerServiceIntent, 0);

        LocationServices.FusedLocationApi
                .requestLocationUpdates(googleApiClient, locationRequest, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, TruckTrackerService.class);
        startService(intent);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this).build();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        truckTrackerServiceIntent = new Intent(this, TruckTrackerService.class);

        setContentView(R.layout.truck_tracker_layout);

        SeekBar seekBar = (SeekBar)findViewById(R.id.vibrationThreshSlider);
        seekBar.setOnSeekBarChangeListener(this);

        SeekBar seekBar2 = (SeekBar)findViewById(R.id.speedThreshSlider);
        seekBar2.setOnSeekBarChangeListener(this);

        if (savedInstanceState != null) {
            truckState = (TruckTrackerService.TruckState) savedInstanceState.getSerializable(TruckTrackerService.TRUCK_STATE_EXTRA);
            if (truckState != null) {
                TextView textView = (TextView) findViewById(R.id.stateValueLabel);
                textView.setText(truckState.name());
            }
            int progress = savedInstanceState.getInt("vibrationThreshProgress", -1);
            if (progress != -1) {
                SeekBar seekBar3 = (SeekBar)findViewById(R.id.vibrationThreshSlider);
                seekBar3.setProgress(progress);
            }
            CharSequence charSequence = savedInstanceState.getCharSequence("vibrationThreshValue");
            if (charSequence != null) {
                TextView textView = (TextView)findViewById(R.id.vibrationThreshValue);
                textView.setText(charSequence);
            }
            int progress2 = savedInstanceState.getInt("speedThreshProgress", -1);
            if (progress2 != -1) {
                SeekBar seekBar3 = (SeekBar)findViewById(R.id.speedThreshSlider);
                seekBar3.setProgress(progress2);
            }
            CharSequence charSequence2 = savedInstanceState.getCharSequence("speedThreshValue");
            if (charSequence2 != null) {
                TextView textView = (TextView)findViewById(R.id.speedThreshValue);
                textView.setText(charSequence2);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        googleApiClient.connect();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TruckTrackerService.TRUCK_STATE_BROADCAST_ACTION);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        bindService(truckTrackerServiceIntent, truckTrackerServiceConnection, 0);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        truckState = (TruckTrackerService.TruckState)savedInstanceState.getSerializable(TruckTrackerService.TRUCK_STATE_EXTRA);
        if (truckState != null) {
            TextView textView = (TextView)findViewById(R.id.stateValueLabel);
            textView.setText(truckState.name());
        }
        int progress = savedInstanceState.getInt("vibrationThreshProgress", -1);
        if (progress != -1) {
            SeekBar seekBar = (SeekBar)findViewById(R.id.vibrationThreshSlider);
            seekBar.setProgress(progress);
        }
        CharSequence charSequence = savedInstanceState.getCharSequence("vibrationThreshValue");
        if (charSequence != null) {
            TextView textView = (TextView)findViewById(R.id.vibrationThreshValue);
            textView.setText(charSequence);
        }
        int progress2 = savedInstanceState.getInt("speedThreshProgress", -1);
        if (progress2 != -1) {
            SeekBar seekBar = (SeekBar)findViewById(R.id.speedThreshSlider);
            seekBar.setProgress(progress2);
        }
        CharSequence charSequence2 = savedInstanceState.getCharSequence("speedThreshValue");
        if (charSequence2 != null) {
            TextView textView = (TextView)findViewById(R.id.speedThreshValue);
            textView.setText(charSequence2);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState == null) {
            return;
        }
        outState.putSerializable(TruckTrackerService.TRUCK_STATE_EXTRA, truckState);
        SeekBar seekBar = (SeekBar)findViewById(R.id.vibrationThreshSlider);
        TextView textView = (TextView)findViewById(R.id.vibrationThreshValue);
        outState.putInt("vibrationThreshProgress", seekBar.getProgress());
        outState.putCharSequence("vibrationThreshValue", textView.getText());
        SeekBar seekBar2 = (SeekBar)findViewById(R.id.speedThreshSlider);
        TextView textView2 = (TextView)findViewById(R.id.speedThreshValue);
        outState.putInt("speedThreshProgress", seekBar2.getProgress());
        outState.putCharSequence("speedThreshValue", textView2.getText());
    }

    @Override
    protected void onStop() {
        unbindService(truckTrackerServiceConnection);

        localBroadcastManager.unregisterReceiver(broadcastReceiver);

        googleApiClient.disconnect();

        super.onStop();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (truckTrackerService == null) {
            return;
        }
        if (seekBar.getId() == R.id.vibrationThreshSlider) {
            truckTrackerService.setAccelerationThreshold(progress / 10.0f);
            TextView textView = (TextView)findViewById(R.id.vibrationThreshValue);
            textView.setText(progress / 10.0f + " m/s^2");
            return;
        }
        truckTrackerService.setSpeedThreshold(progress / 10.0f);
        TextView textView = (TextView)findViewById(R.id.speedThreshValue);
        textView.setText(progress / 10.0f + " m/s");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
