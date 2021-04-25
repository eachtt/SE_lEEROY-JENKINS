package com.example.runnertest.maptools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;

import com.example.runnertest.DrawUtil.DrawThread;

import java.util.List;

public class MapView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    private static final float VELOCITY_MULTI = 1.0f;
    private static final int VELOCITY_DURATION = 600;

    private static final int CLICK = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;

    private int mStatus = 0;
    private int mClick = 0;
    private float mStartDistance;
    private float mPicWidth, mPicHeight;
    private float screenWidth, screenHeight;
    private PointF mStartPoint = new PointF();
    private float scale, scaleFirst;
    private float bx, by;
    private DrawThread drawThread;
    private final SurfaceHolder surfaceHolder;
    private BitAdapter adapter;

    private final Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private final Paint loopPaint;
    private final Paint textPaint;

    @SuppressLint("ClickableViewAccessibility")
    public MapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.setOnTouchListener(this);
        getHolder().addCallback(this);
        surfaceHolder = getHolder();
        mScroller = new Scroller(context);
        loopPaint = new Paint();
        textPaint = new Paint();
        textPaint.setColor(Color.RED);
    }

    public void setAdapter(BitAdapter adapter) {
        this.adapter = adapter;
        if (screenHeight > 0 && screenWidth > 0) setAdapterInit();
    }

    private void setAdapterInit() {
        adapter.setOnAdapterListener(() -> {
            setScale();
            setPicInit();
        });
    }

    private void setPicInit() {
        if (bx != 0 && by != 0) return;

        mPicWidth = adapter.getBitBuffer().getWidth() * scale;
        mPicHeight = adapter.getBitBuffer().getHeight() * scale;

        bx = (screenWidth - mPicWidth) / 2;
        by = (screenHeight - mPicHeight) / 2;
    }

    private void setScale() {
        float scaleWidth = screenWidth / adapter.getBitBuffer().getWidth();
        float scaleHeight = screenHeight / adapter.getBitBuffer().getHeight();
        scale = Math.min(scaleWidth, scaleHeight);
        scaleFirst = scale;
    }

    /*
    private float getXFromLoc(float locX) {

    }

    private float getYFromLoc(float locY) {

    }
*/

    private void looperRun() {
        drawThread = new DrawThread(surfaceHolder);
        drawThread.onDrawingListener(c -> {
            if (c != null && adapter != null && adapter.getBitBuffer() != null) {
                synchronized (MapView.class) {
                    c.drawColor(Color.GRAY);
                    c.scale(scale, scale);
                    c.drawBitmap(adapter.getBitBuffer(), bx / scale, by / scale, loopPaint);
                    //c.drawBitmap(adapter.getBitBuffer(), bx, by, loopPaint);
                    c.drawText("12345yu", (bx + mPicWidth) / scale, (by + mPicHeight) / scale, textPaint);
                    // 描点
                    List<LocPoint> locPoints = adapter.getLocPoints();
                    if (locPoints != null) {
                        for (LocPoint locPoint : locPoints) {
                            c.drawCircle((bx + locPoint.getX() * mPicWidth) / scale,
                                    (by + locPoint.getY() * mPicHeight) / scale, 3, textPaint);
                        }
                    }

                }
            }
        });
        drawThread.start();
    }

    private float spacing(MotionEvent event) {
        if (event == null) return 0;
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }
    private void zoomMap(MotionEvent event) {
        synchronized (MapView.class) {
            float newDist = spacing(event);
            float scale1 = newDist / mStartDistance;
            mStartDistance = newDist;
            float tmp = scale * scale1;
            if (!(tmp < scaleFirst * 10 && tmp > scaleFirst * 1)) {
                return;
            }
            float fx = (event.getX(1) - event.getX(0)) / 2 + event.getX(0);
            float fy = (event.getY(1) - event.getY(0)) / 2 + event.getY(0);
            float XIn = fx - bx;
            float YIn = fy - by;
            XIn *= scale1;
            YIn *= scale1;
            bx = fx - XIn;
            by = fy - YIn;
            scale = tmp;
            mPicHeight *= scale1;
            mPicWidth *= scale1;
        }
    }


    private void constraintXY() {
        if (bx > constraintWL) bx = constraintWL;
        else if (bx + mPicWidth < constraintWR) bx = constraintWR - mPicWidth;
        if (by > constraintHU) by = constraintHU;
        else if (by + mPicHeight < constraintHD) by = constraintHD - mPicHeight;
    }

    private void dragMap(MotionEvent event) {
        synchronized (MapView.class) {
            PointF currentPoint = new PointF();
            currentPoint.set(event.getX(), event.getY());
            int offsetX = (int) (currentPoint.x - mStartPoint.x);
            int offsetY = (int) (currentPoint.y - mStartPoint.y);
            mStartPoint = currentPoint;
            bx += offsetX;
            by += offsetY;
            constraintXY();
        }
    }

    private int x, y;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (adapter != null) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getX();
                    y = (int) event.getY();
                    mClick = CLICK;
                    mStartPoint.set(event.getX(), event.getY());
                    mStatus = DRAG;
                    drawThread.setCanPaint(true);
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    float distance = spacing(event);
                    if (distance > 5.0f) {
                        mStatus = ZOOM;
                        mStartDistance = distance;
                    }

                    drawThread.setCanPaint(true);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (Math.abs(x - event.getX()) < 3 || Math.abs(y - event.getY()) < 3) {
                        mClick = CLICK;
                    } else {
                        if (mStatus == DRAG) {
                            dragMap(event);
                            mClick = DRAG;

                            if (mVelocityTracker == null) {
                                mVelocityTracker = VelocityTracker.obtain();
                            }
                            mVelocityTracker.addMovement(event);
                        } else {
                            if (event.getPointerCount() == 1) return true;
                            zoomMap(event);
                            mClick = DRAG;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mClick == CLICK) {
                        drawThread.setCanPaint(false);
                    } else {
                        int dx = 0;
                        int dy = 0;
                        if (mVelocityTracker != null) {
                            mVelocityTracker.computeCurrentVelocity(100);
                            dx = (int) (mVelocityTracker.getXVelocity() * VELOCITY_MULTI);
                            dy = (int) (mVelocityTracker.getYVelocity() * VELOCITY_MULTI);
                        }
                        mScroller.startScroll((int) mStartPoint.x, (int) mStartPoint.y, dx, dy, VELOCITY_DURATION);
                        invalidate();
                        if (mVelocityTracker != null) mVelocityTracker.clear();
                    }
                    break;
            }
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset() && mStatus == DRAG && mClick == DRAG) {
            PointF currentPoint = new PointF();
            currentPoint.set(mScroller.getCurrX(), mScroller.getCurrY());
            int offsetX = (int) (currentPoint.x - mStartPoint.x);
            int offsetY = (int) (currentPoint.y - mStartPoint.y);
            mStartPoint = currentPoint;
            bx += offsetX;
            by += offsetY;
            constraintXY();
            postInvalidate();
        } else if (mStatus == DRAG && mClick == DRAG) {
            drawThread.setCanPaint(false);
        }
        super.computeScroll();
    }

    private float constraintWL = 400;
    private float constraintWR = 800;
    private float constraintHU = 800;
    private float constraintHD = 1500;
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        screenWidth = this.getWidth();
        screenHeight = this.getHeight();
        constraintWL = screenWidth / 4;
        constraintWR = constraintWL * 3;
        constraintHU = screenHeight / 3;
        constraintHD = constraintHU * 2;
        if (adapter != null) setAdapterInit();
        looperRun();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        drawThread.setThreadRun(false);
        drawThread.interrupt();
        drawThread = null;
    }

}
