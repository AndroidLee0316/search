package com.search.demo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C) 2016 pasc Licensed under the Apache License, Version 2.0 (the "License");
 *
 * @author yangzijian
 * @date 2018/7/16
 * @des 公共头信息
 * @modify
 **/
public class HeaderUtil {
    /**
     * 获取版本名
     */
    public static String getVersionName(Context ctx) {
        String versionName = "";
        try {
            PackageInfo packageInfo =
                ctx.getApplicationContext().getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
    public static Map<String, String> getHeaders(Context context, boolean isDebug, Map<String, String> headers) {
        Map<String, String> commonHeaders = new HashMap<>();

        // 下个版本在上，跟后台沟通过
        //        commonHeaders.put("SN", "ANDROID_" + (isDebug ? "DEBUG" : "RELEASE"));
        //        String versionName = "";
        //        if (AppProxy.getInstance().getApplication() != null) {
        //            versionName = AppUtils.getVersionName(AppProxy.getInstance().getApplication());
        //        }
        //        /****app软件标识为  【平台标识】【产品标识】
        //         app的主次版本信息  【主版本次版本】
        //         ANDROID_RE
        //         **/
        //        commonHeaders.put("VN", versionName);
        //        /****app的发布类型  【发布类型 release、beta、…】***/
        //        commonHeaders.put("PT", isDebug?"DEBUG" : "RELEASE");
        //        /*****Build no  从rdm上获取build号****/
        //        commonHeaders.put("BN", "220");
        //        /******设备的厂商名****/
        //        commonHeaders.put("VC", Build.BRAND);
        //        /****机型信息****/
        //        commonHeaders.put("MO", Build.MODEL);
        //        /****屏幕分辨率         【屏幕宽】 _【屏幕高】*****/
        //        commonHeaders.put("RL", ScreenUtils.getScreenWidth() + "_" + ScreenUtils.getScreenHeight());
        //        /****渠道号****/
        //        commonHeaders.put("CHID", "10000");
        //        /****ROM版本号****/
        //        commonHeaders.put("RV", Build.MANUFACTURER);
        //        /*****操作系统版本****/
        //        commonHeaders.put("OS", "Android"+Build.VERSION.RELEASE);
        //        /*****Q-UA版本号*****/
        //        commonHeaders.put("QV", "1.0");
        /***后台版本***/
        //        commonHeaders.put("x-api-version", "1.4.0");
        commonHeaders.put("x-api-version", "1.2.0");
        if (headers != null) {
            commonHeaders.putAll(headers);
        }
        try {
            commonHeaders.put ("x-app-name", URLEncoder.encode (getAppName (context), "UTF-8"));
            //commonHeaders.put ("x-app-name", "2.3.0");
        }catch (Exception e){}

        //        commonHeaders.put("x-app-version", getVersionName (context));
        commonHeaders.put("x-app-version", "1.6.4");
        commonHeaders.put("x-os-type", "2");

        commonHeaders.put("Content-Type", "application/json");

        return commonHeaders;

    }
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager ();
            PackageInfo packageInfo = packageManager.getPackageInfo (
                context.getPackageName (), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources ().getString (labelRes);
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return "";
    }

}
