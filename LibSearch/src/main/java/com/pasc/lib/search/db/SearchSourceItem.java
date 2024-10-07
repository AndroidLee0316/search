package com.pasc.lib.search.db;

import com.google.gson.annotations.SerializedName;
import com.pasc.lib.search.ISearchItem;
import com.pasc.lib.search.LocalSearchManager;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by huangtebian535 on 2019/03/01.
 *
 * 存储 "服务" 与 "政企号" 数据。
 */

@Table(database = SearchSourceDB.class)
public class SearchSourceItem extends BaseModel implements ISearchItem {
    @SerializedName ("searchId")
    @PrimaryKey(autoincrement = true) @Column(name ="searchId") public long searchId;

    @SerializedName ("id")
    @Column(name ="id" ,typeConverter = MyConverter.class ) public String id;
    // 1 为个人版， 2为企业版
    @Column(name = "entranceLocation",typeConverter = MyConverter.class) public String entranceLocation;

    @SerializedName ("name")
    @Column(name ="name" ,typeConverter = MyConverter.class) public String name; //业务名称

    @SerializedName ("content")
    @Column(name ="content",typeConverter = MyConverter.class ) public String content; //内容

    @SerializedName ("icon")
    @Column(name ="icon" ,typeConverter = MyConverter.class) public String icon; //图标

    @SerializedName ("url")
    @Column(name ="url" ,typeConverter = MyConverter.class) public String url; //图标

    @SerializedName ("date")
    @Column(name ="date",typeConverter = MyConverter.class ) public String date; //日期

    @SerializedName ("firstChars")
    @Column(name ="firstChars" ,typeConverter = MyConverter.class) public String firstChars; //首批

    @SerializedName ("pinyins")
    @Column(name ="pinyins" ,typeConverter = MyConverter.class) public String pinyins; //全拼

    @SerializedName ("pinyinsSpilt")
    @Column(name ="pinyinsSpilt" ,typeConverter = MyConverter.class) public String pinyinsSpilt; //全拼

    @SerializedName ("type")
    @Column(name ="type" ,typeConverter = MyConverter.class)public String type; //业务类型,  服务、 政企号

    @SerializedName ("jsonContent")
    @Column(name ="jsonContent" ,typeConverter = MyConverter.class) public String jsonContent; //业务事项具体内容

    // 高亮 索引 index start end
    public int start;
    public int end;
    private String[] pinyinArr;
    public String[] pinyinArr(){
        if (pinyinArr==null) {
            pinyinArr= pinyinsSpilt.split (";");
        }
        return pinyinArr;
    }

    public boolean hasIndex(){
        return end>start && start>=0;
    }

    @Override
    public String title() {
        return name;
    }

    @Override
    public String content() {
        return content;
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public String icon() {
        return icon;
    }

    @Override
    public String date() {
        return date;
    }

    @Override
    public String searchType() {
        return type;
    }

    @Override
    public int getItemType() {
        return LocalSearchManager.instance ().getType ().getItemTypeFromType (type);

    }

    @Override
    public String jsonContent() {
        return jsonContent;
    }
}
