package com.vornet.mapdoodle;

import android.app.Activity;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vorn on 6/26/2016.
 */

public class GeoPathAnimator {
    public final int UPDATE_RATE = 500;

    private Activity mActivity;
    private GoogleMap mMap;
    private GeoPath mPathToAnimate;
    private double mSpeed;

    private ScheduledExecutorService mScheduledPool;
    private ScheduledFuture<?> mScheduler;
    private AnimationContext mAnimationContext;
    private Runnable mAnimateRunnable = new Runnable() {
        @Override
        public void run() {
            GeoPoint startPoint = mPathToAnimate.getPoint(mAnimationContext.currentPointIndex);
            GeoPoint endPoint = mPathToAnimate.getPoint(mAnimationContext.currentPointIndex + 1);

            double la;
            double lo;

            if (startPoint.equals(endPoint)) {
                mAnimationContext.currentPointIndex++;
                return;
            }

            // Distance
            double distance = calculateDistaince(startPoint, endPoint) / 1000;

            double speedInMs = mSpeed / 3600 / 1000;
            double divider = distance * 1 / (speedInMs * UPDATE_RATE);

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

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAnimationContext.polyline.setPoints(mAnimationContext.drawPointList);
                    }
                });

            } else {
                mAnimationContext.currentPointIndex++;
                mAnimationContext.xOffset = 0;
                mAnimationContext.endPointLatDiff = 2147483647;
                mAnimationContext.endPointLongDiff = 2147483647;

                if (mAnimationContext.currentPointIndex >= mPathToAnimate.size() - 1) {
                    mAnimationContext.drawPointList.clear();
                    mAnimationContext.currentPointIndex = 0;
                    mAnimationContext.xOffset = 0;
                }

            }
        }
    };

    public GeoPathAnimator(Activity activity, GoogleMap map, GeoPath pathToAnimate, double speed) {
        mActivity = activity;
        mMap = map;
        mPathToAnimate = pathToAnimate;
        mScheduledPool = Executors.newScheduledThreadPool(1);
        mSpeed = speed;
    }

    public void start() {
        mAnimationContext = new AnimationContext();

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(10);
        polylineOptions.color(Color.BLUE);

        mAnimationContext.polyline = mMap.addPolyline(polylineOptions);

        mScheduler = mScheduledPool.scheduleWithFixedDelay(mAnimateRunnable, 500, UPDATE_RATE, TimeUnit.MILLISECONDS);
    }

    public void pause() {

    }

    public void stop() {
        mScheduler.cancel(false);
        mAnimationContext.drawPointList.clear();
        mAnimationContext.currentPointIndex = 0;
        mAnimationContext.xOffset = 0;
    }

    private double toRadians(double val) {
        return (Math.PI / 180) * val;
    }

    private double calculateDistaince(GeoPoint point1, GeoPoint point2) {
        double R = 6371000;
        double φ1 = toRadians(point1.getLatitude());
        double φ2 = toRadians(point2.getLatitude());
        double Δφ = toRadians(point2.getLatitude() - point1.getLatitude());
        double Δλ = toRadians(point2.getLongitude() - point1.getLongitude());

        double a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
                Math.cos(φ1) * Math.cos(φ2) *
                        Math.sin(Δλ / 2) * Math.sin(Δλ / 2);
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

        public AnimationContext() {
            drawPointList = new ArrayList<>();
        }
    }
}
