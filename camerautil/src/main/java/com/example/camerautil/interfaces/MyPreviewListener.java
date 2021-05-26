package com.example.camerautil.interfaces;

import com.example.camerautil.bean.PreviewMessage;

public interface MyPreviewListener {
    int PREVIEW_OPEN_IO_ERROR = 1;
    int PREVIEW_NOT_START = 2;

    void onPreviewStart(PreviewMessage msg);
    void onPreviewError(int code,String msg);
    void onPreviewStop();
}
