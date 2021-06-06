package com.example.camerautil.bean;

import android.hardware.Camera;

import com.example.camerautil.preview.MyPreview;

public class MyCameraConfig {
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

    private Builder mBuilder;

    public MyCameraConfig(Builder builder){
        mBuilder = builder;
    }


    public boolean isDynamicUpdatePreviewSize() {
        return mBuilder.isDynamicUpdatePreviewSize;
    }

    public void setDynamicUpdatePreviewSize(boolean dynamicUpdatePreviewSize) {
        mBuilder.isDynamicUpdatePreviewSize = dynamicUpdatePreviewSize;
    }

    public boolean isYUVCallback() {
        return mBuilder.isYUVCallback;
    }

    public void setYUVCallback(boolean YUVCallback) {
        mBuilder.isYUVCallback = YUVCallback;
    }

    public Camera.Size getPictureSize() {
        return mBuilder.pictureSize;
    }

    public void setPictureSize(Camera.Size pictureSize) {
        mBuilder.pictureSize = pictureSize;
    }

    public int getPictureFormat() {
        return mBuilder.pictureFormat;
    }

    public void setPictureFormat(int pictureFormat) {
        mBuilder.pictureFormat = pictureFormat;
    }

    public boolean isPreviewAutoFocus() {
        return mBuilder.isPreviewAutoFocus;
    }

    public void setPreviewAutoFocus(boolean previewAutoFocus) {
        mBuilder.isPreviewAutoFocus = previewAutoFocus;
    }

    public Camera.Size getPreviewSize() {
        return mBuilder.previewSize;
    }

    public void setPreviewSize(Camera.Size previewSize) {
        mBuilder.previewSize = previewSize;
    }

    public String getFocusMode() {
        return mBuilder.focusMode;
    }

    public void setFocusMode(String focusMode) {
        mBuilder.focusMode = focusMode;
    }

    public String getFlashMode() {
        return mBuilder.flashMode;
    }

    public void setFlashMode(String flashMode) {
        mBuilder.flashMode = flashMode;
    }

    public int getPreviewFormat() {
        return mBuilder.previewFormat;
    }

    public void setKeepPreviewAfterTakePicture(boolean keepPreviewAfterTakePicture) {
        mBuilder.keepPreviewAfterTakePicture = keepPreviewAfterTakePicture;
    }

    public boolean isKeepPreviewAfterTakePicture() {
        return mBuilder.keepPreviewAfterTakePicture;
    }

    public void setPreviewFormat(int previewFormat) {
        mBuilder.previewFormat = previewFormat;
    }

    public MyPreview getPreview() {
        return mBuilder.preview;
    }

    public void setPreview(MyPreview mPreview) {
        mBuilder.preview = mPreview;
    }

    public int getFacing() {
        return mBuilder.facing;
    }

    public void setFacing(int facing) {
        mBuilder.facing = facing;
    }



    public static class Builder{
        private int facing = 0;
        private boolean keepPreviewAfterTakePicture = true;
        private Camera.Size previewSize;
        private Camera.Size pictureSize;
        private MyPreview preview;

        private String flashMode = FLASH_MODE_OFF;
        private String focusMode = FOCUS_MODE_CONTINUOUS_VIDEO;

        // 预览帧的像素编码，决定了每个像素的大小；camera1默认是NV21，编号是17
        private int previewFormat = 17;
        private int pictureFormat = -1;

        private boolean isDynamicUpdatePreviewSize = false;
        private boolean isYUVCallback = false;

        // 预览的时候是否自动对焦
        private boolean isPreviewAutoFocus = true;



        public Builder setDynamicUpdatePreviewSize(boolean dynamicUpdatePreviewSize) {
            isDynamicUpdatePreviewSize = dynamicUpdatePreviewSize;
            return this;
        }


        public Builder setYUVCallback(boolean YUVCallback) {
            isYUVCallback = YUVCallback;
            return this;
        }


        public Builder setPictureSize(Camera.Size pictureSize) {
            this.pictureSize = pictureSize;
            return this;
        }


        public Builder setPictureFormat(int pictureFormat) {
            this.pictureFormat = pictureFormat;
            return this;
        }

        public Builder setPreviewAutoFocus(boolean previewAutoFocus) {
            isPreviewAutoFocus = previewAutoFocus;
            return this;
        }


        public Builder setPreviewSize(Camera.Size previewSize) {
            this.previewSize = previewSize;
            return this;
        }


        public Builder setFocusMode(String focusMode) {
            this.focusMode = focusMode;
            return this;
        }


        public Builder setFlashMode(String flashMode) {
            this.flashMode = flashMode;
            return this;
        }

        public Builder setKeepPreviewAfterTakePicture(boolean keepPreviewAfterTakePicture) {
            this.keepPreviewAfterTakePicture = keepPreviewAfterTakePicture;
            return this;
        }

        public Builder setPreviewFormat(int previewFormat) {
            this.previewFormat = previewFormat;
            return this;
        }

        public Builder setPreview(MyPreview mPreview) {
            this.preview = mPreview;
            return this;
        }

        public Builder setFacing(int facing) {
            this.facing = facing;
            return this;
        }

        public MyCameraConfig build(){
            return new MyCameraConfig(this);
        }
    }
}
