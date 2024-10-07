package com.pasc.business.voice;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.pasc.business.search.SearchManager;
import com.pasc.lib.pavoice.PaSpeakEngine;
import com.pasc.lib.pavoice.PaSpeechEngine;
import com.pasc.lib.search.util.ToastUtil;
import com.pasc.lib.voice.BaseSpeakListener;
import com.pasc.lib.voice.BaseSpeechListener;
import com.pasc.lib.voice.IVoiceInitListener;
import com.pasc.lib.voice.VoiceManager;

import java.util.Map;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @date 2019/5/30
 * @des
 * @modify
 **/
public class VoiceControlManagerSave {
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

    public static VoiceControlManagerSave instance() {
        return Single.instance;
    }
    private boolean isFirst=false;
    private static final class Single {
        private final static VoiceControlManagerSave instance = new VoiceControlManagerSave ();
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

    public void setVoiceView(final VoiceView voiceView) {
        this.voiceView = voiceView;
        voiceView.setVoiceListener (new VoiceView.IVoiceListener () {
            @Override
            public void close() {
                showOrHideVoice (false);
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
    public void showStartSpeech(){
        showOrHideVoice (true);
        startSpeech ();
    }

    public void showStartSpeak(boolean isFirst){
        this.isFirst=isFirst;
        showOrHideVoice (true);
        voiceTip (false);
    }

    /*********speech  start**********/
    private void initSpeechListener() {
        VoiceManager.instance ().setSpeechListener (new BaseSpeechListener () {
            @Override
            public void speechBegin() {
                if (voiceView == null) {
                    return;
                }
                voiceView.showStatus (false, "", "");
                voiceView.setTvTmpMsg ("");
            }

            @Override
            public void speechEnd(boolean isManualStop) {
                if (voiceView == null) {
                    return;
                }
                if (!isManualStop) {
                    voiceTip (true);
                }

            }

            @Override
            public void speechResult(String text, boolean isFinalResult) {
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
                    handler.sendEmptyMessageDelayed (delayHideTag, 1000);
                }

            }

            @Override
            public void speechError(String code, String msg) {
                if (voiceView == null) {
                    return;
                }
                showOrHideVoice (false);
                ToastUtil.showToast (msg);

            }

            @Override
            public void speechVolumeChanged(float volume) {
                if (voiceView == null) {
                    return;
                }
                if (voiceView.isOpen ()) {
                    voiceView.setVolume (volume);
                }
            }
        });
    }

    private void voiceTip(final boolean needTip) {
        io.reactivex.Single<Map<String, String>> mapSingle = SearchManager.instance ().getApi ().voiceTip ();
        if (mapSingle != null) {
            mapSingle
                    .subscribe (new Consumer<Map<String, String>> () {
                        @Override
                        public void accept(Map<String, String> stringStringMap) throws Exception {
                            String tip = stringStringMap.get ("tip");
                            String showTip = stringStringMap.get ("showTip");
                            String showMsg = stringStringMap.get ("showMsg");
                            if (!needTip){
                                tip="";
                            }
                            speakSay (tip,showTip,showMsg);
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
    private void defaultSay(){
        String tip = "很抱歉，没听到你说话。";
        String showTip = "你可以这样说";
        String showMsg = "搜索公积金";
        speakSay (tip,showTip,showMsg);
    }
    private void speakSay(String tip,String showTip,String showMsg) {
        if (TextUtils.isEmpty (tip)) {
            tip = "很抱歉，没听到你说话。";
        }
        if (TextUtils.isEmpty (showTip)) {
            showTip = "你可以这样说";
        }
        if (TextUtils.isEmpty (showMsg)) {
            showMsg = "搜索公积金";
        }
        String sss = tip +","+ showTip+ "," + showMsg;
        voiceView.showStatus (true, showTip, "\"" + showMsg + "\"");
        startSpeak (sss);
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
        VoiceManager.instance ().setSpeechEngine (paSpeechEngine);
        initSpeechListener ();
    }

    public void initSpeech(Context context, IVoiceInitListener initListener, boolean isDebug) {
        VoiceManager.instance ().initSpeech (context, initListener,isDebug);
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
    public boolean isSpeechInit(){
       return VoiceManager.instance ().isSpeechInit ();
    }
    /*********speech  end**********/


    /*********speak  start**********/
    private void initSpeakListener() {
        VoiceManager.instance ().setSpeakListener (new BaseSpeakListener () {
            @Override
            public void speakBegin() {
            }

            @Override
            public void speakPause() {
            }

            @Override
            public void speakResume() {
            }

            @Override
            public void speakProgress(int percent, int beginPos, int endPos) {
            }

            @Override
            public void speakError(String code, String msg) {
            }

            @Override
            public void speakComplete(String msg) {
                startSpeech ();
            }
        });
    }

    public void initSpeakEngine() {
        VoiceManager.instance ().setSpeakEngine (new PaSpeakEngine ());
        initSpeakListener ();

    }

    public void initSpeak(Context context, IVoiceInitListener initListener, boolean isDebug) {
        VoiceManager.instance ().initSpeak (context, initListener,isDebug);
    }

    public void startSpeak(String text) {
        VoiceManager.instance ().startSpeak (text);

    }

    public void stopSpeak() {
        VoiceManager.instance ().stopSpeak ();

    }
    public boolean isSpeakInit(){
        return VoiceManager.instance ().isSpeakInit ();
    }

    /*********speak  end**********/


    public void destroyAll() {
        VoiceManager.instance ().setSpeechListener (null);
        VoiceManager.instance ().setSpeakListener (null);
        VoiceManager.instance ().destroySpeech ();
        VoiceManager.instance ().destroySpeak ();
        cancelVoiceInterceptor ();
        isFirst=false;
        handler.removeMessages (delayHideTag);
    }

    private void cancelVoiceInterceptor() {
        dispose (disposable);
    }
    private void dispose(Disposable disposable){
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
