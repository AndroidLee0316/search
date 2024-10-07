package com.pasc.business.search.more.model.task;

import com.google.gson.annotations.SerializedName;

/**
 * @author yangzijian
 * @date 2019/3/4
 * @des 对应区划的部门
 * @modify
 **/
public class DeptBean extends IDeptBean {
    //主键
    @SerializedName("id")
    public String id;

    //地区编号
    @SerializedName("areacode")
    public String areacode;

    //行政区划名称
    @SerializedName("deptName")
    public String deptName;

    //部门名称
    @SerializedName("deptCode")
    public String deptCode;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getAreacode() {
        return areacode;
    }


    @Override
    public String getDeptName() {
        return deptName;
    }

    @Override
    public String getDeptCode() {
        return deptCode;
    }


}

abstract class IDeptBean implements IMultiPickBean {
    @Override
    public String getAreaCodeText() {
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
