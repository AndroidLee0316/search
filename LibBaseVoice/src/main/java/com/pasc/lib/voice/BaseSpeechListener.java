package com.pasc.lib.voice;

/**
 * @date 2019/6/3
 * @des
 * @modify
 **/
public abstract class BaseSpeechListener {
    public void speechBegin() {
    }

    public void speechEnd(boolean isManualStop) {
    }

    public void speechResult(String text,boolean isFinalResult) {

    }

    public void speechError(String code,String msg){

    }

    /***
     * 0 - 1.0f
     * @param volume
     */
    public void speechVolumeChanged(float volume) {

    }


}
