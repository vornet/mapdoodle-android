package com.vornet.mapdoodle;

/**
 * @author Vorn Mom (vornet)
 */

public class GeoPoint {
    private double mLatitude;
    private double mLongitude;

    public double getLatitude() {
        return mLatitude;
    }
    public double getLongitude() {
        return mLongitude;
    }

    public void setLatitude(double value) {
        mLatitude =  value;
    }
    public void setLongitude(double value) {
        mLongitude =  value;
    }

    public GeoPoint() {

    }

    public GeoPoint(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    @Override
    public boolean equals(Object otherObj) {
        GeoPoint otherPoint = (GeoPoint)otherObj;
        return otherPoint.getLatitude() == mLatitude &&
                otherPoint.getLongitude() == mLongitude;
    }
}
