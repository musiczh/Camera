package com.example.camerautil.interfaces;

import android.hardware.Camera;

import java.util.List;

public interface MyCameraCapture {

    /**
     * 根据用户参数打开摄像头,并开始预览
     * attention： 需要获取相机权限之后再调用此方法，否则会出现授权之后无法预览的情况
     */
    void startCamera();

    /**
     * 切换摄像头
     * @param facing 切换的摄像头
     */
    void switchCamera(int facing);

    /**
     * 释放相机资源，但是没有释放管理类内部的线程
     * 注意，在activity@onPause()方法中需要释放资源
     */
    void stopCamera();

    /**
     * 获取预览支持的尺寸
     * @return
     */
    public List<Camera.Size> getSupportPreviewSize();



    /**
     * 完全释放相机资源，关闭内部线程
     * 一般在activity@onDestroy()中调用
     */
    void release();

    /**
     * 拍照
     * @param callBack 拍照回调，负责存储照片信息
     */
    void takePicture(Camera.ShutterCallback callBack);

    /**
     * 改变相机方向
     * @param isLeft 是否往左边旋转
     */
    void changeOrientation(boolean isLeft);

    /**
     * 判断相机是否已经打开
     * @return 相机是否已经打开
     */
    boolean isOpen();

    /**
     * 获取正在打开的相机id
     * @return 正在使用的相机id
     */
    int getFacing();

    /**
     * 设置预览监听器。在预览的开始、错误和结束时回调
     * @param listener 预览监听器
     */
    void setPreviewListener(MyPreviewListener listener);

    /**
     * 设置预览帧监听器，预览的每一帧数据会通过这个监听器回调
     * @param listener 预览帧监听器
     */
    void setPreviewFrameListener(MyPreviewFrameListener listener);

    /**
     * 相机监听器。相机打开、释放、错误时回调此监听器
     * @param listener 相机监听器
     */
    void setCameraListener(MyCameraListener listener);
//
//    void snapshot();
//
//    void toggleFlash();
//
//    boolean isFlashOn();
//
//    /** @deprecated */
//    @Deprecated
//    void autoFocus(Camera.AutoFocusCallback var1);
//
//    /** @deprecated */
//    @Deprecated
//    void cancelAutoFocus();
//
//    void setActivityOrientation(int var1);

//    void setPreviewListener(APMPreviewListener var1);
//
//    void setPreviewFrameListener(APMPreviewFrameListener var1);
//
//    void setPictureResultListener(APMPictureResultListener var1);
//
//    void setCameraListener(APMCameraListener var1);
//
//    void onRequestPermissionsResult(int var1, String[] var2, int[] var3);
//
//    CameraParameters getCameraParameters();
//
//    /** @deprecated */
//    @Deprecated
//    void tapFocus(FocusParam var1);
//
//    void tapFocus(FocusArea var1);

//    void setFlashMode(String var1);
//
//    String getFlashMode();
//
//    void setFocusMode(String var1);
//
//    void setZoom(float var1);

}
