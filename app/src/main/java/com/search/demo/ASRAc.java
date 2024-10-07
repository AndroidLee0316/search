package com.search.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.pasc.lib.pavoice.PaSpeakEngine;
import com.pasc.lib.pavoice.PaSpeechEngine;
import com.pasc.lib.voice.BaseSpeakListener;
import com.pasc.lib.voice.BaseSpeechListener;
import com.pasc.lib.voice.VoiceManager;
//import com.pingan.demo.paspeechsdk.R;
import com.pingan.smt.R;

/**
 * @date 2019/6/3
 * @des
 * @modify
 **/
public class ASRAc extends Activity {
    private String TAG="yzj";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.asrac);
        VoiceManager.instance ().setSpeechEngine (new PaSpeechEngine ());
        VoiceManager.instance ().initSpeech (this,null,true);
        VoiceManager.instance ().setSpeechListener ( new BaseSpeechListener () {
            @Override
            public void speechResult(String text,boolean isFinalResult) {
                Log.e (TAG, "speechResult: "+text );
            }

            @Override
            public void speechError(String code, String msg) {
                Log.e (TAG, "speechError: "+code );

            }

            @Override
            public void speechVolumeChanged(float volume) {
                Log.e (TAG, "speechVolumeChanged: "+volume );

            }

            @Override
            public void speechEnd(boolean isManualStop) {
                if (!isManualStop){
                    startSpeak (null);
                }
            }
        });

        VoiceManager.instance ().setSpeakEngine (new PaSpeakEngine ());
        VoiceManager.instance ().initSpeak (this,null,true);
        VoiceManager.instance ().setSpeakListener ( new BaseSpeakListener () {
            @Override
            public void speakBegin() {
                super.speakBegin ();
            }

            @Override
            public void speakPause() {
                super.speakPause ();
            }

            @Override
            public void speakResume() {
                super.speakResume ();
            }

            @Override
            public void speakProgress(int percent, int beginPos, int endPos) {
                super.speakProgress (percent, beginPos, endPos);
            }

            @Override
            public void speakError(String code, String msg) {
                super.speakError (code, msg);
            }

            @Override
            public void speakComplete(String msg) {
                super.speakComplete (msg);
                startSpeech (null);
            }
        });
    }

    public void startSpeech(View view) {
        VoiceManager.instance ().startSpeech ();
    }

    public void stopSpeech(View view) {
        VoiceManager.instance ().stopSpeech ();
    }

    public void startSpeak(View view) {
        VoiceManager.instance ().stopSpeech ();
        VoiceManager.instance ().startSpeak ("你好，欢迎来到王者荣耀");
    }

    public void stopSpeak(View view) {
        VoiceManager.instance ().stopSpeak ();
    }

    @Override
    protected void onDestroy() {
        VoiceManager.instance ().destroySpeech ();
        VoiceManager.instance ().destroySpeak ();

        super.onDestroy ();
    }
}
