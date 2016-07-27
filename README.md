# Map Doodle for Android

[ ![Download](https://api.bintray.com/packages/vornet/maven/mapdoodle/images/download.svg) ](https://bintray.com/vornet/maven/mapdoodle/_latestVersion)
[![Build Status](https://travis-ci.org/vornet/mapdoodle-android.svg)](https://travis-ci.org/vornet/mapdoodle-android)
[![GitHub license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/vornet/mapdoodle/blob/master/LICENSE.txt)

![Screenshots](https://raw.githubusercontent.com/vornet/mapdoodle-android/master/art/mapdoodledemo.gif)

Android library to create common map animations for Google Maps and other providers.  Note, work-in-progress.

Shameless Plug: This library was written for my skiing & snowboarding app, [Mogul Bunny](http://www.mogulbunny.com/).

# Sample Project

The sample requires setting up and adding an Android API key for Google Maps.  For more information, see [this page.](https://developers.google.com/maps/documentation/android-api/signup)

Once the API key has been obtained, open `sample/src/res/values/strings.xml` and add the API key.

---

# Gradle Dependency

### Repository

The Gradle dependency is available via [jCenter](https://bintray.com/vornet/maven/mapdoole/view).
jCenter is the default Maven repository used by Android Studio.

### Core

The *core* module contains all the major classes of this library.  Other modules might be added in future revision of this library.

```gradle
dependencies {
	// ... other dependencies here
    compile 'com.github.vornet.mapdoodle:core:0.0.6'
}
```

# Basic Usage

Once you get an instance of GoogleMaps, just pass it along to an instance of MapDoodler.
Then, add your waypoints to a `GeoPath`.

```java
public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private MapDoodler mMapDoodler;
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        
        Context context = getBaseContext();
        GoogleMap googleMap = mGoogleMap;
        
        // Note: You may need to use a layout listener to determine when views has been laid out. (see sample)    
        int viewPortWidth = mMapView.getWidth();
        int viewPortHeight = mMapView.getHeight();
        int refreshRate = 500;
        
        mMapDoodler = new MapDoodler(
                context,
                googleMap,
                viewPortWidth,
                viewPortHeight,
                refreshRate);
        
        AnimatedPathDoodleStyle pathDoodleStyle = new AnimatedPathDoodleStyle();
        pathDoodleStyle.setThickness(10.0);
        pathDoodleStyle.setColor(Color.BLUE);
        pathDoodleStyle.setTracerThickness(5.0);
        pathDoodleStyle.setTracerColor(Color.GRAY);
        pathDoodleStyle.setSpeed(60.0); // Km Per Hr
                
        // Shining Sea Bikeway Path
        GeoPoint[] shiningSeaBikewayPoints = new GeoPoint[]{
            new GeoPoint(41.551490, -70.627179),
            new GeoPoint(41.550410, -70.627761),
            new GeoPoint(41.534456, -70.641752),
            new GeoPoint(41.534319, -70.642047),
            new GeoPoint(41.534032, -70.642455),
            new GeoPoint(41.531242, -70.645581),
            new GeoPoint(41.524383, -70.653310),
            new GeoPoint(41.524383, -70.653310)
        };     
        
        // Add the path to MapDoodler
        Doodle shiningSeaBikewayDoodle = mMapDoodler.addAnimatedPathDoodle(pathDoodleStyle, shiningSeaBikewayPoints);
        
        // Zoom to fit path, animating and rotate view to fit such that the first and last points line up vertically.
        int padding = 0;
        int shouldAnimate = true;
        int shouldChangeBearing = true;
        mMapDoodler.zoomToFitDoodle(shiningSeaBikewayDoodle, padding, shouldAnimate, shouldChangeBearing);
    }

```

Static (not animated) path doodles can also be added.

```java

PathDoodleStyle pathDoodleStyle = new PathDoodleStyle();
pathDoodleStyle.setThickness(10.0);
pathDoodleStyle.setColor(Color.BLUE);
                
GeoPoint[] shiningSeaBikewayPoints = new GeoPoint[] { 
    // ... 
}

Doodle shiningSeaBikewayDoodle = mMapDoodler.addPathDoodle(pathDoodleStyle, shiningSeaBikewayPoints);

```

Doodles can be added at anytime (for example, via user interaction.)

The following is supported for doodle management:

```java
MapDoodler mapDoodler = new MapDoodler();
// Add a new doodle.
Doodle doodle = mMapDoodler.addPathDoodle(pathDoodleStyle, points);
// Assign a string ID to a doodle.
doodle.setId("foo");
// Later, you can retrieve the doodle by ID.
Doodle doodle = mapDoodler.getDoodleById("foo");
// Remove a doodle from the map
mapDoodler.removeDoodle(doodle);
// Zoom to fit a doodle.
mapDoodler.zoomToFitDoodle();
// Zoom to fit all doodles on the map.
mapDoodler.zoomToFitAllDoodles();
// Change the style of a doodle. Style must be for the correct doodle type, otherwise no-op.
doodle.setStyle(newStyle);

```