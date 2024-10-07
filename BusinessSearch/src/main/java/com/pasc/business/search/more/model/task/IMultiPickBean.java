package com.pasc.business.search.more.model.task;


public interface IMultiPickBean {
    //主键
    String getId();

    //地区编号
    String getAreacode();

    //地区名称
    String getAreaCodeText();

    //行政区划名称
    String getDeptName();

    //部门名称
    String getDeptCode();

    //事项编码
    String getTaskCode();

    //事项名称
    String getTaskName();

    //办事记录详情地址
    String getTaskInfoUrl();

}
