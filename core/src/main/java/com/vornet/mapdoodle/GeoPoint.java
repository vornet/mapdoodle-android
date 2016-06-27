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
