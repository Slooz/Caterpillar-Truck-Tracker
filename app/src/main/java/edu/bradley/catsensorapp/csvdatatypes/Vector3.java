package edu.bradley.catsensorapp.csvdatatypes;

/**
 * Implementation of a 3 dimensional vector which allows for insertion of x,y,z data into a csv file line
 * Created by dakotaleonard on 10/29/15.
 */
public class Vector3 implements ICsvWritable
{
    float x,y,z;

    /**
     * Creates a vector 3 based upon passed x, y, and z
     * @param x
     * @param y
     * @param z
     */
    public Vector3(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Creates a vector3 with all components set to 0
     */
    public Vector3()
    {
        x = y = z = 0f;
    }

    public void setX(float x)
    {
        this.x = x;
    }

    public float getX()
    {
        return x;
    }

    public void setY(float y)
    {
        this.y = y;
    }

    public float getY()
    {
        return y;
    }

    public void setZ(float z)
    {
        this.z = z;
    }

    public float getZ()
    {
        return z;
    }

    /**
     * Returns a string for insertion into a csv line
     * @return Formatted data as x,y,z
     */
    @Override
    public String getCsvSegment()
    {
        return String.format("%f,%f,%f",x,y,z);
    }
}
