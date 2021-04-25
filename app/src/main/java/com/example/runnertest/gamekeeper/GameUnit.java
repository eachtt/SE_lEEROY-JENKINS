package com.example.runnertest.gamekeeper;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class GameUnit {
    private final Bitmap bitmap;
    private boolean canTouch = false;
    private final Rect srcRect, dstRect;
    private ClickListener clickListener;

    interface ClickListener {
        void onClick();
    }

    public GameUnit(Rect src, Rect dst, Bitmap bitmap) {
        this.srcRect = src;
        this.dstRect = dst;
        this.bitmap = Bitmap.createBitmap(bitmap);
    }

    public Rect getSrcRect() {
        return this.srcRect;
    }

    public Rect getDstRect() {
        return this.dstRect;
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }

    public void setClickListener(ClickListener clickListener) {
        if (clickListener == null) return;
        this.clickListener = clickListener;
        this.canTouch = true;
    }

    public boolean isCanTouch() {
        return canTouch;
    }

    public boolean contains(int x, int y) {
        return this.dstRect.contains(x, y);
    }

    public void onClick() {
        if (this.canTouch) this.clickListener.onClick();
    }
}
