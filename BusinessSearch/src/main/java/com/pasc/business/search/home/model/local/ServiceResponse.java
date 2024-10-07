package com.pasc.business.search.home.model.local;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yangzijian
 * @date 2019/3/13
 * @des
 * @modify
 **/
public class ServiceResponse {

    // 服务
    @SerializedName("serviceList")
    public List<ServiceBean> serviceList;
    //服务版本
    @SerializedName("versionNo")
    public String versionNo;
}
