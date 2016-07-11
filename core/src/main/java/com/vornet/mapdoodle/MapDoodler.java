package com.vornet.mapdoodle;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vorn on 7/3/2016.
 */

public class MapDoodler {
    private GoogleMap mMap;
    private Context mContext;
    private int mViewportWidth;
    private int mViewportHeight;
    private int mRefreshRate;

    private ArrayList<Doodle> mDoodles;

    private ScheduledExecutorService mScheduledPool;
    private ScheduledFuture<?> mScheduler;
    private Runnable mAnimateRunnable = new Runnable() {
        @Override
        public void run() {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    DoodleContext doodleContext = new DoodleContext(mMap, mRefreshRate);
                    for (Doodle doodle : mDoodles) {
                        doodle.draw(doodleContext);
                    }
                }
            });
        }
    };

    public MapDoodler(Context context, GoogleMap map, int viewPortWidth, int viewportHeight, int refreshRate) {
        mContext = context;
        mMap = map;
        mViewportWidth = viewPortWidth;
        mViewportHeight = viewportHeight;
        mRefreshRate = refreshRate;

        mScheduledPool = Executors.newScheduledThreadPool(1);
        mDoodles = new ArrayList<>();
        mScheduler = mScheduledPool.scheduleWithFixedDelay(mAnimateRunnable, 0, mRefreshRate, TimeUnit.MILLISECONDS);
    }

    public Doodle addPathDoodle(PathDoodleStyle style, GeoPoint[] points) {
        PathDoodle doodle = new PathDoodle(this, style, points);
        mDoodles.add(doodle);
        return doodle;
    }

    public Doodle addAnimatedPathDoodle(AnimatedPathDoodleStyle style, GeoPoint[] points) {
        AnimatedPathDoodle doodle = new AnimatedPathDoodle(this, style, points);
        mDoodles.add(doodle);
        return doodle;
    }

    // Padding in meters
    public void zoomFitAllDoodles(int padding, boolean shouldAnimate) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Doodle doodle : mDoodles) {
            for (GeoPoint point : doodle.getBounds()) {
                builder.include(toGoogleLatLong(point));
            }
        }

        if (shouldAnimate) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), padding));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), padding));
        }

    }

    public void zoomToFitDoodle(Doodle doodle, int padding, boolean shouldAnimate, boolean shouldChangeBearing) {
        int doodleIndex = mDoodles.indexOf(doodle);
        if (doodleIndex >= 0) {
            CameraPosition.Builder cpb = new CameraPosition.Builder();

            float bearing = 0;
            if (shouldChangeBearing) {
                GeoPoint[] points = doodle.getPoints();
                bearing = (float) LocationUtil.getBearing(points[points.length - 1], points[0]);
                cpb = cpb.bearing(bearing);
            }

            CameraPosition cp = cpb.target(LocationUtil.geoPointToLatLng(LocationUtil.calculateCenter(doodle.getBounds())))
                    .zoom(LocationUtil.getBoundsZoomLevel(mContext, doodle.getBounds()[0], doodle.getBounds()[1], bearing, mViewportWidth, mViewportHeight, padding))
                    .build();

            CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cp);

            if (shouldAnimate) {
                mMap.animateCamera(cu);
            } else {
                mMap.moveCamera(cu);
            }
        }
    }

    private LatLng toGoogleLatLong(GeoPoint geoPoint) {
        return new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
    }

    public Doodle getDoodleById(String id) {
        for (Doodle doodle : mDoodles) {
            if (doodle != null) {
                if (doodle.getId().equals(id)) {
                    return doodle;
                }
            }
        }
        return null;
    }

    public ArrayList<Doodle> getAllDoodles() {
        return mDoodles;
    }

    public void removeDoodle(Doodle doodle) {
        doodle.remove();
        mDoodles.remove(doodle);
    }

    public int getRefreshRate() {
        return mRefreshRate;
    }
}
