package com.fenda.ai;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aispeech.dui.dds.DDS;
import com.aispeech.dui.dds.exceptions.DDSNotInitCompleteException;
import com.fenda.ai.authz.DDSService;
import com.fenda.ai.authz.ObService;


/**
 * Created by chuck.liuzhaopeng on 2019/6/21.
 */

public class BootReceiver extends BroadcastReceiver {
    private Context mContext = FDApplication.getContext();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            mContext.startService(new Intent(mContext, DDSService.class));
            mContext.startService(new Intent(mContext, ObService.class));
        }
    }

    // 打开唤醒，调用后才能语音唤醒
    void enableWakeup() {
        try {
            DDS.getInstance().getAgent().getWakeupEngine().enableWakeup();
        } catch (DDSNotInitCompleteException e) {
            e.printStackTrace();
        }
    }
}
