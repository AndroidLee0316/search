package com.pasc.business.search.more.presenter;

import com.pasc.business.search.SearchManager;
import com.pasc.business.search.more.model.policy.UnitSearchBean;
import com.pasc.business.search.more.net.MoreBiz;
import com.pasc.business.search.more.net.MoreBizBase;
import com.pasc.business.search.more.view.PolicyDetailSearchView;
import com.pasc.lib.net.resp.BaseRespThrowableObserver;
import com.pasc.lib.search.base.WrapperPresenter;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * @author yangzijian
 * @date 2019/2/21
 * @des 政策法规
 * @modify
 **/
public class PolicyPresenter extends WrapperPresenter<PolicyDetailSearchView> {
  private CompositeDisposable compositeDisposable = new CompositeDisposable();

  /**
   * 从网络搜索(发文单位搜索)
   */
  public void getPolicyUnitSearchFromNet() {
    compositeDisposable.add(
        (SearchManager.instance().isUseBaseApi() ? MoreBizBase.policyUnitSearch()
            : MoreBiz.policyUnitSearch())
            .subscribe(new Consumer<UnitSearchBean>() {
              @Override
              public void accept(UnitSearchBean bean) {

                if (bean != null) {
                  if (bean.list == null) {
                    bean.list = new ArrayList<>();
                  }
                  String ss = "全部单位";
                  boolean has = false;
                  for (UnitSearchBean.DataBean dataBean : bean.list) {
                    if (ss.equals(dataBean.unitName)) {
                      has = true;
                      break;
                    }
                  }
                  if (!has) {
                    UnitSearchBean.DataBean dataBean = new UnitSearchBean.DataBean();
                    dataBean.unitName = ss;
                    dataBean.code = "";
                    bean.list.add(0, dataBean);
                  }
                }

                getView().getPolicyUnitSearch(bean);
              }
            }, new BaseRespThrowableObserver() {
              @Override
              public void onV2Error(String code, String msg) {
                getView().onError(code, msg);
              }
            }));
  }

  @Override
  public void onMvpDetachView(boolean retainInstance) {
    compositeDisposable.clear();
    super.onMvpDetachView(retainInstance);
  }
}
