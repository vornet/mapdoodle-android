package com.vornet.mapdoodle;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by Vorn on 7/4/2016.
 */

public class DoodleContext {
    GoogleMap mMap;
    double mElapsedTime;

    public DoodleContext(GoogleMap map, double elapsedTime) {
        mMap = map;
        mElapsedTime = elapsedTime;
    }

    public GoogleMap getMap() {
        return mMap;
    }
}
