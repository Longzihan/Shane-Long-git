package com.fenda.ai.skill;

import android.util.Log;

import com.aispeech.dui.plugin.iqiyi.IQiyiPlugin;
import com.aispeech.dui.plugin.mediactrl.MediaCtrl;
import com.aispeech.dui.plugin.mediactrl.MediaCtrlPlugin;
import com.aispeech.dui.plugin.music.MusicPlugin;
import com.fenda.ai.authz.AccessibilityMonitorService;

/**
 * Created by chuck.liuzhaopeng on 2019/6/24.
 */

public class MediaControl extends MediaCtrl{
    private static final String TAG = "MediaControl";
    private static final String MUSIC_PKG = "com.tencent.qqmusictv";
    private static final String QIYIMOBILE_PKG = "com.qiyi.video.speaker";

    @Override
    public int play() {
        if (AccessibilityMonitorService.getTopPackageName().equals(MUSIC_PKG)) {//如果前台应用是QQ音乐
            return MusicPlugin.get().getMusicApi().resume();
        } else if (AccessibilityMonitorService.getTopPackageName().equals(QIYIMOBILE_PKG)) {//如果前台应用是爱奇艺
            return IQiyiPlugin.get().getVideoApi().resume();
        }else
            return IQiyiPlugin.ERR_NOT_SUPPORT;
    }

    @Override
    public int pause() {
        if (AccessibilityMonitorService.getTopPackageName().equals(MUSIC_PKG)) {//如果前台应用是QQ音乐
            return MusicPlugin.get().getMusicApi().pause();
        } else if (AccessibilityMonitorService.getTopPackageName().equals(QIYIMOBILE_PKG)) {//如果前台应用是爱奇艺
            return IQiyiPlugin.get().getVideoApi().pause();
        }else
            return MediaCtrlPlugin.ERR_NOT_SUPPORT;
    }

    @Override
    public int stop() {
        if (AccessibilityMonitorService.getTopPackageName().equals(MUSIC_PKG)) {//如果前台应用是QQ音乐
            return MusicPlugin.get().getMusicApi().exit();
        } else if (AccessibilityMonitorService.getTopPackageName().equals(QIYIMOBILE_PKG)) {//如果前台应用是爱奇艺
            return IQiyiPlugin.get().getVideoApi().exit();
        }
        return MediaCtrlPlugin.ERR_NOT_SUPPORT;
    }

    @Override
    public int rePlay() {
        if (AccessibilityMonitorService.getTopPackageName().equals(MUSIC_PKG)) {//如果前台应用是QQ音乐
            return MediaCtrlPlugin.ERR_NOT_SUPPORT;
        } else if (AccessibilityMonitorService.getTopPackageName().equals(QIYIMOBILE_PKG)) {//如果前台应用是爱奇艺
            return IQiyiPlugin.get().getVideoApi().replay();
        }
        return MediaCtrlPlugin.ERR_NOT_SUPPORT;
    }

    @Override
    public int prev() {
        if (AccessibilityMonitorService.getTopPackageName().equals(MUSIC_PKG)) {//如果前台应用是QQ音乐
            return MusicPlugin.get().getMusicApi().prev();
        } else if (AccessibilityMonitorService.getTopPackageName().equals(QIYIMOBILE_PKG)) {//如果前台应用是爱奇艺
            return IQiyiPlugin.get().getVideoApi().prevEpisode();
        }else
            return MediaCtrlPlugin.ERR_NOT_SUPPORT;
    }

    @Override
    public int next() {
        if (AccessibilityMonitorService.getTopPackageName().equals(MUSIC_PKG)) {//如果前台应用是QQ音乐
            return MusicPlugin.get().getMusicApi().next();
        } else if (AccessibilityMonitorService.getTopPackageName().equals(QIYIMOBILE_PKG)) {//如果前台应用是爱奇艺
            return IQiyiPlugin.get().getVideoApi().nextEpisode();
        }else
            return MediaCtrlPlugin.ERR_NOT_SUPPORT;
    }

    @Override
    public int orderPlay(boolean b) {
        if (AccessibilityMonitorService.getTopPackageName().equals(MUSIC_PKG)) {//如果前台应用是QQ音乐
            return MusicPlugin.get().getMusicApi().orderPlay();
        } else if (AccessibilityMonitorService.getTopPackageName().equals(QIYIMOBILE_PKG)) {//如果前台应用是爱奇艺
            return MediaCtrlPlugin.ERR_NOT_SUPPORT;
        }
        return MediaCtrlPlugin.ERR_NOT_SUPPORT;
    }

    @Override
    public int loopListPlay(boolean b) {
        if (AccessibilityMonitorService.getTopPackageName().equals(MUSIC_PKG)) {//如果前台应用是QQ音乐
            return MusicPlugin.get().getMusicApi().orderPlay();
        } else if (AccessibilityMonitorService.getTopPackageName().equals(QIYIMOBILE_PKG)) {//如果前台应用是爱奇艺
            return MediaCtrlPlugin.ERR_NOT_SUPPORT;
        }
        return MediaCtrlPlugin.ERR_NOT_SUPPORT;
    }

    @Override
    public int loopSinglePlay(boolean b) {
        if (AccessibilityMonitorService.getTopPackageName().equals(MUSIC_PKG)) {//如果前台应用是QQ音乐
            return MusicPlugin.get().getMusicApi().singleLoop();
        } else if (AccessibilityMonitorService.getTopPackageName().equals(QIYIMOBILE_PKG)) {//如果前台应用是爱奇艺
            return MediaCtrlPlugin.ERR_NOT_SUPPORT;
        }
        return MediaCtrlPlugin.ERR_NOT_SUPPORT;
    }

    @Override
    public int randomPlay(boolean b) {
        if (AccessibilityMonitorService.getTopPackageName().equals(MUSIC_PKG)) {//如果前台应用是QQ音乐
            return MusicPlugin.get().getMusicApi().randomPlay();
        } else if (AccessibilityMonitorService.getTopPackageName().equals(QIYIMOBILE_PKG)) {//如果前台应用是爱奇艺
            return MediaCtrlPlugin.ERR_NOT_SUPPORT;
        }
        return MediaCtrlPlugin.ERR_NOT_SUPPORT;
    }

    @Override
    public int favorite(boolean b) {
        if (AccessibilityMonitorService.getTopPackageName().equals(MUSIC_PKG)) {//如果前台应用是QQ音乐
            if (b) {
                return MusicPlugin.get().getMusicApi().favorite();
            } else {
                return MusicPlugin.get().getMusicApi().cancelFavorite();
            }
        } else if (AccessibilityMonitorService.getTopPackageName().equals(QIYIMOBILE_PKG)) {//如果前台应用是爱奇艺
            if (b) {
                return IQiyiPlugin.get().getVideoApi().favorite();
            } else {
                return IQiyiPlugin.get().getVideoApi().unFavorite();
            }
        }
        return MediaCtrlPlugin.ERR_NOT_SUPPORT;
    }

    @Override
    public int fullScreen(boolean b) {
        Log.d(TAG, "fullScreen: " + b);
        if (AccessibilityMonitorService.getTopPackageName().equals(MUSIC_PKG)) {//如果前台应用是QQ音乐
            return MediaCtrlPlugin.ERR_NOT_SUPPORT;
        } else if (AccessibilityMonitorService.getTopPackageName().equals(QIYIMOBILE_PKG)) {//如果前台应用是爱奇艺
            Log.d(TAG, "fullScreen: aqiyi");
            if (b) {
                return IQiyiPlugin.get().getVideoApi().screenPlay();
            } else {
                return IQiyiPlugin.get().getVideoApi().unSreenPlay();
            }
        }
        return MediaCtrlPlugin.ERR_NOT_SUPPORT;
    }

    @Override
    public int definition(String s) {
        if (AccessibilityMonitorService.getTopPackageName().equals(MUSIC_PKG)) {//如果前台应用是QQ音乐
            return MediaCtrlPlugin.ERR_NOT_SUPPORT;
        } else if (AccessibilityMonitorService.getTopPackageName().equals(QIYIMOBILE_PKG)) {//如果前台应用是爱奇艺
            return IQiyiPlugin.get().getVideoApi().definition(s);
        }
        return MediaCtrlPlugin.ERR_NOT_SUPPORT;
    }

    @Override
    public int forward(int relativeTime, int absoluteTime) {
        if (AccessibilityMonitorService.getTopPackageName().equals(MUSIC_PKG)) {//如果前台应用是QQ音乐
            return MusicPlugin.get().getMusicApi().forward(relativeTime);
        } else if (AccessibilityMonitorService.getTopPackageName().equals(QIYIMOBILE_PKG)) {//如果前台应用是爱奇艺
            return IQiyiPlugin.get().getVideoApi().forward(relativeTime, absoluteTime);
        }
        return MediaCtrlPlugin.ERR_NOT_SUPPORT;
    }

    @Override
    public int backward(int relativeTime, int absoluteTime) {
        if (AccessibilityMonitorService.getTopPackageName().equals(MUSIC_PKG)) {//如果前台应用是QQ音乐
            return MusicPlugin.get().getMusicApi().backward(relativeTime);
        } else if (AccessibilityMonitorService.getTopPackageName().equals(QIYIMOBILE_PKG)) {//如果前台应用是爱奇艺
            return IQiyiPlugin.get().getVideoApi().backward(relativeTime, absoluteTime);
        }
        return MediaCtrlPlugin.ERR_NOT_SUPPORT;
    }

    @Override
    public int setSpeed(String speed) {
        if (AccessibilityMonitorService.getTopPackageName().equals(MUSIC_PKG)) {//如果前台应用是QQ音乐
            return MediaCtrlPlugin.ERR_NOT_SUPPORT;
        } else if (AccessibilityMonitorService.getTopPackageName().equals(QIYIMOBILE_PKG)) {//如果前台应用是爱奇艺
            return IQiyiPlugin.get().getVideoApi().setSpeed(speed);
        }
        return MediaCtrlPlugin.ERR_NOT_SUPPORT;
    }

    @Override
    public int setPart(boolean skip, String part) {
        if (AccessibilityMonitorService.getTopPackageName().equals(MUSIC_PKG)) {//如果前台应用是QQ音乐
            return MediaCtrlPlugin.ERR_NOT_SUPPORT;
        } else if (AccessibilityMonitorService.getTopPackageName().equals(QIYIMOBILE_PKG)) {//如果前台应用是爱奇艺
            return IQiyiPlugin.get().getVideoApi().setSkipPart(skip, part);
        }
        return MediaCtrlPlugin.ERR_NOT_SUPPORT;
    }

    @Override
    public int setSize(String size) {
        if (AccessibilityMonitorService.getTopPackageName().equals(MUSIC_PKG)) {//如果前台应用是QQ音乐
            return MediaCtrlPlugin.ERR_NOT_SUPPORT;
        } else if (AccessibilityMonitorService.getTopPackageName().equals(QIYIMOBILE_PKG)) {//如果前台应用是爱奇艺
            return IQiyiPlugin.get().getVideoApi().setSize(size);
        }
        return MediaCtrlPlugin.ERR_NOT_SUPPORT;
    }
}
