package com.fenda.ai.skill;

import android.util.Log;

import com.aispeech.dui.plugin.iqiyi.IQiyiPlugin;
import com.aispeech.dui.plugin.setting.SettingPlugin;
import com.aispeech.dui.plugin.setting.SystemCtrl;
import com.fenda.ai.authz.AccessibilityMonitorService;

/**
 * Created by chuck.liuzhaopeng on 2019/6/24.
 */

public class SystemControl extends SystemCtrl {
    private static final String TAG = "SystemControl";
    private static final String QIYIMOBILE_PKG = "com.qiyi.video.speaker";

    @Override
    public int refresh() {//刷新首页内容
        if (AccessibilityMonitorService.getTopPackageName().equals(QIYIMOBILE_PKG)) {
            return IQiyiPlugin.get().getVideoApi().refresh();
        }
        return SettingPlugin.ERR_NOT_SUPPORT;
    }

    @Override
    public int boot(String s, String s1, String s2, String s3) {
        Log.d(TAG, "boot s " + s + " s1 "+ s1 + "  s2 " + s2 + " s3: " +s3);
        return super.boot(s, s1, s2, s3);
    }



    @Override
    public int shutDown(String s, String s1, String s2, String s3) {
        Log.d(TAG, "shutDown s " + s + " s1 "+ s1 + "  s2 " + s2 + " s3: " +s3);
        return super.shutDown(s, s1, s2, s3);
    }

    @Override
    public int goBack() {//爱奇艺内返回
        if (AccessibilityMonitorService.getTopPackageName().equals(QIYIMOBILE_PKG)) {
            return IQiyiPlugin.get().getVideoApi().back();
        }
        return super.goBack();
    }

    @Override
    public int screenOff() {
        return super.screenOff();
    }

    @Override
    public int screenOn() {
        return super.screenOn();
    }
}
