package com.example.camerautil;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.ErrorCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import androidx.annotation.NonNull;

import java.io.IOException;

public class MyCameraCaptureImpl implements MyCameraCapture {

    private Context mContext;
    private int cameraNum = -1;
    private CameraHandler mHandler;
    private Camera mCamera;
    private CameraInfo cameraInfo;
    private Parameters parameters;
    private MyCameraParam mCameraConfig;
    private ErrorCallback errorCallback;
    private boolean isPreviewing = false;
    private boolean isOpen = false;
    private int degree = 0;
    private int cameraId = -1;
    private boolean isDestroy = false;

    public MyCameraCaptureImpl(Context context){
        mContext = context;
        HandlerThread handlerThread = new  HandlerThread("MyCameraCaptureImpl");
        handlerThread.start();
        mHandler = new CameraHandler(handlerThread.getLooper());
        errorCallback = new ErrorCallback() {
            @Override
            public void onError(int error, Camera camera) {
                onErrorCamera(3);
            }
        };
    }

    // 设备是否拥有相机
    private boolean checkCamera(){
        if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            return true;
        }else{
            return false;
        }
    }


    @Override
    public boolean isPreviewShow() {
        return false;
    }

    @Override
    public void startCamera() {

    }



    public void openCamera(MyCameraParam param) {
        mCameraConfig = param;
        sendMessage(1);
    }


    @Override
    public void switchCamera() {
        sendMessage(4);
    }


    @Override
    public void releaseCamera() {
        sendMessage(3);
    }




    @Override
    public void stopPreview() {

    }


    @Override
    public void release() {
        sendMessage(5);
    }

    @Override
    public boolean isOpen(){
        return isOpen;
    }


    @Override
    public void takePicture(Camera.ShutterCallback callBack) {

    }

    // 旋转90度
    public void changeOrientation(boolean isLeft){
        if (!isOpen){
            onErrorCamera(5);
            return;
        }
        if (isLeft){
            degree = Math.abs((degree-90)%360);
        }else{
            degree = (degree+90)%360;
        }
        mCamera.setDisplayOrientation(degree);
    }


    // 统一在子线程处理消息
    private void sendMessage(int what){
        if (!isDestroy){
            mHandler.sendEmptyMessage(what);
        }else{
            onErrorCamera(7);
        }

    }

    private class CameraHandler extends Handler {
        CameraHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1 :
                    openCameraReal(false);
                    break;
                case 2:
                    // TODO
                    break;
                case 3:
                    releaseCameraReal();
                    break;
                case 4:
                    switchCameraReal();
                    break;
                case 5:
                    releaseReal();
                    break;

            }
        }
    }

    private void releaseReal(){
        releaseCameraReal();
        mHandler.getLooper().quit();
        isDestroy = true;
    }

    private void onErrorCamera(int errorCode){
        switch (errorCode){
            case 1:
                PermissionUtil.requirePermission((Activity)mContext,101,"android.permission.CAMERA");
                break;
        }
        // 0 设备问题
        // 1 权限问题
        // 3 camera错误回调
        // 4 状态错误
        // 5 相机还没打开
        // 6 不能重复打开相机
        // 7 capture已经完全退出

    }

    private void switchCameraReal(){
        if (isOpen){
            changeFacing();
            openCameraReal(true);
            startPreviewReal();
        }else{
            onErrorCamera(5);
        }

    }

    private void changeFacing(){
        if (mCameraConfig.getFacing()==0){
            mCameraConfig.setFacing(1);
        }else{
            mCameraConfig.setFacing(0);
        }
    }

    private void startPreviewReal(){
        if (isPreviewShow()){
            stopPreviewReal();
        }
        SurfaceView view = mCameraConfig.getSurfaceView();
        SurfaceHolder holder = view.getHolder();
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            isPreviewing = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void openCameraInner(){
        if (!checkCamera() || -1 == (cameraNum = Camera.getNumberOfCameras())){
            onErrorCamera(0);
            return ;
        }
        if (!PermissionUtil.hasPermission("android.permission.CAMERA",mContext)){
            onErrorCamera(1);
            return ;
        }

        cameraInfo = new CameraInfo();
        for(int i = 0; i < cameraNum; ++i) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == this.mCameraConfig.getFacing()) {
                this.cameraId = i;
                break;
            }
        }

        if (mCamera!=null){
            releaseCameraReal();
        }

        mCamera = Camera.open(cameraId);
        parameters = mCamera.getParameters();
        mCamera.setErrorCallback(errorCallback);
        isOpen = true;
    }

    private void openCameraReal(boolean ifSwitch){
        if (!ifSwitch && isOpen){
            onErrorCamera(6);
        }else{
            openCameraInner();
        }
    }

    private void releaseCameraReal(){
        if (isPreviewing){
            stopPreviewReal();
        }
        mCamera.release();
        isOpen = false;
    }

    private void stopPreviewReal(){
        mCamera.stopPreview();
        isPreviewing = false;
    }

}
