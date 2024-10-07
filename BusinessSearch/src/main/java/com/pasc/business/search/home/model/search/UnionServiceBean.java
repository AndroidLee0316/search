package com.pasc.business.search.home.model.search;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.pasc.lib.search.ISearchItem;
import com.pasc.business.search.ItemType;

/**
 * @author yangzijian
 * @date 2019/3/4
 * @des  证企号服务
 * @modify
 **/
public class UnionServiceBean implements ISearchItem {
    @SerializedName("serviceId")
    public String serviceId;
    @SerializedName("serviceName")
    public String serviceName;
    @SerializedName("serviceUrl")
    public String serviceUrl;
    /**
     * appId : null
     * serviceDesc : 我要评价
     * testUrl : http://39.108.57.190:8080/orderSys/forwordController/getIndexForword.do?toHtml=myevaluate&openweb=paschy
     * prodUrl : http://39.108.57.190:8080/orderSys/forwordController/getIndexForword.do?toHtml=myevaluate&openweb=paschy
     * status : 1
     * createdDate : 1547690138000
     * thirdPartyServicesName : null
     */

//    @SerializedName("appId")
//    public Object appId;
    @SerializedName("serviceDesc")
    public String serviceDesc;
    @SerializedName("testUrl")
    public String testUrl;
    @SerializedName("prodUrl")
    public String prodUrl;
    @SerializedName("status")
    public String status;
    @SerializedName("createdDate")
    public String createdDate;
//    @SerializedName("thirdPartyServicesName")
//    public Object thirdPartyServicesName;
    //部门名称
    @SerializedName ("unionName")
    public String unionName;

    @Override
    public String title() {
        return serviceName;
    }

    @Override
    public String content() {
        return unionName;
    }

    @Override
    public String url() {
        return prodUrl;
    }

    @Override
    public String icon() {
        return null;
    }

    @Override
    public String date() {
        return null;
    }

    @Override
    public String searchType() {
        return ItemType.personal_zhengqi_server;
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
