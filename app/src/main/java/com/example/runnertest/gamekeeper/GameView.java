package com.example.runnertest.gamekeeper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.example.runnertest.DrawUtil.DrawThread;

import java.util.LinkedList;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    private DrawThread drawThread;
    private final SurfaceHolder surfaceHolder;
    private final Paint loopPaint = new Paint();
    private GameAdapter gameAdapter;

    private float screenHeight, screenWidth;

    @SuppressLint("ClickableViewAccessibility")
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
        getHolder().addCallback(this);
        surfaceHolder = getHolder();
    }

    public void setGameAdapter(GameAdapter gameAdapter) {
        this.gameAdapter = gameAdapter;
    }

    /*******************************************************/
    private void looperRun() {
        drawThread = new DrawThread(surfaceHolder);
        drawThread.onDrawingListener(c -> {
            if (c != null && gameAdapter != null) {
                synchronized (GameView.class) {
                    // draw something
                    c.drawColor(Color.GRAY);
                    LinkedList<GameUnit> gameUnitList = gameAdapter.getGameUnitList();
                    for (GameUnit gameUnit : gameUnitList) {
                        c.drawBitmap(gameUnit.getBitmap(),
                                    gameUnit.getSrcRect(),
                                    gameUnit.getDstRect(),
                                    loopPaint);
                    }
                }
            }
        });
        drawThread.start();
    }
    /******************************************************/

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (gameAdapter != null) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    gameAdapter.clickMap(event);
                    break;
            }
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        screenHeight = this.getHeight();
        screenWidth = this.getWidth();
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
