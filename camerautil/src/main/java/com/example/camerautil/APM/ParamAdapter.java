package com.example.camerautil.APM;

/**
 * 记住用户的设置状态
 */
class ParamAdapter {
    private String focusMode;
    private String flashMode;
    private boolean userSetFocusMode;
    private boolean userSetFlashMode;

    ParamAdapter() {
    }

    public String getFocusMode() {
        return this.focusMode;
    }

    public void setFocusMode(String focusMode, boolean offerUpdate) {
        if (!this.userSetFocusMode || offerUpdate) {
            this.focusMode = focusMode;
            this.userSetFocusMode = offerUpdate;
        }

    }

    public String getFlashMode() {
        return this.flashMode;
    }

    public void setFlashMode(String flashMode, boolean offerUpdate) {
        if (!this.userSetFlashMode || offerUpdate) {
            this.userSetFlashMode = offerUpdate;
            this.flashMode = flashMode;
        }

    }
}
