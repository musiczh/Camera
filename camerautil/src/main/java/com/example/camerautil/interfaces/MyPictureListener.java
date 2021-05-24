package com.example.camerautil.interfaces;

import com.example.camerautil.bean.PictureData;

/**
 * 拍照数据监听回调。用户通过设置这个监听器，可以得到拍照的数据
 */
public interface MyPictureListener {
    /**
     * 当拍照完成之后，照片数据通过这个方法进行回调
     * @param data 照片数据
     */
    void onPictureTaken(PictureData data);
}
