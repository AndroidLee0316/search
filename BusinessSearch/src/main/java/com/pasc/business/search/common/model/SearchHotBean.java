package com.pasc.business.search.common.model;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangzijian
 * @date 2019/3/4
 * @des
 * @modify
 **/
public class SearchHotBean {

    /**
     * id : personal_serve_page
     * pageName : 更多服务页
     * entranceLocation : 1
     * searchText : 文案文
     * searchHot : 亚瑟,甄姬
     * status : 1
     * createdBy : sys
     * createdDate : 1551177812000
     * updatedBy : sys
     * updatedDate : 1551177817000
     */
    // 类型
    @SerializedName("id")
    public String id;
    @SerializedName("pageName")
    public String pageName;
    @SerializedName("entranceLocation")
    public int entranceLocation;

    //提示文案
    @SerializedName("searchText")
    public String searchText;

    //热门关键词
    @SerializedName("searchHot")
    public String searchHot;
    @SerializedName("status")
    public int status;
    @SerializedName("createdBy")
    public String createdBy;
    @SerializedName("createdDate")
    public long createdDate;
    @SerializedName("updatedBy")
    public String updatedBy;
    @SerializedName("updatedDate")
    public long updatedDate;

    @SerializedName("searchHotList")
    public List<String> searchHotList;

    private List<HotBean> hotBeans;

    /****获取热门关键词***/
    public List<HotBean> getHotBeans() {
        if (hotBeans == null) {
            hotBeans = new ArrayList<> ();
            if (searchHotList != null) {
                for (String hot : searchHotList) {
                    HotBean hotBean = new HotBean (hot, "");
                    hotBeans.add (hotBean);
                }
            }

        }
        return hotBeans;
    }
}
