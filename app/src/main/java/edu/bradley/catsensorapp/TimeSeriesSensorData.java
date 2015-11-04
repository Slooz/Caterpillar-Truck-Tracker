package edu.bradley.catsensorapp;

import android.content.Context;
import android.hardware.Sensor;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.bradley.catsensorapp.csvdatatypes.GPSData;
import edu.bradley.catsensorapp.csvdatatypes.ICsvWritable;
import edu.bradley.catsensorapp.csvdatatypes.Vector3;

/**
 * Created by dakotaleonard on 10/5/15.
 * No Longer Used
 */
public class TimeSeriesSensorData implements Iterable<TimeSensorData> , Serializable
{
    final private short DEFAULT_SIZE = 100;
    private TimeSensorData[] dataPoints;
    private int curInsertIndex = 0;
    private long startTime;
    public String sensor;

    /**
     * Iterator for TimeSeriesSensorData class
     */
    private class selfIter implements Iterator<TimeSensorData>
    {
        TimeSeriesSensorData series;
        int iterIndex;

        public selfIter(TimeSeriesSensorData series)
        {
            startTime = System.currentTimeMillis();
            this.series = series;
            iterIndex = 0;
        }

        @Override
        public boolean hasNext()
        {
            return iterIndex < series.curInsertIndex;
        }

        @Override
        public TimeSensorData next()
        {
            if(hasNext())
                return series.dataPoints[iterIndex++];
            else
                throw new NoSuchElementException("No element exists pass current elements pointed to by iterator");
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }

    public TimeSeriesSensorData(Sensor sensor)
    {
        this.sensor = sensor.getName();
        dataPoints = new TimeSensorData[DEFAULT_SIZE];
        startTime = System.currentTimeMillis();
    }

    public TimeSeriesSensorData()
    {
        dataPoints = new TimeSensorData[DEFAULT_SIZE];
        startTime = System.currentTimeMillis();
    }

    public TimeSensorData StorePoint(ICsvWritable value, TimeSensorData.TractorState curState, long time)
    {
        TimeSensorData newData = new TimeSensorData(value, time-startTime, curState);
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
        return new selfIter(this);
    }

    public void writeSerial (File file, Context context) throws Exception
    {
        if (dataPoints.length == 0)
        {
            throw new Exception("This data series contains no data points");
        }

        ObjectOutputStream oos = null;
        try
        {
            System.out.println(file);
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(this);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                oos.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void writeToCSV(File file, Context context) throws Exception
    {
        if(dataPoints.length == 0)
        {
            throw new Exception("This data series contains no data points");
        }

        FileOutputStream os = null;
        try
        {
            System.out.println(file);
            os = new FileOutputStream(file);
            if(dataPoints[0].getValue() instanceof Vector3)
            {
                os.write(new String("x,y,z,timestamp,state\n").getBytes());
            }
            else if(dataPoints[0].getValue() instanceof GPSData)
            {
                os.write(new String("longitude,latitude,altitude,bearing,speed,timestamp,state\n").getBytes());
            }

            for(TimeSensorData d : this)
            {
                os.write(String.format("%s,%d,%s\n",d.getValue().getCsvSegment(), d.getTime(),TimeSensorData.getStateString(d.getState())).getBytes());
            }

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
        curInsertIndex = 0;
        startTime = System.currentTimeMillis();
    }
}
