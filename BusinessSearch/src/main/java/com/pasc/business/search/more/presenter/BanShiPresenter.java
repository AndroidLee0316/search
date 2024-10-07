package com.pasc.business.search.more.presenter;

import com.pasc.business.search.SearchManager;
import com.pasc.business.search.more.model.param.task.DeptParam;
import com.pasc.business.search.more.model.param.task.StreetParam;
import com.pasc.business.search.more.model.task.AreaBean;
import com.pasc.business.search.more.model.task.DeptBean;
import com.pasc.business.search.more.net.MoreBiz;
import com.pasc.business.search.more.net.MoreBizBase;
import com.pasc.business.search.more.view.BanShiView;
import com.pasc.lib.net.resp.BaseRespThrowableObserver;
import com.pasc.lib.search.base.WrapperPresenter;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class BanShiPresenter extends WrapperPresenter<BanShiView> {
  Disposable disposableAreaList, disposableDept, disposableStreet;

  public final static int AreaType = 1, StreetType = 2, DeptType = 3;

  private void dispose(Disposable disposable) {
    if (disposable != null && !disposable.isDisposed()) {
      disposable.dispose();
      disposable = null;
    }
  }

  /**
   * 行政区划列表--办事指南
   */
  public void getAreaList() {
    dispose(disposableAreaList);
    disposableAreaList = (SearchManager.instance().isUseBaseApi() ? MoreBizBase.getAreaList()
        : MoreBiz.getAreaList())
        .subscribe(new Consumer<List<AreaBean>>() {
          @Override
          public void accept(List<AreaBean> beans) {
            getView().getAreaList(beans);
          }
        }, new BaseRespThrowableObserver() {
          @Override
          public void onV2Error(String code, String msg) {
            getView().onError(code, msg, AreaType);
          }
        });
  }

  /**
   * 对应区划的部门--办事指南
   */
  public void getDeptByArea(final String areaCode) {
    dispose(disposableDept);
    disposableDept =
        (SearchManager.instance().isUseBaseApi() ? MoreBizBase.getDeptByArea(
            new DeptParam(areaCode))
            : MoreBiz.getDeptByArea(new DeptParam(areaCode)))
            .subscribe(new Consumer<List<DeptBean>>() {
              @Override
              public void accept(List<DeptBean> beans) {
                getView().getDeptByAreaData(beans);
              }
            }, new BaseRespThrowableObserver() {
              @Override
              public void onV2Error(String code, String msg) {
                getView().onError(code, msg, StreetType);
              }
            });
  }

  /**
   * 对应区划的街道--办事指南
   */
  public void getStreetByArea(final String areaCode) {
    dispose(disposableStreet);
    disposableStreet = (SearchManager.instance().isUseBaseApi() ? MoreBizBase.getStreetByArea(
        new StreetParam(areaCode)) : MoreBiz.getStreetByArea(new StreetParam(areaCode)))
        .subscribe(new Consumer<List<DeptBean>>() {
          @Override
          public void accept(List<DeptBean> beans) {
            getView().getStreetByAreaData(beans);
          }
        }, new BaseRespThrowableObserver() {
          @Override
          public void onV2Error(String code, String msg) {
            getView().onError(code, msg, DeptType);
          }
        });
  }

  @Override
  public void onMvpDetachView(boolean retainInstance) {
    dispose(disposableAreaList);
    dispose(disposableDept);
    dispose(disposableStreet);

    super.onMvpDetachView(retainInstance);
  }
}
