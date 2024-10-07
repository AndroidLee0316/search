package com.pasc.business.search.home.presenter;

import android.text.TextUtils;
import com.pasc.business.search.ItemType;
import com.pasc.business.search.home.model.param.NetQueryParam;
import com.pasc.business.search.home.model.search.NetQueryBean;
import com.pasc.business.search.home.model.search.NetQueryResponse;
import com.pasc.business.search.home.net.HomeBizBase;
import com.pasc.business.search.home.view.SearchHomeView;
import com.pasc.business.search.router.Table;
import com.pasc.lib.net.resp.BaseRespThrowableObserver;
import com.pasc.lib.search.ISearchGroup;
import com.pasc.lib.search.SearchSourceGroup;
import com.pasc.lib.search.base.WrapperPresenter;
import com.pasc.lib.search.db.SearchBiz;
import com.pasc.lib.search.db.SearchSourceItem;
import com.pasc.lib.search.util.SearchUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yangzijian
 * @date 2019/2/18
 * @des
 * @modify
 **/
public class SearchHomePresenter extends WrapperPresenter<SearchHomeView> {
  private CompositeDisposable compositeDisposable = new CompositeDisposable();

  private void dispose(Disposable disposable) {
    if (disposable != null && !disposable.isDisposed()) {
      disposable.dispose();
      disposable = null;
    }
  }

  Disposable netDisposable;

  public void searchLocal(String keyword, String entranceLocation) {
    if (SearchUtil.isEmpty(keyword)) {
      getView().localData(new ArrayList<SearchSourceGroup>());
      getView().showContentView(false);
      return;
    }
    getView().showContentView(true);

    Disposable disposable = SearchBiz.queryLocal(keyword.trim(), entranceLocation, "")
        .observeOn(Schedulers.io())
        .map(new Function<List<SearchSourceItem>, List<SearchSourceGroup>>() {
          @Override
          public List<SearchSourceGroup> apply(List<SearchSourceItem> sourceItems)
              throws Exception {
            List<SearchSourceGroup> sourceGroups = new ArrayList<>();

            // 分组
            Map<String, List<SearchSourceItem>> mp = new HashMap<>();
            for (SearchSourceItem item : sourceItems) {
              List<SearchSourceItem> items = mp.get(item.type);
              if (items != null) {
                items.add(item);
              } else {
                List<SearchSourceItem> itemList = new ArrayList<>();
                itemList.add(item);
                mp.put(item.type, itemList);
              }
            }
            //排序
            for (Map.Entry<String, List<SearchSourceItem>> entry : mp.entrySet()) {
              SearchSourceGroup<SearchSourceItem> sourceGroup = new SearchSourceGroup<>();
              String type = entry.getKey();
              sourceGroup.priority = ItemType.getPriorityFromType(type); // 根据type 获取排序优先级
              sourceGroup.groupName = ItemType.getGroupNameFromType(type); // 根据type 获取组名
              sourceGroup.data = entry.getValue();
              sourceGroup.type = type;
              sourceGroup.extraData.put(Table.Key.key_themeConfigId,Table.Value.ThemeId.local);
              sourceGroups.add(sourceGroup);
            }

            Collections.sort(sourceGroups, new Comparator<SearchSourceGroup>() {
              @Override
              public int compare(SearchSourceGroup o1, SearchSourceGroup o2) {
                return o1.priority - o2.priority;
              }
            });

            return sourceGroups;
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<SearchSourceGroup>>() {
          @Override
          public void accept(List<SearchSourceGroup> sourceItems) throws Exception {
            for (int i = 0; i < sourceItems.size(); i++) {
              sourceItems.get(i).optimizeData(sourceItems.size());
            }
            getView().localData(sourceItems);
          }
        }, new Consumer<Throwable>() {
          @Override
          public void accept(Throwable throwable) throws Exception {
            System.out.println();
          }
        });
    compositeDisposable.add(disposable);
  }

//  public void searchNet(String keyword, final String entranceLocation, final String source) {
//    SearchParam searchParam = SearchParam.allParam(source, keyword, entranceLocation);
//    dispose(netDisposable);
//    netDisposable = (SearchManager.instance().isUseBaseApi() ? HomeBizBase.appNetworkSearch(
//        searchParam) : HomeBiz.appNetworkSearch(searchParam))
//        .observeOn(Schedulers.io())
//        .map(new Function<SearchResponse, List<SearchSourceGroup>>() {
//          @Override
//          public List<SearchSourceGroup> apply(SearchResponse searchResponse) throws Exception {
//            List<SearchSourceGroup> sourceGroups = new ArrayList<>();
//
//            //服务
//            if (searchResponse.servicePoolData != null) {
//              List<ServiceBean> list = searchResponse.servicePoolData.list;
//              if (list != null && list.size() > 0) {
//                SearchSourceGroup<SearchSourceItem> sourceGroup = new SearchSourceGroup<>();
//                sourceGroup.extraData.put(ItemType.SearchContentTypeKey, source);
//                String type = ItemType.personal_server;
//                List<SearchSourceItem> data = new ArrayList<>();
//                for (ServiceBean bean : list) {
//                  data.add(bean.convert(entranceLocation));
//                }
//                sourceGroup.groupName = ItemType.getGroupNameFromType(type);
//                sourceGroup.type = type;
//                sourceGroup.data = data;
//                sourceGroup.priority = ItemType.getPriorityFromType(type);
//                sourceGroups.add(sourceGroup);
//              }
//            }
//
//            //服务部门 / 证企号
//            if (searchResponse.serviceDepartmentData != null) {
//              List<UnionBean> list = searchResponse.serviceDepartmentData.list;
//              if (list != null && list.size() > 0) {
//                SearchSourceGroup<SearchSourceItem> sourceGroup = new SearchSourceGroup<>();
//                sourceGroup.extraData.put(ItemType.SearchContentTypeKey, source);
//
//                String type = ItemType.personal_zhengqi_number;
//                List<SearchSourceItem> data = new ArrayList<>();
//                for (UnionBean unionBean : list) {
//                  data.add(unionBean.convert());
//                }
//                sourceGroup.data = data;
//                sourceGroup.groupName = ItemType.getGroupNameFromType(type);
//                sourceGroup.type = type;
//                sourceGroup.priority = ItemType.getPriorityFromType(type);
//                sourceGroups.add(sourceGroup);
//              }
//            }
//
//            //证企号服务
//            if (searchResponse.unionServiceData != null) {
//              List<UnionServiceBean> list = searchResponse.unionServiceData.list;
//              if (list != null && list.size() > 0) {
//                SearchSourceGroup<UnionServiceBean> sourceGroup = new SearchSourceGroup<>();
//                sourceGroup.extraData.put(ItemType.SearchContentTypeKey, source);
//
//                String type = list.get(0).searchType();
//                sourceGroup.data = list;
//                sourceGroup.groupName = ItemType.getGroupNameFromType(type);
//                sourceGroup.type = type;
//                sourceGroup.priority = ItemType.getPriorityFromType(type);
//                sourceGroups.add(sourceGroup);
//              }
//            }
//            //办事指南
//            if (searchResponse.serviceGuideData != null) {
//              List<ServiceGuideDataBean> list = searchResponse.serviceGuideData.list;
//              if (list != null && list.size() > 0) {
//                SearchSourceGroup<ServiceGuideDataBean> sourceGroup = new SearchSourceGroup<>();
//                sourceGroup.extraData.put(ItemType.SearchContentTypeKey, source);
//
//                String type = list.get(0).searchType();
//                sourceGroup.data = list;
//                sourceGroup.groupName = ItemType.getGroupNameFromType(type);
//                sourceGroup.type = type;
//                sourceGroup.priority = ItemType.getPriorityFromType(type);
//                sourceGroups.add(sourceGroup);
//              }
//            }
//            //政策法规
//            if (searchResponse.policyData != null) {
//              List<PolicyDataBean> list = searchResponse.policyData.list;
//              if (list != null && list.size() > 0) {
//                SearchSourceGroup<PolicyDataBean> sourceGroup = new SearchSourceGroup<>();
//                sourceGroup.extraData.put(ItemType.SearchContentTypeKey, source);
//
//                String type = list.get(0).searchType();
//                sourceGroup.data = list;
//                sourceGroup.groupName = ItemType.getGroupNameFromType(type);
//                sourceGroup.type = type;
//                sourceGroup.priority = ItemType.getPriorityFromType(type);
//                sourceGroups.add(sourceGroup);
//              }
//            }
//
//            return sourceGroups;
//          }
//        })
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe(new Consumer<List<SearchSourceGroup>>() {
//          @Override
//          public void accept(List<SearchSourceGroup> searchResponse) throws Exception {
//            getView().netData(searchResponse);
//          }
//        }, new BaseRespThrowableObserver() {
//          @Override
//          public void onV2Error(String code, String msg) {
//            getView().netError(code, msg);
//          }
//        });
//  }

  public void searchNet(final List<ISearchGroup> searchItems, String keyword, final String entranceLocation, final String entranceId, final String source) {
    NetQueryParam queryParam = new NetQueryParam();
    queryParam.entranceLocation = entranceLocation;
    queryParam.searchWord = keyword;
    queryParam.entranceId = entranceId;
    dispose(netDisposable);
    netDisposable = HomeBizBase.appNetQuery(
            queryParam)
            .observeOn(Schedulers.io())
            .map(new Function<NetQueryResponse, List<SearchSourceGroup>>() {
              @Override
              public List<SearchSourceGroup> apply(NetQueryResponse searchResponse) throws Exception {
                List<SearchSourceGroup> sourceGroups = new ArrayList<>();
                if(searchResponse.analyzers != null){
                  getView().setAnalyzers(searchResponse.analyzers);
                }
                if (searchResponse.dataList == null) {
                  return sourceGroups;
                }
                for(NetQueryResponse.DataList dataList:searchResponse.dataList){
                  if(dataList.list != null && dataList.list.size() > 0) {
                    SearchSourceGroup<NetQueryBean> sourceGroup = new SearchSourceGroup<>();
                    sourceGroup.groupName = dataList.themeName;
                    sourceGroup.data = dataList.list;
                    sourceGroup.extraData.put(Table.Key.key_themeConfigId,dataList.themeConfigId);
                    sourceGroup.extraData.put(Table.Key.key_totalSize,String.valueOf(dataList.totalSize));
                    boolean hasIcon = false;
                    for(NetQueryBean bean:dataList.list){
                      if(!TextUtils.isEmpty(bean.logo)){
                        hasIcon = true;
                        break;
                      }
                    }
                    if(hasIcon) {
                      sourceGroup.type = ItemType.personal_server;
                    }else{
                      sourceGroup.type = ItemType.personal_policy_rule;
                    }
                    sourceGroups.add(sourceGroup);
                  }
                }
                //手动输入搜索，将本地数据加入到网络数据中
                if (ItemType.ManualSearchType.equals(source)) {
                  for (ISearchGroup item : searchItems) {
                    sourceGroups.add((SearchSourceGroup) item);
                  }
                }

                for (int i = 0; i < sourceGroups.size(); i++) {
                  sourceGroups.get(i).optimizeData(sourceGroups.size());
                }

                return sourceGroups;
              }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<List<SearchSourceGroup>>() {
              @Override
              public void accept(List<SearchSourceGroup> searchResponse) throws Exception {
                getView().netData(searchResponse);
              }
            }, new BaseRespThrowableObserver() {
              @Override
              public void onV2Error(String code, String msg) {
                getView().netError(code, msg);
              }
            });
  }

  @Override
  public void onMvpDetachView(boolean retainInstance) {
    dispose(netDisposable);
    compositeDisposable.clear();
    super.onMvpDetachView(retainInstance);
  }
}
