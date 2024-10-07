package com.pasc.business.search.home.model.local;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.pasc.business.search.router.Table;
import com.pasc.business.search.ItemType;
import com.pasc.lib.search.db.SearchSourceItem;

/**
 * @author yangzijian
 * @date 2019/3/4
 * @des  证企号
 * @modify
 **/
public class UnionBean {
    /**
     * id : 42
     * unionId : f3d6dd845c884650baf93d09e7c9cbf4
     * unionName : 演示政企号
     * homepage : null
     * authStatus : 1
     * contactName : 演示人
     * password : null
     * mobileNo : 13688888888
     * email : 13688888888@yanshi.com.cn
     * subjectInformation : 演示政企号
     * remark : 演示政企号i深圳
     * logo : https://isz-web.sz.gov.cn/admin/image/openplatform/logo_default@2x.png
     * createdDate : 1548150823000
     * createdBy : sys
     * updatedDate : 1548150823000
     * updatedBy : sys
     * status : 1
     */

    @SerializedName("id")
    public String id;
    @SerializedName("unionId")
    public String unionId;
    @SerializedName("unionName")
    public String unionName;
    @SerializedName("homepage")
    public String homepage;
    @SerializedName("authStatus")
    public int authStatus;
    @SerializedName("contactName")
    public String contactName;
    @SerializedName("password")
    public String password;
    @SerializedName("mobileNo")
    public String mobileNo;
    @SerializedName("email")
    public String email;
    @SerializedName("subjectInformation")
    public String subjectInformation;
    @SerializedName("remark")
    public String remark;
    @SerializedName("logo")
    public String logo;
    @SerializedName("createdDate")
    public long createdDate;
    @SerializedName("createdBy")
    public String createdBy;
    @SerializedName("updatedDate")
    public long updatedDate;
    @SerializedName("updatedBy")
    public String updatedBy;
    @SerializedName("status")
    public int status;

//    @SerializedName("union_id") public String unionId;
//    @SerializedName("union_name") public String unionName;
//    @SerializedName("icon") public String icon;
//    @SerializedName("label") public String label;
//    @SerializedName("keyWord") public String keyWord;
//    @SerializedName("nativePage") public String nativePage;
//    @SerializedName("needLogin") public String needLogin;
//    @SerializedName("verifyTokenByServer") public String verifyTokenByServer;
//    @SerializedName("checkLocationPermission") public String checkLocationPermission;
//    @SerializedName("type") public String type;
//    @SerializedName("extension") public String extension;
//    @SerializedName("H5Info") public H5Info H5Info;






    public SearchSourceItem convert(){
        SearchSourceItem item=new SearchSourceItem ();
        item.id=unionId;
        item.entranceLocation= Table.Value.EntranceLocation.person_type;
        item.name=unionName;
        item.url=homepage;
        item.content="";
        item.icon=logo;
        item.type= ItemType.personal_zhengqi_number;
        item.jsonContent=new Gson ().toJson (this);
        return item;
    }
}
