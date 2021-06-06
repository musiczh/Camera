package com.example.camerautil.APM.bean;

import android.hardware.Camera.Size;

public class PictureResult {
    public Size mPictureSize;
    public int mDisplayOrientation;
    public byte[] mPictureData;
    public int facing;

    public PictureResult() {
    }
}
