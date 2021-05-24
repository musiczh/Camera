package com.example.camerautil.preview;

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
    public void setTarget(Object target) {
        if (target instanceof SurfaceTexture){
            mSurfaceTexture = (SurfaceTexture)target;
        }
    }

}
