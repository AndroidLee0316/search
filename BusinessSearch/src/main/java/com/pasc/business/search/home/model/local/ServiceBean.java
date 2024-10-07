package com.pasc.business.search.home.model.local;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.pasc.business.search.ItemType;
import com.pasc.lib.search.db.SearchSourceItem;

import java.io.Serializable;

/**
 * @author yangzijian
 * @date 2019/3/4
 * @des    服务
 * @modify
 **/
public class ServiceBean implements Serializable {


    @SerializedName("identifier") public String identifier;
    @SerializedName("name") public String name;
    @SerializedName("icon") public String icon;
    @SerializedName("label") public String label;
    @SerializedName("keyWord") public String keyWord;
    @SerializedName("nativePage") public String nativePage;
    @SerializedName("needLogin") public String needLogin;
    @SerializedName("verifyTokenByServer") public String verifyTokenByServer;
    @SerializedName("checkLocationPermission") public String checkLocationPermission;
    @SerializedName("type") public String type;
    @SerializedName("extension") public String extension;
    @SerializedName("H5Info") public H5Info H5Info;
    @SerializedName("eventId") public String eventId;
    @SerializedName("checkUserVerify")public boolean checkUserVerify;

    public SearchSourceItem convert(String entranceLocation){
        SearchSourceItem item=new SearchSourceItem ();
        item.id=identifier;
        item.entranceLocation=entranceLocation;
        item.name=name;
        item.url="";
        item.content=label;
        item.icon=icon;
        item.type= ItemType.personal_server;
        item.jsonContent=new Gson ().toJson (this);
        return item;
    }


}
