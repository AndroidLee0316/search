package com.pasc.business.search.home.view;

import com.pasc.lib.search.SearchSourceGroup;
import com.pasc.lib.search.base.BaseView;

import java.util.List;

/**
 * @author yangzijian
 * @date 2019/2/18
 * @des
 * @modify
 **/
public interface SearchHomeView extends BaseView {

    void localData(List<SearchSourceGroup> searchListItemGroups);

    void netData(List<SearchSourceGroup> searchListItemGroups);

    void netError(String code, String msg);

    void showContentView(boolean show);

    void setAnalyzers(List<String> analyzers);

}
