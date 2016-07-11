package com.vornet.mapdoodle;

/**
 * Created by Vorn on 7/3/2016.
 */

public class AnimatedPathDoodleStyle {
    int mColor;
    double mThickness;
    float mZIndex = 100;
    int mTracerColor;
    double mTracerThickness;
    double mSpeed;

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

    public int getTracerColor() {
        return mTracerColor;
    }

    public void setTracerColor(int color) {
        mTracerColor = color;
    }

    public double getTracerThickness() {
        return mTracerThickness;
    }

    public void setTracerThickness(double thickness) {
        mTracerThickness = thickness;
    }

    public double getSpeed() {
        return mSpeed;
    }

    public void setSpeed(double speed) {
        mSpeed = speed;
    }

    public float getZIndex() {
        return mZIndex;
    }

    public void setZIndex(float zIndex) {
        mZIndex = zIndex;
    }
}
