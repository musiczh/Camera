package com.example.camerautil.bean;


import android.hardware.Camera;

public class PreviewFrameData {

    private byte[] data;
    private Camera camera;
    private int facing;



    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public int getFacing() {
        return facing;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }
}
