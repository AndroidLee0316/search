package com.pasc.business.search.common.param;

import com.google.gson.annotations.SerializedName;

/**
 * @author yangzijian
 * @date 2019/3/7
 * @des
 * @modify
 **/
public class HotParam {
    @SerializedName ("id")
    public String id;

    public HotParam(String id) {
        this.id = id;
    }
}
