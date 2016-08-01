package com.vornet.mapdoodledemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.vornet.mapdoodle.AnimatedPathDoodleStyle;
import com.vornet.mapdoodle.Doodle;
import com.vornet.mapdoodle.GeoPoint;
import com.vornet.mapdoodle.MapDoodler;

/**
 * @author Vorn Mom (vornet)
 */
public class MainActivity extends Activity implements OnMapReadyCallback, View.OnClickListener {
    private MapDoodler mMapDoodler;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private boolean mMapViewFinishedLayout = false;
    private Doodle[] mDoodles;
    private int mVisibleDoodle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = (MapView) findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        mMapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mMapViewFinishedLayout = true;
                setupMapDoodle();
            }
        });

        Button switchButton = (Button) findViewById(R.id.switch_button);
        switchButton.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mMapDoodler.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        if(mMapDoodler != null) { mMapDoodler.onPause(); }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if(mMapDoodler != null) { mMapDoodler.onResume(); }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        setupMapDoodle();
    }

    public void setupMapDoodle() {
        if (mMapDoodler != null || mGoogleMap == null || !mMapViewFinishedLayout) {
            return;
        }

        mMapDoodler = new MapDoodler(
                getBaseContext(),
                mGoogleMap,
                mMapView.getWidth(),
                mMapView.getHeight(),
                500);

        AnimatedPathDoodleStyle pathDoodleStyle = new AnimatedPathDoodleStyle();
        pathDoodleStyle.setThickness(10);
        pathDoodleStyle.setColor(Color.BLUE);
        pathDoodleStyle.setTracerThickness(5.0);
        pathDoodleStyle.setTracerColor(Color.GRAY);
        pathDoodleStyle.setSpeed(60.0);

        // Shining Sea Bikeway
        GeoPoint[] shiningSeaBikewayPoints = new GeoPoint[]{
                new GeoPoint(41.551490, -70.627179),
                new GeoPoint(41.550410, -70.627761),
                new GeoPoint(41.534456, -70.641752),
                new GeoPoint(41.534319, -70.642047),
                new GeoPoint(41.534032, -70.642455),
                new GeoPoint(41.531242, -70.645581),
                new GeoPoint(41.524383, -70.653310),
                new GeoPoint(41.524383, -70.653310),
                new GeoPoint(41.523319, -70.655396),
                new GeoPoint(41.523034, -70.656157),
                new GeoPoint(41.522540, -70.659166),
                new GeoPoint(41.522524, -70.661014),
                new GeoPoint(41.522825, -70.663299),
                new GeoPoint(41.523331, -70.665032),
                new GeoPoint(41.523395, -70.666019),
                new GeoPoint(41.523001, -70.668535),
                new GeoPoint(41.523049, -70.668605),
                new GeoPoint(41.523061, -70.668922),
                new GeoPoint(41.523001, -70.669019),
                new GeoPoint(41.523017, -70.669158),
                new GeoPoint(41.523101, -70.669233)
        };

        // Cape Cod Canal Service Road
        GeoPoint[] capeCodeCanalServiceRoadPoints = new GeoPoint[]{
                new GeoPoint(41.743239, -70.613286),
                new GeoPoint(41.745799, -70.601731),
                new GeoPoint(41.747392, -70.594167),
                new GeoPoint(41.749841, -70.587451),
                new GeoPoint(41.755180, -70.577988),
                new GeoPoint(41.758533, -70.574018),
                new GeoPoint(41.760342, -70.572505),
                new GeoPoint(41.766240, -70.568664),
                new GeoPoint(41.768489, -70.566808),
                new GeoPoint(41.771314, -70.563579),
                new GeoPoint(41.771521, -70.563517),
                new GeoPoint(41.771606, -70.563392),
                new GeoPoint(41.771658, -70.563126),
                new GeoPoint(41.774158, -70.558663),
                new GeoPoint(41.776142, -70.553008),
                new GeoPoint(41.776824, -70.549567),
                new GeoPoint(41.777240, -70.545823),
                new GeoPoint(41.777280, -70.545431),
                new GeoPoint(41.777160, -70.540834),
                new GeoPoint(41.773926, -70.512234),
                new GeoPoint(41.774070, -70.511566),
                new GeoPoint(41.774320, -70.511046),
                new GeoPoint(41.774696, -70.510429),
                new GeoPoint(41.774846, -70.509933),
                new GeoPoint(41.775782, -70.503557),
                new GeoPoint(41.775812, -70.502865),
                new GeoPoint(41.775664, -70.501186),
                new GeoPoint(41.775802, -70.500440),
                new GeoPoint(41.776408, -70.498739),
                new GeoPoint(41.776982, -70.497967)
        };

        // Doodles can be added or removed at anytime.
        Doodle shiningSeaBikewayDoodle = mMapDoodler.addAnimatedPathDoodle(pathDoodleStyle, shiningSeaBikewayPoints);
        Doodle capeCodeCanalServiceRoadDoodle = mMapDoodler.addAnimatedPathDoodle(pathDoodleStyle, capeCodeCanalServiceRoadPoints);
        mDoodles = new Doodle[] { shiningSeaBikewayDoodle, capeCodeCanalServiceRoadDoodle };

        // Default to fit all doodles.
        mVisibleDoodle = mDoodles.length;
        mMapDoodler.zoomFitAllDoodles(150, false);
    }

    @Override
    public void onClick(View view) {
        mVisibleDoodle++;
        mVisibleDoodle = mVisibleDoodle % (mDoodles.length + 1);

        if (mVisibleDoodle < mDoodles.length) {
            // Zoom to fit each doodle.
            mMapDoodler.zoomToFitDoodle(mDoodles[mVisibleDoodle], 0, true, true);
        } else {
            // Fit all doodles.
            mMapDoodler.zoomFitAllDoodles(150, true);
        }
    }
}