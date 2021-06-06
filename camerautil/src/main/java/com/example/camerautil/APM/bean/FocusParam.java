package com.example.camerautil.APM.bean;

import android.view.MotionEvent;

public class FocusParam {
    private int width;
    private int height;
    private float x;
    private float y;

    private FocusParam() {
    }

    public static FocusParam create() {
        return new FocusParam();
    }

    public FocusParam viewSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public FocusParam tapArea(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public FocusParam tapArea(MotionEvent event) {
        this.x = event.getX();
        this.y = event.getY();
        return this;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public String toString() {
        return "FocusParam{width=" + this.width + ", height=" + this.height + ", x=" + this.x + ", y=" + this.y + '}';
    }
}
