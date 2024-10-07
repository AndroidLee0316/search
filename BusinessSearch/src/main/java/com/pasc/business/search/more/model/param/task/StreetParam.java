package com.pasc.business.search.more.model.param.task;

import com.google.gson.annotations.SerializedName;

/**
 * @author yangzijian
 * @date 2019/3/13
 * @des
 * @modify
 **/
public class StreetParam {


    /**
     * areacode : 440308 行政区code
     */

    @SerializedName("areacode")
    public String areacode;

    public StreetParam(String areacode) {
        this.areacode = areacode;
    }
}
