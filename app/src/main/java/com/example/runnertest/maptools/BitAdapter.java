package com.example.runnertest.maptools;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import com.example.runnertest.MainActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BitAdapter {

    private Bitmap bitmap = null;
    private LocTracker locTracker = null;
    private LinkedList<LocPoint> locPoints;
    private AttrListener listener;

    public BitAdapter() {
        this.locPoints = MainActivity.locPoints;
    }

    public interface AttrListener {
        void onRefresh();
    }

    public void setOnAdapterListener(BitAdapter.AttrListener listener) {
        this.listener = listener;
    }

    public void setBitmap(Bitmap bg) {
        if (bg != null) {
            bitmap = Bitmap.createBitmap(bg.getWidth(), bg.getHeight(), Bitmap.Config.RGB_565);
            Canvas bufferCanvas = new Canvas(bitmap);
            bufferCanvas.drawBitmap(bg,
                    new Rect(0,0, bg.getWidth(), bg.getHeight()),
                    new Rect(0,0,bg.getWidth(), bg.getHeight()),
                    null);
            if (listener != null) listener.onRefresh();
        }
    }

    private final double LOC_LEFT = 121.41815186;
    private final double LOC_WIDTH = 121.44662619 - LOC_LEFT;
    private final double LOC_UP = 31.03716041;
    private final double LOC_HEIGHT = 31.01807420 - LOC_UP;

    private float getLocScaleX(double locX) {
        return (float) ((locX - LOC_LEFT) / LOC_WIDTH);
    }

    private float getLocScaleY(double locY) {
        return (float) ((locY - LOC_UP) / LOC_HEIGHT);
    }

    public void setLocTracker(Context context) {
        if (context != null) {
            this.locTracker = new LocTracker(context);
            this.locTracker.setOnLocTrackerListener(loc -> locPoints.add(new LocPoint(getLocScaleX(loc.getLongitude()), getLocScaleY(loc.getLatitude()))));
        }
    }

    public List<LocPoint> getLocPoints() {
        return locPoints;
    }

    public Bitmap getBitBuffer() {
        return bitmap;
    }
}
