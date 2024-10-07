package com.pasc.business.search.home.model.param;

import com.google.gson.annotations.SerializedName;
import com.pasc.business.search.ItemType;
import com.pasc.business.search.common.param.PageParam;

import java.util.ArrayList;
import java.util.List;

/**
 * 网络搜索查询条件
 **/
public class NetQueryParam {

    /**
     * 必须
     * keyWord : 关键字
     */
    @SerializedName("searchWord")
    public String searchWord;

    /**
     * 入口id
     */
    @SerializedName("entranceId")
    public String entranceId;

    /**
     * 主题搜索id
     */
    @SerializedName("themeConfigId")
    public String themeConfigId;

    /**
     * 默认0
     */
    @SerializedName("from")
    public int from = 0;

    /**
     * 默认0
     */
    @SerializedName("size")
    public int size = 13;



    /****必须
     1. 个人版，2企业版****/
    @SerializedName ("entranceLocation")
    public String entranceLocation;



}
