package com.pasc.lib.search.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * @author yangzijian
 * @date 2019/1/21
 * @des
 * @modify
 **/
public class DeviceUtil {
    private static String uniqueId = "";

    public static String getDeviceId(Context context) {

        if (!SearchUtil.isEmpty (uniqueId)) {
            return uniqueId;
        }
        try {
            String androidID = Secure.getString (context.getContentResolver (), Secure.ANDROID_ID);
            // 模拟器 Build.SERIAL 为 unknow
            // 红米手机 Build.SERIAL 是 0123456789ABCDE
            uniqueId = androidID + "_" + Build.SERIAL;
        } catch (Exception e) {
            uniqueId="device";
        }

        return uniqueId;
    }
    public static int dpTpPx(Context context,float value) {
        DisplayMetrics dm = context.getResources ().getDisplayMetrics ();
        return (int) (TypedValue.applyDimension (TypedValue.COMPLEX_UNIT_DIP, value, dm) + 0.5);
    }

}
