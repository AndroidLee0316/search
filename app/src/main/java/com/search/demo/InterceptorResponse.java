package com.search.demo;

import com.google.gson.annotations.SerializedName;
import com.pasc.business.search.home.model.local.ServiceBean;
import com.pasc.lib.search.ISearchItem;

import java.util.List;

/**
 * @date 2019/6/4
 * @des
 * @modify
 **/
public class InterceptorResponse {

    @SerializedName("items")
    public List<ServiceBean> items;

    public static class MatchBean{
        public ISearchItem searchItem;
        public String keyword;
        public MatchBean(ISearchItem searchItem, String keyword) {
            this.searchItem = searchItem;
            this.keyword = keyword;
        }
    }

}
