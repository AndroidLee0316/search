package com.pasc.business.search.more.presenter;

import android.text.TextUtils;
import com.pasc.business.search.ItemType;
import com.pasc.business.search.SearchManager;
import com.pasc.business.search.home.model.param.NetQueryParam;
import com.pasc.business.search.home.model.param.SearchParam;
import com.pasc.business.search.home.model.search.NetQueryBean;
import com.pasc.business.search.home.model.search.NetQueryResponse;
import com.pasc.business.search.home.net.HomeBizBase;
import com.pasc.business.search.more.model.TotalCountResponse;
import com.pasc.business.search.more.net.MoreBiz;
import com.pasc.business.search.more.net.MoreBizBase;
import com.pasc.business.search.more.view.MoreSearchView;
import com.pasc.lib.net.resp.BaseRespThrowableObserver;
import com.pasc.lib.search.ISearchItem;
import com.pasc.lib.search.base.WrapperPresenter;
import com.pasc.lib.search.db.SearchBiz;
import com.pasc.lib.search.db.SearchSourceItem;
import com.pasc.lib.search.util.LogUtil;
import com.pasc.lib.search.util.SearchUtil;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yangzijian
 * @date 2019/2/21
 * @des
 * @modify
 **/
public class MoreSearchPresenter extends WrapperPresenter<MoreSearchView> {
  Disposable disposableLocal;
  Disposable disposableMore;

  private void dispose(Disposable disposable) {
    if (disposable != null && !disposable.isDisposed()) {
      disposable.dispose();
      disposable = null;
    }
  }

  public void searchLocalByType(String keyword, String entranceLocation, String searchType) {
    if (SearchUtil.isEmpty(keyword)) {
      getView().localData(new ArrayList<ISearchItem>());
      getView().showContentView(false);
      return;
    }
    getView().showContentView(true);
    dispose(disposableLocal);
    disposableLocal = SearchBiz.queryLocal(keyword, entranceLocation, searchType)
        .subscribe(new Consumer<List<SearchSourceItem>>() {
          @Override
          public void accept(List<SearchSourceItem> sourceItems) throws Exception {
            getView().localData(sourceItems);
          }
        }, new Consumer<Throwable>() {
          @Override
          public void accept(Throwable throwable) throws Exception {
            LogUtil.log("searchLocalByType " + throwable.getMessage());
          }
        });
  }

  /*****
   * 证企号服务搜索
   * @param keyWord
   * @param entranceLocation
   * @param unionId
   * @param pageNo
   * @param pageSize
   */
  public void searchUnionService(String source, String keyWord,
      String entranceLocation, String unionId, int pageNo, int pageSize) {
    SearchParam searchParam =
        SearchParam.unionServiceParam(source, keyWord, entranceLocation, unionId, pageNo, pageSize);
    searchMore(searchParam);
  }

  /**
   * 办事指南
   */
  public void searchServiceGuide(String source, String keyWord,
      String entranceLocation, String areaCode, String bureauCode, boolean isOnlineApply,
      int pageNo, int pageSize) {
    SearchParam searchParam =
        SearchParam.serviceGuideParam(source, keyWord, entranceLocation, areaCode, bureauCode,
            isOnlineApply, pageNo, pageSize);
    searchMore(searchParam);
  }

  /**
   * 政策法规
   */
  public void searchPolicy(String source, String keyWord,
      String entranceLocation, String issueUnit, int pageNo, int pageSize) {
    SearchParam searchParam =
        SearchParam.policyParam(source, keyWord, entranceLocation, issueUnit, pageNo, pageSize);
    searchMore(searchParam);
  }

  public void serviceDepartmentParam(String source, String keyWord,
      String entranceLocation, int pageNo, int pageSize) {
    SearchParam searchParam =
        SearchParam.serviceDepartmentParam(source, keyWord, entranceLocation, pageNo, pageSize);
    searchMore(searchParam);
  }

  // 主题搜索
  public void searchTheme(String keyword,
                          String entranceLocation,final String themeConfigId, int pageNo, int pageSize){
    if(themeConfigId == null){
      return;
    }
    getView().showContentView(true);
    NetQueryParam queryParam = new NetQueryParam();
    queryParam.entranceLocation = entranceLocation;
    queryParam.searchWord = keyword;
    queryParam.themeConfigId = themeConfigId;
    queryParam.from = (pageNo-1) * pageSize;
    queryParam.size = pageSize;
    dispose(disposableMore);
    disposableMore = HomeBizBase.appNetQuery(
            queryParam).subscribe(new Consumer<NetQueryResponse>() {
      @Override
      public void accept(NetQueryResponse netQueryResponse) throws Exception {
        if(netQueryResponse.dataList == null){
          getView().netData(new ArrayList<ISearchItem>(), 0);
          return;
        }
        if(netQueryResponse.analyzers != null){
          getView().setAnalyzers(netQueryResponse.analyzers);
        }
        for(NetQueryResponse.DataList dataList:netQueryResponse.dataList){
          if(themeConfigId.equals(dataList.themeConfigId)) {
            if(dataList.list != null && dataList.list.size() > 0 ){

              for(NetQueryBean bean:dataList.list){
                if(!TextUtils.isEmpty(bean.logo)){
                  bean.type = ItemType.personal_server;
                }else{
                  bean.type = ItemType.personal_policy_rule;
                }
              }


              getView().netData(dataList.list, dataList.totalSize);
            }else {
              getView().netData(new ArrayList<ISearchItem>(), 0);
            }
          }
        }

      }
    }, new BaseRespThrowableObserver() {
      @Override
      public void onV2Error(String code, String msg) {
        getView().onSearchError(code, msg);
      }
    });
  }


  public void servicePoolParam(String source, String keyWord,
      String entranceLocation, int pageNo, int pageSize) {
    SearchParam searchParam =
        SearchParam.servicePoolParam(source, keyWord, entranceLocation, pageNo, pageSize);
    searchMore(searchParam);
  }

  private void searchMore(SearchParam searchParam) {
    getView().showContentView(true);

    dispose(disposableMore);

    disposableMore =
        (SearchManager.instance().isUseBaseApi() ? MoreBizBase.searchByCondition(searchParam)
            : MoreBiz.searchByCondition(searchParam)).subscribe(new Consumer<TotalCountResponse>() {
          @Override
          public void accept(TotalCountResponse countResponse) throws Exception {
            getView().netData(countResponse.iSearchItems, countResponse.totalCount);
          }
        }, new BaseRespThrowableObserver() {
          @Override
          public void onV2Error(String code, String msg) {
            getView().onSearchError(code, msg);
          }
        });
  }

  @Override
  public void onMvpDetachView(boolean retainInstance) {
    dispose(disposableLocal);
    dispose(disposableMore);

    super.onMvpDetachView(retainInstance);
  }
}
