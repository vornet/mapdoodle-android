package com.vornet.mapdoodle;

/**
 * Created by Vorn on 7/3/2016.
 */

public class PathDoodleStyle {
    int mColor;
    double mThickness;
    float mZIndex = 100;

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public double getThickness() {
        return mThickness;
    }

    public void setThickness(double thickness) {
        mThickness = thickness;
    }

    public float getZIndex() {
        return mZIndex;
    }

    public void setZIndex(float zIndex) {
        mZIndex = zIndex;
    }
}
