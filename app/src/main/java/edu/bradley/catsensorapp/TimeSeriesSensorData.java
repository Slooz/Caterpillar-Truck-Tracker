package edu.bradley.catsensorapp;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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

    public TimeSeriesSensorData()
    {
        dataPoints = new TimeSensorData[DEFAULT_SIZE];
    }

    public TimeSensorData StorePoint(float[] values, int sensorType, TimeSensorData.TractorState curState)
    {
        TimeSensorData newData = new TimeSensorData(values, System.currentTimeMillis(), sensorType, curState);
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

    /**
     * Returns data across time span, each data point is in an array of size 4
     * 0 - Time
     * 1 - X
     * 2 - Y
     * 3 - Z
     * @param startTime Earliest time of time series data to be returned
     * @param endTime Latest time of time series data to be returned
     * @return Array of array outer array is in order of time, inner array is descrived above
     */
    public List<float[]> getDataAcrossTimeSpan(long startTime, long endTime)
    {
        List<float[]> dataPointsToRet = new ArrayList<float[]>();
        boolean startedAdding = false;

        for(TimeSensorData data : this)
        {
            //Check if we have entered time span, if not check if we need to
            if(!startedAdding)
            {
                startedAdding = data.getTime() >= startTime;
            }

            if(startedAdding)
            {
                if(data.getTime() > endTime)
                    break;
                else
                {
                    float[] toAdd = new float[4];
                    toAdd[0] = data.getTime();
                    toAdd[1] = data.getX();
                    toAdd[2] = data.getY();
                    toAdd[3] = data.getZ();
                    dataPointsToRet.add(toAdd);
                }
            }
        }

        return dataPointsToRet;
    }

    public void writeToCSV(String name, Context con)
    {
        File file = new File(Environment.getExternalStorageDirectory() + "/Documents", name);
        FileOutputStream os = null;
        try
        {
            System.out.println(file);
            os = new FileOutputStream(file);
            os.write(new String("TEST").getBytes());
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                os.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    public void clear()
    {
        dataPoints = new TimeSensorData[DEFAULT_SIZE];
        iterIndex = 0;
        curInsertIndex = 0;
    }
}
