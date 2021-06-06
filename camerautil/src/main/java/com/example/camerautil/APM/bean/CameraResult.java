package com.example.camerautil.APM.bean;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;

public class CameraResult {
    public boolean switchCamera;
    public int facing;
    public CameraInfo cameraInfo;
    public Camera camera;

    public CameraResult() {
    }

}
