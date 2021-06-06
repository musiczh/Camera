package com.example.camerautil.APM.interf;

import com.example.camerautil.APM.bean.CameraResult;

public interface APMCameraListener {
    int ERROR_CAMERA_DESTORY = -1;
    int ERROR_CAMERA_IN_USING = -2;
    int ERROR_CAMERA_NO_NUMBERS = -3;
    int ERROR_CAMERA_UNSUPPORTED_FACING = -4;
    int ERROR_CAMERA_SYS_FAILED_OPEN = -5;
    int ERROR_CAMERA_PERMISSION_DENIED = -6;
    int ERROR_CAMERA_SYS = -7;

    void onCameraOpen(CameraResult var1);

    void onCameraRelease();

    void onCameraError(int var1, String var2, CameraResult var3);
}
