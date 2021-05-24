package com.example.camerautil.bean;

/**
 * 内部错误信息封装类，发送给handler去处理
 */
public class CameraErrorMsg {
    /**
     * code 对应监听器的错误码
     * msg 错误信息
     * cameraMessage 相机信息；如若不需要可传入null
     * previewMessage 预览信息；如若不需要可传入null
     */
    private int code;
    private String msg;
    private CameraMessage cameraMessage;

    public CameraErrorMsg(int code, String msg, CameraMessage cameraMessage) {
        this.code = code;
        this.msg = msg;
        this.cameraMessage = cameraMessage;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public CameraMessage getCameraMessage() {
        return cameraMessage;
    }

    public void setCameraMessage(CameraMessage cameraMessage) {
        this.cameraMessage = cameraMessage;
    }
}
