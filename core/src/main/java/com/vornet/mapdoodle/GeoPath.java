package com.vornet.mapdoodle;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vorn Mom (vornet)
 */

public class GeoPath {
    private ArrayList<GeoPoint> mGeoPoints = new ArrayList<>();

    public void addPoint(double latitude, double longitude) {
        addPoint(new GeoPoint(latitude, longitude));
    }

    public void addPoint(GeoPoint point) {
        mGeoPoints.add(point);
    }

    public GeoPoint getPoint(int index) {
        return mGeoPoints.get(index);
    }

    List<LatLng> toLatLngList() {
        ArrayList<LatLng> latLngList = new ArrayList<>();

        for(GeoPoint point : mGeoPoints) {
            latLngList.add(new LatLng(point.getLatitude(), point.getLongitude()));
        }

        return latLngList;
    }

    public LatLngBounds toLatLngBounds() {
        LatLngBounds.Builder lb = LatLngBounds.builder();

        for(GeoPoint point : mGeoPoints) {
            lb.include(new LatLng(point.getLatitude(), point.getLongitude()));
        }

        return lb.build();
    }

    public int size() {
        return mGeoPoints.size();
    }
}
