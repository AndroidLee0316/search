package com.pasc.business.search.more.model.task;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.pasc.lib.search.ISearchItem;
import com.pasc.business.search.ItemType;

/**
 * @author yangzijian
 * @date 2019/3/4
 * @des  搜索框模糊搜索事项--办事指南
 * @modify
 **/
public class ServiceGuideDataBean implements ISearchItem {

    @SerializedName ("taskCode")
    public String taskCode;  // 事项编号

    @SerializedName ("taskName")
    public String taskName; // 事项名称

    @SerializedName ("areacode")
    public String areacode;  // 地区编号

    @SerializedName ("areaCodeText")
    public String areaCodeText; // 区域名称

    @SerializedName ("taskInfoUrl")
    public String taskInfoUrl; // 办事详情地址

    @Override
    public String title() {
        return taskName;
    }

    @Override
    public String content() {
        return null;
    }

    @Override
    public String url() {
        return taskInfoUrl;
    }

    @Override
    public String icon() {
        return null;
    }

    @Override
    public String date() {
        return null;
    }

    @Override
    public String searchType() {
        return ItemType.personal_work_guide;
    }

    @Override
    public int getItemType() {
        return ItemType.getItemTypeFromType (searchType ());

    }

    @Override
    public String jsonContent() {
        return new Gson ().toJson (this);
    }
}
