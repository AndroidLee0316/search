package com.pasc.business.search.more.fragment;

import android.os.Bundle;

import com.pasc.business.search.router.Table;

/**
 * @date 2019/6/10
 * @des
 * @modify
 **/
public class ServiceNetFragment extends CommonNetFragment {
    @Override
    protected void initData(Bundle bundleData) {
        super.initData (bundleData);
        hotWordPresenter.loadHotWord (Table.Value.MoreType.personal_more_service_page);
        hotWordPresenter.loadHintText (Table.Value.MoreType.personal_more_service_page);

    }
    @Override
    String searchCountTip() {
        return "项";
    }

    @Override
    void searchNet() {
        moreSearchPresenter.servicePoolParam (source,searchView.getKeyword (), entranceLocation, pageNo, pageSize);
    }
}
