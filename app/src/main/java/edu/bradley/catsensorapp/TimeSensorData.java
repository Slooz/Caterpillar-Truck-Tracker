package edu.bradley.catsensorapp;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * Created by dakotaleonard on 10/5/15.
 */
public class TimeSensorData
{
    enum TractorState {UNKNOWN, MOVING, LOADING, UNLOADING, STOPPED};
    public TractorState state;
    public float x, y, z;
    public int sensorType;
    public long time;

    public TimeSensorData(final float[] values, final long time, final int sensorType, final TractorState state)
    {
        x = values[0];
        y = values[1];
        z = values[2];
        this.time = time;
        this.sensorType = sensorType;
        this.state = state;
    }

    public float getX()
    {
        return x;
    }

    public void setX(final float x)
    {
        this.x = x;
    }

    public float getY()
    {
        return y;
    }

    public void setY(final float y)
    {
        this.y = y;
    }

    public float getZ()
    {
        return z;
    }

    public void setZ(final float z)
    {
        this.z = z;
    }

    public int getSensorType()
    {
        return sensorType;
    }

    public void setSensorType(final int sensorType)
    {
        this.sensorType = sensorType;
    }

    public long getTime()
    {
        return time;
    }

    public void setTime(final long time)
    {
        this.time = time;
    }

    public TractorState getState()
    {
        return state;
    }

    public void setState(TractorState state)
    {
        this.state = state;
    }

    public static String getStateString(TractorState state)
    {
        switch (state)
        {
            case STOPPED:
                return "Stopped";
            case UNKNOWN:
                return "Unknown";
            case MOVING:
                return "Moving";
            case LOADING:
                return "Loading";
            case UNLOADING:
                return "Unloading";
        }
        return "";
    }
}
