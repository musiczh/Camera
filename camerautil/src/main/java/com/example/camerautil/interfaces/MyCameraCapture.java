package com.example.camerautil.interfaces;

import android.hardware.Camera;
import android.util.Size;

import com.example.camerautil.bean.FocusAreaParam;

import java.util.List;

/**
 * 相机组件核心接口
 */
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
     * @return 返回支持的预览尺寸
     */
    List<Camera.Size> getSupportPreviewSize();

    /**
     * 设置预览的尺寸
     */
    void setPreviewSize(Camera.Size size);

    /**
     * 获取当前预览尺寸
     * @return 预览尺寸
     */
    Camera.Size getCurrentPreviewSize();



    /**
     * 完全释放相机资源，关闭内部线程
     * 一般在activity@onDestroy()中调用
     */
    void release();

    /**
     * 拍照
     * @param callBack 拍照完成之后通知回调，仅仅只有通知作用
     * @param isAutoFocus 拍照时是否自动对焦
     */
    void takePicture(Camera.ShutterCallback callBack,boolean isAutoFocus);

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

    /**
     * 设置拍照回调。当拍照完成之后，会回调这个方法把照片数据传递过来
     * @param pictureListener 照片回调
     */
    void setPictureListener(MyPictureListener pictureListener);

    /**
     * 获取支持的闪光灯模式
     * 字符串列表匹配见{@link android.hardware.Camera.Parameters}中以FLASH开头的常量
     * @return 返回支持的闪光灯模式列表。返回@null表示出现错误
     */
    List<String> getSupportFlashMode();

    /**
     * 设置闪光灯模式；字符串必须来自 {@link #getSupportFlashMode()}
     * @param flashMode 闪光灯模式
     */
    void setFlashMode(String flashMode);

    /**
     * 获取当前的闪光灯模式
     * @return 闪光灯模式字符串。返回@null表示出现错误
     */
    String getCurrentFlashMode();

    /**
     * 获取相机支持的对焦模式
     * @return 支持的对焦模式。返回@null表示出现错误
     */
    List<String> getSupportFocusMode();

    /**
     * 设置对焦模式
     * @param focusMode 对焦模式
     */
    void setFocusMode(String focusMode);

    /**
     * 获取当前的对焦模式
     * @return 对焦模式。返回@null表示出现错误
     */
    String getCurrentFocusMode();

    /**
     * 点击屏幕手动对焦
     * @param param 采样的对焦区域以及采光区域
     */
    void tapFocus(FocusAreaParam param);

    /**
     * 设置相机的缩放。也就我们在相机拉动放大缩小.缩放不影响preview的size，但是会影响预览的显示
     * @param k 缩放的系数
     */
    void setZoom(float k);

    /**
     * 拍摄快照。和拍照不同的是，这个是从预览帧中获取一张并回调pictureCallback。
     */
    void snapshot();


}
