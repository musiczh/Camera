package com.example.camerautil.preview;

import android.graphics.SurfaceTexture;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class MyPreview {

    abstract public Class getClassType();

    abstract public Object getTarget();

    abstract public void setTarget(Object target);


}
