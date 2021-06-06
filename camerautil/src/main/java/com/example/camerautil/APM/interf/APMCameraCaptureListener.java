package com.example.camerautil.APM.interf;

import com.example.camerautil.APM.bean.CameraResult;
import com.example.camerautil.APM.bean.PictureResult;
import com.example.camerautil.APM.bean.PreviewFrameData;
import com.example.camerautil.APM.bean.PreviewResult;

public class APMCameraCaptureListener implements APMCameraListener, APMPictureResultListener, APMPreviewFrameListener, APMPreviewListener {
    public APMCameraCaptureListener() {
    }

    public void onCameraOpen(CameraResult result) {
    }

    public void onCameraRelease() {
    }

    public void onCameraError(int code, String msg, CameraResult result) {
    }

    public void onTakenPicture(PictureResult data) {
    }

    public void onPreviewFrameData(PreviewFrameData data) {
    }

    public void onPreviewBegin(PreviewResult result) {
    }

    public void onPreviewEnd() {
    }

    public void onPreviewError(int code, String msg) {
    }
}
