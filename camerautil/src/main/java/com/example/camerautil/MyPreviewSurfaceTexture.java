package com.example.camerautil;

import android.graphics.SurfaceTexture;

public class MyPreviewSurfaceTexture extends MyPreview {
    private SurfaceTexture mSurfaceTexture;

    @Override
    public Class getClassType() {
        return SurfaceTexture.class;
    }

    @Override
    public Object getTarget() {
        return mSurfaceTexture;
    }

    @Override
    protected boolean setTargetReal(Object target) {
        if (target instanceof SurfaceTexture){
            mSurfaceTexture = (SurfaceTexture)target;
            return true;
        }
        return false;
    }
}
