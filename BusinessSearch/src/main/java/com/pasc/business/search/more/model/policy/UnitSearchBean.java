package com.pasc.business.search.more.model.policy;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yangzijian
 * @date 2019/3/4
 * @des 发文单位搜索--政策法规
 * @modify
 **/
public class UnitSearchBean {

    @SerializedName("list")
    public List<DataBean> list;

    public static class DataBean {
        //发文名称
        @SerializedName("unitName")
        public String unitName;

        //编码
        @SerializedName("code")
        public String code;
    }
}
