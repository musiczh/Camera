package com.example.cameratest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.camerautil.MyCameraCapture;
import com.example.camerautil.MyCameraCaptureImpl;
import com.example.camerautil.MyCameraParam;
import com.example.camerautil.MySurfaceView;
import com.example.camerautil.PermissionUtil;

public class MainActivity extends AppCompatActivity {
    private MyCameraCapture cameraCapture;
    private SurfaceView surfaceView;
    private MyCameraParam mParam;
    private FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!PermissionUtil.hasPermission("android.permission.CAMERA",this)){
            PermissionUtil.requirePermission(this,101,"android.permission.CAMERA");
        }


        // 创建capture
        surfaceView = new SurfaceView(this);
        MyCameraParam param = new MyCameraParam();
        param.setFacing(0);
        param.setSurfaceView(surfaceView);
        mParam = param;
        cameraCapture = new MyCameraCaptureImpl(this);

        final Button openButton = findViewById(R.id.button_open);
        final Button switchButton = findViewById(R.id.button_switch);
        Button orientationButton = findViewById(R.id.button_orientation);
        container = findViewById(R.id.framelayout);

        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });
        orientationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeOrientation();
            }
        });



    }



    private void openCamera(){
        if (cameraCapture.isOpen()){
            cameraCapture.releaseCamera();
            container.removeView(surfaceView);
        }else{
            container.addView(surfaceView);
            cameraCapture.openCamera(mParam);
            cameraCapture.startPreview();
        }
    }

    private void switchCamera(){
        cameraCapture.switchCamera();

    }

    private void changeOrientation(){
        cameraCapture.changeOrientation(false);
    }


}
