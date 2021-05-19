package com.example.camerautil;

import android.graphics.SurfaceTexture;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class MyPreview {

    private boolean isReady = false;

    abstract public Class getClassType();

    abstract public Object getTarget();

    public void setTarget(Object target){
        isReady = setTargetReal(target);
    }

    public boolean isReady(){
        return isReady;
    }

    abstract protected boolean setTargetReal(Object target);

}
