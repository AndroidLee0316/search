package com.pasc.lib.search;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * @author yangzijian
 * @date 2019/3/6
 * @des
 * @modify
 **/
public interface ISearchItem extends MultiItemEntity {

    String title(); //业务名称

    String content(); //内容
    String url();

    String icon(); //图标

    String date(); //日期

    String searchType(); //业务类型, SERVICE = 服务、GOVERMENT_ENTERPRISE = 政企号

    int getItemType(); // 布局类型

    String jsonContent(); //业务事项具体内容


}
