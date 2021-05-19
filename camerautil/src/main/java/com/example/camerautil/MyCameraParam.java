package com.example.camerautil;

import android.view.SurfaceView;

public class MyCameraParam {

    private int facing = 0;
    private MyPreview mPreview;

    public MyPreview getPreview() {
        return mPreview;
    }

    public void setPreview(MyPreview mPreview) {
        this.mPreview = mPreview;
    }

    public int getFacing() {
        return facing;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }
}
