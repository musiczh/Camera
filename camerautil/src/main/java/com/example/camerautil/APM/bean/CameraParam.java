package com.example.camerautil.APM.bean;

import android.graphics.SurfaceTexture;

import com.example.camerautil.APM.annotation.FlashMode;
import com.example.camerautil.APM.interf.APMRangeSelector;
import com.example.camerautil.APM.interf.APMSizeSelector;

public class CameraParam {
    private CameraParam.Builder mBuilder;
    private Size mSurfaceSize;

    private CameraParam(CameraParam.Builder builder) {
        this.mBuilder = builder;
    }

    public Size previewSize() {
        return this.mBuilder.mPreviewSize;
    }

    public Size pictureSize() {
        return this.mBuilder.mPictureSize;
    }

    public int facing() {
        return this.mBuilder.mFacing;
    }

    public void changeFacing() {
        if (this.facing() == 0) {
            this.mBuilder.mFacing = 1;
        } else {
            this.mBuilder.mFacing = 0;
        }

    }

    public void setSurfaceSize(int width, int height) {
        Size size = new Size(width, height);
        if (this.mBuilder.mPreviewSize == null) {
            this.mBuilder.mPreviewSize = size;
        }

        this.mSurfaceSize = size;
    }

    public Size getSurfaceSize() {
        return this.mSurfaceSize;
    }

    public boolean ignoreDisplayOrientation() {
        return this.mBuilder.ignoreDisplayOrientation;
    }

    public ParamterRange previewFpsRange() {
        return this.mBuilder.mFpsRange;
    }

    public int displayOrientation() {
        return this.mBuilder.mDisplayOrientation;
    }

    @FlashMode
    public String flashMode() {
        return this.mBuilder.mFlashMode;
    }

    public String focusMode() {
        return this.mBuilder.mFocusMode;
    }

    public int previewFormat() {
        return this.mBuilder.mPreviewFormat;
    }

    public int pictureFormat() {
        return this.mBuilder.mPictureFormat;
    }

    public APMRangeSelector previewFpsRangeSelector() {
        return this.mBuilder.mRangeSelector;
    }

    public APMSizeSelector pictureSizeSelector() {
        return this.mBuilder.mPictureSizeSelector;
    }

    public APMSizeSelector previewSizeSelector() {
        return this.mBuilder.mPreviewSizeSelector;
    }

    public SurfaceTexture surfaceTexture() {
        return this.mBuilder.mSurfaceTexture;
    }

    public void setSurfaceTexture(SurfaceTexture texture) {
        this.mBuilder.mSurfaceTexture = texture;
    }

    public boolean autoFocus() {
        return this.mBuilder.autoFocus;
    }

    public boolean needDynamicUpdatePreviewSize() {
        return this.mBuilder.needDynamicUpdatePreviewSize;
    }

    public boolean needYUVCallback() {
        return this.mBuilder.needYUVCallback;
    }

    public static CameraParam.Builder newIns() {
        return new CameraParam.Builder();
    }

    public String toString() {
        return this.mBuilder.toString();
    }

    public static class Builder {
        private int mFacing;
        private Size mPreviewSize;
        private Size mPictureSize;
        private String mFlashMode;
        private String mFocusMode;
        private int mPreviewFormat;
        private int mPictureFormat;
        private APMRangeSelector mRangeSelector;
        private APMSizeSelector mPreviewSizeSelector;
        private APMSizeSelector mPictureSizeSelector;
        private ParamterRange mFpsRange;
        private SurfaceTexture mSurfaceTexture;
        private boolean ignoreDisplayOrientation;
        private int mDisplayOrientation;
        private boolean autoFocus;
        private boolean needDynamicUpdatePreviewSize;
        private boolean needYUVCallback;

        private Builder() {
            this.mFacing = 0;
            this.mPreviewFormat = 17;
            this.mPictureFormat = -1;
            this.mRangeSelector = null;
            this.mFpsRange = null;
            this.ignoreDisplayOrientation = false;
            this.mDisplayOrientation = -1;
            this.autoFocus = true;
            this.needDynamicUpdatePreviewSize = false;
            this.needYUVCallback = true;
        }

        public CameraParam.Builder facing(int facing) {
            this.mFacing = facing;
            return this;
        }

        public CameraParam.Builder previewFpsRange(int min, int max) {
            this.mFpsRange = new ParamterRange(min, max);
            return this;
        }

        public CameraParam.Builder previewSize(Size size) {
            this.mPreviewSize = size;
            return this;
        }

        public CameraParam.Builder pictureSize(Size size) {
            this.mPictureSize = size;
            return this;
        }

        /** @deprecated */
        @Deprecated
        public CameraParam.Builder flashMode(@FlashMode String flashMode) {
            this.mFlashMode = flashMode;
            return this;
        }

        /** @deprecated */
        @Deprecated
        public CameraParam.Builder focusMode(String focusMode) {
            this.mFocusMode = focusMode;
            return this;
        }

        public CameraParam.Builder previewFormat(int previewFormat) {
            this.mPreviewFormat = previewFormat;
            return this;
        }

        public CameraParam.Builder pictureFormat(int pictureFormat) {
            this.mPictureFormat = pictureFormat;
            return this;
        }

        public CameraParam.Builder previewFpsRangeSelector(APMRangeSelector selector) {
            this.mRangeSelector = selector;
            return this;
        }

        public CameraParam.Builder previewSizeSelector(APMSizeSelector selector) {
            this.mPreviewSizeSelector = selector;
            return this;
        }

        public CameraParam.Builder pictureSizeSelector(APMSizeSelector selector) {
            this.mPictureSizeSelector = selector;
            return this;
        }

        public CameraParam.Builder surfaceTexture(SurfaceTexture texture) {
            this.mSurfaceTexture = texture;
            return this;
        }

        public CameraParam.Builder ignoreDisplayOrientation(boolean ignore) {
            this.ignoreDisplayOrientation = ignore;
            return this;
        }

        public CameraParam.Builder setDisplayOrientation(int displayOrientation) {
            this.mDisplayOrientation = displayOrientation;
            return this;
        }

        public CameraParam.Builder autoFocusEnable(boolean autoFocus) {
            this.autoFocus = autoFocus;
            return this;
        }

        public CameraParam.Builder needDynamicUpdatePreviewSize(boolean need) {
            this.needDynamicUpdatePreviewSize = need;
            return this;
        }

        public CameraParam.Builder needYUVCallback(boolean needYUVCallback) {
            this.needYUVCallback = needYUVCallback;
            return this;
        }

        public CameraParam build() {
            return new CameraParam(this);
        }
    }
}
