package com.example.camerautil.bean;

import android.hardware.Camera;

import java.util.List;

/**
 * 手动对焦时需要传入的参数
 */
public class FocusAreaParam {
    /**
     * 对焦采样区域
     */
    private List<Camera.Area> focusAreaList;
    /**
     * 采光采样区域
     */
    private List<Camera.Area> meteringAreaList;
    /**
     * 对焦之后是否保持焦距
     */
    private boolean isKeepFocusMode = true;


    public FocusAreaParam(List<Camera.Area> focusAreaList, List<Camera.Area> meteringAreaList, boolean isKeepFocusMode) {
        this.focusAreaList = focusAreaList;
        this.meteringAreaList = meteringAreaList;
        this.isKeepFocusMode = isKeepFocusMode;
    }

    public boolean isKeepFocusMode() {
        return isKeepFocusMode;
    }

    public void setKeepFocusMode(boolean keepFocusMode) {
        isKeepFocusMode = keepFocusMode;
    }

    public List<Camera.Area> getFocusAreaList() {
        return focusAreaList;
    }

    public void setFocusAreaList(List<Camera.Area> focusAreaList) {
        this.focusAreaList = focusAreaList;
    }

    public List<Camera.Area> getMeteringAreaList() {
        return meteringAreaList;
    }

    public void setMeteringAreaList(List<Camera.Area> meteringAreaList) {
        this.meteringAreaList = meteringAreaList;
    }
}
