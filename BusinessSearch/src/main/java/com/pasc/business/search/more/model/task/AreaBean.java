package com.pasc.business.search.more.model.task;

import com.google.gson.annotations.SerializedName;

/**
 * @author yangzijian
 * @date 2019/3/4
 * @des 行政区划列表
 * @modify
 **/
public class AreaBean extends IAreaBean {
    //主键
    @SerializedName("id")
    public String id;

    //地区编号
    @SerializedName("areacode")
    public String areacode;

    //地区名称
    @SerializedName("areaCodeText")
    public String areaCodeText;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getAreacode() {
        return areacode;
    }

    @Override
    public String getAreaCodeText() {
        return areaCodeText;
    }

}

abstract class IAreaBean implements IMultiPickBean {
    @Override
    public String getDeptName() {
        return "";
    }

    @Override
    public String getDeptCode() {
        return "";
    }

    @Override
    public String getTaskCode() {
        return "";
    }

    @Override
    public String getTaskName() {
        return "";
    }

    @Override
    public String getTaskInfoUrl() {
        return "";
    }
}
