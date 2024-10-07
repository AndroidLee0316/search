package com.pasc.business.search.common.view;

import com.pasc.business.search.common.model.HotBean;
import com.pasc.business.search.common.model.SearchThemeBean;
import com.pasc.lib.search.base.BaseView;

import java.util.List;

/**
 * @author yangzijian
 * @date 2019/3/5
 * @des
 * @modify
 **/
public interface HotView extends BaseView {
    void hotData(List<HotBean> beans);

    void hintText(String text);

    void themeData(List<SearchThemeBean> beans);
}
