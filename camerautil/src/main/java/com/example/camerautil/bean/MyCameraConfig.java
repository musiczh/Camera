package com.example.camerautil.bean;

import com.example.camerautil.preview.MyPreview;

public class MyCameraConfig {

    private int facing = 0;
    private boolean keepPreviewAfterTakePicture = true;
    private String flashMode;
    private String focusMode;
    private MyPreview preview;
    // 预览帧的像素编码，决定了每个像素的大小；camera1默认是NV21，编号是17
    private int previewFormat = 17;


    public String getFocusMode() {
        return focusMode;
    }

    public void setFocusMode(String focusMode) {
        this.focusMode = focusMode;
    }

    public String getFlashMode() {
        return flashMode;
    }

    public void setFlashMode(String flashMode) {
        this.flashMode = flashMode;
    }

    public int getPreviewFormat() {
        return previewFormat;
    }

    public void setKeepPreviewAfterTakePicture(boolean keepPreviewAfterTakePicture) {
        this.keepPreviewAfterTakePicture = keepPreviewAfterTakePicture;
    }

    public boolean isKeepPreviewAfterTakePicture() {
        return keepPreviewAfterTakePicture;
    }

    public void setPreviewFormat(int previewFormat) {
        this.previewFormat = previewFormat;
    }

    public MyPreview getPreview() {
        return preview;
    }

    public void setPreview(MyPreview mPreview) {
        this.preview = mPreview;
    }

    public int getFacing() {
        return facing;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }
}
