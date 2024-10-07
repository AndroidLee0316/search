package com.pasc.business.search.common.view;

import com.pasc.lib.search.base.BaseView;
import com.pasc.lib.search.db.history.HistoryBean;

import java.util.List;

/**
 * @author yangzijian
 * @date 2019/2/27
 * @des
 * @modify
 **/
public interface HistoryView extends BaseView {
    void historyData(List<HistoryBean> beans);
}
