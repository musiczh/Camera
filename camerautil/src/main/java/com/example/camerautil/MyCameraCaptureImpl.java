package com.example.camerautil;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.ErrorCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import androidx.annotation.NonNull;

import java.io.IOException;

public class MyCameraCaptureImpl implements MyCameraCapture {

    public static int CAMERA_FACE_FRONT = 1;
    public static int CAMERA_FACE_BACK = 0;

    private Context mContext;
    private int cameraNum = -1;
    private CameraHandler mHandler;
    private Camera mCamera;
    private CameraInfo mCameraInfo;
    private Parameters parameters;
    private MyCameraParam mCameraConfig;
    private ErrorCallback errorCallback;
    private boolean isPreviewing = false;
    private int degree = 0;
    private int cameraId = -1;
    private boolean isDestroy = false;

    public MyCameraCaptureImpl(Context context , MyCameraParam param){
        mContext = context;
        HandlerThread handlerThread = new  HandlerThread("MyCameraCaptureImpl");
        handlerThread.start();
        mHandler = new CameraHandler(handlerThread.getLooper());
        mCameraConfig = param;
        errorCallback = new ErrorCallback() {
            @Override
            public void onError(int error, Camera camera) {
                onErrorCamera(3);
            }
        };
    }

    // 检查设备是否拥有相机
    private boolean checkCamera(){
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    @Override
    public void startCamera() {
        sendMessage(1);
    }

    @Override
    public void switchCamera(int facing) {
        if (mCameraConfig.getFacing()!=facing){
            mCameraConfig.setFacing(facing);
        }
        sendMessage(4);
    }

    @Override
    public void stopCamera() {
        sendMessage(2);
    }

    @Override
    public void release() {
        sendMessage(5);
    }

    @Override
    public boolean isOpen(){
        return mCamera!=null;
    }

    @Override
    public int getFacing() {
        return mCameraConfig.getFacing();
    }

    @Override
    public void takePicture(Camera.ShutterCallback callBack) {

    }

    // 旋转90度
    public void changeOrientation(boolean isLeft){
        if (mCamera==null){
            onErrorCamera(5);
            return;
        }
        if (isLeft){
            degree = Math.abs((degree-90)%360);
        }else{
            degree = (degree+90)%360;
        }
        mCamera.setDisplayOrientation(calcDisplayOrientation(degree));
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
                    startCameraReal();
                    break;
                case 2:
                    stopCameraReal();
                    break;
                case 3:
                    //
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

    private void stopCameraReal(){
        stopPreview();
        releaseCamera();
    }

    private void startCameraReal(){
        openCamera(false);
        startPreview();
    }

    private void releaseReal(){
        releaseCamera();
        mHandler.getLooper().quit();
        isDestroy = true;
    }

    private void switchCameraReal(){
        if (mCamera!=null){
            openCamera(true);
            startPreview();
        }else{
            onErrorCamera(5);
        }

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

    private void startPreview(){
        MyPreview preview = mCameraConfig.getPreview();
        if (!preview.isReady() || mCamera==null){
            return;
        }
        if (isPreviewing){
            stopPreview();
        }
        try{
            if (preview.getClassType() == SurfaceView.class){
                SurfaceView surfaceView = (SurfaceView)preview.getTarget();
                mCamera.setPreviewDisplay(surfaceView.getHolder());
            }else{
                SurfaceTexture surfaceTexture = (SurfaceTexture)preview.getTarget();
                mCamera.setPreviewTexture(surfaceTexture);
            }
            isPreviewing = true;
            mCamera.startPreview();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    private void openCamera(boolean ifSwitch){
        // 相机已经启动且不是需要切换摄像头，不允许重复打开相机
        if (!ifSwitch && mCamera!=null){
            onErrorCamera(6);
        }else{
            startCameraInner();
        }
    }

    private void startCameraInner(){
        // 检查设备与权限
        if (!checkCamera() || -1 == (cameraNum = Camera.getNumberOfCameras())){
            onErrorCamera(0);
            return ;
        }
        if (!PermissionUtil.hasPermission("android.permission.CAMERA",mContext)){
            onErrorCamera(1);
            return ;
        }

        // 获取选择的相机信息
        mCameraInfo = new CameraInfo();
        for(int i = 0; i < cameraNum; ++i) {
            Camera.getCameraInfo(i, mCameraInfo);
            if (mCameraInfo.facing == this.mCameraConfig.getFacing()) {
                this.cameraId = i;
                break;
            }
        }

        // 如果相机正在使用则先释放相机
        if (mCamera!=null){
            releaseCamera();
        }

        // 打开相机
        mCamera = Camera.open(cameraId);
        parameters = mCamera.getParameters();
        mCamera.setDisplayOrientation(calcDisplayOrientation(degree));

    }

    /**
     * 计算相机需要旋转的角度，让预览图正向
     * @param screenOrientationDegrees 屏幕旋转的角度
     * @return 需要顺时针旋转的角度
     */
    private int calcDisplayOrientation(int screenOrientationDegrees) {
        // 前置摄像头是镜像的
        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return (360 - (mCameraInfo.orientation + screenOrientationDegrees) % 360) % 360;
        } else {
            return (mCameraInfo.orientation - screenOrientationDegrees + 360) % 360;
        }
    }



    private void releaseCamera(){
        if (isPreviewing){
            stopPreview();
        }
        mCamera.release();
        mCamera = null;
    }


    private void stopPreview(){
        mCamera.stopPreview();
        isPreviewing = false;
    }

}
