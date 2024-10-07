package com.pasc.business.search.more.fragment;

import android.os.Bundle;
import android.text.TextUtils;

import com.pasc.business.search.router.Table;

/**
 * @date
 * @des 主题搜索
 * @modify
 **/
public class ThemeNetFragment extends CommonNetFragment {
    String entranceId;

    @Override
    protected void initData(Bundle bundleData) {
        entranceId = bundleData.getString (Table.Key.key_themeConfigId, "");
        super.initData (bundleData);
        //hotWordPresenter.loadHotWord (Table.Value.MoreType.personal_union_page);
        //hotWordPresenter.loadHintText (Table.Value.MoreType.personal_union_page);
        if(TextUtils.isEmpty(mSearchTypeName)){
            hintText = "输入关键字搜索";
        }else {
            hintText = "搜索" + '“' + mSearchTypeName + '”';

        }
        searchView.setHint(hintText);
    }

    @Override
    String searchCountTip() {
        return "个";
    }

    @Override
    void searchNet() {
        moreSearchPresenter.searchTheme (searchView.getKeyword (), entranceLocation,entranceId, pageNo, pageSize);
    }

}
