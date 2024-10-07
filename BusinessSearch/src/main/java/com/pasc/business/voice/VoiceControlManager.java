package com.pasc.business.voice;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.pasc.business.search.SearchManager;
import com.pasc.business.search.event.EventTable;
import com.pasc.lib.pavoice.PaSpeakEngine;
import com.pasc.lib.pavoice.PaSpeechEngine;
import com.pasc.lib.search.util.ToastUtil;
import com.pasc.lib.voice.BaseSpeechListener;
import com.pasc.lib.voice.IVoiceInitListener;
import com.pasc.lib.voice.VoiceManager;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @date 2019/5/30
 * @des
 * @modify
 **/
public class VoiceControlManager {
    private final int delayHideTag = 0x111;
    private final Handler handler = new Handler (Looper.getMainLooper ()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage (msg);
            if (msg.what == delayHideTag) {
                showOrHideVoice (false);
            }
        }
    };
    public static int maxSpeakTime = 1;
    private int speakTime = maxSpeakTime;
    private void resetSpeakTime(){
        speakTime = maxSpeakTime;
    }
    public static VoiceControlManager instance() {
        return Single.instance;
    }

    private static final class Single {
        private final static VoiceControlManager instance = new VoiceControlManager ();
    }

    private VoiceView voiceView;
    private View actionView;
    private Disposable disposable;

    public void showOrHideVoice(boolean show) {

        if (show) {
            actionView.setVisibility (View.GONE);
            voiceView.show ();
        } else {
            voiceView.close ();
            actionView.setVisibility (View.VISIBLE);
        }
    }

    private boolean isVoiceViewShow(){
        return voiceView!=null && voiceView.getVisibility ()==View.VISIBLE;
//        return voiceView!=null;
    }

    public void setVoiceView(final VoiceView voiceView) {
        this.voiceView = voiceView;
        voiceView.setVoiceListener (new VoiceView.IVoiceListener () {
            @Override
            public void close() {
                showOrHideVoice (false);
                resetSpeakTime ();
                stopSpeech ();
                stopSpeak ();
            }
        });
    }

    public void setActionView(final View actionView) {
        this.actionView = actionView;
    }

    /***
     * actionView 点击开始说话
     */
    public void showStartSpeech() {
        showOrHideVoice (true);
        startSpeech ();
    }


    /*********speech  start**********/
    private void initSpeechListener() {
        VoiceManager.instance ().setSpeechListener (new BaseSpeechListener () {
            @Override
            public void speechBegin() {
                if (!isVoiceViewShow ()) {
                    return;
                }
                voiceView.showStatus (false, "", "");
                voiceView.setTvTmpMsg ("");
            }

            @Override
            public void speechEnd(boolean isManualStop) {
                if (!isVoiceViewShow ()) {
                    return;
                }
                if (!isManualStop) {
                    if (speakTime<=0){
                        resetSpeakTime ();
                        stopSpeech ();
                        stopSpeak ();
                        showOrHideVoice (false);
                        return;
                    }
                    voiceTip ();
                    if (speakTime>0){
                        speakTime--;
                    }
                }

            }

            @Override
            public void speechResult(String text, boolean isFinalResult) {
                if (!isVoiceViewShow ()) {
                    return;
                }
                //临时文案
                voiceView.setTvTmpMsg (text);
                // 关闭语音
                if (isFinalResult) {
                    stopSpeech ();
                    stopSpeak ();
                    //最后一句关闭 波浪
                    voiceView.onlyCloseVoice ();
                    searchKeyword (text);
                    handler.removeMessages (delayHideTag);
                    handler.sendEmptyMessageDelayed (delayHideTag, 500);
                }

            }

            @Override
            public void speechError(String code, String msg) {
                if (!isVoiceViewShow ()) {
                    return;
                }
                showOrHideVoice (false);
                ToastUtil.showToast (msg);

            }

            @Override
            public void speechVolumeChanged(float volume) {
                if (!isVoiceViewShow ()) {
                    return;
                }
                if (voiceView.isOpen ()) {
                    voiceView.setVolume (volume);
                }
            }
        });
    }


    private void voiceTip() {
        SearchManager.instance ().getApi ().onEvent (voiceView.getContext (), EventTable.HomeVoiceEventId
                , EventTable.NoVoiceLabel, new HashMap ());
        io.reactivex.Single<Map<String, String>> mapSingle = SearchManager.instance ().getApi ().voiceTip ();
        if (mapSingle != null) {
            mapSingle
                    .subscribe (new Consumer<Map<String, String>> () {
                        @Override
                        public void accept(Map<String, String> stringStringMap) throws Exception {
                            String voicePath = stringStringMap.get ("voicePath");
                            String showTip = stringStringMap.get ("showTip");
                            String showMsg = stringStringMap.get ("showMsg");
                            speakSay (voicePath, showTip, showMsg);
                        }
                    }, new Consumer<Throwable> () {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            defaultSay ();
                        }
                    });
        } else {
            defaultSay ();
        }
    }

    private void defaultSay() {
        String showTip = "您可以这样说";
        String showMsg = "公积金查询";
        speakSay ("search/search_tts.wav", showTip, showMsg);
    }

    private void speakSay(String voicePath, String showTip, String showMsg) {
        if (TextUtils.isEmpty (voicePath)) {
            voicePath = "search/search_tts.wav";
        }
        if (TextUtils.isEmpty (showTip)) {
            showTip = "您可以这样说";
        }
        if (TextUtils.isEmpty (showMsg)) {
            showMsg = "公积金查询";
        }
        voiceView.showStatus (true, showTip, "\"" + showMsg + "\"");
        startSpeak (voicePath);
    }

    /**
     * 语音结果
     *
     * @param text
     */
    private void searchKeyword(final String text) {
        io.reactivex.Single<Boolean> single = SearchManager.instance ().getApi ().voiceInterceptorSearch (voiceView.getContext (), text);
        if (single == null) {
            if (voiceListener != null) {
                voiceListener.voiceCallback (text);
            }
            return;
        }
        cancelVoiceInterceptor ();
        disposable = single
                .subscribe (new Consumer<Boolean> () {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            // 本地直达搜索 ，在ApiGet 里面处理

//                    ToastUtil.showToast ("匹配成功");
                        } else {
                            if (voiceListener != null) {
                                voiceListener.voiceCallback (text);
                            }
                        }
                    }
                }, new Consumer<Throwable> () {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (voiceListener != null) {
                            voiceListener.voiceCallback (text);
                        }
                    }
                });
    }


    public void initSpeechEngine() {
        PaSpeechEngine paSpeechEngine = new PaSpeechEngine ();
        //后端静音
        paSpeechEngine.eos="1";
        //前端静音
        paSpeechEngine.bos="3";
        //热词优化
//        paSpeechEngine.sceneid="com.pasc.smt";
        //新热词场景
        paSpeechEngine.sceneid="PAishenzhen";
        VoiceManager.instance ().setSpeechEngine (paSpeechEngine);
        initSpeechListener ();
    }

    public void initSpeech(Context context, IVoiceInitListener initListener, boolean isDebug) {
        VoiceManager.instance ().initSpeech (context, initListener, isDebug);
    }

    public void startSpeech() {
        if (voiceListener != null) {
            voiceListener.startSpeech ();
        }
        VoiceManager.instance ().startSpeech ();
    }

    public void stopSpeech() {
        if (voiceListener != null) {
            voiceListener.stopSpeech ();
        }
        VoiceManager.instance ().stopSpeech ();
    }

    public boolean isSpeechInit() {
        return VoiceManager.instance ().isSpeechInit ();
    }
    /*********speech  end**********/


    /*********speak  start**********/
    private void initSpeakListener() {
        MediaPlayManager.instance ().setMediaPlayListener (new MediaPlayManager.MediaPlayListener () {
            @Override
            public void onStart() {

            }

            @Override
            public void onError() {

            }

            @Override
            public void onComplete() {
                if (!isVoiceViewShow ()) {
                    return;
                }
                startSpeech ();
            }
        });
    }

    public void initSpeakEngine() {
        VoiceManager.instance ().setSpeakEngine (new PaSpeakEngine ());
        initSpeakListener ();

    }

    public void initSpeak(Context context, IVoiceInitListener initListener, boolean isDebug) {

    }

    public void startSpeak(String fileName) {
        MediaPlayManager.instance ().start (SearchManager.instance ().getApp (), fileName);
    }

    public void stopSpeak() {
        MediaPlayManager.instance ().stop ();
    }

    /*********speak  end**********/


    public void destroyAll() {
        VoiceManager.instance ().setSpeechListener (null);
        VoiceManager.instance ().destroySpeech ();
        MediaPlayManager.instance ().destroy ();
        MediaPlayManager.instance ().setMediaPlayListener (null);
        cancelVoiceInterceptor ();
        handler.removeMessages (delayHideTag);
        resetSpeakTime ();
    }

    private void cancelVoiceInterceptor() {
        dispose (disposable);
    }

    private void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed ()) {
            disposable.dispose ();
        }
        disposable = null;
    }

    public interface VoiceListener {
        void voiceCallback(String keyword);

        void startSpeech();

        void stopSpeech();
    }

    private VoiceListener voiceListener;

    public void setVoiceListener(VoiceListener voiceListener) {
        this.voiceListener = voiceListener;
    }
}
