package com.vornet.mapdoodle;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vorn on 7/4/2016.
 */

public class AnimatedPathDoodle implements Doodle {
    private String mId;
    private GeoPoint[] mGeoPoints;
    private AnimatedPathDoodleStyle mStyle;
    private Polyline mPolyline;
    private PathDoodle mTracerDoodle;
    private AnimationContext mAnimationContext;
    private boolean mIsBeingRemoved = false;
    private MapDoodler mCanvas;

    public AnimatedPathDoodle(MapDoodler canvas, AnimatedPathDoodleStyle style, GeoPoint[] points) {
        mCanvas = canvas;
        mStyle = style;
        mGeoPoints = points;
    }

    @Override
    public void draw(DoodleContext context) {
        if (mIsBeingRemoved) {
            return;
        }

        if (mPolyline == null) {

            if (mStyle.getTracerThickness() > 0) {
                PathDoodleStyle pathStyle = new PathDoodleStyle();
                pathStyle.setThickness(mStyle.getTracerThickness());
                pathStyle.setColor(mStyle.getTracerColor());
                pathStyle.setZIndex(mStyle.getZIndex());

                mTracerDoodle = new PathDoodle(mCanvas, pathStyle, mGeoPoints);
                mTracerDoodle.draw(context);

            }

            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.width((float) mStyle.getThickness());
            polylineOptions.color(mStyle.getColor());

            mPolyline = context.getMap().addPolyline(polylineOptions);
            mPolyline.setPoints(toLatLngList());
            mPolyline.setZIndex(mStyle.getZIndex() + 1);
            mAnimationContext = new AnimationContext(mPolyline);
        }

        // Draw the tracer.
        if (mTracerDoodle != null) {
            mTracerDoodle.draw(context);
        }

        GeoPoint startPoint = mGeoPoints[mAnimationContext.currentPointIndex];
        GeoPoint endPoint = mGeoPoints[mAnimationContext.currentPointIndex + 1];

        double la;
        double lo;

        if (startPoint.equals(endPoint)) {
            mAnimationContext.currentPointIndex++;
            return;
        }

        // Distance
        double distance = calculateDistance(startPoint, endPoint) / 1000;

        double speedInMs = mStyle.getSpeed() / 3600 / 1000;
        double divider = distance * 1 / (speedInMs * mCanvas.getRefreshRate());

        double step = Math.sqrt(Math.pow(endPoint.getLatitude() - startPoint.getLatitude(), 2) + Math.pow(endPoint.getLongitude() - startPoint.getLongitude(), 2)) / divider;

        double x0 = startPoint.getLatitude();
        double y0 = startPoint.getLongitude();
        double x1 = endPoint.getLatitude();
        double y1 = endPoint.getLongitude();
        // Slope
        double m = (y1 - y0) / (x1 - x0);

        // flow direction
        if (x1 < x0) {
            la = x0 - mAnimationContext.xOffset / Math.sqrt(1 + Math.pow(m, 2));
        } else {
            la = x0 + mAnimationContext.xOffset / Math.sqrt(1 + Math.pow(m, 2));
        }
        lo = m * (la - x0) + y0;
        mAnimationContext.xOffset += step;

        if (Math.abs(la - endPoint.getLatitude()) < mAnimationContext.endPointLatDiff &&
                Math.abs(lo - endPoint.getLongitude()) < mAnimationContext.endPointLongDiff) {
            mAnimationContext.endPointLatDiff = Math.abs(la - endPoint.getLatitude());
            mAnimationContext.endPointLongDiff = Math.abs(lo - endPoint.getLongitude());
            mAnimationContext.drawPointList.add(new com.google.android.gms.maps.model.LatLng(la, lo));
            mAnimationContext.polyline.setPoints(mAnimationContext.drawPointList);
        } else {
            mAnimationContext.currentPointIndex++;
            mAnimationContext.xOffset = 0;
            mAnimationContext.endPointLatDiff = 2147483647;
            mAnimationContext.endPointLongDiff = 2147483647;

            if (mAnimationContext.currentPointIndex >= mGeoPoints.length - 1) {
                mAnimationContext.drawPointList.clear();
                mAnimationContext.currentPointIndex = 0;
                mAnimationContext.xOffset = 0;
            }

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
        if (mTracerDoodle != null) {
            mTracerDoodle.remove();
        }
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

    private double toRadians(double val) {
        return (Math.PI / 180) * val;
    }

    private double calculateDistance(GeoPoint point1, GeoPoint point2) {
        double R = 6371000;
        double o1 = toRadians(point1.getLatitude());
        double o2 = toRadians(point2.getLatitude());
        double dd = toRadians(point2.getLatitude() - point1.getLatitude());
        double dl = toRadians(point2.getLongitude() - point1.getLongitude());

        double a = Math.sin(dd / 2) * Math.sin(dd / 2) +
                Math.cos(o1) * Math.cos(o2) *
                        Math.sin(dl / 2) * Math.sin(dl / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private class AnimationContext {
        public int currentPointIndex;
        public double xOffset;
        public List<LatLng> drawPointList;
        public Polyline polyline;
        public double endPointLatDiff = 360;
        public double endPointLongDiff = 360;

        public AnimationContext(Polyline polyline) {
            drawPointList = new ArrayList<>();
            this.polyline = polyline;
        }
    }
}
