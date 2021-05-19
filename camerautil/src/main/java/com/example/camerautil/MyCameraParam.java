package com.example.camerautil;

import android.view.SurfaceView;

public class MyCameraParam {

    private int facing = 0;
    private SurfaceView surfaceView;

    public SurfaceView getSurfaceView() {
        return surfaceView;
    }

    public void setSurfaceView(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;
    }

    public int getFacing() {
        return facing;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }
}
