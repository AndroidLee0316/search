package com.pasc.lib.search.util;

import android.util.Log;

import com.pasc.lib.search.LocalSearchManager;

/**
 * @author yangzijian
 * @date 2019/2/21
 * @des
 * @modify
 **/
public class LogUtil {
    public static void log(String msg){
        if (LocalSearchManager.instance ().isDebug ()) {
            if (!SearchUtil.isEmpty (msg)) {
                Log.e ("searchTag", msg);
            }
        }
    }
}
