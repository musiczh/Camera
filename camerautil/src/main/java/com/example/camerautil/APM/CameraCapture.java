package com.example.camerautil.APM;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.Display;
import android.view.Surface;

import androidx.annotation.NonNull;

import com.alipay.xmedia.common.biz.utils.PermissionHelper;
import com.example.camerautil.APM.bean.CameraParam;
import com.example.camerautil.APM.bean.CameraParameters;
import com.example.camerautil.APM.bean.CameraResult;
import com.example.camerautil.APM.bean.FocusArea;
import com.example.camerautil.APM.bean.FocusParam;
import com.example.camerautil.APM.bean.ParamterRange;
import com.example.camerautil.APM.bean.PictureResult;
import com.example.camerautil.APM.bean.PreviewFrameData;
import com.example.camerautil.APM.bean.PreviewResult;
import com.example.camerautil.APM.bean.Size;
import com.example.camerautil.APM.interf.APMCameraCapture;
import com.example.camerautil.APM.interf.APMCameraListener;
import com.example.camerautil.APM.interf.APMPictureResultListener;
import com.example.camerautil.APM.interf.APMPreviewFrameListener;
import com.example.camerautil.APM.interf.APMPreviewListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.transform.sax.TemplatesHandler;

public class CameraCapture implements APMCameraCapture {
    private CameraParam mCameraConfig;
    private Context mContext;
    private Camera mCamera;

    private ExecuteHandler mExecuteHandler;
    private CallbackHandler mCallbackHandler;

    private int mCameraId = -1;
    private CameraInfo mCameraInfo;
    private Camera.Parameters mCameraParameters;
    private int mDisplayOrientation = -1;
    private byte[][] mPreviewBuffer = new byte[3][];
    private int mBufferIndex = 0;
    private ParamAdapter mParamAdapter;
    private Camera.Size mPreviewSize;
    private Camera.Size mPictureSize;
    private volatile boolean hasOpen = false;
    private volatile boolean hasPreview = false;
    private volatile boolean snapshot = false;
    private APMCameraListener mCameraListener;
    private APMPreviewListener mPreviewListener;
    private APMPreviewFrameListener mPreviewFrameListener;
    private APMPictureResultListener mPictureResultListener;
    private Camera.ErrorCallback mCameraErrorCallback = new Camera.ErrorCallback() {
        @Override
        public void onError(int error, Camera camera) {
            onCameraErrorCallback(APMCameraListener.ERROR_CAMERA_SYS,"sys error code=" + error, null,false);
        }
    };
    private Camera.PictureCallback mCameraPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            onPictureCallback(data,mCameraParameters.getPictureSize());
            try{
                mCamera.cancelAutoFocus();
                mCamera.startPreview();
            }catch (Throwable e){
                e.printStackTrace();
            }
        }
    };

    public CameraCapture(Context context) {
        mContext = context;

        HandlerThread handlerThread = new HandlerThread("camera_capture_execute"+System.currentTimeMillis());
        handlerThread.start();
        mExecuteHandler = new ExecuteHandler(handlerThread.getLooper());

        HandlerThread handlerThread1 = new HandlerThread("camera_capture_callback"+System.currentTimeMillis());
        handlerThread1.start();
        mCallbackHandler = new CallbackHandler(handlerThread1.getLooper());

        mParamAdapter = new ParamAdapter();
    }

    @Override
    public boolean isPreviewShow() {
        return hasPreview;
    }

    @Override
    public synchronized void openCamera(CameraParam var1) {
        sendExecuteMsg(0,var1);
    }

    @Override
    public synchronized void startPreview() {
        sendExecuteMsg(1,null);
    }

    @Override
    public void switchCamera() {
        sendExecuteMsg(5,null);
    }

    @Override
    public synchronized void releaseCamera() {
        sendExecuteMsg(3,null);
    }



    @Override
    public synchronized void stopPreview() {
        sendExecuteMsg(2,null);
    }

    @Override
    public void release() {
        sendExecuteMsg(4,null);
    }

    @Override
    public void takePicture(Camera.ShutterCallback var1) {
        if (!hasOpen){
            return ;
        }
        try{
            mCamera.takePicture(var1, null, mCameraPictureCallback);
        }catch (Throwable e){
            e.printStackTrace();
        }
    }

    @Override
    public void snapshot() {
        if  (!snapshot){
            snapshot = true;
        }
    }

    @Override
    public synchronized void toggleFlash() {
        if (!hasOpen || mCameraParameters==null){
            return;
        }
        List<String> supportFlashMode = mCameraParameters.getSupportedFlashModes();
        if (supportFlashMode.contains(Camera.Parameters.FLASH_MODE_TORCH)){
            if (isFlashOn()){
                mParamAdapter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH,true);
            }else{
                mParamAdapter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF,true);
            }
            mCameraParameters.setFlashMode(mParamAdapter.getFlashMode());
            mCamera.setParameters(mCameraParameters);
        }
    }

    @Override
    public boolean isFlashOn() {
        return hasOpen && mCameraParameters!=null &&  Camera.Parameters.FLASH_MODE_OFF.equals(mParamAdapter.getFlashMode());
    }

    @Override
    public void autoFocus(Camera.AutoFocusCallback var1) {

    }

    @Override
    public void cancelAutoFocus() {

    }

    @Override
    public void setActivityOrientation(int var1) {

    }

    @Override
    public void setPreviewListener(APMPreviewListener var1) {
        mPreviewListener = var1;
    }

    @Override
    public void setPreviewFrameListener(APMPreviewFrameListener var1) {
        mPreviewFrameListener = var1;
    }

    @Override
    public void setPictureResultListener(APMPictureResultListener var1) {
        mPictureResultListener = var1;
    }

    @Override
    public void setCameraListener(APMCameraListener var1) {
        mCameraListener = var1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (272 == requestCode && grantResults != null && grantResults.length > 0) {
            for(int i = 0; i < grantResults.length; ++i) {
                boolean granted = grantResults[i] == 0;
                if ("android.permission.CAMERA".equals(permissions[i])) {
                    if (granted) {
                        openCameraInner(false);
                    } else {
                        onCameraErrorCallback(APMCameraListener.ERROR_CAMERA_PERMISSION_DENIED, "camera permission is denied", null,false);
                    }
                    break;
                }
            }

        }
    }

    @Override
    public CameraParameters getCameraParameters() {
        return null;
    }

    @Override
    public void tapFocus(FocusParam var1) {

    }

    @Override
    public void tapFocus(FocusArea var1) {

    }

    @Override
    public void setFlashMode(String requestFlashMode) {
        if (!hasOpen || mCameraParameters==null){
            return ;
        }
        List<String> supportFlashMode = mCameraParameters.getSupportedFlashModes();
        if (supportFlashMode.contains(requestFlashMode)){
            try{
                mParamAdapter.setFocusMode(requestFlashMode,true);
                mCameraParameters.setFocusMode(mParamAdapter.getFlashMode());
                mCamera.setParameters(mCameraParameters);
            }catch (Throwable e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getFlashMode() {
        if (mCameraParameters == null){
            return null;
        }
        return mCameraParameters.getFlashMode();
    }

    @Override
    public void setFocusMode(String requestFocusMode) {
        if (!hasOpen || mCameraParameters==null){
            return ;
        }
        List<String> supportFocusMode = mCameraParameters.getSupportedFocusModes();
        if (supportFocusMode.contains(requestFocusMode)){
            try{
                mParamAdapter.setFocusMode(requestFocusMode,true);
                mCameraParameters.setFocusMode(mParamAdapter.getFocusMode());
                mCamera.setParameters(mCameraParameters);
            }catch (Throwable e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setZoom(float var1) {
        sendExecuteMsg(7, var1);
    }

    // 切换到子线程处理请求信息
    private void sendExecuteMsg(int what,Object obj){
        mExecuteHandler.removeMessages(what);
        Message msg = Message.obtain();
        msg.obj = obj;
        msg.what = what;
        mExecuteHandler.sendMessage(msg);
    }
    // 切换到子线程处理回调信息
    private void sendCallbackMsg(int what,Object obj){
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        mCallbackHandler.sendMessageDelayed(msg,5);
    }


    private void releaseReal(){
        releaseCameraReal(false,false);
        if (mCallbackHandler!=null){
            mCallbackHandler.getLooper().quit();
        }
        if (mExecuteHandler != null){
            mExecuteHandler.getLooper().quit();
        }

    }
    private void startPreviewReal(){
        if (!hasOpen || mCameraConfig==null|| mCameraConfig.surfaceTexture()==null){
            return ;
        }
        try{
            if (hasPreview){
                stopPreviewReal(true);
            }
            if (mCameraConfig.needDynamicUpdatePreviewSize()){
                configPreviewSize();
                mCamera.setParameters(mCameraParameters);
            }
            initBuffer();
            mCamera.setPreviewTexture(mCameraConfig.surfaceTexture());
            mCamera.startPreview();
            hasPreview = true;
            onPreviewStartCallback();

        }catch (Throwable throwable){
            releaseCameraReal(true,false);
            onPreviewErrorCallback(APMPreviewListener.ERROR_PREVIEW_BEGINE,"failed to startPreview",throwable);
        }
    }
    private void initBuffer(){
        // 只有当用户设置了需要预览帧数据才会捕获
        if (mCameraConfig != null && mCameraConfig.needYUVCallback()){
            Camera.Size size = mCameraParameters.getPreviewSize();
            int bufferSize = size.width * size.height * ImageFormat.getBitsPerPixel(mCameraConfig.previewFormat()) / 8;
            for (int i=0;i<mPreviewBuffer.length;i++){
                mPreviewBuffer[i] = new byte[bufferSize];
            }
            mBufferIndex = 0;
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.addCallbackBuffer(mPreviewBuffer[0]);
            mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    onPreviewFrameCallback(data);
                    mBufferIndex = (mBufferIndex+1)%3;
                    mCamera.addCallbackBuffer(mPreviewBuffer[mBufferIndex]);
                }
            });
        }
    }

    private void stopPreviewReal(boolean isInner){
        if (!hasOpen && !hasPreview){
            return;
        }
        try {
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.stopPreview();
            hasPreview = false;
            if (!isInner){
                onPreviewStopCallback();
            }
        }catch (Throwable e){
            releaseCameraReal(isInner,false);
            if (!isInner){
                onPreviewErrorCallback(APMPreviewListener.ERROR_PREVIEW_END,"failed to stopPreviewInner",e);
            }
        }

    }

    private void openCameraReal(CameraParam param,boolean isSwitch){
        if (hasOpen && !isSwitch){
            onCameraErrorCallback(APMCameraListener.ERROR_CAMERA_IN_USING, "camera is using", null ,false);
            return ;
        }
        mCameraConfig = param;
        if (PermissionHelper.hasPermission("android.permission.CAMERA")) {
            openCameraInner(isSwitch);
        } else {
            PermissionHelper.requirePermission(mContext, 272, "android.permission.CAMERA");
        }
    }
    private void openCameraInner( boolean isSwitchCamera){
        int cameraNum = Camera.getNumberOfCameras();
        if (cameraNum == 0){
            onCameraErrorCallback(APMCameraListener.ERROR_CAMERA_NO_NUMBERS, " no camera to use", null,isSwitchCamera);
            return;
        }

        // 获取选择的相机信息
        CameraInfo cameraInfo = new CameraInfo();
        for(int i = 0; i < cameraNum; ++i) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == this.mCameraConfig.facing()) {
                mCameraId = i;
                mCameraInfo = cameraInfo;
                break;
            }
        }
        if (mCameraId == -1){
            onCameraErrorCallback(APMCameraListener.ERROR_CAMERA_UNSUPPORTED_FACING,"notSupported cameraFacing=" + mCameraConfig.facing(), null,isSwitchCamera);
            return ;
        }

        try{
            // 如果相机正在使用则先释放相机
            if (hasOpen){
                releaseCameraReal(true,isSwitchCamera);
            }

            // 打开相机
            mCamera = Camera.open(mCameraId);
            mCameraParameters = mCamera.getParameters();
            mCamera.setErrorCallback(mCameraErrorCallback);

            // 根据用户的配置修改相机参数
            configParameters();
            hasOpen = true;
        } catch (Throwable throwable){
            onCameraErrorCallback(APMCameraListener.ERROR_CAMERA_SYS_FAILED_OPEN, "failed to invoke system camera.open", throwable, isSwitchCamera);
            releaseCameraReal(true,isSwitchCamera);
        }
        onCameraOpenCallback(isSwitchCamera);
    }

    /**
     * 配置相机的相关参数
     */
    private void configParameters(){
        mParamAdapter.setFocusMode(mCameraConfig.focusMode(),false);
        mParamAdapter.setFlashMode(mCameraConfig.flashMode(),false);

        configFormat();
        configFlashMode();
        configFocusMode();
        configPreviewSize();
        configPictureSize();
        configDisplayOrientation();
        configPreviewFpsRange();

        mCamera.setParameters(mCameraParameters);
    }

    private void configFormat(){
        List<Integer> previewFormats = mCameraParameters.getSupportedPreviewFormats();
        List<Integer> pictureFormats = mCameraParameters.getSupportedPictureFormats();
        if (previewFormats.contains(mCameraConfig.previewFormat())){
            mCameraParameters.setPreviewFormat(mCameraConfig.previewFormat());
        }
        if (pictureFormats.contains(mCameraConfig.pictureFormat())){
            mCameraParameters.setPictureFormat(mCameraConfig.pictureFormat());
        }
    }
    private void configFocusMode(){
        List<String> supportFocusMode = mCameraParameters.getSupportedFocusModes();
        if (supportFocusMode.contains(mParamAdapter.getFocusMode())){
            mCameraParameters.setFocusMode(mParamAdapter.getFocusMode());
        }else if (mCameraConfig.autoFocus()){
            // 如果配置了预览自动对焦，且没有在配置参数中指定对焦模式，则选择下面的自动对焦模式
            if (supportFocusMode.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)){
                mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }else if (supportFocusMode.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){
                mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
        }
    }
    private void configFlashMode(){
        List<String> supportFlashMode = mCameraParameters.getSupportedFlashModes();
        if (supportFlashMode.contains(mParamAdapter.getFlashMode())){
            mCameraParameters.setFlashMode(mParamAdapter.getFlashMode());
        }
    }
    private void configPreviewSize(){
        List<Camera.Size> supportSize = mCameraParameters.getSupportedPreviewSizes();
        Camera.Size previewSize = null;
        if (mCameraConfig.previewSizeSelector()!=null){
            previewSize = mCameraConfig.previewSizeSelector().select(supportSize,mCameraConfig.previewSize());
        }
        if (previewSize == null){
            previewSize = setDefaultSize(supportSize,mCameraConfig.previewSize());
        }
        mPreviewSize = previewSize;
        mCameraParameters.setPreviewSize(previewSize.width,previewSize.height);
    }
    private void configPictureSize(){
        List<Camera.Size> supportSize = mCameraParameters.getSupportedPictureSizes();
        Camera.Size pictureSize = null;
        if (mCameraConfig.pictureSizeSelector()!=null){
            pictureSize = mCameraConfig.pictureSizeSelector().select(supportSize,mCameraConfig.pictureSize());
        }
        if (pictureSize == null){
            pictureSize = setDefaultSize(supportSize,mCameraConfig.pictureSize());
        }
        mPictureSize = pictureSize;
        mCameraParameters.setPictureSize(pictureSize.width,pictureSize.height);
    }
    private void configDisplayOrientation(){
        if (mContext instanceof Activity){
            Activity activity = (Activity) mContext;
            Display display = activity.getDisplay();
            if (display != null){
                int rotation = display.getRotation();
                switch (rotation) {
                    case Surface.ROTATION_0: mDisplayOrientation = 0; break;
                    case Surface.ROTATION_90: mDisplayOrientation = 90; break;
                    case Surface.ROTATION_180: mDisplayOrientation = 180; break;
                    case Surface.ROTATION_270: mDisplayOrientation = 270; break;
                }
            }
        }else{
            mDisplayOrientation = 0;
        }

        int rotation = 0;
        if (mCameraInfo.facing == 1) {
            rotation = (mCameraInfo.orientation + mDisplayOrientation) % 360;
        } else {
            int landscapeFlip = isLandscape(mDisplayOrientation) ? 180 : 0;
            rotation = (mCameraInfo.orientation + mDisplayOrientation + landscapeFlip) % 360;
        }

        mCameraParameters.setRotation(rotation);
        mCamera.setDisplayOrientation(calcDisplayOrientation(mDisplayOrientation));
    }
    private boolean isLandscape(int orientationDegrees) {
        return orientationDegrees == 90 || orientationDegrees == 270;
    }
    /**
     * 计算相机需要旋转的角度，让预览图正向
     * @param screenOrientationDegrees 屏幕旋转的角度
     * @return 需要顺时针旋转的角度
     */
    private int calcDisplayOrientation(int screenOrientationDegrees) {
        if (mCameraConfig.facing() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return (360 - (mCameraInfo.orientation + screenOrientationDegrees) % 360)%360;
        } else {
            return (mCameraInfo.orientation - screenOrientationDegrees + 360) % 360;
        }
    }
    private void configPreviewFpsRange(){
        if (mCameraConfig.previewFpsRange()!=null){
            mCameraParameters.setPreviewFpsRange(mCameraConfig.previewFpsRange().min,mCameraConfig.previewFpsRange().max);
        }else{
            ParamterRange range = null;
            if (mCameraConfig.previewFpsRangeSelector()!=null){
                range = mCameraConfig.previewFpsRangeSelector().select(mCameraParameters.getSupportedPreviewFpsRange());
            }
            if (range == null){
                List<int[]> supportPreviewFps = mCameraParameters.getSupportedPreviewFpsRange();
                Collections.sort(supportPreviewFps, new Comparator<int[]>() {
                    @Override
                    public int compare(int[] o1, int[] o2) {
                        return o2[0]-o1[0];
                    }
                });
                int maxFps = 0;
                int minFps = 0;
                // 在支持的情况下控制帧率在30以内，16以上，且接受一定的区间波动
                for (int i=0 ;i<supportPreviewFps.size();i++){
                    int[] fpsArray = supportPreviewFps.get(i);
                    maxFps = fpsArray[1];
                    minFps = fpsArray[0];
                    if (maxFps<=30000 && fpsArray[0]<fpsArray[1]){
                        if (maxFps<16000 && i>0){
                            fpsArray = supportPreviewFps.get(i-1);
                            maxFps = fpsArray[1];
                            minFps = fpsArray[0];
                        }
                        break;
                    }
                }
                if (maxFps!=0) {
                    range = new ParamterRange(minFps, maxFps);
                }
            }
            if (range != null){
                mCameraParameters.setPreviewFpsRange(range.min,range.max);
            }

        }
    }
    private Camera.Size setDefaultSize(List<Camera.Size> supportSize,Size requestSize){
        for(Camera.Size size : supportSize){
            if (requestSize.width == size.width && requestSize.height == size.height){
                return size;
            }
        }
        // 升序排序
        Collections.sort(supportSize, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size o1, Camera.Size o2) {
                return Long.signum((long)o1.width*(long)o1.height-(long)o2.width*(long)o2.height);
            }
        });
        for (Camera.Size size : supportSize){
            if (size.width >= requestSize.width && size.height >= requestSize.height){
                return size;
            }
        }
        return supportSize.get(supportSize.size()-1);
    }



    private void switchCameraReal(){
        if (!hasOpen){
            return ;
        }
        mCameraConfig.changeFacing();
        openCameraInner(true);
        startPreviewReal();
    }

    private void releaseCameraReal(boolean isInner,boolean isSwitch){
        if (!hasOpen){
            return;
        }
        try{
            if (hasPreview){
                stopPreviewReal(true);
            }
            mCamera.setErrorCallback(null);
            mCamera.setPreviewCallback(null);
            mCamera.release();
            hasOpen = false;
            mCameraParameters = null;
            if (!isInner){
                onCameraReleaseCallback();
            }
        }catch (Throwable e){
            onCameraErrorCallback(APMCameraListener.ERROR_CAMERA_DESTORY,"failed to destoryCamera", e, isSwitch);
        }
    }




    private class ExecuteHandler extends Handler{
        ExecuteHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 0:
                    if (msg.obj instanceof CameraParam){
                        openCameraReal((CameraParam)msg.obj,false);
                    }
                    break;
                case 1:
                    startPreviewReal();
                    break;
                case 2:
                    stopPreviewReal(false);
                    break;
                case 3:
                    releaseCameraReal(false,false);
                    break;
                case 4:
                    releaseReal();
                    break;
                case 5:
                    switchCameraReal();
                    break;
                case 6:
                    // todo
                    break;
                case 7:
                    if (msg.obj instanceof Float){
                        setZoomReal((Float) msg.obj);
                    }
                    break;
                case 8:
                    //todo
                    break;
                case 9:
                    // todo
                    break;
                case 10:
                    // todo
                    break;

            }
        }
    }

    private void setZoomReal(float k) {
        if (mCamera==null){
            return;
        }
        if (k<0f){
            k=0f;
        }
        if (k>1f){
            k=1f;
        }
        float oldZoom = mCameraParameters.getZoom();
        if (Float.compare(oldZoom,k)!=0 && mCameraParameters.isZoomSupported()){
            try{
                int maxZoom = mCameraParameters.getMaxZoom();
                mCameraParameters.setZoom((int)(maxZoom*k));
                mCamera.setParameters(mCameraParameters);
            } catch (Throwable e){
                e.printStackTrace();
            }
        }
    }

    private void onCameraOpenCallback(boolean isSwitchCamera){
        CameraResult result = new CameraResult();
        result.facing = this.mCameraInfo == null ? -1 : this.mCameraInfo.facing;
        result.switchCamera = isSwitchCamera;
        result.cameraInfo = this.mCameraInfo;
        result.camera = this.mCamera;
        sendCallbackMsg(1, result);
    }
    private void onCameraReleaseCallback(){
        sendCallbackMsg(2,null);
    }
    private void onCameraErrorCallback(int code,String msg,Throwable e,boolean switchCamera){
        if (this.mCameraListener != null) {
            CameraResult result = new CameraResult();
            result.switchCamera = switchCamera;
            result.facing = this.mCameraInfo != null ? this.mCameraInfo.facing : -1;
            result.cameraInfo = this.mCameraInfo;
            result.camera = this.mCamera;
            CameraCapture.CameraError error = new CameraCapture.CameraError();
            error.code = code;
            error.errMsg = msg;
            error.result = result;
            sendCallbackMsg(3, error);
        }
    }
    private void onPreviewStartCallback(){
        if (this.mPreviewListener != null) {
            PreviewResult bundle = new PreviewResult();
            if (this.mPreviewSize != null) {
                bundle.previewSize = new Size(this.mPreviewSize.width, this.mPreviewSize.height);
            } else {
                bundle.previewSize = new Size(0, 0);
            }

            bundle.displayOrientation = this.mDisplayOrientation;
            bundle.curCameraInfo = mCameraInfo;
            bundle.camera = this.mCamera;
            sendCallbackMsg(4, bundle);
        }
    }
    private void onPreviewStopCallback(){
        sendCallbackMsg(5,null);
    }
    private void onPreviewErrorCallback(int code,String msg,Throwable e){
        CameraError error = new CameraError();
        error.code = code;
        error.errMsg = msg;
        sendCallbackMsg(6, error);
    }
    private void onPictureCallback(byte[] data, Camera.Size size){
        if (this.mPictureResultListener != null) {
            PictureResult result = new PictureResult();
            result.mPictureData = data;
            result.mPictureSize = size;
            result.facing = this.mCameraInfo != null ? this.mCameraInfo.facing : 0;
            result.mDisplayOrientation = this.mDisplayOrientation;
            sendCallbackMsg(8, result);
        }
    }
    private void onPreviewFrameCallback(byte[] data){
        if (this.mPreviewFrameListener != null) {
            PreviewFrameData frameData = new PreviewFrameData();
            frameData.facing = mCameraInfo.facing;
            frameData.mPreviewSize = this.mPreviewSize;
            frameData.displayOrientation = this.mDisplayOrientation;
            frameData.mYUVData = data;
            sendCallbackMsg(7, frameData);
        }

        if (snapshot) {
            onPictureCallback(data, this.mPreviewSize);
        }
    }


    private class CallbackHandler extends Handler{
        CallbackHandler(Looper looper){
            super(looper);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {

            CameraCapture.CameraError preE;
            switch(msg.what) {
                case 1:
                    if (CameraCapture.this.mCameraListener != null && msg.obj instanceof CameraResult) {
                        CameraCapture.this.mCameraListener.onCameraOpen((CameraResult)msg.obj);
                    }
                    break;
                case 2:
                    if (CameraCapture.this.mCameraListener != null) {
                        CameraCapture.this.mCameraListener.onCameraRelease();
                    }
                    break;
                case 3:
                    if (CameraCapture.this.mCameraListener != null && msg.obj instanceof CameraCapture.CameraError) {
                        preE = (CameraCapture.CameraError)msg.obj;
                        CameraCapture.this.mCameraListener.onCameraError(preE.code, preE.errMsg, preE.result);
                    }
                    break;
                case 4:
                    if (CameraCapture.this.mPreviewListener != null && msg.obj instanceof PreviewResult) {
                        CameraCapture.this.mPreviewListener.onPreviewBegin((PreviewResult)msg.obj);
                    }
                    break;
                case 5:
                    if (CameraCapture.this.mPreviewListener != null) {
                        CameraCapture.this.mPreviewListener.onPreviewEnd();
                    }
                    break;
                case 6:
                    if (CameraCapture.this.mPreviewListener != null && msg.obj instanceof CameraCapture.CameraError) {
                        preE = (CameraCapture.CameraError)msg.obj;
                        CameraCapture.this.mPreviewListener.onPreviewError(preE.code, preE.errMsg);
                    }
                    break;
                case 7:
                    if (CameraCapture.this.mPreviewFrameListener != null && msg.obj instanceof PreviewFrameData) {
                        CameraCapture.this.mPreviewFrameListener.onPreviewFrameData((PreviewFrameData)msg.obj);
                    }
                    break;
                case 8:
                    if (CameraCapture.this.mPictureResultListener != null && msg.obj instanceof PictureResult) {
                        CameraCapture.this.mPictureResultListener.onTakenPicture((PictureResult)msg.obj);
                    }
            }
        }
    }
    private static class CameraError {
         int code;
         String errMsg;
         CameraResult result;

        private CameraError() {
        }
    }

}
