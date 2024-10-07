package com.pasc.lib.search;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;
import java.util.Map;

/**
 * @author yangzijian
 * @date 2019/3/6
 * @des
 * @modify
 **/
public interface ISearchGroup<T extends ISearchItem> extends MultiItemEntity {

    List<T> data();
    String groupName();
    int priority();
    String searchType();
    int getItemType();
    Map<String,String> extraData();
}
