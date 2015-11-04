package edu.bradley.catsensorapp;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.bradley.catsensorapp.csvdatatypes.ICsvWritable;

/**
 * Created by dakotaleonard on 10/5/15.
 * No Longer Used
 */
public class TimeSeriesSensorData implements Iterable<TimeSensorData>
{
    final private short DEFAULT_SIZE = 100;
    private TimeSensorData[] dataPoints;
    private int curInsertIndex = 0;
    private long startTime;

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

    public TimeSeriesSensorData()
    {
        dataPoints = new TimeSensorData[DEFAULT_SIZE];
    }

    public TimeSensorData StorePoint(ICsvWritable value, int sensorType, TimeSensorData.TractorState curState, long time)
    {
        TimeSensorData newData = new TimeSensorData(value, time-startTime, sensorType, curState);
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

    public void writeToCSV(String name, Context context)
    {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + File.separator + name);
        System.out.println(file + "\t" + file.exists());
        FileOutputStream os = null;
        try
        {
            System.out.println(file);
            os = new FileOutputStream(file);
            for(TimeSensorData d : this)
            {
                os.write(String.format("%s,%d,%s\n",d.getValue().getCsvSegment(), d.getTime(),TimeSensorData.getStateString(d.getState())).getBytes());
            }
            Toast.makeText(context, "Saved file to documents", Toast.LENGTH_LONG).show();

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
    }
}
