package com.czh.myrob.ui;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;

import com.czh.myrob.R;
import com.czh.myrob.base.App;
import com.czh.myrob.base.BaseActivity;
import com.czh.myrob.utils.Constant;
import com.czh.myrob.utils.DebugLogUtil;
import com.turing.androidsdk.InitListener;
import com.turing.androidsdk.SDKInit;
import com.turing.androidsdk.SDKInitBuilder;
import com.turing.androidsdk.TuringApiManager;
import com.turing.androidsdk.asr.VoiceRecognizeListener;
import com.turing.androidsdk.asr.VoiceRecognizeManager;
import com.turing.androidsdk.tts.TTSListener;
import com.turing.androidsdk.tts.TTSManager;

import org.json.JSONException;
import org.json.JSONObject;

import turing.os.http.core.ErrorMessage;
import turing.os.http.core.HttpConnectionListener;
import turing.os.http.core.RequestResult;

/*************************************************
 * @desc 改包名 com.czh.myrob
 * 1 语义识别解析 ：requestTuringAPI(String requestInfo)
 * 2 语音合成播报：ttsManager.startTTS("亲，说两句呗");
 * 3
 * @auther LiJianfei
 * @time 2016/8/6 9:41
 ************************************/

public class MainActivity extends BaseActivity {
    private InitListener initListener;
    private TuringApiManager m;
    private TTSListener ttsListener;
    private TTSManager ttsManager;
    private VoiceRecognizeManager voiceRecognizeManager;
    private String ttsString;// 将要播报的语音
    private AudioManager audioManager;
    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        web = fView(R.id.web);
        //设置WebView属性，能够执行Javascript脚本
        web.getSettings().setJavaScriptEnabled(true);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0);
        setRecognize();
    }

    /**
     * 启动语音识别
     *
     * @param v
     */
    public void reqVoice(View v) {
        voiceRecognizeManager.startRecognize();// 开始收集语音
        ttsManager.startTTS("你好，我是Siri");
        showSnack(v, "语音识别已启动");
    }

    public void stopVoice(View v) {
//        voiceRecognizeManager.stopRecognize();
        ttsManager.startTTS("");
        DebugLogUtil.getInstance().Info(voiceRecognizeManager + "停止");
//        showSnack(v, "语音识别已关闭");
    }

    public void introduce(View v) {
        m.requestTuringAPI("晨之晖");
//        showSnack(v, "语音识别已关闭");
    }

    public void bequiet(View v) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
//        showSnack(v, "语音识别已关闭");
    }

    public void cover(View v) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0);
//        showSnack(v, "语音识别已关闭");
    }


    private void initTuling() {
        /**
         *E 初始化图灵组件
         */
        SDKInitBuilder sdkInitBuilder = new SDKInitBuilder(this);
        sdkInitBuilder.context = this;
        sdkInitBuilder.setTuringKey(Constant.TULING_KEY);
        sdkInitBuilder.setSecret(Constant.TULING_SECRET);
        sdkInitBuilder.setUniqueId(Constant.UniqueId);
        SDKInit.init(sdkInitBuilder, initListener);
    }


    private void setRecognize() {
        //  A      初始化ASR语音识别状态监听
        VoiceRecognizeListener listener = new VoiceRecognizeListener() {
            @Override
            public void onStartRecognize() {
//                DebugLogUtil.getInstance().Info("onStartRecognize=");
            }

            @Override
            public void onRecordStart() {
//                DebugLogUtil.getInstance().Info("onRecordStart=");
            }

            @Override
            public void onRecordEnd() {
//                showSnack(web,"请重新点击开始按钮");
                DebugLogUtil.getInstance().Info("识别结束");
            }

            @Override
            public void onRecognizeResult(String s) {
//        获取识别结果，在步骤2里listener的回调方法onRecognizeResult中获取识别结果。然后传入语义解析
                /**
                 * 在这里可以根据结果判断 对本地进行操作  比如关闭语音助手  挥着直接退出应用
                 *
                 */

                try {
                    if (!(s == null) && !s.equals("")) {
                        DebugLogUtil.getInstance().Info(" 获取语音识别结果=" + s);
                        if (s.equals("关闭")) {
                            voiceRecognizeManager.stopRecognize();
                            showSnack(web, "语音识别已关闭");
                        } else if (s.equals("退出") || s.equals("退下")) {
                            ttsManager.startTTS("感谢使用，再见！");
                            voiceRecognizeManager.stopRecognize();
                            showToast("感谢使用，再见！");
                            finish();
                        } else {
                            m.requestTuringAPI(s);
                        }
                    } else {
                        s = "";
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    setRecognize();
                    DebugLogUtil.getInstance().Info(" 出错了=" + e.getMessage());

                }
            }

            @Override
            public void onRecognizeError(String s) {
//                TODO  这里应该做人性化判断  提醒用户说话
                DebugLogUtil.getInstance().Info("onRecognizeError=" + s);
                if (s != null && !s.equals("")) {
                    if (s.equals("没有说话") || s.equals("整体识别超时")) {
                        ttsManager.startTTS("亲，说两句呗");
                    } else if (s.equals("语音质量错误")) {
                        ttsManager.startTTS("亲，我没有听清楚哦");
                    }
                }
            }

            @Override
            public void onVolumeChange(int i) {
                DebugLogUtil.getInstance().Info("onVolumeChange=" + i);
            }
        };
        //        初始化百度语音
        voiceRecognizeManager = new VoiceRecognizeManager(this, Constant.BDtuisongAPIKey, Constant.BDtuisongSecret_Key);
//        设定ASR状态监听
        voiceRecognizeManager.setVoiceRecognizeListener(listener);
        /*B  网络请求状态监听*/
        final HttpConnectionListener httpConnectionListener = new HttpConnectionListener() {
            @Override
            public void onError(ErrorMessage errorMessage) {
                DebugLogUtil.getInstance().Info("网络请求监听onError");
                showSnack(web, "您没有连接到互联网");
            }

            @Override
            public void onSuccess(RequestResult requestResult) {
//                TODO 在这里应该做解析和判断
                String text = "";
                String code = "";
                String url = null;
                JSONObject object = (JSONObject) requestResult.getContent();
                try {
                    code = object.getString("code");
                    if (code.equals("200000")) {// 解析图片和其他生活帮助等信息返回的页面
                        url = object.getString("url");
                        web.loadUrl(url);
                    }
                    text = object.getString("text");
                    ttsManager.startTTS(text);// 合成 播报
                } catch (JSONException e) {
                    e.printStackTrace();
                    DebugLogUtil.getInstance().Info("objec解析失败");
                }

                DebugLogUtil.getInstance().Info(code + "," + text + ",url=" + url);
            }
        };
        /*C  组件状态监听*/
        initListener = new InitListener() {
            @Override
            public void onComplete() {

//                实例化TuringApiManager类
                m = new TuringApiManager(App.getInstance());
                m.setHttpListener(httpConnectionListener);
            }

            @Override
            public void onFail(String s) {
                showSnack(web, "请重新点击开始按钮");
                DebugLogUtil.getInstance().Error("图灵初始化onFail" + s);
//                setRecognize();
            }
        };
        initTTSListner();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
            web.goBack(); //goBack()表示返回WebView的上一页面
            return true;
        }
        return false;
    }

    /**
     * 2  初始化语音识别
     */
    private void initTTSListner() {
    /*D 初始化语音合成监听*/
        ttsListener = new TTSListener() {
            @Override
            public void onSpeechStart() {
                DebugLogUtil.getInstance().Error("语音识别onSpeechStart");
            }

            @Override
            public void onSpeechProgressChanged() {
//                DebugLogUtil.getInstance().Error("语音识别onSpeechProgressChanged");
            }

            @Override
            public void onSpeechPause() {
                DebugLogUtil.getInstance().Error("语音识别onSpeechPause");
            }

            @Override
            public void onSpeechFinish() {
                /*语音合成后，就会触发onSpeechFinish(),这样即可在其方法中添加相应的逻辑。*/
                DebugLogUtil.getInstance().Error("语音播报结束Finish");
//                TODO  能在这里调用语音识别  不用再按启动语音识别吗   能！
                voiceRecognizeManager.startRecognize();// 重新开始收集语音
            }

            @Override
            public void onSpeechError(int i) {
                DebugLogUtil.getInstance().Error("语音识别onSpeechError" + i);
            }

            @Override
            public void onSpeechCancel() {
                DebugLogUtil.getInstance().Error("语音识别 onSpeechCancel");
            }
        };
        ttsManager = new TTSManager(this, Constant.BDtuisongAPIKey, Constant.BDtuisongSecret_Key);
        ttsManager.setTTSListener(ttsListener);
        initTuling();// 最后调用这里
    }

    @Override
    protected void onPause() {
//        退出界面要提升系统音量  最完美的应该是进入时判断目前音量 退出app时 恢复这个音量！
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0);
        super.onPause();
    }
}
