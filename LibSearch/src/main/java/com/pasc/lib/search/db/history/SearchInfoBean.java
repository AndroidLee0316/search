package com.pasc.lib.search.db.history;

import com.google.gson.annotations.SerializedName;
import com.pasc.lib.search.db.MyConverter;
import com.pasc.lib.search.db.SearchSourceDB;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @author yangzijian
 * @date 2019/3/26
 * @des
 * @modify
 **/
@Table(database = SearchSourceDB.class)
public class SearchInfoBean extends BaseModel {

    @Column(name = "hintId")
    @PrimaryKey
    public long hintId;

    //搜索入口id
    @SerializedName ("id")
    @Column(name = "id",typeConverter = MyConverter.class)
    public String id;

    // 搜索文案
    @SerializedName ("searchText")
    @Column(name = "searchText",typeConverter = MyConverter.class)
    public String searchText;

    // 页面名称
    @SerializedName ("pageName")
    @Column(name = "pageName",typeConverter = MyConverter.class)
    public String pageName;

    // 热门搜索
    @SerializedName ("searchHot")
    @Column(name = "searchHot",typeConverter = MyConverter.class)
    public String searchHot;

    // 入口位置 1：个人版 2：企业版
    @SerializedName ("entranceLocation")
    @Column(name = "entranceLocation")
    public int entranceLocation;
}
