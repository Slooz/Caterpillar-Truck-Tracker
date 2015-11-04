package edu.bradley.catsensorapp;

import edu.bradley.catsensorapp.csvdatatypes.ICsvWritable;

/**
 * Created by dakotaleonard on 10/5/15.
 */
public class TimeSensorData<DataType extends ICsvWritable>
{
    enum TractorState {UNKNOWN, MOVING, LOADING, UNLOADING, STOPPED};
    public TractorState state;
    public DataType value;
    public int sensorType;
    public long time;

    public TimeSensorData(DataType value, final long time, final int sensorType, final TractorState state)
    {
        this.value = value;
        this.time = time;
        this.sensorType = sensorType;
        this.state = state;
    }

    public DataType getValue()
    {
        return value;
    }

    public void setValue(DataType value)
    {
        this.value = value;
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
