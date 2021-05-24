package com.example.camerautil.interfaces;

import com.example.camerautil.bean.PreviewMessage;

public interface MyPreviewListener {
    int PREVIEW_OPEN_IO_ERROR = 1;

    void onPreviewStart(PreviewMessage msg);
    void onPreviewError(int code,String msg);
    void onPreviewStop();
}
