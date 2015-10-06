package edu.bradley.catsensorapp;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * Created by dakotaleonard on 10/5/15.
 */
public class TimeSensorData
{

    public float x, y, z;
    public int sensorType;
    public long time;

    public TimeSensorData(final float[] values, final long time, final int sensorType)
    {
        x = values[0];
        y = values[1];
        z = values[2];
        this.time = time;
        this.sensorType = sensorType;
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

}
