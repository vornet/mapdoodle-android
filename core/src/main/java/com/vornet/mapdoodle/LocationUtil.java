package com.vornet.mapdoodle;

import android.content.Context;
import android.util.DisplayMetrics;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Vorn on 6/5/2016.
 */

public class LocationUtil {
    private static final double LN2 = 0.6931471805599453;
    private static final int WORLD_DP_HEIGHT = 256;
    private static final int WORLD_DP_WIDTH = 256;
    private static final int ZOOM_MAX = 21;

    public static LatLng geoPointToLatLng(GeoPoint point) {
        return new LatLng(point.getLatitude(), point.getLongitude());
    }

    public static GeoPoint calculateCenter(GeoPoint[] geoPoints) {
        return calculateCenter(Arrays.asList(geoPoints));
    }

    public static GeoPoint calculateCenter(List<GeoPoint> geoPoints) {
        if (geoPoints.size() == 1) {
            return geoPoints.get(0);
        }

        double x = 0;
        double y = 0;
        double z = 0;

        for (GeoPoint geoPoint : geoPoints) {
            double latitude = geoPoint.getLatitude() * Math.PI / 180;
            double longitude = geoPoint.getLongitude() * Math.PI / 180;

            x += Math.cos(latitude) * Math.cos(longitude);
            y += Math.cos(latitude) * Math.sin(longitude);
            z += Math.sin(latitude);
        }

        double total = geoPoints.size();

        x = x / total;
        y = y / total;
        z = z / total;

        double centralLongitude = Math.atan2(y, x);
        double centralSquareRoot = Math.sqrt(x * x + y * y);
        double centralLatitude = Math.atan2(z, centralSquareRoot);

        return new GeoPoint(centralLatitude * 180 / Math.PI, centralLongitude * 180 / Math.PI);
    }

    public static double calculateDistance(GeoPoint point1, GeoPoint point2) {
        double R = 6371000;
        double φ1 = Math.toRadians(point1.getLatitude());
        double φ2 = Math.toRadians(point2.getLatitude());
        double Δφ = Math.toRadians(point2.getLatitude() - point1.getLatitude());
        double Δλ = Math.toRadians(point2.getLongitude() - point1.getLongitude());

        double a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
                Math.cos(φ1) * Math.cos(φ2) *
                        Math.sin(Δλ / 2) * Math.sin(Δλ / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public static double getBearing(GeoPoint start, GeoPoint end) {
        double startLat = Math.toRadians(start.getLatitude());
        double startLong = Math.toRadians(start.getLongitude());
        double endLat = Math.toRadians(end.getLatitude());
        double endLong = Math.toRadians(end.getLongitude());

        double dLong = endLong - startLong;

        double dPhi = Math.log(Math.tan(endLat / 2.0 + Math.PI / 4.0) / Math.tan(startLat / 2.0 + Math.PI / 4.0));

        if (Math.abs(dLong) > Math.PI) {
            if (dLong > 0.0) {
                dLong = -(2.0 * Math.PI - dLong);
            } else {
                dLong = (2.0 * Math.PI + dLong);
            }
        }

        return (Math.toDegrees(Math.atan2(dLong, dPhi)) + 360.0) % 360.0;
    }

    public static double degreeToRadians(double degree) {
        return (Math.PI * degree) / 180;
    }

    public static GeoPoint[] getBounds(GeoPoint[] geoPoints) {
        return getBounds(Arrays.asList(geoPoints));
    }

    public static GeoPoint[] getBounds(List<GeoPoint> points) {
        double bigLat = 0;
        double bigLong = 0;
        double smallLat = 360;
        double smallLong = 180;

        for (GeoPoint point : points) {

            double normLat = point.getLatitude() + 180;
            double normLong = point.getLongitude() + 90;

            if (normLat > bigLat) {
                bigLat = normLat;
            }
            if (normLat < smallLat) {
                smallLat = normLat;
            }
            if (normLong > bigLong) {
                bigLong = normLong;
            }
            if (normLong < smallLong) {
                smallLong = normLong;
            }
        }

        return new GeoPoint[]{
                new GeoPoint(bigLat - 180, bigLong - 90),
                new GeoPoint(smallLat - 180, smallLong - 90)};
    }

    public static float getBoundsZoomLevel(Context context, GeoPoint northEast, GeoPoint southWest, double rotation, int mapWidthPx, int mapHeightPx, int padding) {

        double width = northEast.getLatitude() - southWest.getLatitude();
        double height = northEast.getLongitude() - southWest.getLongitude();

        double centerLat = northEast.getLatitude() - (width / 2);
        double centerLong = northEast.getLongitude() - (height / 2);

        GeoPoint[] accountedForRotation = rotate(centerLat, centerLong, width, height, rotation);
        GeoPoint[] newBounds = getBounds(accountedForRotation);

        northEast = newBounds[0];
        southWest = newBounds[1];

        double latFraction = (latRad(northEast.getLatitude()) - latRad(southWest.getLatitude())) / Math.PI;

        double lngDiff = northEast.getLongitude() - southWest.getLongitude();
        double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;

        double latZoom = zoom(mapHeightPx, convertDpToPx(context, WORLD_DP_HEIGHT), latFraction);
        double lngZoom = zoom(mapWidthPx, convertDpToPx(context, WORLD_DP_WIDTH), lngFraction);

        double result = Math.min(latZoom, lngZoom);

        double mapPixels = 256 * result;
        double paddingMultiplyer = (mapPixels - padding) / mapPixels;

        return (float) Math.min(result * paddingMultiplyer, ZOOM_MAX);
    }

    private static int convertDpToPx(Context context, int dp) {
        return Math.round(dp * (context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));

    }

    private static GeoPoint[] rotate(double x, double y, double width, double height, double rotation) {
        double upperLeftX = x + (width / 2) * Math.cos(rotation) - (height / 2) * Math.sin(rotation);
        double upperLeftY = y + (height / 2) * Math.cos(rotation) + (width / 2) * Math.sin(rotation);

        double upperRightX = x - (width / 2) * Math.cos(rotation) - (height / 2) * Math.sin(rotation);
        double upperRightY = y + (height / 2) * Math.cos(rotation) - (width / 2) * Math.sin(rotation);

        double bottomLeftX = x + (width / 2) * Math.cos(rotation) + (height / 2) * Math.sin(rotation);
        double bottomLeftY = y - (height / 2) * Math.cos(rotation) + (width / 2) * Math.sin(rotation);

        double bottomRightX = x - (width / 2) * Math.cos(rotation) + (height / 2) * Math.sin(rotation);
        double bottomRightY = y - (height / 2) * Math.cos(rotation) - (width / 2) * Math.sin(rotation);

        return new GeoPoint[]{
                new GeoPoint(upperLeftX, upperLeftY),
                new GeoPoint(upperRightX, upperRightY),
                new GeoPoint(bottomLeftX, bottomLeftY),
                new GeoPoint(bottomRightX, bottomRightY)
        };
    }

    private static double latRad(double lat) {
        double sin = Math.sin(lat * Math.PI / 180);
        double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
        return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
    }

    private static double zoom(int mapPx, int worldPx, double fraction) {
        return Math.log(mapPx / worldPx / fraction) / LN2;
    }
}
