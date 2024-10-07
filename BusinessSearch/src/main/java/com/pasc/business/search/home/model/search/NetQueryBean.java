package com.pasc.business.search.home.model.search;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.pasc.business.search.ItemType;
import com.pasc.lib.search.ISearchItem;

/**
 * @author yangzijian
 * @date 2019/3/4
 * @des  证企号服务
 * @modify
 **/
public class NetQueryBean implements ISearchItem {
    @SerializedName("title")
    public String title;
    @SerializedName("secondTitle")
    public String secondTitle;
    @SerializedName("url")
    public String url;

    @SerializedName ("id")
    public String id;

    @SerializedName ("logo")
    public String logo;

    public String type = ItemType.personal_policy_rule;

    @Override
    public String title() {
        return title;
    }

    @Override
    public String content() {
        return secondTitle;
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public String icon() {
        return logo;
    }

    @Override
    public String date() {
        return null;
    }

    @Override
    public String searchType() {
        return type;
    }

    @Override
    public int getItemType() {
        return ItemType.getItemTypeFromType (searchType ());

    }

    @Override
    public String jsonContent() {
        return new Gson ().toJson (this);
    }
}
