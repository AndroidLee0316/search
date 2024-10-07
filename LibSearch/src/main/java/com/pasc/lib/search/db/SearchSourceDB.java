package com.pasc.lib.search.db;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by huangtebian535 on 2019/03/01.
 */

@Database(name = SearchSourceDB.NAME, version = SearchSourceDB.VERSION)
public class SearchSourceDB {

    //版本号
    public static final int VERSION = 3;

    //数据库名称
    public static final String NAME = "SEARCH_SOURCE_DB";
}
