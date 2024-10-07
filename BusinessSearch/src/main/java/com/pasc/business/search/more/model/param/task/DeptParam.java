package com.pasc.business.search.more.model.param.task;

import com.google.gson.annotations.SerializedName;

/**
 * @author yangzijian
 * @date 2019/3/4
 * @des
 * @modify
 **/
public class DeptParam {
    @SerializedName("areacode")
    public String areacode; // 行政区划编号

    public DeptParam(String areacode) {
        this.areacode = areacode;
    }
}
