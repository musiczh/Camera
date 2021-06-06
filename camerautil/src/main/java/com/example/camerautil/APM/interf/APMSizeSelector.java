package com.example.camerautil.APM.interf;

import android.hardware.Camera;

import com.example.camerautil.APM.bean.Size;

import java.util.List;

public interface APMSizeSelector {
    Camera.Size select(List<Camera.Size> var1, Size var2);
}