package edu.bradley.catsensorapp.csvdatatypes;

import android.location.Location;

/**
 * Class representing a piece of GPS data
 * Contains the angles for longitude and latitude, the bearing angle, altitude, and current speed
 * See type definitions and references in {@link android.location.Location}
 * Created by dakotaleonard on 10/30/15.
 */
public class GPSData implements ICsvWritable
{
    double longitude, latitude, altitude;
    float bearing, speed;

    /**
     * Creates data point based off of GPS location Data
     * @param location
     */
    public GPSData(Location location)
    {
        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
        this.altitude = location.getAltitude();
        this.bearing = location.getBearing();
        this.speed = location.getSpeed();
    }

    /**
     * Creates a datapoint directly assigning values
     * @param longitude
     * @param latitude
     * @param altitude
     * @param bearing
     * @param speed
     */
    public GPSData(double longitude, double latitude, double altitude, float bearing, float speed)
    {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.bearing = bearing;
        this.speed = speed;
    }

    /**
     * Creates datapoint with all values set to 0
     */
    public GPSData()
    {
        longitude = latitude = altitude = 0.0;
        bearing = speed = 0f;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getAltitude()
    {
        return altitude;
    }

    public void setAltitude(double altitude)
    {
        this.altitude = altitude;
    }

    public float getBearing()
    {
        return bearing;
    }

    public void setBearing(float bearing)
    {
        this.bearing = bearing;
    }

    public float getSpeed()
    {
        return speed;
    }

    public void setSpeed(float speed)
    {
        this.speed = speed;
    }

    @Override
    public String getCsvSegment()
    {
        return String.format("%f,%f,%f,%f,%f", longitude, latitude, altitude, bearing, speed);
    }
}
