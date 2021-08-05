package com.fenda.ai.observer;


import android.util.Log;
import com.aispeech.dui.dds.DDS;
import com.aispeech.dui.dsk.duiwidget.CommandObserver;

import com.fenda.ai.FDApplication;

import com.fenda.ai.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 客户端CommandObserver, 用于处理客户端动作的执行以及快捷唤醒中的命令响应.
 * 例如在平台配置客户端动作： command://call?phone=$phone$&name=#name#,
 * 那么在CommandObserver的onCall方法中会回调topic为"call", data为
 */
public class DuiCommandObserver implements CommandObserver {
    private String TAG = "DuiCommandObserver";


    public DuiCommandObserver() {
    }

    // 注册当前更新消息
    public void regist() {
        DDS.getInstance().getAgent().subscribe(new String[]{
                        "call",
                },
                this);
    }

    // 注销当前更新消息
    public void unregist() {
        DDS.getInstance().getAgent().unSubscribe(this);
    }

    @Override
    public void onCall(String command, String data) {
        Log.e(TAG, "command: " + command + "  data: " + data);
        if (command.equals("call")) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                String phone = jsonObject.optString("phone");
                if ("$phone$".equals(phone)) {

                    AppUtils.jump2SmartCallApp(FDApplication.getContext());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
