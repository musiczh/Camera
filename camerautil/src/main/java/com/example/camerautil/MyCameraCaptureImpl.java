package com.example.camerautil;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.ErrorCallback;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.example.camerautil.bean.CameraErrorMsg;
import com.example.camerautil.bean.CameraMessage;
import com.example.camerautil.bean.FocusAreaParam;
import com.example.camerautil.bean.MyCameraConfig;
import com.example.camerautil.bean.PictureData;
import com.example.camerautil.bean.PreviewFrameData;
import com.example.camerautil.bean.PreviewMessage;
import com.example.camerautil.interfaces.MyCameraCapture;
import com.example.camerautil.interfaces.MyCameraListener;
import com.example.camerautil.interfaces.MyPictureListener;
import com.example.camerautil.interfaces.MyPreviewFrameListener;
import com.example.camerautil.interfaces.MyPreviewListener;
import com.example.camerautil.preview.MyPreview;
import com.example.camerautil.util.PermissionUtil;

import java.io.IOException;
import java.util.List;

public class MyCameraCaptureImpl implements MyCameraCapture {

    public static int CAMERA_FACE_FRONT = 1;
    public static int CAMERA_FACE_BACK = 0;
    private static int PREVIEW_BUFFER_SIZE = 3;

    private Context mContext;
    private int cameraNum = -1;
    private CameraHandler mHandler;
    private CallbackHandler mExecuteHandler;
    private Camera mCamera;
    private CameraInfo mCameraInfo;
    private Parameters mCameraParams;
    private MyCameraConfig mCameraConfig;
    private ErrorCallback mErrorCallback;
    private Camera.PictureCallback mPictureCallback;
    private boolean isPreviewing = false;
    private int degree = 0;
    private int cameraId = -1;
    private boolean isDestroy = false;
    private byte[][] mPreviewBuffer = new byte[PREVIEW_BUFFER_SIZE][];
    private int mBufferIndex = 0;
    private boolean isFocusing = false;
    private boolean isSnapshot = false;

    // ????????????
    private final int CODE_PREVIEW_FRAME = 1;
    private final int CODE_PREVIEW_START = 2;
    private final int CODE_PREVIEW_ERROR = 3;
    private final int CODE_PREVIEW_STOP = 4;
    private final int CODE_CAMERA_OPEN = 5;
    private final int CODE_CAMERA_ERROR = 6;
    private final int CODE_CAMERA_RELEASE = 7;
    private final int CODE_PICTURE_TOKEN = 8;

    // ??????????????????????????????
    private MyPreviewFrameListener mPreviewFrameListener;
    private MyCameraListener mCameraListener;
    private MyPreviewListener mPreviewListener;
    private MyPictureListener mPictureListener;

    public MyCameraCaptureImpl(Context context , MyCameraConfig config){
        mContext = context;

        HandlerThread handlerThread = new  HandlerThread("MyCameraCaptureImpl");
        HandlerThread handlerThread2 = new HandlerThread("MyCameraCaptureImpl-executeHandler");
        handlerThread.start();
        handlerThread2.start();
        mHandler = new CameraHandler(handlerThread.getLooper());
        mExecuteHandler = new CallbackHandler(handlerThread2.getLooper());

        mCameraConfig = config;
        if (context instanceof Activity ){
            Activity activity = (Activity) context;
            Display display = activity.getDisplay();
            if (display != null){
                int rotation = display.getRotation();
                switch (rotation) {
                    case Surface.ROTATION_0: degree = 0; break;
                    case Surface.ROTATION_90: degree = 90; break;
                    case Surface.ROTATION_180: degree = 180; break;
                    case Surface.ROTATION_270: degree = 270; break;
                }
            }
        }
        mErrorCallback = new ErrorCallback() {
            @Override
            public void onError(int error, Camera camera) {
                CameraErrorMsg cameraErrorMsg = new CameraErrorMsg(MyCameraListener.CAMERA_SYS_ERROR,"errorcode = "+error,getCameraMessage(false));
                sendMsgCallbackHandler(CODE_CAMERA_ERROR,cameraErrorMsg);
            }
        };
        mPictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                pictureCallback(data);
                if (camera!=null){
                    camera.cancelAutoFocus();
                    if (mCameraConfig.isKeepPreviewAfterTakePicture()){
                        mCamera.startPreview();
                    }
                }
            }
        };
    }

    private void pictureCallback(byte[] data){
        PictureData pictureData = new PictureData(mCameraParams.getPictureSize(),degree,data,mCameraConfig.getFacing());
        sendMsgCallbackHandler(CODE_PICTURE_TOKEN,pictureData);
    }

    private CameraMessage getCameraMessage(boolean switchCamera){
        return new CameraMessage(switchCamera,mCameraConfig.getFacing(),mCameraInfo,mCamera);
    }

    // ??????????????????????????????
    private boolean checkCamera(){
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    @Override
    public void startCamera() {
        sendMessage(1);
    }

    @Override
    public void switchCamera(int facing) {
        if (mCameraConfig.getFacing()!=facing){
            mCameraConfig.setFacing(facing);
        }
        sendMessage(4);
    }

    @Override
    public void stopCamera() {
        sendMessage(2);
    }

    @Override
    public void release() {
        sendMessage(5);
    }

    @Override
    public boolean isOpen(){
        return mCamera!=null;
    }

    @Override
    public int getFacing() {
        return mCameraConfig.getFacing();
    }

    @Override
    public void setPreviewListener(MyPreviewListener listener) {
        this.mPreviewListener = listener;
    }

    @Override
    public void setPreviewFrameListener(MyPreviewFrameListener listener) {
        this.mPreviewFrameListener = listener;
    }

    @Override
    public void setCameraListener(MyCameraListener listener) {
        this.mCameraListener = listener;
    }

    @Override
    public void setPictureListener(MyPictureListener pictureListener) {
        this.mPictureListener = pictureListener;
    }

    @Override
    public List<String> getSupportFlashMode() {
        if (mCamera==null){
            postCameraNotOpenMsg(false);
            return null;
        }
        return mCameraParams.getSupportedFlashModes();
    }

    @Override
    public void setFlashMode(String flashMode) {
        mCameraConfig.setFlashMode(flashMode);
        configFlashModeInner();
        mCamera.setParameters(mCameraParams);
    }

    @Override
    public String getCurrentFlashMode() {
        if (mCamera != null){
            return mCameraParams.getFlashMode();
        }else{
            postCameraNotOpenMsg(false);
            return null;
        }
    }

    @Override
    public List<String> getSupportFocusMode() {
        if (mCamera != null){
            return mCameraParams.getSupportedFocusModes();
        }else{
            postCameraNotOpenMsg(false);
            return null;
        }
    }

    @Override
    public void setFocusMode(String focusMode) {
        mCameraConfig.setFocusMode(focusMode);
        configFocusMode();
        mCamera.setParameters(mCameraParams);

    }

    @Override
    public String getCurrentFocusMode() {
        if (mCamera==null){
            postCameraNotOpenMsg(false);
            return null;
        }
        return mCameraParams.getFocusMode();
    }

    @Override
    public void tapFocus(final FocusAreaParam param) {
        if (mCamera == null){
            postCameraNotOpenMsg(false);
            return;
        }
        List<String> focusMode = mCameraParams.getSupportedFocusModes();
        if (!focusMode.contains(Parameters.FOCUS_MODE_AUTO)){
            CameraMessage msg = new CameraMessage(false,mCameraConfig.getFacing(),mCameraInfo,mCamera);
            sendMsgCallbackHandler(CODE_CAMERA_ERROR,new CameraErrorMsg(MyCameraListener.CAMERA_NOT_SUPPORT_AUTO_FOCUS,"??????????????????????????????",msg));
            return ;
        }
        if (isFocusing){
            return;
        }else{
            isFocusing = true;
        }

        final String oldFocusMode = mCameraParams.getFocusMode();
        int maxFocusArea = mCameraParams.getMaxNumFocusAreas();
        int maxMeteringArea = mCameraParams.getMaxNumMeteringAreas();

        if (param.getFocusAreaList().size()<=maxFocusArea){
            mCameraParams.setFocusAreas(param.getFocusAreaList());
        }
        if (param.getMeteringAreaList().size()<=maxMeteringArea){
            mCameraParams.setMeteringAreas(param.getMeteringAreaList());
        }
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (param.isKeepFocusMode()){
                    mCameraParams.setFocusMode(oldFocusMode);
                }
                mCameraParams.setMeteringAreas(null);
                mCameraParams.setFocusAreas(null);
                mCamera.cancelAutoFocus();
                mCamera.setParameters(mCameraParams);
                isFocusing = false;
            }
        });


    }

    @Override
    public void setZoom(float k) {
        if (mCamera==null){
            postCameraNotOpenMsg(false);
            return;
        }
        if (k<0f){
            k=0f;
        }
        if (k>1f){
            k=1f;
        }
        float oldZoom = mCameraParams.getZoom();
        if (Float.compare(oldZoom,k)!=0 && mCameraParams.isZoomSupported()){
            int maxZoom = mCameraParams.getMaxZoom();
            mCameraParams.setZoom((int)(maxZoom*k));
            mCamera.setParameters(mCameraParams);
        }
    }

    @Override
    public void snapshot() {
        isSnapshot = true;
    }

    @Override
    public void openCamera() {

    }

    @Override
    public void startPreview() {

    }


    private void configFocusMode(){
        if (mCamera == null){
            postCameraNotOpenMsg(false);
            return ;
        }
        List<String> supportFocusMode = getSupportFocusMode();
        if (supportFocusMode.contains(mCameraConfig.getFocusMode())){
            mCameraParams.setFocusMode(mCameraConfig.getFocusMode());
        }else if (supportFocusMode.contains(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){
            mCameraParams.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }else if (supportFocusMode.contains(Parameters.SCENE_MODE_AUTO)){
            mCameraParams.setFocusMode(Parameters.FOCUS_MODE_AUTO);
        }else{
            mCameraParams.setFocusMode(supportFocusMode.get(0));
        }
    }

    private void configFlashModeInner(){
        if (mCamera == null){
            postCameraNotOpenMsg(false);
            return ;
        }
        List<String> supportFlashMode = getSupportFlashMode();
        if (supportFlashMode != null && supportFlashMode.contains(mCameraConfig.getFlashMode())){
            mCameraParams.setFlashMode(mCameraConfig.getFlashMode());
        }
    }

    @Override
    public void takePicture(final Camera.ShutterCallback callBack, boolean isAutoFocus) {
        // ???????????????????????????????????????????????????????????????handler?????????
        // ???????????????????????????????????????????????????????????????????????????
        if (mCamera == null){
            postCameraNotOpenMsg(false);
            return ;
        }
        // ?????????????????????????????????????????????????????????
        if (isAutoFocus && mCameraParams.getSupportedFocusModes().contains(Parameters.FOCUS_MODE_AUTO)){
            final String oldFocusMode = mCameraParams.getFocusMode();
            mCameraParams.setFocusMode(Parameters.FOCUS_MODE_AUTO);
            mCamera.setParameters(mCameraParams);
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (camera!=null){
                        camera.takePicture(callBack,null,null,mPictureCallback);
                        camera.cancelAutoFocus();
                        mCameraParams.setFocusMode(oldFocusMode);
                        camera.setParameters(mCameraParams);
                    }
                }
            });
        }else{
            mCamera.takePicture(callBack,null,null,mPictureCallback);
        }
    }

    @Override
    public List<Camera.Size> getSupportPreviewSize(){
        return mCameraParams.getSupportedPreviewSizes();
    }

    @Override
    public void setPreviewSize(Camera.Size size) {
        if (mCamera == null){
            postCameraNotOpenMsg(false);
            return;
        }
        mCameraConfig.setPreviewSize(size);
        configPreviewSize();
        if (isPreviewing){
            mCamera.stopPreview();
        }
        mCamera.setParameters(mCameraParams);
        mCamera.startPreview();
    }

    @Override
    public Camera.Size getCurrentPreviewSize() {
        if (isPreviewing){
            return mCameraParams.getPreviewSize();
        }else{
            return null;
        }
    }

    private void configPreviewSize(){
        if (mCamera == null){
            postCameraNotOpenMsg(false);
            return;
        }
        List<Camera.Size> sizes = mCameraParams.getSupportedPreviewSizes();
        if (sizes.contains(mCameraConfig.getPreviewSize())){
            mCameraParams.setPreviewSize(mCameraConfig.getPreviewSize().width,mCameraConfig.getPreviewSize().height);
        }
    }

    // ??????90???
    public void changeOrientation(boolean isLeft){
        if (mCamera==null){
            postCameraNotOpenMsg(false);
            return;
        }
        if (isLeft){
            degree = Math.abs((degree-90)%360);
        }else{
            degree = (degree+90)%360;
        }
        mCamera.setDisplayOrientation(calcDisplayOrientation(degree));
        Toast.makeText(mContext,"degree="+degree+" cameraInfo.orientation="+mCameraInfo.orientation,Toast.LENGTH_SHORT).show();
        mCamera.setDisplayOrientation(degree);
    }


    // ????????????????????????????????????????????????????????????
    private void sendMessage(int what){
        if (!isDestroy){
            mHandler.sendEmptyMessage(what);
        }
    }

    // ????????????????????????????????????
    private void sendMsgCallbackHandler(int what, Object obj){
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        mExecuteHandler.sendMessage(msg);
    }

    private void postCameraNotOpenMsg(boolean switchCamera){
        CameraErrorMsg msg = new CameraErrorMsg(MyCameraListener.CAMERA_NOT_OPEN,"??????????????????",getCameraMessage(switchCamera));
        sendMsgCallbackHandler(CODE_CAMERA_ERROR,msg);
    }


    private class CallbackHandler extends Handler{
        CallbackHandler(Looper looper){
            super(looper);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            CameraErrorMsg errorMsg = null;
            if (msg.obj instanceof CameraErrorMsg){
                errorMsg = (CameraErrorMsg) msg.obj;
            }
            switch (msg.what){
                case CODE_PREVIEW_FRAME:
                    if (mPreviewFrameListener!=null && msg.obj instanceof PreviewFrameData){
                        mPreviewFrameListener.onPreviewFrameArrive((PreviewFrameData)msg.obj);
                    }
                    break;
                case CODE_PREVIEW_START:
                    if (mPreviewListener!=null && msg.obj instanceof PreviewMessage){
                        mPreviewListener.onPreviewStart((PreviewMessage)msg.obj);
                    }
                    break;
                case CODE_PREVIEW_ERROR:
                    if (mPreviewListener!=null && errorMsg!=null){
                        mPreviewListener.onPreviewError(errorMsg.getCode(),errorMsg.getMsg());
                    }
                    break;
                case CODE_PREVIEW_STOP:
                    if (mPreviewListener!=null){
                        mPreviewListener.onPreviewStop();
                    }
                    break;
                case CODE_CAMERA_OPEN:
                    if (mCameraListener!=null && msg.obj instanceof CameraMessage){
                        mCameraListener.onCameraOpen((CameraMessage) msg.obj);
                    }
                    break;
                case CODE_CAMERA_ERROR:
                    if (mCameraListener!=null  && errorMsg!=null){
                        mCameraListener.onCameraError(errorMsg.getCode(),errorMsg.getMsg(),errorMsg.getCameraMessage());
                    }
                    break;
                case CODE_CAMERA_RELEASE:
                    if (mCameraListener!=null){
                        mCameraListener.onCameraRelease();
                    }
                    break;
                case CODE_PICTURE_TOKEN:
                    if (mPictureListener!=null && msg.obj instanceof PictureData){
                        mPictureListener.onPictureTaken((PictureData)msg.obj);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private class CameraHandler extends Handler {
        CameraHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1 :
                    startCameraReal();
                    break;
                case 2:
                    stopCameraReal();
                    break;
                case 3:
                    break;
                case 4:
                    switchCameraReal();
                    break;
                case 5:
                    releaseReal();
                    break;

            }
        }
    }



    private void stopCameraReal(){
        releaseCamera();
    }

    private void startCameraReal(){
        openCamera(false);
        startPreviewInner();
    }

    private void releaseReal(){
        releaseCamera();
        mHandler.getLooper().quit();
        isDestroy = true;
    }

    private void switchCameraReal(){
        if (mCamera!=null){
            openCamera(true);
            startPreviewInner();
        }else{
            postCameraNotOpenMsg(true);
        }

    }

    private void startPreviewInner(){
        MyPreview preview = mCameraConfig.getPreview();
        Object target  = preview.getTarget();
        if (target == null || mCamera==null){
            return;
        }
        if (isPreviewing){
            stopPreview();
        }
        initPreviewBuffer();
        try{
            if (preview.getClassType() == SurfaceHolder.class){
                mCamera.setPreviewDisplay((SurfaceHolder)preview.getTarget());
            }else{
                SurfaceTexture surfaceTexture = (SurfaceTexture)target;
                mCamera.setPreviewTexture(surfaceTexture);
            }
            isPreviewing = true;
            mCamera.startPreview();
            PreviewMessage previewMessage = new PreviewMessage(mCameraParams.getPreviewSize(),degree,mCameraInfo,mCamera);
            sendMsgCallbackHandler(CODE_PREVIEW_START,previewMessage);
        }catch (IOException e){
            CameraErrorMsg msg = new CameraErrorMsg(MyPreviewListener.PREVIEW_OPEN_IO_ERROR,e.getMessage(),null);
            sendMsgCallbackHandler(CODE_PREVIEW_ERROR,msg);
            e.printStackTrace();
        }
    }


    private void initPreviewBuffer(){
        // ???????????????????????????????????????????????????????????????????????????
        if (mPreviewFrameListener != null){
            Camera.Size size = mCameraParams.getPreviewSize();
            int bufferSize = size.width * size.height * ImageFormat.getBitsPerPixel(mCameraConfig.getPreviewFormat()) / 8;
            for (int i=0;i<mPreviewBuffer.length;i++){
                mPreviewBuffer[i] = new byte[bufferSize];
            }
            mBufferIndex = 0;
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.addCallbackBuffer(mPreviewBuffer[0]);
            mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    PreviewFrameData frameData = new PreviewFrameData();
                    frameData.setCamera(camera);
                    frameData.setFacing(mCameraConfig.getFacing());
                    frameData.setData(data);
                    sendMsgCallbackHandler(CODE_PREVIEW_FRAME,frameData);
                    // ????????????????????????????????????????????????????????????
                    mBufferIndex = (mBufferIndex+1)%mPreviewBuffer.length;
                    camera.addCallbackBuffer(mPreviewBuffer[mBufferIndex]);

                    if (isSnapshot){
                        pictureCallback(data);
                        isSnapshot = false;
                    }
                }
            });
        }
    }

    // ????????????????????????????????????????????????
    private int checkPreviewFormatSupport(int format){
        List<Integer> list = mCameraParams.getSupportedPreviewFormats();
        if (list!=null && list.contains(format)){
            return format;
        }else{
            return -1;
        }
    }


    private void openCamera(boolean ifSwitch){
        // ??????????????????????????????????????????????????????????????????????????????
        if (!ifSwitch && mCamera!=null){
            CameraErrorMsg msg = new CameraErrorMsg(MyCameraListener.CAMERA_OPEN_TWICE,"???????????????????????????",getCameraMessage(false));
            sendMsgCallbackHandler(CODE_CAMERA_ERROR,msg);
        }else{
            openCameraInner(ifSwitch);
        }
    }

    private void openCameraInner(boolean ifSwitch){
        // ?????????????????????
        if (!checkCamera() || -1 == (cameraNum = Camera.getNumberOfCameras())){
            CameraErrorMsg msg = new CameraErrorMsg(MyCameraListener.CAMERA_NO_SUPPORT,"?????????????????????",getCameraMessage(ifSwitch));
            sendMsgCallbackHandler(CODE_CAMERA_ERROR,msg);
            return ;
        }
        if (!PermissionUtil.hasPermission("android.permission.CAMERA",mContext)){
            CameraErrorMsg msg = new CameraErrorMsg(MyCameraListener.CAMERA_PERMISSION_DENY,"??????????????????",getCameraMessage(ifSwitch));
            sendMsgCallbackHandler(CODE_CAMERA_ERROR,msg);
            return ;
        }

        // ??????????????????????????????????????????
        if (mCamera!=null){
            releaseCamera();
            cameraId = -1;
        }

        // ???????????????????????????
        mCameraInfo = new CameraInfo();
        for(int i = 0; i < cameraNum; ++i) {
            Camera.getCameraInfo(i, mCameraInfo);
            if (mCameraInfo.facing == this.mCameraConfig.getFacing()) {
                this.cameraId = i;
                break;
            }
        }
        if (cameraId == -1){
            CameraErrorMsg msg = new CameraErrorMsg(MyCameraListener.CAMERA_NO_SUPPORT,"?????????ID= "+mCameraConfig.getFacing()+" ????????????",getCameraMessage(ifSwitch));
            sendMsgCallbackHandler(CODE_CAMERA_ERROR,msg);
            return ;
        }

        // ????????????
        mCamera = Camera.open(cameraId);
        mCameraParams = mCamera.getParameters();
        configDisplayOrientation();
        mCamera.setErrorCallback(mErrorCallback);

        // ???????????????????????????????????????
        configCameraParameters();

        // ????????????
        CameraMessage message = new CameraMessage(ifSwitch,mCameraConfig.getFacing(),mCameraInfo,mCamera);
        sendMsgCallbackHandler(CODE_CAMERA_OPEN,message);

    }

    private void configDisplayOrientation(){

        if (mContext instanceof Activity){
            Activity activity = (Activity) mContext;
            Display display = activity.getDisplay();
            if (display != null){
                int rotation = display.getRotation();
                switch (rotation) {
                    case Surface.ROTATION_0: degree = 0; break;
                    case Surface.ROTATION_90: degree = 90; break;
                    case Surface.ROTATION_180: degree = 180; break;
                    case Surface.ROTATION_270: degree = 270; break;
                }
            }
        }else{
            degree = 0;
        }

        int rotation = 0;
        if (mCameraInfo.facing == 1) {
            rotation = (mCameraInfo.orientation + degree) % 360;
        } else {
            int landscapeFlip = isLandscape(degree) ? 180 : 0;
            rotation = (mCameraInfo.orientation + degree + landscapeFlip) % 360;
        }

        mCameraParams.setRotation(rotation);
        mCamera.setDisplayOrientation(calcDisplayOrientation(degree));
    }
    private boolean isLandscape(int orientationDegrees) {
        return orientationDegrees == 90 || orientationDegrees == 270;
    }


    private void configCameraParameters(){
        int format = checkPreviewFormatSupport(mCameraConfig.getPreviewFormat());
        if (format!=-1){
            mCameraParams.setPreviewFormat(format);
        }
        configFlashModeInner();
        configFocusMode();
        configPreviewSize();
        mCamera.setParameters(mCameraParams);
    }

    /**
     * ??????????????????????????????????????????????????????
     * @param screenOrientationDegrees ?????????????????????
     * @return ??????????????????????????????
     */
    private int calcDisplayOrientation(int screenOrientationDegrees) {
        if (mCameraConfig.getFacing() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return (360 - (mCameraInfo.orientation + screenOrientationDegrees) % 360)%360;
        } else {
            return (mCameraInfo.orientation - screenOrientationDegrees + 360) % 360;
        }
    }

    private void releaseCamera(){
        if (isPreviewing){
            stopPreview();
        }
        if (mCamera!=null){
            mCamera.release();
        }
        sendMsgCallbackHandler(CODE_CAMERA_RELEASE,null);
        mCamera = null;
    }


    private void stopPreview(){
        mCamera.stopPreview();
        isPreviewing = false;
        sendMsgCallbackHandler(CODE_PREVIEW_STOP,null);
    }

}
