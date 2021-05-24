package com.example.camerautil.bean;

import android.hardware.Camera;

public class PreviewMessage {
    private Camera.Size previewSize;
    private int displayOrientation;
    private Camera.CameraInfo curCameraInfo;
    private Camera camera;

    public PreviewMessage(Camera.Size previewSize, int displayOrientation, Camera.CameraInfo curCameraInfo, Camera camera) {
        this.previewSize = previewSize;
        this.displayOrientation = displayOrientation;
        this.curCameraInfo = curCameraInfo;
        this.camera = camera;
    }

    public Camera.Size getPreviewSize() {
        return previewSize;
    }

    public void setPreviewSize(Camera.Size previewSize) {
        this.previewSize = previewSize;
    }

    public int getDisplayOrientation() {
        return displayOrientation;
    }

    public void setDisplayOrientation(int displayOrientation) {
        this.displayOrientation = displayOrientation;
    }

    public Camera.CameraInfo getCurCameraInfo() {
        return curCameraInfo;
    }

    public void setCurCameraInfo(Camera.CameraInfo curCameraInfo) {
        this.curCameraInfo = curCameraInfo;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
