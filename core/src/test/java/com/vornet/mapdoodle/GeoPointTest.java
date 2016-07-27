package com.vornet.mapdoodle;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
/**
 * Created by Vorn on 7/17/2016.
 */
public class GeoPointTest {
    @Test
    public void Ctor_LatLongProvided_LatLongProperty() {
        // Arrange
        double expectedLat = 42.360401;
        double expectedLong = -71.057990;

        // Act
        GeoPoint target = new GeoPoint(expectedLat, expectedLong);

        // Assert
        assertThat(target.getLatitude(), is(expectedLat));
        assertThat(target.getLongitude(), is(expectedLong));
    }
}