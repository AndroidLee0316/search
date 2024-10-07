package com.pasc.lib.search;

import android.app.Activity;
import android.content.Context;


import java.util.Map;

import io.reactivex.Single;

/**
 * @author yangzijian
 * @date 2019/2/19
 * @des
 * @modify
 **/
public abstract class ApiGet {
    public abstract String getPhone();
    public abstract String getToken();
    public boolean interceptSearch(Activity activity,String keyword){
        return false;
    }
    public boolean ignoreLength(String keyWord){

        return false;
    }
    public abstract void searchItemClick(Activity activity,ISearchItem item);
    public abstract void onEvent(Context context, String eventId, String label, Map map);
    public Single<Boolean> voiceInterceptorSearch(Context context,String keyword){
        return null;
    }
    public Single<Map<String,String>> voiceTip(){
        return null;
    }
}
