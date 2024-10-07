package com.pasc.business.search.common.presenter;

import android.text.TextUtils;

import com.pasc.business.search.SearchManager;
import com.pasc.business.search.common.model.HotBean;
import com.pasc.business.search.common.model.SearchHotBean;
import com.pasc.business.search.common.model.SearchThemeBean;
import com.pasc.business.search.common.net.CommonBiz;
import com.pasc.business.search.common.net.CommonBizBase;
import com.pasc.business.search.common.view.HotView;
import com.pasc.lib.search.base.WrapperPresenter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author yangzijian
 * @date 2019/3/5
 * @des
 * @modify
 **/
public class HotWordPresenter extends WrapperPresenter<HotView> {
  private CompositeDisposable compositeDisposable = new CompositeDisposable();

  public void loadHotWord(String id) {
    Disposable disposable = CommonBizBase.getSearchHot(id).subscribe(new Consumer<String>() {
      @Override
      public void accept(String searchHot) throws Exception {

        List<HotBean> hotBeans = new ArrayList<HotBean>();

        if(!TextUtils.isEmpty(searchHot)){
          String[] hots = searchHot.split(";");
          for(String hotItem:hots){
              HotBean hotBean = new HotBean(hotItem,"");
              hotBeans.add(hotBean);
          }
        }

        if (hotBeans.size() >= 6) {
          // 最多三行两列
          hotBeans = hotBeans.subList(0, 6);
        }
        getView().hotData(hotBeans);
      }
    }, new Consumer<Throwable>() {
      @Override
      public void accept(Throwable throwable) throws Exception {

      }
    });
    compositeDisposable.add(disposable);
  }

  public void loadHintText(String id) {
    Disposable disposable =
        (SearchManager.instance().isUseBaseApi() ? CommonBizBase.getSearchHint(id)
            : CommonBiz.getSearchHint(id)).subscribe(new Consumer<String>() {
          @Override
          public void accept(String searchText) throws Exception {
            getView().hintText(searchText);
          }
        }, new Consumer<Throwable>() {
          @Override
          public void accept(Throwable throwable) throws Exception {

          }
        });
    compositeDisposable.add(disposable);
  }

  public void searchTheme(String entranceLocation, String id) {
    Disposable disposable =
        (SearchManager.instance().isUseBaseApi() ? CommonBizBase.searchTheme(entranceLocation,id)
            : CommonBiz.searchTheme(entranceLocation)).subscribe(
            new Consumer<List<SearchThemeBean>>() {
              @Override
              public void accept(List<SearchThemeBean> searchThemeBeans) throws Exception {
                getView().themeData(searchThemeBeans);
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
