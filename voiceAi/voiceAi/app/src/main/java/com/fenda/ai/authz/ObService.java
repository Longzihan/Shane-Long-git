package com.fenda.ai.authz;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.media.MediaMetadataCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.aispeech.ailog.AILog;
import com.aispeech.dui.dds.DDS;
import com.aispeech.dui.dds.exceptions.DDSNotInitCompleteException;
import com.aispeech.dui.plugin.iqiyi.IQiyiPlugin;
import com.aispeech.dui.plugin.music.MusicPlugin;

import com.fenda.ai.FDApplication;
import com.fenda.ai.R;
import com.fenda.ai.bean.MessageBean;

import com.fenda.ai.observer.DuiCommandObserver;
import com.fenda.ai.observer.DuiMessageObserver;
import com.fenda.ai.observer.DuiNativeApiObserver;
import com.fenda.ai.observer.DuiUpdateObserver;
import com.fenda.ai.utils.Jsonparse;
import com.fenda.ai.utils.Md5Utils;
import com.fenda.ai.view.SpeechView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ObService extends Service implements DuiUpdateObserver.UpdateCallback, DuiMessageObserver.MessageCallback {
    private static String TAG = "ObService";
    private boolean isInit = false;
    private Handler mHandler = new Handler();
    private MyReceiver mInitReceiver;// 初始化监听广播
    private int mAuthCount = 0;// 授权次数,用来记录自动授权
    private DuiMessageObserver mMessageObserver ;// 消息监听器
    private DuiUpdateObserver mUpdateObserver ;// dds更新监听器
    private DuiCommandObserver mCommandObserver ;// 命令监听器
    private DuiNativeApiObserver mNativeObserver ;
    private String str_tmp;
    private boolean isFirstVar = true;
    private boolean hasvar = false;
    private static final String MUSIC_PKG = "com.tencent.qqmusictv";
    private static final String QIYIMOBILE_PKG = "com.qiyi.video.speaker";
    private SpeechView speechView;
    public ObService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        // 添加一个初始成功的广播监听器
        IntentFilter filter = new IntentFilter();
        filter.addAction("ddsdemo.action.init_complete");
        mInitReceiver = new MyReceiver();
        registerReceiver(mInitReceiver, filter);

        // 注册一个广播,接收service中发送的dds初始状态广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);// WIFI成功的广播
        intentFilter.addAction("ddsdemo.action.auth_success");// 认证成功的广播
        intentFilter.addAction("ddsdemo.action.auth_failed");// 认证失败的广播
        registerReceiver(authReceiver, intentFilter);

        IntentFilter smartFilter = new IntentFilter();
        smartFilter.addAction("com.fenda.smartcall.ACTION_MIC_ENABLE");// 认证成功的广播
        smartFilter.addAction("com.fenda.smartcall.ACTION_MIC_ABLE");// 认证成功的广播
        registerReceiver(smartReceiver, smartFilter);

        speechView = new SpeechView(FDApplication.getContext());
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init_auth();
        return super.onStartCommand(intent, flags, startId);
    }


    // 认证广播
    private BroadcastReceiver authReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()))
            {
                NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (info != null) {
                    //如果当前的网络连接成功并且网络连接可用
                    if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                        if (info.getType() == ConnectivityManager.TYPE_WIFI ) {
                            doauth_when_net_ok();
                            Log.i("TAG",  "FD------连上");
                        }
                    } else {
                        Log.i("TAG",  "FD-------断开");
                    }
                }
            }
            else if (TextUtils.equals(intent.getAction(), "ddsdemo.action.auth_success")) {
                Log.i("TAG",  "FD------auth ok 2");
                PlayWelcomeTTS();
                showToast("授权成功!");
            } else if (TextUtils.equals(intent.getAction(), "ddsdemo.action.auth_failed")) {
                doAutoAuth();
            }
        }
    };

    // 认证广播
    private BroadcastReceiver smartReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.fenda.smartcall.ACTION_MIC_ENABLE")) {
                try {
                    Log.d(TAG, "FD-------com.fenda.smartcall.ACTION_MIC_ENABLE ");
                    DDS.getInstance().getAgent().getWakeupEngine().disableWakeup();
                } catch (DDSNotInitCompleteException e) {
                    e.printStackTrace();
                }
            } else if (intent.getAction().equals("com.fenda.smartcall.ACTION_MIC_ABLE")) {
                try {
                    Log.d(TAG, "FD-------com.fenda.smartcall.ACTION_MIC_ABLE ");
                    DDS.getInstance().getAgent().getWakeupEngine().enableWakeup();
                } catch (DDSNotInitCompleteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    // 执行自动授权
    private void doAutoAuth(){
        // 自动执行授权5次,如果5次授权失败之后,给用户弹提示框
        if (mAuthCount < 5) {
            try {
                DDS.getInstance().doAuth();
                mAuthCount++;
            } catch (DDSNotInitCompleteException e) {
                e.printStackTrace();
            }
        } else {
            showToast("授权失败!");
        }
    }


    private void init_auth() {
        new Thread() {
            public void run() {
                checkDDSReady();
            }
        }.start();

    }

   private void doauth_when_net_ok()
    {
        if (DDS.getInstance().getInitStatus() == DDS.INIT_COMPLETE_FULL ||
                DDS.getInstance().getInitStatus() == DDS.INIT_COMPLETE_NOT_FULL) {
            try {
                if (!DDS.getInstance().isAuthSuccess()) {
                    mAuthCount = 0;
                    doAutoAuth();
                }
            } catch (DDSNotInitCompleteException e) {
                e.printStackTrace();
            }
        }
    }

    // 检查dds是否初始成功
    public void checkDDSReady() {
        while (true) {
            if (DDS.getInstance().getInitStatus() == DDS.INIT_COMPLETE_FULL ||
                    DDS.getInstance().getInitStatus() == DDS.INIT_COMPLETE_NOT_FULL) {
                try {
                    if (DDS.getInstance().isAuthSuccess()) {
                        //PlayWelcomeTTS();
                        Log.i("TAG",  "FD------auth ok 1");
                        showToast("授权成功!");
                        break;
                    } else {
                        // 自动授权
                        doAutoAuth();
                    }
                } catch (DDSNotInitCompleteException e) {
                    e.printStackTrace();
                }
                break;
            } else {
                AILog.w(TAG, "waiting  init complete finish...");
            }
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onMessage() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //mMusicAdapter.notifyDataSetChanged();
            }
        });
    }

    // DuiMessageObserver中当前状态的回调
    @Override
    public void onState(String message,String state) {
        //Log.v(TAG,"FD-----"+message+"||||||"+state);
        switch(message)
        {
            case "sys.dialog.start":
                String  packageName = AccessibilityMonitorService.getTopPackageName();
                if (packageName == null) {
                    break;
                }
                if (!packageName.equals(MUSIC_PKG) && !packageName.equals(QIYIMOBILE_PKG))
                    speechView.showView("");
                break;
            case "sys.dialog.end":
                speechView.closeView();
                break;
           // case "sys.dialog.state":
            //    handleMesagestate(state);
             //   break;
            case "context.input.text":
                speechView.showView(Jsonparse.parseInputText(state));
                break;
            case "context.widget.media":
            case "context.widget.web":
            case "context.widget.list":
            case "context.widget.content":
            case "context.widget.custom":
                HandleMessage(message,state);
                break;

        }
    }
    private void handleMesagestate(String state)
    {
        switch (state) {
            case "avatar.silence":
                //closeDialog();
                Log.v(TAG, "   ----------3");
                break;
            case "avatar.listening":


                break;
            case "avatar.understanding":

                break;
            case "avatar.speaking":
                //closeDialog();
                //Intent intent = new Intent(mContext, AIspeechWebActivity.class);
                //mContext.startActivity(intent);
                break;
        }
    }

    private void PlayWelcomeTTS()
    {
        Log.i("TAG",  "FD------play welcome TTS---");
        String[] wakeupWords = new String[0];
        String minorWakeupWord = null;
        try {
            wakeupWords = DDS.getInstance().getAgent().getWakeupEngine().getWakeupWords();
            minorWakeupWord = DDS.getInstance().getAgent().getWakeupEngine().getMinorWakeupWord();
        } catch (DDSNotInitCompleteException e) {
            e.printStackTrace();
        }
        String hiStr = "";
        if (wakeupWords != null && minorWakeupWord != null) {
            hiStr = this.getString(R.string.hi_str2, wakeupWords[0], minorWakeupWord);
        } else if (wakeupWords != null && wakeupWords.length == 2) {
            hiStr = this.getString(R.string.hi_str2, wakeupWords[0], wakeupWords[1]);
        } else if (wakeupWords != null && wakeupWords.length > 0) {
            hiStr = this.getString(R.string.hi_str, wakeupWords[0]);
        }
        try {
            Log.i("TAG",  "FD------play welcome TTS");
            DDS.getInstance().getAgent().getTTSEngine().speak(hiStr, 1);
        } catch (DDSNotInitCompleteException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUpdate(int type, String result) {
        Log.d(TAG, "onUpdate type: " + type +  "result:" + result);
    }


    // 打开唤醒，调用后才能语音唤醒
    private void enableWakeup() {
        try {
            if (!isInit) {
                mMessageObserver = new DuiMessageObserver();// 消息监听器
                mUpdateObserver = new DuiUpdateObserver();// dds更新监听器
                mCommandObserver = new DuiCommandObserver();// 命令监听器
                mNativeObserver = new DuiNativeApiObserver();
                mNativeObserver.regist();
                mMessageObserver.regist(this);
                mUpdateObserver.regist(this);
                mCommandObserver.regist();
                isInit = true;
            }
            DDS.getInstance().getAgent().getWakeupEngine().enableWakeup();

        } catch (DDSNotInitCompleteException e) {
            e.printStackTrace();
        }
    }


    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String name = intent.getAction();
            if (name.equals("ddsdemo.action.init_complete")) {
                Log.v(TAG, "FD-------init succes");
                    enableWakeup();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mInitReceiver);
        mMessageObserver.unregist();
        mUpdateObserver.unregist();
        mCommandObserver.unregist();
        unregisterReceiver(authReceiver);
        unregisterReceiver(smartReceiver);
        Intent localIntent = new Intent();
        localIntent.setClass(this, ObService.class); //销毁时重新启动Service
        this.startService(localIntent);
    }


    private void showToast(String text)
    {
        str_tmp=text;
        //Log.v(TAG,"FD-------"+text);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), str_tmp , Toast.LENGTH_LONG).show();
            }
        });
    }

    private void StartMsgActivity()
    {
        MusicPlugin.get().getMusicApi().exit();
        IQiyiPlugin.get().getVideoApi().exit();
    }

    private void HandleMessage(String message,String data) {
        //Log.d(TAG, "FD-----message : " + message + " data : " + data);
        MessageBean bean = null;

        switch (message) {
            case "context.output.text":
                bean = new MessageBean();
                String txt = "";
                try {
                    JSONObject jo = new JSONObject(data);
                    txt = jo.optString("text", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                bean.setText(txt);
                bean.setType(MessageBean.TYPE_OUTPUT);

                break;
            case "context.input.text":
                bean = new MessageBean();
                try {
                    JSONObject jo = new JSONObject(data);
                    if (jo.has("var")) {
                        String var = jo.optString("var", "");
                        if (isFirstVar) {
                            isFirstVar = false;
                            hasvar = true;
                            bean.setText(var);
                            bean.setType(MessageBean.TYPE_INPUT);

                        } else {

                            bean.setText(var);
                            bean.setType(MessageBean.TYPE_INPUT);

                        }
                    }
                    if (jo.has("text")) {
                        if (hasvar) {

                            hasvar = false;
                            isFirstVar = true;
                        }
                        String text = jo.optString("text", "");
                        bean.setText(text);
                        bean.setType(MessageBean.TYPE_INPUT);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "context.widget.content":
                bean = new MessageBean();
                try {
                    JSONObject jo = new JSONObject(data);
                    String title = jo.optString("title", "");
                    String subTitle = jo.optString("subTitle", "");
                    String imgUrl = jo.optString("imageUrl", "");
                    bean.setTitle(title);
                    bean.setSubTitle(subTitle);
                    bean.setImgUrl(imgUrl);
                    bean.setType(MessageBean.TYPE_WIDGET_CONTENT);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                StartMsgActivity();
                break;
            case "context.widget.list":
                bean = new MessageBean();
                try {
                    JSONObject jo = new JSONObject(data);
                    JSONArray array = jo.optJSONArray("content");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.optJSONObject(i);
                        String title = object.optString("title", "");
                        String subTitle = object.optString("subTitle", "");
                        MessageBean b = new MessageBean();
                        b.setTitle(title);
                        b.setSubTitle(subTitle);
                        bean.addMessageBean(b);
                    }
                    int currentPage = jo.optInt("currentPage");
                    bean.setCurrentPage(currentPage);
                    bean.setType(MessageBean.TYPE_WIDGET_LIST);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                StartMsgActivity();
                break;
            case "context.widget.web":
                bean = new MessageBean();
                try {
                    JSONObject jo = new JSONObject(data);
                    String url = jo.optString("url");
                    bean.setUrl(url);
                    bean.setType(MessageBean.TYPE_WIDGET_WEB);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                StartMsgActivity();
                break;
            //media widget 1
            case "context.widget.media":
                mediaHandler(data);
                break;
            //custom widget 1 收到自定义控件消息
            case "context.widget.custom":
                if (AccessibilityMonitorService.getTopPackageName().equals(QIYIMOBILE_PKG))
                    Log.v(TAG,"FD----CRENT APP IS AIYIQI");
                //customHandler(data);
                //StartMsgActivity();
                startWeatherActivity(data);
                break;
        }

       // StartMsgActivity();
    }

    private void startWeatherActivity(String data) {
        try{
            HashMap<String, String> tWeatherArgMap = new HashMap();
            JSONObject dataJsonObject = new JSONObject(data);
            JSONObject extra = dataJsonObject.getJSONObject("webhookResp").getJSONObject("extra");
            tWeatherArgMap.put("keyCity", dataJsonObject.optString("cityName"));

            JSONArray forecastArray = extra.optJSONArray("forecast");
            JSONObject indexObject = extra.optJSONObject("Index");
            JSONObject aqiObject = indexObject.optJSONObject("aqi");
            JSONObject forecastObject=forecastArray.getJSONObject(0);
            if (forecastArray == null||forecastObject==null || indexObject == null  ||aqiObject == null) {
                return;
            }
            String weather = forecastObject.optString("weather");
            tWeatherArgMap.put("keyWeatherTemperature", forecastObject.optString("temperature"));
            tWeatherArgMap.put("keyWind", forecastObject.optString("wind"));
            tWeatherArgMap.put("keyWeather", weather);

            tWeatherArgMap.put("keyAirQty", aqiObject.optString("AQL"));
            if (weather.contains("云")) {
                tWeatherArgMap.put("keyWeatherCode", "3"); //1 晴 2 雨  3 阴
            } else if (weather.contains("雨")) {
                tWeatherArgMap.put("keyWeatherCode", "2"); //1 晴 2 雨  3 阴
            } else {
                tWeatherArgMap.put("keyWeatherCode", "1"); //1 晴 2 雨  3 阴
            }

//            FDWeatherHelper.openWeather(this, tWeatherArgMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void customHandler(String data) {
        JSONObject object = new JSONObject();
        try {
            if (data != null) {
                object = new JSONObject(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String widgetName = object.optString("name", "");
        if (TextUtils.isEmpty(widgetName)) {
            throw new RuntimeException("widget name must not be empty");
        }

        MessageBean bean = null;
        //custom widget 2 判断自定义控件类型
        switch (widgetName) {
            case "weather": {
                bean = new MessageBean();
                bean.setType(MessageBean.TYPE_WIDGET_WEATHER);
                bean.setExtraData(data);
                bean.setKeepSingle(true);

//                for(MessageBean temp : mMessageList) {
//                    if(temp.getType() == MessageBean.TYPE_WIDGET_WEATHER) {
//                        mMessageList.remove(temp);
//                        break;
//                    }
//                }
//                mMessageList.add(bean);
            }
        }
    }

    public void mediaHandler(String data) {
        JSONObject object = new JSONObject();
        try {
            if (data != null) {
                object = new JSONObject(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String widgetName = object.optString("name", "");
        if (TextUtils.isEmpty(widgetName)) {
            throw new RuntimeException("widget name must not be empty");
        }

        //handleDefaultMediaWidget(object);
        handleMusicMediaWidget(object);
    }


    private void handleDefaultMediaWidget(JSONObject dataJsonObject) {
        JSONArray contentArray = null;
        try {
            if (dataJsonObject.get("content") instanceof JSONArray) {
                contentArray = dataJsonObject.getJSONArray("content");
                if (contentArray == null || contentArray.length() <= 0) {
                    return;
                }
            } else {
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }



        List<MediaMetadataCompat> playlist = new ArrayList<>();
        for (int i = 0; i < contentArray.length(); i++) {
            try {
                JSONObject itemJsonObject = contentArray.getJSONObject(i);
                String title = itemJsonObject.optString("title", "");
                String subTitle = itemJsonObject.optString("subTitle", "");
                String imageUrl = itemJsonObject.optString("imageUrl", "");
                String linkUrl = itemJsonObject.optString("linkUrl", "");
                String mediaid = itemJsonObject.optString("mediaId", "");
                if (TextUtils.isEmpty(mediaid)) {
                    mediaid = Md5Utils.md5(linkUrl);
                }

                if(linkUrl.equals("")) {
                    return;
                }

                MediaMetadataCompat mediaMetadata = new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, subTitle)
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, linkUrl)
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, imageUrl)
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaid)
                        .build();
                playlist.add(mediaMetadata);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //media widget 2 播放音乐列表
//        MusicPlayerHelper.openQueue(playlist, true);
//        Log.d(TAG, "FD----- MEDIAplayer"+playlist);
//        MessageBean bean = new MessageBean();
//        bean.setType(MessageBean.TYPE_WIDGET_MEDIA);
//        bean.setKeepSingle(true);
//        for(MessageBean temp : mMessageList) {
//            if(temp.getType() == MessageBean.TYPE_WIDGET_MEDIA) {
//                mMessageList.remove(temp);
//                break;
//            }
//        }
//
//        mMessageList.add(bean);

//        StartMsgActivity();
    }


    private void handleMusicMediaWidget(JSONObject dataJsonObject) {
        JSONArray contentArray = null;
        try {
            if (dataJsonObject.get("content") instanceof JSONArray) {
                contentArray = dataJsonObject.getJSONArray("content");
                if (contentArray == null || contentArray.length() <= 0) {
                    return;
                }
            } else {
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

//        ArrayList<FDMusic> playlist = new ArrayList<>();
//        FDMusic fdmusic;
//        for (int i = 0; i < contentArray.length(); i++) {
//            try {
//                JSONObject itemJsonObject = contentArray.getJSONObject(i);
//                String title = itemJsonObject.optString("title", "");
//                String subTitle = itemJsonObject.optString("subTitle", "");
//                String imageUrl = itemJsonObject.optString("imageUrl", "");
//                String linkUrl = itemJsonObject.optString("linkUrl", "");
//                String mediaid = itemJsonObject.optString("mediaId", "");
//                if (TextUtils.isEmpty(mediaid)) {
//                    mediaid = Md5Utils.md5(linkUrl);
//                }
//                 if(linkUrl.equals("")) {
//                     return;
//                 }
//                fdmusic=new FDMusic(title,linkUrl,imageUrl,subTitle);
//
//                playlist.add(fdmusic);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        Intent intent = new Intent(this, MusicActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putParcelableArrayListExtra(FDMusicPlay.keyDataMusicList,playlist);
//        startActivity(intent);
    }
}
