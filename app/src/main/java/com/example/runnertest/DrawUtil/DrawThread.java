package com.example.runnertest.DrawUtil;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.example.runnertest.DrawUtil.DrawFramesListener;

public class DrawThread extends Thread {
    private boolean _run = true;
    private boolean canPaint = true;
    private DrawFramesListener listener;

    private final SurfaceHolder surfaceHolder;

    public DrawThread(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
    }

    public void onDrawingListener(DrawFramesListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        while (_run) {
            showBit();
        }
    }

    private void showBit() {
        int FRAME_INTERVAL = 10;
        if (canPaint && surfaceHolder != null) {
            long startTime = System.currentTimeMillis();
            Canvas c = surfaceHolder.lockCanvas();
            try {
                synchronized (surfaceHolder) {
                    this.listener.onDraw(c);
                    long endTime = System.currentTimeMillis();
                    int diffTime = (int) (endTime - startTime);
                    if (diffTime < FRAME_INTERVAL) {
                        try {
                            Thread.sleep(FRAME_INTERVAL - diffTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } finally {
                if (c != null) surfaceHolder.unlockCanvasAndPost(c);
            }
        } else {
            try {
                Thread.sleep(FRAME_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setThreadRun(boolean run) {
        this._run = run;
    }

    public void setCanPaint(boolean can) {
        this.canPaint = can;
    }
}
