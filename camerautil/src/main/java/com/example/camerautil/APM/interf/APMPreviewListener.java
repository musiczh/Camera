package com.example.camerautil.APM.interf;

import com.example.camerautil.APM.bean.PreviewResult;

public interface APMPreviewListener {
    int ERROR_PREVIEW_BEGINE = -11;
    int ERROR_PREVIEW_END = -12;

    void onPreviewBegin(PreviewResult var1);

    void onPreviewEnd();

    void onPreviewError(int var1, String var2);
}

