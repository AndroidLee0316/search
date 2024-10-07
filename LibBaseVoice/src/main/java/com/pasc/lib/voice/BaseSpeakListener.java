package com.pasc.lib.voice;
/**
 * @date 2019/6/3
 * @des
 * @modify
 **/
public abstract class BaseSpeakListener {
    public void speakBegin() {
    }

    public void speakPause() {
    }

    public void speakResume() {
    }

    public void speakProgress(int percent, int beginPos, int endPos){

    }

    public void speakError(String code,String msg){

    }

    public void speakComplete(String msg){

    }

}
