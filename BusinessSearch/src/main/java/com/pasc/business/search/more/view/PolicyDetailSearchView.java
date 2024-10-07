package com.pasc.business.search.more.view;

import com.pasc.business.search.more.model.policy.UnitSearchBean;
import com.pasc.lib.search.base.BaseView;

public interface PolicyDetailSearchView extends BaseView {

    void getPolicyUnitSearch(UnitSearchBean bean);

    void onError(String code, String errorMsg);
}
