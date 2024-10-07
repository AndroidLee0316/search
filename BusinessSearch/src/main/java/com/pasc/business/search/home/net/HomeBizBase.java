package com.pasc.business.search.home.net;

import com.pasc.business.search.ItemType;
import com.pasc.business.search.home.model.local.ServiceBean;
import com.pasc.business.search.home.model.local.ServiceResponse;
import com.pasc.business.search.home.model.local.UnionBean;
import com.pasc.business.search.home.model.param.AppStartParam;
import com.pasc.business.search.home.model.param.NetQueryParam;
import com.pasc.business.search.home.model.param.SearchParam;
import com.pasc.business.search.home.model.search.NetQueryResponse;
import com.pasc.business.search.home.model.search.SearchResponse;
import com.pasc.business.search.router.Table;
import com.pasc.lib.net.ApiGenerator;
import com.pasc.lib.net.param.BaseParam;
import com.pasc.lib.net.resp.BaseV2Resp;
import com.pasc.lib.net.resp.VoidObject;
import com.pasc.lib.net.transform.RespV2Transformer;
import com.pasc.lib.search.db.SearchBiz;
import com.pasc.lib.search.db.SearchSourceDB;
import com.pasc.lib.search.db.SearchSourceItem;
import com.pasc.lib.search.db.SearchSourceItem_Table;
import com.pasc.lib.search.util.LogUtil;
import com.pasc.lib.search.util.SharePreUtil;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yangzijian
 * @date 2019/2/19
 * @des
 * @modify
 **/
public class HomeBizBase {

  public static void insertServiceLocalData(final String entranceLocation, final String appVersion,
      final String serviceVersion) {

    SearchBiz.queryCount(ItemType.personal_server, entranceLocation)
        .flatMap(new Function<Long, SingleSource<ServiceResponse>>() {
          @Override
          public SingleSource<ServiceResponse> apply(Long aLong) throws Exception {
            String mServiceVersion = serviceVersion;
            if (aLong == 0) {
              mServiceVersion = "1";
            }
            return appStartServiceCache(entranceLocation, appVersion, mServiceVersion);
          }
        })
        .observeOn(Schedulers.io())
        .map(new Function<ServiceResponse, List<SearchSourceItem>>() {
          @Override
          public List<SearchSourceItem> apply(ServiceResponse serviceResponse) throws Exception {
            SharePreUtil.setString(Table.Prefer.pre_type_key + "_" + entranceLocation,
                serviceResponse.versionNo);
            List<SearchSourceItem> sourceItems = new ArrayList<>();
            if (serviceResponse != null && serviceResponse.serviceList != null) {

              if (serviceResponse.serviceList.size() > 0) {
                //先删数据
                FlowManager.getDatabase(SearchSourceDB.class)
                    .executeTransaction(new ITransaction() {
                      @Override
                      public void execute(DatabaseWrapper databaseWrapper) {
                        SQLite.delete(SearchSourceItem.class)
                            .where(SearchSourceItem_Table.type.eq(ItemType.personal_server))
                            .and(SearchSourceItem_Table.entranceLocation.eq(entranceLocation))
                            .execute();
                      }
                    });
                for (ServiceBean serviceBean : serviceResponse.serviceList) {
                  sourceItems.add(serviceBean.convert(entranceLocation));
                }
              }
            }

            return sourceItems;
          }
        })
        .flatMap(new Function<List<SearchSourceItem>, SingleSource<Integer>>() {
          @Override
          public SingleSource<Integer> apply(List<SearchSourceItem> sourceItems) throws Exception {
            // 在插入数据
            return SearchBiz.addSources(sourceItems);
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Integer>() {
          @Override
          public void accept(Integer integer) throws Exception {
            LogUtil.log("插入数据 服务 " + integer.intValue() + "条");
          }
        }, new Consumer<Throwable>() {
          @Override
          public void accept(Throwable throwable) throws Exception {
            LogUtil.log("插入数据 服务 " + throwable.getMessage());
          }
        });
  }

  public static void insertUnionLocalData() {
    //先删除之前的数据,每次都更新
    SearchBiz.deleteAll(ItemType.personal_zhengqi_number, Table.Value.EntranceLocation.person_type)
        .flatMap(new Function<Integer, SingleSource<List<UnionBean>>>() {
          @Override
          public SingleSource<List<UnionBean>> apply(Integer integer) throws Exception {
            return appStartUnionCache();
          }
        })
        .observeOn(Schedulers.io())
        .map(new Function<List<UnionBean>, List<SearchSourceItem>>() {
          @Override
          public List<SearchSourceItem> apply(List<UnionBean> unionBeans) throws Exception {

            List<SearchSourceItem> sourceItems = new ArrayList<>();
            if (unionBeans != null) {
              for (UnionBean unionBean : unionBeans) {
                sourceItems.add(unionBean.convert());
              }
            }

            return sourceItems;
          }
        })
        .flatMap(new Function<List<SearchSourceItem>, SingleSource<Integer>>() {
          @Override
          public SingleSource<Integer> apply(List<SearchSourceItem> sourceItems) throws Exception {
            return SearchBiz.addSources(sourceItems);
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Integer>() {
          @Override
          public void accept(Integer integer) throws Exception {
            LogUtil.log("插入数据 证企号 " + integer.intValue() + "条");
          }
        }, new Consumer<Throwable>() {
          @Override
          public void accept(Throwable throwable) throws Exception {
            LogUtil.log("插入数据 证企号 " + throwable.getMessage());
          }
        });
  }

  public static Single<ServiceResponse> appStartServiceCache(String entranceLocation,
      String appVersion, String serviceVersion) {
    return ApiGenerator.createApi(HomeApiBase.class)
        .serviceSearchCache(AppStartParam.instance(entranceLocation, appVersion, serviceVersion))
        .subscribeOn(Schedulers.io())
        .compose(RespV2Transformer.<ServiceResponse>newInstance())
        .observeOn(AndroidSchedulers.mainThread());
  }

  public static Single<List<UnionBean>> appStartUnionCache() {
    return ApiGenerator.createApi(HomeApiBase.class).unionSearchCache(VoidObject.getInstance())
        .subscribeOn(Schedulers.io())
        .compose(RespV2Transformer.<List<UnionBean>>newInstance())
        .observeOn(AndroidSchedulers.mainThread());
  }

  /***
   * 网络搜索
   * @return
   */
  public static Single<SearchResponse> appNetworkSearch(SearchParam searchParam) {
    Single<BaseV2Resp<SearchResponse>> single = null;
    //        if (ItemType.VoiceSearchType.equals (searchParam.source)) {
    //            single=  ApiGenerator.createApi (HomeApiBase.class).appNetworkSearchVoice (new BaseParam<SearchParam> (searchParam));
    //        } else {
    single = ApiGenerator.createApi(HomeApiBase.class).appNetworkSearch(searchParam);
    //        }
    return single
        .subscribeOn(Schedulers.io())
        .compose(RespV2Transformer.<SearchResponse>newInstance())
        .observeOn(AndroidSchedulers.mainThread());
  }

    /***
     * 网络搜索
     * @return
     */
    public static Single<NetQueryResponse> appNetQuery(NetQueryParam searchParam) {
        Single<BaseV2Resp<NetQueryResponse>> single = null;
        single = ApiGenerator.createApi(HomeApiBase.class).appNetQuery(searchParam);
        return single
                .subscribeOn(Schedulers.io())
                .compose(RespV2Transformer.<NetQueryResponse>newInstance())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
