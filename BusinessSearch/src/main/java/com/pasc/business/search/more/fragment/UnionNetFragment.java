package com.pasc.business.search.more.fragment;

import android.os.Bundle;

import com.pasc.business.search.router.Table;

/**
 * @date 2019/6/10
 * @des
 * @modify
 **/
public class UnionNetFragment extends CommonNetFragment {

    @Override
    protected void initData(Bundle bundleData) {
        super.initData (bundleData);
        hotWordPresenter.loadHotWord (Table.Value.MoreType.personal_union_page);
        hotWordPresenter.loadHintText (Table.Value.MoreType.personal_union_page);
    }

    @Override
    String searchCountTip() {
        return "个";
    }

    @Override
    void searchNet() {
        moreSearchPresenter.serviceDepartmentParam (source,searchView.getKeyword (), entranceLocation, pageNo, pageSize);
    }
}
