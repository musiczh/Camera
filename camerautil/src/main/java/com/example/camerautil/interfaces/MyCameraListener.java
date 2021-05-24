package com.example.camerautil.interfaces;

import com.example.camerautil.bean.CameraMessage;

public interface MyCameraListener {
    int CAMERA_NOT_OPEN = 1;
    int CAMERA_OPEN_TWICE = 2;
    int CAMERA_NO_SUPPORT = 3;
    int CAMERA_SYS_ERROR = 4;
    int CAMERA_PERMISSION_DENY = 5;
    void onCameraOpen(CameraMessage cameraMessage);
    void onCameraRelease();
    void onCameraError(int code, String msg, CameraMessage cameraMessage);
}
