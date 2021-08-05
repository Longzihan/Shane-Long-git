package com.fenda.ai.skill;

import android.text.TextUtils;
import android.util.Log;

import com.aispeech.dui.dds.DDS;
import com.aispeech.dui.dds.exceptions.DDSNotInitCompleteException;
import com.aispeech.dui.plugin.iqiyi.IQiyiPlugin;
import com.aispeech.dui.plugin.tvctrl.TVCtrl;
import com.aispeech.dui.plugin.tvctrl.TVCtrlPlugin;
import com.fenda.ai.authz.AccessibilityMonitorService;

/**
 * Created by chuck.liuzhaopeng on 2019/6/24.
 */

public class TVControl extends TVCtrl {
    private static final String TAG = "TVControl";
    private static final String QIYIMOBILE_PKG = "com.qiyi.video.speaker";
    public static boolean isInVideo = false;

    @Override
    public int openPage(String page) {
        Log.d(TAG, "openPage: " + page);
        if (AccessibilityMonitorService.getTopPackageName().equals(QIYIMOBILE_PKG)) {
            IQiyiPlugin.get().getVideoApi().open();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (page.equals("登录")) {
                IQiyiPlugin.get().getVideoApi().login();
            } else if (page.equals("登出")) {
                IQiyiPlugin.get().getVideoApi().logout();
            } else if (page.equals("购买会员")) {
                IQiyiPlugin.get().getVideoApi().buyVip();
            } else if (page.equals("轮播台")) {
                IQiyiPlugin.get().getVideoApi().openRadio();
            } else {
                try {
                    DDS.getInstance().getAgent().getTTSEngine().speak("为您找到以下资源",1);
                } catch (DDSNotInitCompleteException e) {
                    e.printStackTrace();
                }
                IQiyiPlugin.get().getVideoApi().channel(page);
            }
            isInVideo = true;
            return TVCtrlPlugin.ERR_OK;
        } else {
            return TVCtrlPlugin.ERR_NOT_SUPPORT;
        }
    }

    @Override
    public int select(String num, String row, String rank, String page) {
        Log.d(TAG, "select: " + num + "," + page);
        if (AccessibilityMonitorService.getTopPackageName().equals(QIYIMOBILE_PKG)) {
            if (!TextUtils.isEmpty(num)) {
                IQiyiPlugin.get().getVideoApi().playIndex(Integer.valueOf(num));//首页的第几个
            } else if (!TextUtils.isEmpty(page)) {
                if (page.contains("+")) {
                    IQiyiPlugin.get().getVideoApi().nextPage();//首页下一页
                } else if (page.contains("-")) {
                    IQiyiPlugin.get().getVideoApi().prevPage();//首页上一页
                }
            }
            isInVideo = true;
            return TVCtrlPlugin.ERR_OK;
        } else {
            return TVCtrlPlugin.ERR_NOT_SUPPORT;
        }
    }
}
