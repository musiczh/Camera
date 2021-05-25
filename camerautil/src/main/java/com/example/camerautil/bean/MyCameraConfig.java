package com.example.camerautil.bean;

import android.hardware.Camera;

import com.example.camerautil.preview.MyPreview;

public class MyCameraConfig {


    private int facing = 0;
    private boolean keepPreviewAfterTakePicture = true;
    private Camera.Size previewSize;
    private String flashMode;
    private String focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
    private MyPreview preview;
    // 预览帧的像素编码，决定了每个像素的大小；camera1默认是NV21，编号是17
    private int previewFormat = 17;


    /**
     * 前置摄像头
     */
    public static final int FACING_FRONT = 1;
    /**
     * 后置摄像头（默认）
     */
    public static final int FACING_BACK = 0;


    /**
     * 关闭闪光灯，任何情况下都不使用
     */
    public static final String FLASH_MODE_OFF = "off";
    /**
     * 预览时关闭闪光灯。拍照的时候自动根据光线判断是否打开闪光灯
     */
    public static final String FLASH_MODE_AUTO = "auto";
    /**
     * 拍照的时候打开闪光灯
     */
    public static final String FLASH_MODE_ON = "on";
    /**
     * 闪光灯常亮
     */
    public static final String FLASH_MODE_TORCH = "torch";


    /**
     * 必须手动点击对焦才会自动对焦
     */
    public static final String FOCUS_MODE_AUTO = "auto";
    /**
     * 焦距固定无限远，无法手动对焦
     */
    public static final String FOCUS_MODE_INFINITY = "infinity";
    /**
     * 焦距固定，无法修改
     */
    public static final String FOCUS_MODE_FIXED = "fixed";
    /**
     * 根据实际情况自动对焦，但对焦的过程比较柔和
     */
    public static final String FOCUS_MODE_CONTINUOUS_VIDEO = "continuous-video";
    /**
     * 根据实际情况自动对焦，但对焦的过程比较快
     */
    public static final String FOCUS_MODE_CONTINUOUS_PICTURE = "continuous-picture";


    public Camera.Size getPreviewSize() {
        return previewSize;
    }

    public void setPreviewSize(Camera.Size previewSize) {
        this.previewSize = previewSize;
    }

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
