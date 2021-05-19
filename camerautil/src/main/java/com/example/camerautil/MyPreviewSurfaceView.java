package com.example.camerautil;

import android.graphics.SurfaceTexture;
import android.view.SurfaceView;

public class MyPreviewSurfaceView extends MyPreview{
    private SurfaceView mSurfaceView;

    @Override
    public Class getClassType() {
        return SurfaceView.class;
    }

    @Override
    public Object getTarget() {
        return mSurfaceView;
    }

    @Override
    protected boolean setTargetReal(Object target) {
        if (target instanceof SurfaceView){
            mSurfaceView = (SurfaceView)target;
            return true;
        }
        return false;
    }
}
