package com.pasc.lib.voice;

import android.content.Context;

/**
 * @date 2019/6/3
 * @des
 * @modify
 **/
public interface ISpeakEngine {
    public boolean isInit();
    public void init(Context context,IVoiceInitListener initListener,boolean isDebug);
    public void setSpeakListener(BaseSpeakListener speakListener);
    public void start(String text);

    public void pause();

    public void resume();

    public void stop();

    public void destroy();
}
