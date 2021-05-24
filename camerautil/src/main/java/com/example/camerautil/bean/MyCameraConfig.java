package com.example.camerautil.bean;

import com.example.camerautil.preview.MyPreview;

public class MyCameraConfig {

    private int facing = 0;
    private MyPreview mPreview;
    // 预览帧的像素编码，决定了每个像素的大小；camera1默认是NV21，编号是17
    private int previewFormat = 17;

    public int getPreviewFormat() {
        return previewFormat;
    }

    public void setPreviewFormat(int previewFormat) {
        this.previewFormat = previewFormat;
    }

    public MyPreview getPreview() {
        return mPreview;
    }

    public void setPreview(MyPreview mPreview) {
        this.mPreview = mPreview;
    }

    public int getFacing() {
        return facing;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }
}
