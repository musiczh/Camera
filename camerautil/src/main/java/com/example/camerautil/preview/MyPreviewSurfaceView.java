package com.example.camerautil.preview;

import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MyPreviewSurfaceView extends MyPreview{
    private SurfaceView mSurfaceView;

    @Override
    public Class getClassType() {
        return SurfaceHolder.class;
    }

    @Override
    public Object getTarget() {
        if (mSurfaceView==null){
            return null;
        }
        return mSurfaceView.getHolder();
    }

    @Override
    public void setTarget(Object target) {
        if (target instanceof SurfaceView){
            mSurfaceView = (SurfaceView)target;
        }
    }


}
