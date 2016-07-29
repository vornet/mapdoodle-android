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
    private boolean mShouldRedraw = true;
    private boolean mIsBeingRemoved = false;
    private MapDoodler mCanvas;

    public AnimatedPathDoodle(MapDoodler canvas, AnimatedPathDoodleStyle style, GeoPoint[] points) {
        mCanvas = canvas;
        mStyle = style;
        mGeoPoints = points;
    }

    private void calculatePathPoints() {
        Point[] path = new Point[mGeoPoints.length];
        for(int i = 0; i < mGeoPoints.length; i++) {
            GeoPoint geoPoint = mGeoPoints[i];

            // Normalize the coordinates.
            double x = (geoPoint.getLongitude() + 180);
            double y = (geoPoint.getLatitude() + 90);

            path[i] = new Point(x, y);
        }

        // Each degree of latitude/longitude is approximately 69 miles (111 kilometers) apart.
        double speedInLatLong = mStyle.getSpeed() / 111 / 3600 / 1000 * mCanvas.getRefreshRate();
        //speedInLatLong = 0.000075;

        // Not very accurate if the points are too far apart, but good enough for close distances.
        Point[] pointsBetweenPaths = MathUtil.pointsBetweenPath(path, speedInLatLong);

        mAnimationContext.points = new GeoPoint[pointsBetweenPaths.length];
        // Unnormalize back to GPS coordinates.
        for(int i = 0; i < pointsBetweenPaths.length; i++) {
            mAnimationContext.points[i] = new GeoPoint(pointsBetweenPaths[i].getY() - 90, pointsBetweenPaths[i].getX() - 180);
        }
    }

    @Override
    public void draw(DoodleContext context) {
        if (mIsBeingRemoved) {
            return;
        }

        if (mPolyline == null || mShouldRedraw) {
            if (mPolyline != null) {
                mPolyline.remove();
                mTracerDoodle.remove();
            }

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
            calculatePathPoints();
            mShouldRedraw = false;
        }

        // Draw the tracer.
        if (mTracerDoodle != null) {
            mTracerDoodle.draw(context);
        }

        GeoPoint currentPoint = mAnimationContext.points[mAnimationContext.currentPointIndex];
        mAnimationContext.drawPointList.add(new com.google.android.gms.maps.model.LatLng(currentPoint.getLatitude(), currentPoint.getLongitude()));
        mAnimationContext.polyline.setPoints(mAnimationContext.drawPointList);
        mAnimationContext.currentPointIndex++;

        if(mAnimationContext.currentPointIndex >= mAnimationContext.points.length) {
            mAnimationContext.drawPointList.clear();
            mAnimationContext.currentPointIndex = 0;
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

    @Override
    public void setId(String id) {
        this.mId = id;
    }

    @Override
    public void setStyle(Object style) {
        AnimatedPathDoodleStyle animatedPathDoodleStyle = (style instanceof AnimatedPathDoodleStyle ? (AnimatedPathDoodleStyle) style : null);
        mStyle = animatedPathDoodleStyle;
        mShouldRedraw = true;
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
        public GeoPoint[] points;
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
