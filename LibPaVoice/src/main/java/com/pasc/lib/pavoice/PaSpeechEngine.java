package com.pasc.lib.pavoice;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.pasc.lib.voice.BaseSpeechListener;
import com.pasc.lib.voice.ISpeechEngine;
import com.pasc.lib.voice.IVoiceInitListener;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.Iterator;

import pingan.speech.asr.PARecognizer;
import pingan.speech.asr.PARecognizerListener;
import pingan.speech.constant.PASpeechParam;
import pingan.speech.constant.PASpeechSDKError;
import pingan.speech.login.InitSDKListener;
import pingan.speech.login.LoginSDK;


/**
 * @date 2019/6/3
 * @des 平安语音
 * @modify
 **/
public class PaSpeechEngine implements ISpeechEngine {
    private final static String TAG = "speechTag";
    public boolean matchContinuous = true; // 连续匹配
    public boolean showVolumeChanged=false;
    public String sceneid = "";
    public String userid = "";
    public String eos = "3";
    public String bos = "1";
    public String vadFilePath = "";
    public String outputFile = "";
    public String removePun = "true";
    private BaseSpeechListener speechListener;
    private PARecognizer asr;           //语音识别对象
    private boolean isDebug;

    @Override
    public boolean isInit() {
        return asr!=null;
    }

    @Override
    public void init(final Context context, final IVoiceInitListener initListener, boolean isDebug) {
        destroy ();
        this.isDebug = isDebug;
        // 初始化对象
        LoginSDK.setOnCallInitSDKStateListener (new InitSDKListener () {
            @Override
            public void onInitSDKState(boolean success, PASpeechSDKError error) {
                loge ("Speech initSDKListener--->success:" + success);
                //创建识别实例asr
                asr = PARecognizer.createRecognizer (context);
                if (initListener!=null){
                    initListener.onInitSDKState (success,error);
                }
            }
        });
        LoginSDK.initialSDK (context);
    }

    @Override
    public void setSpeechListener(BaseSpeechListener speechListener) {
        this.speechListener = speechListener;
    }

    @Override
    public void start() {
        if (asr == null) {
            return;
        }
        setParamHelper ();
        boolean isListening = asr.isListening ();
        if (!isListening) {
            int err = asr.startListening (paRecognizerListener);
            if (err != 0) {
                if (speechListener != null) {
                    speechListener.speechError (err + "", "启动失败，操作过于频繁");
                }
            }
        }

    }

    @Override
    public void stop() {
        if (asr != null) {
            boolean isListening = asr.isListening ();
            if (isListening) {
                asr.stopListening ();
            }
        }
    }

    @Override
    public void destroy() {
        if (asr != null) {
            asr.destroy ();
        }
    }

    private void setParamHelper() {
        if (asr != null) {
            asr.setParams (PASpeechParam.SCENE_ID, sceneid);
            asr.setParams (PASpeechParam.USER_ID, userid);
            asr.setParams (PASpeechParam.EOS_TIME, eos);
            asr.setParams (PASpeechParam.BOS_TIME, bos);
            asr.setParams (PASpeechParam.VAD_RES, vadFilePath);
            asr.setParams (PASpeechParam.OUTPUT_FILE, outputFile);
            asr.setParams (PASpeechParam.REMOVE_PUNCTUATION, removePun);
//            增加上传音频格式，目前只支持wav和aac，默认格式为aac
//            asr.setParams(PASpeechParam.UPLOAD_FILE_FORMAT,"wav");
        }
    }

    private PARecognizerListener paRecognizerListener = new PARecognizerListener () {
        String tmp = "";
//        long pre=0;
        @Override
        public void onVolumeChanged(int volume) {
//            if (pre>0){
//                long tt=  SystemClock.currentThreadTimeMillis ()-pre;
//                loge ("onVolumeChanged time: "+tt);
//            }
//            pre=  SystemClock.currentThreadTimeMillis ()-pre;
            if (showVolumeChanged){
                loge ("onVolumeChanged: " + volume);
            }
            if (speechListener != null) {
                float percent = (volume - 40) / 40.0f;

                if (percent <= 0) {
                    percent = 0;
                } else if (percent >= 1) {
                    percent = 1;
                }
                speechListener.speechVolumeChanged (percent);
            }
        }

        @Override
        public void onBeginOfSpeech() {
            tmp = "";
            loge ("onBeginOfSpeech: ");
            if (speechListener != null) {
                speechListener.speechBegin ();
            }

        }

        @Override
        public void onEndOfSpeech() {
            loge ("onEndOfSpeech: ");
            if (speechListener != null) {
                speechListener.speechEnd (true);
            }
        }

        @Override
        public void onResult(String result, boolean isLast) {
            loge ("onResult: result: " + result + ", isLast:" + isLast);
            String texttemp = "";
            int pgs = 0;
            String outputfile = "";
            if (result != null && !result.equals ("")) {
                try {
                    JSONTokener tokener = new JSONTokener (result);
                    JSONObject joResult = new JSONObject (tokener);

                    Iterator iterator = joResult.keys ();
                    while (iterator.hasNext ()) {
                        String key = iterator.next () + "";
                        switch (key) {
                            case "result":
                                texttemp = joResult.getString ("result");
                                break;
                            case "pgs":
                                pgs = joResult.getInt ("pgs");
                                break;
                            case "filepath":
                                outputfile = joResult.getString ("filepath");
                                File outFile = new File (outputfile);
                                if (outFile.exists ()) {
//                                    Log.e(TAG, "file exists");
                                }
//                                Log.e(TAG, "保存文件完成：" + outputfile);
                                break;
                            default:
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace ();
                }
            }
            // 1 == pgs 说话完整
            //builder.append (texttemp);

            //连续匹配
            if (matchContinuous) {
                if (isLast) {
                    //最终结果
                    if (pgs == 1) {
                        tmp += texttemp;
                    }
                    if (!TextUtils.isEmpty (tmp)) {
                        if (speechListener != null) {
                            speechListener.speechResult (tmp, true);
                        }
                        tmp = "";
                    } else {
                        //没有任何结果
                        if (speechListener != null) {
                            speechListener.speechEnd (false);
                        }
                    }
                } else {
                    if (!TextUtils.isEmpty (texttemp)) {
                        if (pgs == 1) {
                            tmp += texttemp;
                        } else {
                        }
                        if (speechListener != null) {
                            speechListener.speechResult (texttemp, false);
                        }
                    }
                }
            } else {
                // 不连续匹配的
                if (!TextUtils.isEmpty (texttemp)) {
                    if (speechListener != null) {
                        speechListener.speechResult (tmp, true);
                    }
                }
            }


        }

        @Override
        public void onError(int errorCode, String errorDescription) {
            loge ("onError: errorCode: " + errorCode + ", errorDescription:" + errorDescription);
            if (speechListener != null) {
                speechListener.speechError (errorCode + "", errorDescription);
            }

        }

        @Override
        public void onEosEnd() {

        }


    };

    private void loge(String msg) {
        if (isDebug) {
            Log.e (TAG, msg);

        }
    }
}
