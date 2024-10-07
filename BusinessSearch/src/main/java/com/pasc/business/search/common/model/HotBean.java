package com.pasc.business.search.common.model;

import com.google.gson.annotations.SerializedName;
import com.pasc.lib.search.common.IKeywordItem;

/**
 * @author yangzijian
 * @date 2019/2/19
 * @des
 * @modify
 **/
public class HotBean implements IKeywordItem {

    @SerializedName ("keyword")
    public String keyword = "";
    @SerializedName ("type")
    public String type;

    public HotBean(String keyword, String type) {
        this.keyword = keyword;
        this.type = type;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public String keyword() {
        return keyword;
    }
}
