package com.pasc.business.search.common.param;

import com.google.gson.annotations.SerializedName;

/**
 * @author yangzijian
 * @date 2019/3/9
 * @des
 * @modify
 **/
public class PageParam {
    @SerializedName("pageNo")
    public String pageNo;

    @SerializedName ("pageSize")
    public String pageSize;

    public PageParam(int pageNo, int pageSize) {
        this.pageNo = pageNo+"";
        this.pageSize = pageSize+"";
    }
}
