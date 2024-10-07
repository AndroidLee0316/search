package com.pasc.lib.pavoice;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.aipsdk.util.SpeechError;
import com.pasc.lib.voice.BaseSpeakListener;
import com.pasc.lib.voice.ISpeakEngine;
import com.pasc.lib.voice.IVoiceInitListener;

import pingan.speech.constant.PASpeechParam;
import pingan.speech.constant.PASpeechSDKError;
import pingan.speech.login.InitSDKListener;
import pingan.speech.login.LoginSDK;
import pingan.speech.tts.PASynthesizer;
import pingan.speech.tts.PASynthesizerListener;

/**
 * @date 2019/6/3
 * @des 平安语音播放
 * @modify
 **/
public class PaSpeakEngine implements ISpeakEngine {
    //属性
    public String scene_id = "";
    public String user_id = "";
    public String output_file = "";
//        <item>60190-yifeng-男</item>
//        <item>65600-xiaoyi-女</item>
//        <item>65040-xiaoxue-女</item>
//        <item>65620-chongchong-女</item>
//        <item>65580-chunchun-女</item>
    public String vid="65600";
    public String punctuation="0";
    public String pit;
    public String spd;
    public String vol;
    public String num_style="0";
    public String eng_style="0";

    private int mPercentForBuffering = 0;        //缓冲进度
    private int mPercentForPlaying = 0;            //播放进度
    private final static String TAG = "speakTag";
    private PASynthesizer tts;           // 语音播报对象
    private boolean isDebug;
    private BaseSpeakListener speakListener;

    @Override
    public boolean isInit() {
        return tts!=null;
    }

    @Override
    public void init(final Context context,final IVoiceInitListener initListener, boolean isDebug) {
        destroy ();
        this.isDebug = isDebug;
        LoginSDK.setOnCallInitSDKStateListener (new InitSDKListener () {
            @Override
            public void onInitSDKState(boolean success, PASpeechSDKError error) {
                loge ("speak initSDKListener--->success:" + success);
                // 初始化对象
                tts = PASynthesizer.createSynthesizer (context);
                if (initListener!=null){
                    initListener.onInitSDKState (success,error);
                }
            }
        });

        LoginSDK.initialSDK (context);
    }

    @Override
    public void setSpeakListener(BaseSpeakListener speakListener) {
        this.speakListener = speakListener;
    }


    @Override
    public void start(String text) {
        if (tts == null) {
            return;
        }
        if (tts.isSpeaking ()){
            return;
        }
        setParamHelper ();
        tts.startSpeaking (text, paSynthesizerListener);
    }

    @Override
    public void pause() {
        if (tts != null && tts.isSpeaking ()) {
            tts.pauseSpeaking ();
        }
    }

    @Override
    public void resume() {
        if (tts != null) {
            tts.resumeSpeaking ();

        }
    }

    @Override
    public void stop() {
        if (tts != null) {
            tts.stopSpeaking ();
        }
    }

    @Override
    public void destroy() {
        if (tts != null) {
            tts.stopSpeaking ();
            // 退出时释放连接
            tts.destroy ();
        }
    }

    private void loge(String msg) {
        if (isDebug) {
            Log.e (TAG, msg);

        }
    }

    private void setParamHelper() {
        if (tts != null) {
            tts.setParams (PASpeechParam.SCENE_ID, scene_id);
            tts.setParams (PASpeechParam.USER_ID, user_id);
            tts.setParams (PASpeechParam.OUTPUT_FILE, output_file);
            tts.setParams (PASpeechParam.TTS_SPEAK_VOICE_CODE, vid);
            tts.setParams (PASpeechParam.TTS_SPEAK_PUNCTUATION_MARK_MODE, punctuation);
            tts.setParams (PASpeechParam.TTS_SPEAK_VOICE_SPEED, spd);
            tts.setParams (PASpeechParam.TTS_SPEAK_VOICE_PITCH, pit);
            tts.setParams (PASpeechParam.TTS_SPEAK_VOICE_VOLUME, vol);
            tts.setParams (PASpeechParam.TTS_SPEAK_NUMBER_MODE, num_style);
            tts.setParams (PASpeechParam.TTS_SPEAK_ENGLISH_MODE, eng_style);
        }
    }

    private PASynthesizerListener paSynthesizerListener = new PASynthesizerListener () {
        @Override
        public void onSpeakBegin() {
            loge ("onSpeakBegin");
            if (speakListener!=null){
                speakListener.speakBegin ();
            }
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            mPercentForBuffering = percent;
            loge ("onBufferProgress" + String.format ("缓冲进度为%d%%，播放进度为%d%%",
                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onSpeakPaused() {
            loge ("onSpeakPaused");
            if (speakListener!=null){
                speakListener.speakPause ();
            }
        }

        @Override
        public void onSpeakResumed() {
            if (speakListener!=null){
                speakListener.speakResume ();
            }
            loge ("onSpeakResumed");
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            if (speakListener!=null){
                speakListener.speakProgress (percent,beginPos,endPos);
            }
            mPercentForPlaying = percent;
            loge ("onSpeakProgress" + String.format ("缓冲进度为%d%%，播放进度为%d%%",
                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onCompleted(SpeechError e) {
            String msg=e == null ? "success" : "error: "+e.getErrorDescription ();
            if (speakListener!=null){
                speakListener.speakComplete (msg);
            }
            loge ("onCompleted: " + msg);
        }

        @Override
        public void onBufferCompleted(String path, int i) {
            loge ("onBufferCompleted--缓存音频文件保存路径--s=" + path + "---i=" + i);            //输出音频文件路径
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
        }

        @Override
        public void onPreError(int code) {
            if (speakListener!=null){
                speakListener.speakError (code+"","");
            }
            loge ("错误码：" + code);
        }

    };
}
