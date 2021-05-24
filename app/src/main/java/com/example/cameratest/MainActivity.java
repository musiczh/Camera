package com.example.cameratest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.camerautil.MyCameraCaptureListenerImpl;
import com.example.camerautil.bean.CameraMessage;
import com.example.camerautil.bean.PreviewFrameData;
import com.example.camerautil.bean.PreviewMessage;
import com.example.camerautil.interfaces.MyCameraCapture;
import com.example.camerautil.MyCameraCaptureImpl;
import com.example.camerautil.bean.MyCameraConfig;
import com.example.camerautil.preview.MyPreview;
import com.example.camerautil.preview.MyPreviewSurfaceView;
import com.example.camerautil.util.PermissionUtil;

public class MainActivity extends AppCompatActivity {
    private MyCameraCapture cameraCapture;
    private SurfaceView surfaceView;
    private MyCameraConfig mParam;
    private FrameLayout container;
    
    private String TAG = "huan_mainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!PermissionUtil.hasPermission("android.permission.CAMERA",this)){
            PermissionUtil.requirePermission(this,101,"android.permission.CAMERA");
        }


        // 创建capture
        surfaceView = new SurfaceView(this);
        MyPreview preview = new MyPreviewSurfaceView();
        preview.setTarget(surfaceView);
        MyCameraConfig param = new MyCameraConfig();
        param.setFacing(0);
        param.setPreview(preview);
        mParam = param;
        cameraCapture = new MyCameraCaptureImpl(this,param);

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
            cameraCapture.stopCamera();
            container.removeView(surfaceView);
        }else{
            container.addView(surfaceView);
            MyCameraCaptureListenerImpl listener = new MyCameraCaptureListenerImpl(){
                @Override
                public void onCameraOpen(CameraMessage cameraMessage) {
                    Log.d(TAG, "onCameraOpen: ");
                }

                @Override
                public void onCameraRelease() {
                    super.onCameraRelease();
                    Log.d(TAG, "onCameraRelease: ");
                }

                @Override
                public void onCameraError(int code, String msg, CameraMessage cameraMessage) {
                    super.onCameraError(code, msg, cameraMessage);
                    Log.d(TAG, "onCameraError: ");
                }

                @Override
                public void onPreviewFrameArrive(PreviewFrameData data) {
                    super.onPreviewFrameArrive(data);
                    Log.d(TAG, "onPreviewFrameArrive: ");
                }

                @Override
                public void onPreviewStart(PreviewMessage msg) {
                    super.onPreviewStart(msg);
                    Log.d(TAG, "onPreviewStart: ");
                }

                @Override
                public void onPreviewError(int code, String msg) {
                    super.onPreviewError(code, msg);
                    Log.d(TAG, "onPreviewError: ");
                }

                @Override
                public void onPreviewStop() {
                    super.onPreviewStop();
                    Log.d(TAG, "onPreviewStop: ");
                }
            };
            cameraCapture.setCameraListener(listener);
            cameraCapture.setPreviewFrameListener(listener);
            cameraCapture.setPreviewListener(listener);
            cameraCapture.startCamera();
            

        }
    }

    private void switchCamera(){
        if (cameraCapture.getFacing()==MyCameraCaptureImpl.CAMERA_FACE_FRONT){
            cameraCapture.switchCamera(MyCameraCaptureImpl.CAMERA_FACE_BACK);
        }else{
            cameraCapture.switchCamera(MyCameraCaptureImpl.CAMERA_FACE_FRONT);
        }

    }

    private void changeOrientation(){
        cameraCapture.changeOrientation(false);
    }


}
