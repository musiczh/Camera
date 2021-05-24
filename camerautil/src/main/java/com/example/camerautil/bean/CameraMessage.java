package com.example.camerautil.bean;

import android.hardware.Camera;

public class CameraMessage {
    private boolean switchCamera;
    private int facing;
    private Camera.CameraInfo cameraInfo;
    private Camera camera;

    public CameraMessage(boolean switchCamera, int facing, Camera.CameraInfo cameraInfo, Camera camera) {
        this.switchCamera = switchCamera;
        this.facing = facing;
        this.cameraInfo = cameraInfo;
        this.camera = camera;
    }

    public boolean isSwitchCamera() {
        return switchCamera;
    }

    public void setSwitchCamera(boolean switchCamera) {
        this.switchCamera = switchCamera;
    }

    public int getFacing() {
        return facing;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }

    public Camera.CameraInfo getCameraInfo() {
        return cameraInfo;
    }

    public void setCameraInfo(Camera.CameraInfo cameraInfo) {
        this.cameraInfo = cameraInfo;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
