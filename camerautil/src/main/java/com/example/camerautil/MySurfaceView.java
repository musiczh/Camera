package com.example.camerautil;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.io.IOException;

public class MySurfaceView extends SurfaceView {
    private Camera mCamera;
    private String TAG = "huan_surfaceView";
    private SurfaceHolder mHolder;

    public MySurfaceView(Context context) {
        super(context);


    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }




}
