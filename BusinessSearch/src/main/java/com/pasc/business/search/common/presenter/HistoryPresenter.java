package com.pasc.business.search.common.presenter;

import com.pasc.business.search.SearchManager;
import com.pasc.business.search.common.net.CommonBiz;
import com.pasc.business.search.common.net.CommonBizBase;
import com.pasc.business.search.common.view.HistoryView;
import com.pasc.lib.search.base.WrapperPresenter;
import com.pasc.lib.search.db.history.HistoryBean;
import com.pasc.lib.search.util.SearchUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author yangzijian
 * @date 2019/2/27
 * @des
 * @modify
 **/
public class HistoryPresenter extends WrapperPresenter<HistoryView> {
  private CompositeDisposable compositeDisposable = new CompositeDisposable();

  public void saveKeyword(final String keyword, final String type) {
    if (!SearchUtil.isEmpty(keyword)) {
      Disposable disposable =
          (SearchManager.instance().isUseBaseApi() ? CommonBizBase.saveKeyword(keyword, type)
              : CommonBiz.saveKeyword(keyword, type))
              .subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                  loadHistory(type);
                }
              }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {

                }
              });
      compositeDisposable.add(disposable);
    }
  }

  public void clearHistory(final String type) {
    Disposable disposable =
        (SearchManager.instance().isUseBaseApi() ? CommonBizBase.clearHistoryBeans(type)
            : CommonBiz.clearHistoryBeans(type)).subscribe(new Consumer<Boolean>() {
          @Override
          public void accept(Boolean aBoolean) throws Exception {
            getView().historyData(new ArrayList<HistoryBean>());
            loadHistory(type);
          }
        }, new Consumer<Throwable>() {
          @Override
          public void accept(Throwable throwable) throws Exception {

          }
        });
    compositeDisposable.add(disposable);
  }

  public void loadHistory(String type) {
    Disposable disposable =
        (SearchManager.instance().isUseBaseApi() ? CommonBizBase.getHistoryBeans(type)
            : CommonBiz.getHistoryBeans(type)).subscribe(new Consumer<List<HistoryBean>>() {
          @Override
          public void accept(List<HistoryBean> beans) throws Exception {
            getView().historyData(beans);
          }
        }, new Consumer<Throwable>() {
          @Override
          public void accept(Throwable throwable) throws Exception {

          }
        });
    compositeDisposable.add(disposable);
  }

  @Override
  public void onMvpDetachView(boolean retainInstance) {
    compositeDisposable.clear();
    super.onMvpDetachView(retainInstance);
  }
}
