package com.example.camerautil.APM.annotation;

import android.hardware.Camera;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({Camera.Parameters.FLASH_MODE_AUTO, Camera.Parameters.FLASH_MODE_OFF,Camera.Parameters.FLASH_MODE_ON,Camera.Parameters.FLASH_MODE_TORCH})
@Retention(RetentionPolicy.CLASS)
public @interface FlashMode {
}
