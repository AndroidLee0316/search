package com.pasc.business.search.more.fragment;

import android.os.Bundle;

import com.pasc.business.search.router.Table;

/**
 * @author yangzijian
 * @date 2019/2/21
 * @des 证企号服务
 * @modify
 **/
public class ZQHServiceFragment extends CommonNetFragment {

    @Override
    protected void initData(Bundle bundleData) {
        super.initData (bundleData);
        hotWordPresenter.loadHintText (Table.Value.MoreType.personal_zhengqi_server);

    }

    @Override
    String searchCountTip() {
        return "条";
    }

    @Override
    void searchNet() {
        moreSearchPresenter.searchUnionService (source,searchView.getKeyword (), entranceLocation, null, pageNo, pageSize);
    }
}
