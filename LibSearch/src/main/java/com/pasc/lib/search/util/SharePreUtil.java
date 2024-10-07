package com.pasc.lib.search.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.pasc.lib.search.LocalSearchManager;


/**
 * @author yangzijian
 * @date 2018/9/19
 * @des
 * @modify
 **/
public class SharePreUtil {
    private final static String updateShareName = "pasc_search_prefer";

    public static void setInt(String key, int value) {
        getShared().edit().putInt(key, value).commit();
    }

    public static int getInt(String key, int defaultValue) {
        return getShared().getInt(key, defaultValue);
    }

    public static void setFloat(String key, float value) {
        getShared().edit().putFloat(key, value).commit();
    }

    public static float getFloat(String key, float defaultValue) {
        return getShared().getFloat(key, defaultValue);
    }

    public static void setLong(String key, long value) {
        getShared().edit().putLong(key, value).commit();
    }

    public static long getLong(String key, long defaultValue) {
        return getShared().getLong(key, defaultValue);
    }

    public static void setString(String key, String value) {
        getShared().edit().putString(key, value).commit();
    }

    public static String getString(String key, String value) {
        return getShared().getString(key, value);
    }

    public static void setBoolean(String key, boolean value) {
        getShared().edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(String key, boolean value) {
        return getShared().getBoolean(key, value);
    }

    private static SharedPreferences getShared() {
        return LocalSearchManager.instance ().getApp ().getSharedPreferences(updateShareName, Context.MODE_PRIVATE);
    }


}
