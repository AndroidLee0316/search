package com.pasc.business.search.common.model;

import com.google.gson.annotations.SerializedName;
import com.pasc.lib.search.common.IKeywordItem;
import com.pasc.lib.search.util.SearchUtil;

/**
 * @author yangzijian
 * @date 2019/3/4
 * @des  搜索指定内容，搜索主题
 * @modify
 **/
public class SearchThemeBean implements IKeywordItem {


    /**
     * id : personal_policy_rule
     * entranceLocation : 1
     * themeName : 政策法规
     * showName : 政策法规
     * hasVerticalEntry : 1
     * status : 1
     * serialNumber : 5
     * createdBy : sys
     * createdDate : 1551321622000
     * updatedBy : sys
     * updatedDate : 1551321622000
     */

    @SerializedName("id")
    public String id;

    //入口位置 1：个人版 2：企业版
    @SerializedName("entranceLocation")
    public int entranceLocation;

    //主题
    @SerializedName("themeName")
    public String themeName;

    //客户端展示名称
    @SerializedName("showName")
    public String showName;

    //是否有垂直入口 0：否  1：是
    @SerializedName("hasVerticalEntry")
    public int hasVerticalEntry;

    //状态 0：停用  1：启用
    @SerializedName("status")
    public int status;
    @SerializedName("serialNumber")
    public int serialNumber;
    @SerializedName("createdBy")
    public String createdBy;
    @SerializedName("createdDate")
    public long createdDate;
    @SerializedName("updatedBy")
    public String updatedBy;
    @SerializedName("updatedDate")
    public long updatedDate;

    @Override
    public String type() {
        return id;
    }

    /***
     * 如果showName 不为空，则显示showName
     * 反之则显示 themeName
     * @return
     */
    @Override
    public String keyword() {
        if (!SearchUtil.isEmpty (showName)){
            return showName;
        }
        return themeName;
    }
}
