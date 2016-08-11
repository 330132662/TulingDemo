package com.czh.myrob.base;

import android.app.Application;

import com.czh.myrob.utils.DebugLogUtil;
import com.turing.androidsdk.InitListener;
import com.turing.androidsdk.SDKInitBuilder;
import com.turing.androidsdk.TuringApiManager;
import com.turing.androidsdk.asr.VoiceRecognizeManager;

import turing.os.http.core.HttpConnectionListener;

/*************************************************
 * @desc application
 * @auther LiJianfei
 * @time 2016/7/27 9:50
 ************************************/
public class App extends Application {
    private static App appContext;
    private SDKInitBuilder sdkInitBuilder;
    InitListener initListener;
    HttpConnectionListener httpConnectionListener;
    public static TuringApiManager m;
    public static VoiceRecognizeManager voiceRecognizeManager;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        initDebugLog();
    }



    private void initDebugLog() {
        DebugLogUtil.getInstance().setDebug(true);
        DebugLogUtil.getInstance().setFilter("lijianfei");// 防止与手机内其他进程的debug信息混淆

    }

    public static App getInstance() {
        if (appContext != null) {
            return appContext;
        } else {
            appContext = new App();
            return appContext;
        }
    }

}
