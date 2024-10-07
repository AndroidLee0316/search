package com.pasc.lib.voice;

import android.content.Context;

/**
 * @date 2019/6/3
 * @des
 * @modify
 **/
public class VoiceManager {

    public static VoiceManager instance() {
        return Single.instance;
    }

    private static final class Single {
        private final static VoiceManager instance = new VoiceManager ();
    }

    private ISpeechEngine speechEngine;

    public void setSpeechEngine(ISpeechEngine speechEngine) {
        destroySpeech ();
        this.speechEngine = speechEngine;
    }

    private ISpeakEngine speakEngine;

    public void setSpeakEngine(ISpeakEngine speakEngine) {
        destroySpeak ();
        this.speakEngine = speakEngine;
    }

    /***********speech start***************/
    public boolean isSpeechInit(){
        if (speechEngine != null) {
            return speechEngine.isInit ();
        }
        return false;
    }
    public void initSpeech(Context context,IVoiceInitListener initListener, boolean isDebug) {
        if (speechEngine != null) {
            speechEngine.init (context,initListener, isDebug);
        }
    }
    public void setSpeechListener(BaseSpeechListener speechListener) {
        if (speechEngine != null) {
            speechEngine.setSpeechListener (speechListener);
        }
    }

    public void startSpeech() {
        if (speechEngine != null) {
            speechEngine.start ();
        }
    }

    public void stopSpeech() {
        if (speechEngine != null) {
            speechEngine.stop ();
        }
    }

    public void destroySpeech() {
        if (speechEngine != null) {
            speechEngine.destroy ();
            speechEngine = null;
        }
    }
    /***********speech end***************/


    /***********speak start***************/
    public boolean isSpeakInit(){
        if (speakEngine != null) {
            return speakEngine.isInit ();
        }
        return false;
    }
    public void initSpeak(Context context,IVoiceInitListener initListener, boolean isDebug) {
        if (speakEngine != null) {
            speakEngine.init (context,initListener, isDebug);
        }
    }
    public void setSpeakListener(BaseSpeakListener speakListener){
        if (speakEngine != null) {
            speakEngine.setSpeakListener ( speakListener);
        }
    }

    public void startSpeak(String text) {
        if (speakEngine != null) {
            speakEngine.start (text);
        }
    }

    public void pauseSpeak() {
        if (speakEngine != null) {
            speakEngine.pause ();
        }
    }

    public void resumeSpeak() {
        if (speakEngine != null) {
            speakEngine.resume ();
        }
    }

    public void stopSpeak() {
        if (speakEngine != null) {
            speakEngine.stop ();
        }
    }

    public void destroySpeak() {
        if (speakEngine != null) {
            speakEngine.destroy ();
            speakEngine = null;
        }
    }

    /***********speak end***************/

}
