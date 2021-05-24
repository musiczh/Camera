package com.example.camerautil.bean;

import android.hardware.Camera;

public class PictureData {
    private Camera.Size pictureSize;
    private int displayOrientation;
    private byte[] pictureData;
    private int facing;

    public PictureData(Camera.Size pictureSize, int displayOrientation, byte[] pictureData, int facing) {
        this.pictureSize = pictureSize;
        this.displayOrientation = displayOrientation;
        this.pictureData = pictureData;
        this.facing = facing;
    }

    public Camera.Size getPictureSize() {
        return pictureSize;
    }

    public void setPictureSize(Camera.Size pictureSize) {
        this.pictureSize = pictureSize;
    }

    public int getDisplayOrientation() {
        return displayOrientation;
    }

    public void setDisplayOrientation(int displayOrientation) {
        this.displayOrientation = displayOrientation;
    }

    public byte[] getPictureData() {
        return pictureData;
    }

    public void setPictureData(byte[] pictureData) {
        this.pictureData = pictureData;
    }

    public int getFacing() {
        return facing;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }
}
