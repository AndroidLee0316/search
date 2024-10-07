package com.pasc.business.search.more.model.policy;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.pasc.lib.search.ISearchItem;
import com.pasc.business.search.ItemType;

/**
 * @author yangzijian
 * @date 2019/3/4
 * @des  政策法规
 * @modify
 **/
public class PolicyDataBean  implements ISearchItem {


    /**
     * classify : reprehenderit consectetur nostrud
     * subheading : labore occaecat ullamco proident
     * writingdate : proident fugiat esse labore dolore
     * serviceunit : nisi cupidatat incididunt
     * pid : dolor irure cillum con
     * simpleContent : cupidatat est ullamco esse
     * title : veniam
     */

    @SerializedName("classify")
    public String classify;  // 分类
    @SerializedName("subheading")
    public String subheading; //副标题
//    @SerializedName("writingdate")
//    public String writingdate; // 发文日期
    @SerializedName("serviceunit")
    public String serviceunit; //发文单位
    @SerializedName("pid")
    public String pid; //文档id
    @SerializedName("simpleContent")
    public String simpleContent; //包含该字段的内容
    @SerializedName("title")
    public String title; //标题
    /**
     * documentId : 13523815
     * writingDate : 2018-07-04
     * serviceUnit : 深圳市人民政府办公厅
     * code : AB-------------
     */

    @SerializedName("documentId")
    public String documentId;
    @SerializedName("writingDate")
    public String writingDate;
    @SerializedName("serviceUnit")
    public String serviceUnit;
    @SerializedName("code")
    public String code;


    @Override
    public String title() {
        return title;
    }

    @Override
    public String content() {
        return simpleContent;
    }

    @Override
    public String url() {
        return "";
    }

    @Override
    public String icon() {
        return null;
    }

    @Override
    public String date() {
        return writingDate;
    }

    @Override
    public String searchType() {
        return ItemType.personal_policy_rule;
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
