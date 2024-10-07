package com.pasc.business.search.more.view;

import com.pasc.business.search.more.model.task.AreaBean;
import com.pasc.business.search.more.model.task.DeptBean;
import com.pasc.lib.search.base.BaseView;

import java.util.List;

/**
 * 多条件筛选
 */
public interface BanShiView extends BaseView {

    void getAreaList(List<AreaBean> beans);

    void getDeptByAreaData(List<DeptBean> beans);

    void getStreetByAreaData(List<DeptBean> beans);

    void onError(String code, String errorMsg, int ConditionType);
}
