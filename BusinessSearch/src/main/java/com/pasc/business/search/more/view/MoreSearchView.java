package com.pasc.business.search.more.view;

import com.pasc.lib.search.ISearchItem;
import com.pasc.lib.search.base.BaseView;

import java.util.List;

/**
 * @author yangzijian
 * @date 2019/2/21
 * @des
 * @modify
 **/
public interface MoreSearchView extends BaseView {

    void localData(List<? extends ISearchItem> items);

    void netData(List<? extends ISearchItem> items, int totalCount);

    void showContentView(boolean show);

    void onSearchError(String code, String msg);

    void setAnalyzers(List<String> analyzers);
}
