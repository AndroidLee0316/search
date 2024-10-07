package com.pasc.lib.search.db.history;

import com.pasc.lib.search.common.IKeywordItem;
import com.pasc.lib.search.db.MyConverter;
import com.pasc.lib.search.db.SearchSourceDB;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @author yangzijian
 * @date 2019/2/19
 * @des
 * @modify
 **/
@Table(database = SearchSourceDB.class)
public class HistoryBean extends BaseModel implements IKeywordItem {

    @PrimaryKey(autoincrement = true)
    @Column(name = "id")
    public long id;

    //存储类型，用于保存 登陆和没登录的 类型
    @Column(name = "storeType",typeConverter = MyConverter.class)
    public String storeType;

    @Column(name = "keyword",typeConverter = MyConverter.class)
    public String keyword;

    @Column(name ="date")
    public long date;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass () != o.getClass ()) return false;
        HistoryBean that = (HistoryBean) o;
        return equals (storeType, that.storeType) &&
                equals (keyword, that.keyword);
    }


    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals (b));
    }

    @Override
    public String type() {
        return "";
    }

    @Override
    public String keyword() {
        return keyword;
    }
}
