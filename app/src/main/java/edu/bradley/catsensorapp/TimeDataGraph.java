package edu.bradley.catsensorapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.util.AttributeSet;

import java.util.Arrays;
import java.util.List;

/**
 * NEEDS ALOT OF WORK
 * Created by dakotaleonard on 10/6/15.
 */
public class TimeDataGraph extends View
{

    public short millisPerPix = 5;
    public float curMax;


   TimeSeriesSensorData dataSeries;


    public TimeDataGraph(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void attachDataSeries(TimeSeriesSensorData dataSeries)
    {
        this.dataSeries = dataSeries;
    }

    public void detachDataSeries()
    {
        dataSeries = null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0x111111ff);
        canvas.drawRect(0,0,getWidth(),getHeight(),paint);
        if(dataSeries != null)
        {


            long baseTime = System.currentTimeMillis()- getWidth() * millisPerPix;
            long endTime = baseTime + getWidth() * millisPerPix;

            List<float[]> data = dataSeries.getDataAcrossTimeSpan(baseTime, endTime);
            float[] xData = new float[data.size() * 2];
            float[] yData = new float[data.size() * 2];
            float[] zData = new float[data.size() * 2];
            float newCurMax = 0;

            for (int i = 0; i < data.size(); ++i)
            {
                float[] f = data.get(i);

                //Convert time into x coord
                float timeAsX = (float) ((long) f[0] - baseTime) / millisPerPix;
                xData[i * 2] = yData[i * 2] = zData[i * 2] = timeAsX;

                //Convert data to ycoord
                float myZero = getHeight() / 2.0f;
                float valToPixRatio = (curMax) / getHeight();
                xData[i * 2 + 1] = f[1] * valToPixRatio;
                yData[i * 2 + 1] = f[2] * valToPixRatio;
                zData[i * 2 + 1] = f[3] * valToPixRatio;

                //Check if any of this data is new max
                for (int j = 0; j < 3; ++j)
                    if (Math.abs(f[j]) > newCurMax)
                        newCurMax = Math.abs(f[j]);
            }

            paint.setColor(Color.RED);
            canvas.drawLines(xData, paint);
            paint.setColor(Color.GREEN);
            canvas.drawLines(yData, paint);
            paint.setColor(Color.BLUE);
            canvas.drawLines(zData, paint);
            //PRINTOUT
            System.out.println(Arrays.toString(xData));
        }
    }
}
