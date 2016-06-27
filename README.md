# Map Doodle for Android

[ ![Core](https://api.bintray.com/packages/vornet/maven/mapdoodle%3Acore/images/download.svg) ](https://bintray.com/vornet/maven/mapdoodle%3Acore/_latestVersion)
[![Build Status](https://travis-ci.org/vornet/mapdoodle-android.svg)](https://travis-ci.org/vornet/mapdoodle-android)
[![GitHub license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/vornet/mapdoodle/blob/master/LICENSE.txt)

![Screenshots](https://raw.githubusercontent.com/vornet/mapdoodle-android/master/art/mapdoodledemo.gif)

Android library to create common map animations for Google Maps and other providers.  Note, work-in-progress.

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
    compile 'com.vornet.mapdoodle:core:0.0.0.1'
}
```

# Basic Usage

Usage is really simple.  Once you get an instance of GoogleMaps, just pass it along to an instance of MapDoodler.
Then, add your waypoints to a `GeoPath`.

```java
public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private MapDoodler mMapDoodler;
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapDoodler = new MapDoodler(this, googleMap);
        
        // Shining Sea Bikeway Path
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
        
        // Add the path to MapDoodler
        mMapDoodler.addPath(shiningSeaBikeway);
        
        // Animate the path
        double speedInKmPerHour = 15.0;
        mMapDoodler.animatePath(speedInKmPerHour);
    }


```
