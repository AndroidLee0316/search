package com.pasc.business.search.home.model.param;

import com.google.gson.annotations.SerializedName;

/**
 * @author yangzijian
 * @date 2019/3/4
 * @des
 * @modify
 **/
public class AppStartParam {


    /**
     * appType : /系统： 1：ios  2：android
     * appVersion : 1.0.1
     * serviceVersion : 1
     */

    @SerializedName("appType")
    public String appType="2";  // // ios android
    @SerializedName("appVersion")
    public String appVersion; //app版本号
    @SerializedName("versionNo")
    public String versionNo; ////版本控制
    @SerializedName ("entranceLocation")
    public String entranceLocation;//":1,//入口位置 1：个人版 2：企业版 必传

    public static AppStartParam instance(String entranceLocation,String appVersion,String serviceVersion){
        AppStartParam param=new AppStartParam () ;
        param.entranceLocation=entranceLocation;
        param.appVersion=appVersion; //SearchManager.instance ().getVersionName ();
        param.versionNo=serviceVersion;
        return param;
    }
}
