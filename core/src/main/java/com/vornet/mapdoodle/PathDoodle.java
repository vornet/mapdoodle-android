package com.vornet.mapdoodle;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vorn Mom (vornet)
 */

class PathDoodle implements Doodle {
    private String mId;
    private GeoPoint[] mGeoPoints;
    private PathDoodleStyle mStyle;
    private Polyline mPolyline;
    private boolean mShouldRedraw = true;
    private boolean mIsBeingRemoved = false;
    private MapDoodler mCanvas;

    public PathDoodle(MapDoodler canvas, PathDoodleStyle style, GeoPoint[] points) {
        mCanvas = canvas;
        mStyle = style;
        mGeoPoints = points;
    }

    public LatLngBounds toLatLngBounds() {
        LatLngBounds.Builder lb = LatLngBounds.builder();

        for (GeoPoint point : mGeoPoints) {
            lb.include(new LatLng(point.getLatitude(), point.getLongitude()));
        }

        return lb.build();
    }

    @Override
    public void draw(DoodleContext context) {
        if (mIsBeingRemoved) {
            return;
        }
        if (mPolyline == null || mShouldRedraw) {
            if (mPolyline != null) {
                mPolyline.remove();
            }
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.width((float) mStyle.getThickness());
            polylineOptions.color(mStyle.getColor());

            mPolyline = context.getMap().addPolyline(polylineOptions);
            mPolyline.setPoints(toLatLngList());
            mPolyline.setZIndex(mStyle.getZIndex());
            mShouldRedraw = false;
        }
    }

    @Override
    public void remove() {
        if (mIsBeingRemoved) {
            return;
        }

        mIsBeingRemoved = true;
        if (mPolyline != null) {
            mPolyline.remove();
            mPolyline = null;
        }
    }

    @Override
    public void setStyle(Object style) {
        PathDoodleStyle pathDoodleStyle = (style instanceof PathDoodleStyle ? (PathDoodleStyle) style : null);
        mStyle = pathDoodleStyle;
        mShouldRedraw = true;
    }

    @Override
    public GeoPoint[] getBounds() {
        return LocationUtil.getBounds(mGeoPoints);
    }

    @Override
    public GeoPoint[] getPoints() {
        return mGeoPoints;
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public void setId(String id) {
        this.mId = id;
    }

    private List<LatLng> toLatLngList() {
        ArrayList<LatLng> latLngList = new ArrayList<>();

        for (GeoPoint point : mGeoPoints) {
            latLngList.add(new LatLng(point.getLatitude(), point.getLongitude()));
        }

        return latLngList;
    }
}
