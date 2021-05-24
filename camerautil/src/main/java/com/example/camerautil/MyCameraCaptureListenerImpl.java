package com.example.camerautil;

import com.example.camerautil.bean.CameraMessage;
import com.example.camerautil.bean.PreviewFrameData;
import com.example.camerautil.bean.PreviewMessage;
import com.example.camerautil.interfaces.MyCameraListener;
import com.example.camerautil.interfaces.MyPreviewFrameListener;
import com.example.camerautil.interfaces.MyPreviewListener;

public class MyCameraCaptureListenerImpl implements MyCameraListener, MyPreviewListener, MyPreviewFrameListener {

    @Override
    public void onCameraOpen(CameraMessage cameraMessage) {

    }

    @Override
    public void onCameraRelease() {

    }

    @Override
    public void onCameraError(int code, String msg, CameraMessage cameraMessage) {

    }

    @Override
    public void onPreviewFrameArrive(PreviewFrameData data) {

    }

    @Override
    public void onPreviewStart(PreviewMessage msg) {

    }

    @Override
    public void onPreviewError(int code, String msg) {

    }

    @Override
    public void onPreviewStop() {

    }
}
