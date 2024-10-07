package com.pasc.business.search.more.fragment;

import android.os.Bundle;
import android.text.TextUtils;

import com.pasc.business.search.router.Table;

/**
 * @date 2019/6/10
 * @des
 * @modify
 **/
public class ServiceLocalFragment extends CommonLocalFragment {

    @Override
    String searchCountTip() {
        return "项";
    }

    @Override
    protected void initData(Bundle bundleData) {
        super.initData (bundleData);
//        hotWordPresenter.loadHotWord (Table.Value.MoreType.personal_more_service_page);
//        hotWordPresenter.loadHintText (Table.Value.MoreType.personal_more_service_page);
        if(TextUtils.isEmpty(mSearchTypeName)){
            searchView.setHint ("输入关键字搜索");
        }else {
            searchView.setHint("搜索" + '“' + mSearchTypeName + '”');
        }
    }
}
