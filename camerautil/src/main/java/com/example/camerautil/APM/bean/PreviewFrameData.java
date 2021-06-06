package com.example.camerautil.APM.bean;

import android.hardware.Camera;

public class PreviewFrameData {
    public Camera.Size mPreviewSize;
    public byte[] mYUVData;
    public int displayOrientation;
    public int facing;

    public PreviewFrameData() {
    }

    public boolean isFacingBack() {
        return this.facing == 0;
    }
}