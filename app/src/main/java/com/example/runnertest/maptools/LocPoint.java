package com.example.runnertest.maptools;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;

import java.util.List;

public class LocPoint {

    private float x;
    private float y;

    public LocPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
