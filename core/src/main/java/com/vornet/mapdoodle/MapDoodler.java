package com.vornet.mapdoodle;

import android.app.Activity;
import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * @author Vorn Mom (vornet)
 */

public class MapDoodler implements GoogleMap.OnCameraChangeListener {
    Activity mActivity;
    GoogleMap mMap;
    GeoPath mPath;

    boolean mFirstCameraChange = true;


    public MapDoodler(Activity activity, GoogleMap map) {
        mActivity = activity;
        mMap = map;
        mMap.setOnCameraChangeListener(this);
    }

    public void addPath(GeoPath geoPath) {
        mPath = geoPath;

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(5);
        polylineOptions.color(Color.RED);
        Polyline polyline = mMap.addPolyline(polylineOptions);
        polyline.setPoints(mPath.toLatLngList());
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (mFirstCameraChange) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mPath.toLatLngBounds(), 50));
            mFirstCameraChange = false;
        }
    }

    public GeoPathAnimator animatePath(double speed) {
        GeoPathAnimator pathAnimator = new GeoPathAnimator(mActivity, mMap, mPath, speed);
        pathAnimator.start();
        return pathAnimator;
    }
}
