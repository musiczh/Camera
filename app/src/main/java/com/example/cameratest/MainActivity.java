package com.example.cameratest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.camerautil.MyCameraCaptureListenerImpl;
import com.example.camerautil.bean.CameraMessage;
import com.example.camerautil.bean.PictureData;
import com.example.camerautil.bean.PreviewFrameData;
import com.example.camerautil.bean.PreviewMessage;
import com.example.camerautil.interfaces.MyCameraCapture;
import com.example.camerautil.MyCameraCaptureImpl;
import com.example.camerautil.bean.MyCameraConfig;
import com.example.camerautil.preview.MyPreview;
import com.example.camerautil.preview.MyPreviewSurfaceView;
import com.example.camerautil.util.PermissionUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private MyCameraCapture cameraCapture;
    private SurfaceView surfaceView;
    private MyCameraConfig mParam;
    private FrameLayout container;
    private Handler mHandler;
    
    private String TAG = "huan_mainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!PermissionUtil.hasPermission("android.permission.CAMERA",this)){
            PermissionUtil.requirePermission(this,101,"android.permission.CAMERA");
        }
        PermissionUtil.requirePermission(this,101,"android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE");

        HandlerThread handlerThread = new HandlerThread("mainactivity_background");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());


        // 创建capture
        surfaceView = new SurfaceView(this);
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                return false;
            }
        });
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
        Button takeButton = findViewById(R.id.button_take);
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
        takeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // cameraCapture.setZoom(0.5f);
                cameraCapture.takePicture(new Camera.ShutterCallback() {
                    @Override
                    public void onShutter() {
                        Log.d(TAG, "onShutter: 拍照完成啦");
                    }
                },true);
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
                    //Toast.makeText(MainActivity.this,"previewcode="+code,Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onPreviewError: ");
                }

                @Override
                public void onPreviewStop() {
                    super.onPreviewStop();
                    Log.d(TAG, "onPreviewStop: ");
                }

                @Override
                public void onPictureTaken(PictureData data) {
                    File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            "picture.jpg");
//                    if (!file.exists()){
//                        boolean b =file.mkdir();
//                    }
                    OutputStream os = null;
                    try {
                        os = new FileOutputStream(file);
                        os.write(data.getPictureData());
                        os.close();
                        Toast.makeText(MainActivity.this,"照片已经存储到 "+file.getAbsolutePath(),Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.w(TAG, "Cannot write to " + file, e);
                    } finally {
                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException e) {
                                // Ignore
                            }
                        }
                    }
                }
            };
            cameraCapture.setCameraListener(listener);
            cameraCapture.setPreviewFrameListener(listener);
            cameraCapture.setPreviewListener(listener);
            cameraCapture.setPictureListener(listener);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.flash:
                showFlashMode();
                break;
            case R.id.scale:
                showResolutionMode();
                break;
            case R.id.ratio:
                showRadioMode();
                break;
            case R.id.snapshot:
                cameraCapture.snapshot();
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    private void showFlashMode(){
        final List<String> flashMode = cameraCapture.getSupportFlashMode();
        String current = cameraCapture.getCurrentFlashMode();
        String[] strings = new String[flashMode.size()];
        for (int i=0;i<flashMode.size();i++){
            if (current.equals(flashMode.get(i))){
                strings[i] = "* "+flashMode.get(i);
            }else{
                strings[i] = flashMode.get(i);
            }

        }
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("选择闪光灯模式").setItems(strings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cameraCapture.setFlashMode(flashMode.get(which));
            }
        }).create();
        dialog.show();
    }

    private void showRadioMode(){
        final List<Camera.Size> sizes = cameraCapture.getSupportPreviewSize();
        Camera.Size currentsize = cameraCapture.getCurrentPreviewSize();
        String[] strings = new String[sizes.size()];
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<sizes.size();i++){
            if (currentsize.equals(sizes.get(i))){
                sb.append("* ");
            }
            Camera.Size size = sizes.get(i);
            sb.append(size.width).append(":").append(size.height);
            strings[i] = sb.toString();
            sb.delete(0,sb.length());
        }
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("选择比例模式").setItems(strings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cameraCapture.setPreviewSize(sizes.get(which));
            }
        }).create();
        dialog.show();
    }

    private void showResolutionMode(){
        final List<String> flashMode = cameraCapture.getSupportFocusMode();
        String currentmode = cameraCapture.getCurrentFocusMode();
        String[] strings = new String[flashMode.size()];
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<flashMode.size();i++){
            if (flashMode.get(i).equals(currentmode)){
                sb.append("*");
            }
            sb.append(flashMode.get(i));
            strings[i] = sb.toString();
            sb.delete(0,sb.length());
        }
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("选择聚焦模式").setItems(strings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cameraCapture.setFocusMode(flashMode.get(which));
            }
        }).create();
        dialog.show();
    }

}
