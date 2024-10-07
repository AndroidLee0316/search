package com.pasc.lib.voice;

import android.content.Context;

/**
 * @date 2019/6/3
 * @des
 * @modify
 **/
public interface ISpeechEngine {
    public boolean isInit();
    public void init(Context context,IVoiceInitListener initListener, boolean isDebug);
    public void setSpeechListener(BaseSpeechListener speechListener);
    public void start();
    public void stop();

    public void destroy();
}
