package com.pasc.business.search.home.model.search;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 网络搜索返回值
 * @des
 * @modify
 **/
public class NetQueryResponse {


    @SerializedName("from")
    public int from;
    @SerializedName("size")
    public int size;
    @SerializedName("dataList")
    public List<DataList> dataList;
    @SerializedName("analyzers")
    public List<String> analyzers;


    //证企号服务
    public static class DataList {
        @SerializedName("totalPage")
        public int totalPage;
        @SerializedName("totalSize")
        public int totalSize;
        @SerializedName ("themeConfigId")
        public String themeConfigId;
        @SerializedName ("themeName")
        public String themeName;

        @SerializedName("list")
        public List<NetQueryBean> list;


    }

}
