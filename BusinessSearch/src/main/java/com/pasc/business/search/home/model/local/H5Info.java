package com.pasc.business.search.home.model.local;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author yangzijian
 * @date 2019/3/4
 * @des
 * @modify
 **/
public class H5Info implements Serializable{

    @SerializedName("debugURL") public String debugURL;
    @SerializedName("productionURL") public String productionURL;
    @SerializedName("useNativeNavigationBar") public boolean useNativeNavigationBar;
    @SerializedName("customWebTitle") public String customWebTitle;//加载webview标题
    @SerializedName("showCloseButton") public boolean showCloseButton;//标题栏是否显示关闭按钮
    @SerializedName("dynamicTitleEnable") public boolean dynamicTitleEnable;//是否接收标题栏变化



}
