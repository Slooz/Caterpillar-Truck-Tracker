package edu.bradley.catsensorapp;

/**
 * Created by dakotaleonard on 10/5/15.
 */
public class TimeSensorData
{
    public enum CatSensorType {ACCEL, ORIENT};

    public float x, y, z;
    private long deltaTime;

    public TimeSensorData(float[] values, long deltaTime)
    {
        x = values[0];
        y = values[1];
        z = values[2];
        this.deltaTime = deltaTime;
    }

    public float getX()
    {
        return x;
    }

    public void setX(float x)
    {
        this.x = x;
    }

    public float getY()
    {
        return y;
    }

    public void setY(float y)
    {
        this.y = y;
    }

    public float getZ()
    {
        return z;
    }

    public void setZ(float z)
    {
        this.z = z;
    }

    public long getDeltaTime()
    {
        return deltaTime;
    }

    public void setDeltaTime(long deltaTime)
    {
        this.deltaTime = deltaTime;
    }

}
