package edu.bradley.catsensorapp.csvdatatypes;

/**
 * Created by dakotaleonard on 10/29/15.
 */
public interface ICsvWritable
{
    /**
     * Converts object into string which can be inserted into a line of csv file
     * Individual fields need to be comma seperated but beginning and end is assumed to be taken care of by called
     * @return CSV stub generated from data
     */
    public String getCsvSegment();
}
