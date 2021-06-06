package com.example.camerautil.APM.bean;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;

public class PreviewResult {
    public Size previewSize;
    public int displayOrientation;
    public CameraInfo curCameraInfo;
    public Camera camera;

    public PreviewResult() {
    }
}