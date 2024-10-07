package com.pasc.business.search.common.net;

import com.pasc.business.search.SearchManager;
import com.pasc.business.search.common.model.SearchHotBean;
import com.pasc.business.search.common.model.SearchThemeBean;
import com.pasc.business.search.common.param.EntranceParam;
import com.pasc.business.search.common.param.HotParam;
import com.pasc.business.search.common.param.ThemeParam;
import com.pasc.business.search.router.Table;
import com.pasc.lib.net.ApiGenerator;
import com.pasc.lib.net.resp.BaseV2Resp;
import com.pasc.lib.net.transform.RespV2Transformer;
import com.pasc.lib.search.db.history.HistoryBean;
import com.pasc.lib.search.db.history.HistoryBean_Table;
import com.pasc.lib.search.db.history.SearchInfoBean;
import com.pasc.lib.search.db.history.SearchInfoBean_Table;
import com.pasc.lib.search.util.DeviceUtil;
import com.pasc.lib.search.util.LogUtil;
import com.pasc.lib.search.util.SearchUtil;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yangzijian
 * @date 2019/2/21
 * @des
 * @modify
 **/
public class CommonBizBase {
  public static String getUniqueId(String type) {
    String phone = SearchManager.instance().getApi().getPhone();
    if (SearchUtil.isEmpty(phone)) {
      phone = "";
    }
    // 获取唯一的
    String uniqueId = DeviceUtil.getDeviceId(SearchManager.instance().getApp())
        + phone + type;
    return uniqueId;
  }

  /****
   * 保存历史搜索词
   * @param keyword
   */
  public static Single<Boolean> saveKeyword(final String keyword, final String type) {
    return Single.create(new SingleOnSubscribe<Boolean>() {
      @Override
      public void subscribe(SingleEmitter<Boolean> emitter) throws Exception {
        HistoryBean historyBean = new HistoryBean();
        historyBean.storeType = getUniqueId(type);
        historyBean.keyword = keyword.trim();
        historyBean.date = System.currentTimeMillis();
        long count = SQLite.selectCountOf().from(HistoryBean.class).where(
            HistoryBean_Table.storeType.eq(historyBean.storeType)
        ).and(HistoryBean_Table.keyword.eq(historyBean.keyword)).count();
        if (count > 0) {
          SQLite.delete(HistoryBean.class).where(HistoryBean_Table.keyword.eq(historyBean.keyword))
              .and(HistoryBean_Table.storeType.eq(historyBean.storeType)).execute();
          historyBean.save();
        } else {
          historyBean.insert();
        }
        emitter.onSuccess(true);
      }
    }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  /***
   * 获取历史词
   * @return
   */
  public static Single<List<HistoryBean>> getHistoryBeans(String type) {
    String uniqueId = getUniqueId(type);
    return RXSQLite.rx(SQLite.select()
        .from(HistoryBean.class)
        .where(HistoryBean_Table.storeType.eq(uniqueId))
    )
        .queryList()
        .map(new Function<List<HistoryBean>, List<HistoryBean>>() {
          @Override
          public List<HistoryBean> apply(List<HistoryBean> historyBeans) throws Exception {
            //排序,升序
            Collections.sort(historyBeans, new Comparator<HistoryBean>() {
              @Override
              public int compare(HistoryBean m1, HistoryBean m2) {
                if (m1.date > m2.date) {
                  return -1;
                } else if (m1.date == m2.date) {
                  return 0;
                } else {
                  return 1;
                }
              }
            });
            // 最多5个
            historyBeans = historyBeans.size() >= 5 ? historyBeans.subList(0, 5) : historyBeans;
            return historyBeans;
          }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  public static Single<Boolean> clearHistoryBeans(final String type) {
    return Single.create(new SingleOnSubscribe<Boolean>() {
      @Override
      public void subscribe(SingleEmitter<Boolean> emitter) throws Exception {
        String uniqueId = getUniqueId(type);
        SQLite.delete(HistoryBean.class).where(HistoryBean_Table.storeType.eq(uniqueId)).execute();
        emitter.onSuccess(true);
      }
    }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  /**
   * 获取热门关键词
   */
  public static Single<SearchHotBean> searchHot(String id) {
    RespV2Transformer<SearchHotBean> transformer = RespV2Transformer.newInstance();
    return ApiGenerator.createApi(CommonApiBase.class).searchHot(new HotParam(id))
        .compose(transformer)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  /**
   * 获取搜索主题-即搜索指定内容集合
   *
   * @param entranceLocation 1 和 2
   */
  public static Single<List<SearchThemeBean>> searchTheme(String entranceLocation, String id) {
    RespV2Transformer<List<SearchThemeBean>> transformer = RespV2Transformer.newInstance();
    return ApiGenerator.createApi(CommonApiBase.class)
        .searchThemeById(new ThemeParam(entranceLocation,id))
        .compose(transformer)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  private final static Map<String, String> mapHints = new HashMap<>();

  static {
    //个人版首页
    mapHints.put(Table.Value.HomeType.personal_home_page, "搜服务，如\"预约挂号\"");

    //个人版更多服务页
    mapHints.put(Table.Value.MoreType.personal_more_service_page, "搜服务，如\"预约挂号\"");

    //个人版政企号
    mapHints.put(Table.Value.MoreType.personal_union_page, "请输入部门名称");

    //todo 证企号服务
    mapHints.put(Table.Value.MoreType.personal_zhengqi_server, "请输入事项关键词");

    //个人版办事指南页
    mapHints.put(Table.Value.MoreType.personal_service_guide_page, "请输入事项关键词");
    //个人版办事大厅
    mapHints.put(Table.Value.MoreType.personal_service_hall, "请输入事项关键词");

    //个人版更多政策法规
    mapHints.put(Table.Value.MoreType.personal_policy_page, "请输入想要搜索的内容");

    //企业版
    mapHints.put(Table.Value.HomeType.business_home_page, "搜服务，如\"企业信用\"");
    //企业版办事指南页
    mapHints.put(Table.Value.MoreType.business_service_guide_page, "请输入事项关键词");
  }

  public static Single<String> getSearchHint(final String idType) {
    return Single.create(new SingleOnSubscribe<String>() {
      @Override
      public void subscribe(SingleEmitter<String> emitter) throws Exception {
        SearchInfoBean bean = SQLite.select()
            .from(SearchInfoBean.class)
            .where(SearchInfoBean_Table.id.eq(idType.trim()))
            .querySingle();
        String hintText = "";
        if (bean != null) {
          hintText = bean.searchText;
        } else {
          String hint = mapHints.get(idType);
          if (!SearchUtil.isEmpty(hint)) {
            hintText = hint;
          }
        }
        emitter.onSuccess(hintText);
      }
    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
  }


  public static Single<String> getSearchHot(final String idType) {
    return Single.create(new SingleOnSubscribe<String>() {
      @Override
      public void subscribe(SingleEmitter<String> emitter) throws Exception {
        SearchInfoBean bean = SQLite.select()
                .from(SearchInfoBean.class)
                .where(SearchInfoBean_Table.id.eq(idType.trim()))
                .querySingle();
        String searchHot = "";
        if (bean != null) {
          searchHot = bean.searchHot;
        } else {
          String hint = mapHints.get(idType);
          if (!SearchUtil.isEmpty(hint)) {
            searchHot = hint;
          }
        }
        emitter.onSuccess(searchHot);
      }
    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
  }

  /****
   * 获取所有的缓存文件
   */
  public static void getAllSearchHint() {
    ApiGenerator.createApi(CommonApiBase.class).searchHint(new EntranceParam("1"))
        .subscribeOn(Schedulers.io())
        .map(new Function<BaseV2Resp<List<SearchInfoBean>>, Integer>() {
          @Override
          public Integer apply(BaseV2Resp<List<SearchInfoBean>> listBaseV2Resp) throws Exception {
            for (SearchInfoBean bean : listBaseV2Resp.data) {
              bean.hintId = bean.id.hashCode();
              if (bean.exists()) {
                bean.update();
              } else {
                bean.insert();
              }
            }
            return listBaseV2Resp.data.size();
          }
        })
        .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
      @Override
      public void accept(Integer s) throws Exception {
        LogUtil.log("searchHints update hints " + s.intValue());
      }
    }, new Consumer<Throwable>() {
      @Override
      public void accept(Throwable throwable) throws Exception {
        LogUtil.log("searchHints error " + throwable.getMessage());
      }
    });
  }

  /****
   * 获取入口缓存文件
   */
  public static void updateSearchHint(String entranceLocation,String entranceId ) {
    ApiGenerator.createApi(CommonApiBase.class).searchHint(new EntranceParam(entranceLocation,entranceId))
            .subscribeOn(Schedulers.io())
            .map(new Function<BaseV2Resp<List<SearchInfoBean>>, Integer>() {
              @Override
              public Integer apply(BaseV2Resp<List<SearchInfoBean>> listBaseV2Resp) throws Exception {
                for (SearchInfoBean bean : listBaseV2Resp.data) {
                  bean.hintId = bean.id.hashCode();
                  if (bean.exists()) {
                    bean.update();
                  } else {
                    bean.insert();
                  }
                }
                return listBaseV2Resp.data.size();
              }
            })
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
      @Override
      public void accept(Integer s) throws Exception {
        LogUtil.log("searchHints update hints " + s.intValue());
      }
    }, new Consumer<Throwable>() {
      @Override
      public void accept(Throwable throwable) throws Exception {
        LogUtil.log("searchHints error " + throwable.getMessage());
      }
    });
  }
}
