package com.vornet.mapdoodledemo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.vornet.mapdoodle.GeoPath;
import com.vornet.mapdoodle.MapDoodler;

/**
 * @author Vorn Mom (vornet)
 */
public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private MapDoodler mMapDoodler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMapDoodler = new MapDoodler(this, mMap);

        // Shining Sea Bikeway
        GeoPath shiningSeaBikeway = new GeoPath();
        shiningSeaBikeway.addPoint(41.551490, -70.627179);
        shiningSeaBikeway.addPoint(41.550410, -70.627761);
        shiningSeaBikeway.addPoint(41.534456, -70.641752);
        shiningSeaBikeway.addPoint(41.534319, -70.642047);
        shiningSeaBikeway.addPoint(41.534032, -70.642455);
        shiningSeaBikeway.addPoint(41.531242, -70.645581);
        shiningSeaBikeway.addPoint(41.524383, -70.653310);
        shiningSeaBikeway.addPoint(41.524383, -70.653310);
        shiningSeaBikeway.addPoint(41.523319, -70.655396);
        shiningSeaBikeway.addPoint(41.523034, -70.656157);
        shiningSeaBikeway.addPoint(41.522540, -70.659166);
        shiningSeaBikeway.addPoint(41.522524, -70.661014);
        shiningSeaBikeway.addPoint(41.522825, -70.663299);
        shiningSeaBikeway.addPoint(41.523331, -70.665032);
        shiningSeaBikeway.addPoint(41.523395, -70.666019);
        shiningSeaBikeway.addPoint(41.523001, -70.668535);
        shiningSeaBikeway.addPoint(41.523049, -70.668605);
        shiningSeaBikeway.addPoint(41.523061, -70.668922);
        shiningSeaBikeway.addPoint(41.523001, -70.669019);
        shiningSeaBikeway.addPoint(41.523017, -70.669158);
        shiningSeaBikeway.addPoint(41.523101, -70.669233);

        mMapDoodler.addPath(shiningSeaBikeway);
        mMapDoodler.animatePath(15);
    }
}