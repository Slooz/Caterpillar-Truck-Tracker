package edu.bradley.catsensorapp;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by dakotaleonard on 10/5/15.
 * No Longer Used
 */
public class TimeSeriesSensorData implements Iterable<TimeSensorData>, Iterator<TimeSensorData>
{
    final private short DEFAULT_SIZE = 100;
    private TimeSensorData[] dataPoints;
    private int curInsertIndex = 0;
    private int iterIndex = 0;
    public long startTime = 0l;

    public TimeSeriesSensorData()
    {
        dataPoints = new TimeSensorData[DEFAULT_SIZE];
        startTime = System.currentTimeMillis();
    }

    public TimeSensorData StorePoint(float[] values, TimeSensorData.CatSensorType sensorType)
    {
        TimeSensorData newData = new TimeSensorData(values, System.currentTimeMillis() - startTime);
        if(curInsertIndex >= dataPoints.length)
        {
            //Expand
            dataPoints = Arrays.copyOf(dataPoints, dataPoints.length*2);
        }

        dataPoints[curInsertIndex++] = newData;

        return newData;
    }

    public TimeSensorData getAtIndex(int index) throws IndexOutOfBoundsException
    {
        if(index >= curInsertIndex)
            throw new IndexOutOfBoundsException("Given index ("+index+") is out of range for series of size ("+curInsertIndex+")");
        return dataPoints[index];
    }


    @Override
    public Iterator<TimeSensorData> iterator()
    {
        return this;
    }

    @Override
    public boolean hasNext()
    {
        return iterIndex < curInsertIndex;
    }

    @Override
    public TimeSensorData next() throws NoSuchElementException
    {
        if(hasNext())
            return dataPoints[iterIndex++];
        else
            throw new NoSuchElementException("No element exists pass current elements pointed to by iterator");
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
