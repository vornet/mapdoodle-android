package com.vornet.mapdoodle;

import java.util.ArrayList;

/**
 * Created by Vorn on 7/29/2016.
 */

public class MathUtil {
    public static Point[] pointsBetweenPath(Point[] path, double distanceStep) {
        ArrayList<Point> pointsList = new ArrayList<>();

        double startDistance = 0;
        for(int i = 0; i < path.length - 1; i ++) {
            double distanceBetweenPoints = distanceBetweenTwoPoints(path[i], path[i+1]);

            double currentDistance;
            for(currentDistance = startDistance; currentDistance < distanceBetweenPoints; currentDistance += distanceStep) {
                Point point = pointBetweenTwoPoints(path[i], path[i+1], currentDistance);
                pointsList.add(point);
            }
            startDistance = currentDistance - distanceBetweenPoints;
        }

        pointsList.add(path[path.length-1]);

        Point[] pointsArray = new Point[pointsList.size()];
        pointsArray = pointsList.toArray(pointsArray);
        return pointsArray;
    }

    public static double distanceBetweenTwoPoints(Point point0, Point point1) {
        return Math.sqrt((point1.getX() - point0.getX()) * (point1.getX() - point0.getX()) + (point1.getY() - point0.getY()) * (point1.getY() - point0.getY()));
    }

    public static Point pointBetweenTwoPoints(Point point0, Point point1, double distance) {
        double v0 = point1.getX() - point0.getX();
        double v1 = point1.getY() - point0.getY();
        double vlen = Math.sqrt(v0*v0 + v1*v1);
        double nv0 = v0 / vlen;
        double nv1 = v1 / vlen;
        double vl0 = point0.getX() + distance * nv0;
        double vl1 = point0.getY() + distance * nv1;

        return new Point(vl0, vl1);
    }
}
