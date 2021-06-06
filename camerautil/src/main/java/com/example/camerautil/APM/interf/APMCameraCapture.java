package com.example.camerautil.APM.interf;

import android.hardware.Camera;

import com.example.camerautil.APM.bean.CameraParam;
import com.example.camerautil.APM.bean.CameraParameters;
import com.example.camerautil.APM.bean.FocusArea;
import com.example.camerautil.APM.bean.FocusParam;

public interface APMCameraCapture {
    boolean isPreviewShow();

    void openCamera(CameraParam var1);

    void switchCamera();

    void releaseCamera();

    void startPreview();

    void stopPreview();

    void release();

    void takePicture(Camera.ShutterCallback var1);

    void snapshot();

    void toggleFlash();

    boolean isFlashOn();

    /** @deprecated */
    @Deprecated
    void autoFocus(Camera.AutoFocusCallback var1);

    /** @deprecated */
    @Deprecated
    void cancelAutoFocus();

    void setActivityOrientation(int var1);

    void setPreviewListener(APMPreviewListener var1);

    void setPreviewFrameListener(APMPreviewFrameListener var1);

    void setPictureResultListener(APMPictureResultListener var1);

    void setCameraListener(APMCameraListener var1);

    void onRequestPermissionsResult(int var1, String[] var2, int[] var3);

    CameraParameters getCameraParameters();

    /** @deprecated */
    @Deprecated
    void tapFocus(FocusParam var1);

    void tapFocus(FocusArea var1);

    void setFlashMode(String var1);

    String getFlashMode();

    void setFocusMode(String var1);

    void setZoom(float var1);
}
