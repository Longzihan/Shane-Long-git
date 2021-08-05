package com.fenda.ai.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;


public class AppUtils {
    public static void jump2SmartCallApp(final Context context) {

        PackageManager packageManager = context.getPackageManager();
        if (checkPackInfo(context, "com.fenda.smartcall")) {
            Intent intent = packageManager.getLaunchIntentForPackage("com.fenda.smartcall");
            context.startActivity(intent);
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "没有安装语音通话", Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

    /**
     * 检查包是否存在
     *
     * @param packname
     * @return
     */
    private static boolean checkPackInfo(Context context, String packname) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packname, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }
}
