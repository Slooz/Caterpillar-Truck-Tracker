package edu.bradley.catsensorapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DataGatheringService extends Service
{
    public DataGatheringService()
    {
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
