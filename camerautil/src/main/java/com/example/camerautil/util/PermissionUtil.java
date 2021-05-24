package com.example.camerautil.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioRecord;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtil {

    public static boolean hasPermission(String permission,Context context) {
        boolean granted = false;

        try {
            if (context != null) {
                if (Build.VERSION.SDK_INT >= 23) {
                    int ret = ContextCompat.checkSelfPermission(context, permission);
                    granted = ret == 0;
                } else {
                    PackageManager pm = context.getPackageManager();
                    granted = PackageManager.PERMISSION_GRANTED == pm.checkPermission(permission, context.getPackageName());
                }
            }
        } catch (Throwable var4) {
            var4.printStackTrace();
        }

        return granted;
    }



    public static void requirePermission(Activity activity, int reqCode, String... permissions) {
        if (permissions != null && permissions.length > 0) {
            ActivityCompat.requestPermissions(activity, permissions, reqCode);
        }
    }
}
