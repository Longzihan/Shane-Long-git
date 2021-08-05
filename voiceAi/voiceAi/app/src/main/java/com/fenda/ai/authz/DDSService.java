package com.fenda.ai.authz;


import android.annotation.TargetApi;
import android.app.LauncherActivity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.RemoteCallbackList;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import com.aispeech.dui.dds.DDS;
import com.aispeech.dui.dds.DDSAuthListener;
import com.aispeech.dui.dds.DDSConfig;
import com.aispeech.dui.dds.DDSInitListener;
import com.aispeech.dui.dds.auth.AuthType;

import com.aispeech.dui.dds.exceptions.DDSNotInitCompleteException;
import com.aispeech.dui.plugin.iqiyi.IQiyiPlugin;
import com.aispeech.dui.plugin.mediactrl.MediaCtrl;
import com.aispeech.dui.plugin.mediactrl.MediaCtrlPlugin;
import com.aispeech.dui.plugin.music.MusicPlugin;
import com.aispeech.dui.plugin.setting.SettingPlugin;
import com.aispeech.dui.plugin.setting.SystemCtrl;
import com.aispeech.dui.plugin.tvctrl.TVCtrl;
import com.aispeech.dui.plugin.tvctrl.TVCtrlPlugin;
import com.fenda.ai.skill.MediaControl;
import com.fenda.ai.skill.SystemControl;
import com.fenda.ai.skill.TVControl;
import com.fenda.ai.utils.Util;


/**
 * 参见Android SDK集成文档: https://www.dui.ai/docs/operation/#/ct_common_Andriod_SDK
 */
public class DDSService extends Service {
    public static final String TAG = "DDSService";

    private static final String MUSIC_PKG = "com.tencent.qqmusictv";
    private static final String QIYIMOBILE_PKG = "com.qiyi.video.speaker";

    public DDSService() {
    }

    @Override
    public void onCreate() {
        if(!AccessibilityMonitorService.isSettingOpen(AccessibilityMonitorService.class,getApplicationContext())){
            AccessibilityMonitorService.jumpToSetting(getApplicationContext());
        }
        setForeground();
        super.onCreate();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void setForeground() {
        Intent intent = new Intent(DDSService.this, LauncherActivity.class);
        PendingIntent pi = PendingIntent.getActivity(DDSService.this, 0, intent, 0);

        Notification notification = Util.pupNotification(DDSService.this, pi, "DUI ...");
        startForeground(1, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = Service.START_STICKY;
        init();
        return super.onStartCommand(intent, flags, startId);
    }

    // 初始化dds组件
    private void init() {
        DDS.getInstance().setDebugMode(2); //在调试时可以打开sdk调试日志，在发布版本时，请关闭 setDebugMode(5)
        DDS.getInstance().init(getApplicationContext(), createConfig(), mInitListener, mAuthListener);
    }

    // dds初始状态监听器,监听init是否成功
    private DDSInitListener mInitListener = new DDSInitListener() {
        @Override
        public void onInitComplete(boolean isFull) {
            Log.d(TAG, "onInitComplete isFull:"  +  isFull);
            if (isFull) {
                // 发送一个init成功的广播
                sendBroadcast(new Intent("ddsdemo.action.init_complete"));
                Log.d(TAG, " after send broadcast  ");
                //音乐技能（QQ音乐）
//                MusicPlugin.init(getApplicationContext());
//                //播控技能
//                MediaCtrlPlugin.init(getApplicationContext());
//                //影视技能（爱奇艺）
//                IQiyiPlugin.init(getApplicationContext());
//                //电视控制技能
//                TVCtrlPlugin.init();
//                //中控技能
//                SettingPlugin.init(getApplicationContext());
//
//                //中控相关回调
//                SettingPlugin.get().setSystemCtrl(new SystemControl());
//
//                MediaCtrlPlugin.get().setContent(true);
//                //播控相关回调
//                MediaCtrlPlugin.get().setMediaCtrl(new MediaControl());
//
//                //电视控制相关回调
//                TVCtrlPlugin.get().setTVCtrl(new TVControl());
                /**
                 * 这部分为爱奇艺移动版和QQ音乐TV版支持的功能
                 * -------------------------------------------------------
                 */
            }

//            RemoteCallbackList<IInterface> mlist = new RemoteCallbackList<>();
//            IInterface iInterface = mlist.getBroadcastItem(0);

        }

        @Override
        public void onError(int what, final String msg) {
            Log.e(TAG, "Init onError: " + what + ", error: " + msg);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    // dds认证状态监听器,监听auth是否成功
    private DDSAuthListener mAuthListener = new DDSAuthListener() {
        @Override
        public void onAuthSuccess() {
            Log.d(TAG, "onAuthSuccess");
            // 发送一个认证成功的广播
            Log.i("TAG",  "FD------send auth ok 2");
            sendBroadcast(new Intent("ddsdemo.action.auth_success"));
        }

        @Override
        public void onAuthFailed(final String errId, final String error) {
            Log.e(TAG, "onAuthFailed: " + errId + ", error:" + error);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "授权错误:" + errId + ":\n" + error + "\n请查看手册处理", Toast.LENGTH_SHORT).show();
                }
            });
            // 发送一个认证失败的广播
            sendBroadcast(new Intent("ddsdemo.action.auth_failed"));
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent localIntent = new Intent();
        localIntent.setClass(this, DDSService.class); //销毁时重新启动Service
        this.startService(localIntent);
        // 在退出app时将dds组件注销
        DDS.getInstance().release();

    }

    // 创建dds配置信息
    private DDSConfig createConfig() {
        DDSConfig config = new DDSConfig();
        config.addConfig(DDSConfig.K_PRODUCT_ID, "278582296"); // 产品ID -- 必填
        config.addConfig(DDSConfig.K_USER_ID, "lavon.liyuanfang@fenda.com");  // 用户ID -- 必填
        config.addConfig(DDSConfig.K_ALIAS_KEY, "test");   // 产品的发布分支 -- 必填
//        config.addConfig(DDSConfig.K_AUTH_TYPE, AuthType.PROFILE); //授权方式, 支持思必驰账号授权和profile文件授权 -- 必填
        config.addConfig(DDSConfig.K_PRODUCT_KEY, "8c1e2e8a558d894e08a871a3da4d8842");// Product Key -- 必填
        config.addConfig(DDSConfig.K_PRODUCT_SECRET, "96198328f4cf4fb639347736c5d24235");// Product Secre -- 必填
        config.addConfig(DDSConfig.K_API_KEY, "e7ef82bae195e7ef82bae1955d1c7ca7");  // 产品授权秘钥，服务端生成，用于产品授权 -- 必填

//        // 资源更新配置项
        config.addConfig(DDSConfig.K_DUICORE_ZIP, "duicore.zip"); // 预置在指定目录下的DUI内核资源包名, 避免在线下载内核消耗流量, 推荐使用
        config.addConfig(DDSConfig.K_CUSTOM_ZIP, "product.zip"); // 预置在指定目录下的DUI产品配置资源包名, 避免在线下载产品配置消耗流量, 推荐使用
        config.addConfig(DDSConfig.K_USE_UPDATE_DUICORE, "false"); //设置为false可以关闭dui内核的热更新功能，可以配合内置dui内核资源使用
        config.addConfig(DDSConfig.K_USE_UPDATE_NOTIFICATION, "false"); // 是否使用内置的资源更新通知栏
        config.addConfig(DDSConfig.K_MIC_TYPE, "2");
        config.addConfig(DDSConfig.K_AEC_MODE, "external");
//        config.addConfig(DDSConfig.K_AUDIO_FOCUS_MODE, "external"); //TTS
        //config.addConfig(DDSConfig.K_WAKEUP_DEBUG, "false"); // 用于唤醒音频调试, 开启后在 "/sdcard/Android/data/包名/cache" 目录下会生成唤醒音频

        config.addConfig(DDSConfig.K_DEVICE_ID,  "JD00000003");//填入唯一的deviceId -- 选填

        // 麦克风阵列配置项
        //config.addConfig(DDSConfig.K_MIC_TYPE, "2"); // 设置硬件采集模组的类型 0：无。默认值。 1：单麦回消 2：线性四麦 3：环形六麦 4：车载双麦 5：家具双麦


       /* config.addConfig(DDSConfig.K_PRODUCT_ID, "278581328"); // 产品ID
        config.addConfig(DDSConfig.K_USER_ID, "lavon.liyuanfang@fenda.com");  // 用户ID
        config.addConfig(DDSConfig.K_ALIAS_KEY, "test");   // 产品的发布分支
        config.addConfig(DDSConfig.K_AUTH_TYPE, AuthType.PROFILE); //授权方式, 支持思必驰账号授权和profile文件授权
        config.addConfig(DDSConfig.K_API_KEY, "73f12a5157b973f12a5157b95cce8e6d");  // 产品授权秘钥，服务端生成，用于产品授权
        config.addConfig(DDSConfig.K_PRODUCT_KEY, "439bd1275f761e015d0986cd027ec497");// Product Key -- 必填
        config.addConfig(DDSConfig.K_PRODUCT_SECRET, "789986f188b5ef722a46e7ce6dc2a7a2");// Product Secre -- 必填
        config.addConfig(DDSConfig.K_DUICORE_ZIP, "duicore.zip"); // 预置在指定目录下的DUI内核资源包名, 避免在线下载内核消耗流量, 推荐使用
        config.addConfig(DDSConfig.K_CUSTOM_ZIP, "product.zip"); // 预置在指定目录下的DUI产品配置资源包名, 避免在线下载产品配置消耗流量, 推荐使用
        config.addConfig(DDSConfig.K_USE_UPDATE_DUICORE, "false"); //设置为false可以关闭dui内核的热更新功能，可以配合内置dui内核资源使用
        config.addConfig(DDSConfig.K_USE_UPDATE_NOTIFICATION, "false"); // 是否使用内置的资源更新通知栏
        config.addConfig(DDSConfig.K_DEVICE_ID,  "000003002");*/
        Log.i(TAG, "config->" + config.toString());
        return config;
    }
}