package com.pasc.business.search;

import android.app.Application;
import android.content.Context;

import com.pasc.business.search.common.net.CommonBiz;
import com.pasc.business.search.common.net.CommonBizBase;
import com.pasc.business.search.home.net.HomeBiz;
import com.pasc.business.search.home.net.HomeBizBase;
import com.pasc.business.search.router.Table;
import com.pasc.lib.search.ApiGet;
import com.pasc.lib.search.DefaultApi;
import com.pasc.lib.search.IType;
import com.pasc.lib.search.LocalSearchManager;
import com.pasc.lib.search.util.SearchUtil;
import com.pasc.lib.search.util.SharePreUtil;

/**
 * @author yangzijian
 * @date 2019/2/17
 * @des
 * @modify
 **/
public class SearchManager {

  private static final class Single {
    private static SearchManager instance = new SearchManager();
  }

  private SearchManager() {
  }

  public static SearchManager instance() {
    return Single.instance;
  }

  private Application application;
  private ApiGet apiGet = new DefaultApi();
  private boolean isShenZhen = false;
  private boolean isDebug = true;
  private boolean matchPinyin = false;
  private boolean showVoice;
  private boolean useBaseApi;
  private boolean hideNetworkSearch;
  private boolean isEnglishParams;

  private boolean needEnterprise = true;
  private boolean needUnion = true;

  public SearchManager setShowVoice(boolean showVoice) {
    this.showVoice = showVoice;
    return this;
  }

  public boolean isShowVoice() {
    return showVoice;
  }

  public boolean isNeedEnterprise() {
    return needEnterprise;
  }

  public void setNeedEnterprise(boolean needEnterprise) {
    this.needEnterprise = needEnterprise;
  }

  public boolean isNeedUnion() {
    return needUnion;
  }

  public void setNeedUnion(boolean needUnion) {
    this.needUnion = needUnion;
  }

  public SearchManager setUseBaseApi(boolean useBaseApi) {
    this.useBaseApi = useBaseApi;
    return this;
  }

  public boolean isEnglishParams() {
    return isEnglishParams;
  }

  public SearchManager setEnglishParams(boolean englishParams) {
    isEnglishParams = englishParams;
    return this;
  }

  public boolean isDebug() {
    return isDebug;
  }

  public boolean isMatchPinyin() {
    return matchPinyin;
  }

  public boolean isShenZhen() {
    return isShenZhen;
  }

  public boolean isUseBaseApi() {
    return useBaseApi;
  }

  public ApiGet getApi() {
    return apiGet;
  }

  public Application getApp() {
    return application;
  }

  public boolean isHideNetworkSearch() {
    return hideNetworkSearch;
  }

  public void setHideNetworkSearch(boolean hideNetworkSearch) {
    this.hideNetworkSearch = hideNetworkSearch;
  }

  /****
   * 初始化搜索资源
   * @param application
   */
  public SearchManager initSearch(Application application, ApiGet apiGet, String appVersion,
      boolean isDebug) {
    return initSearch(application, null, apiGet, appVersion, isDebug);
  }

  public SearchManager initSearch(Application application, ApiGet apiGet, String appVersion,
      boolean isDebug, boolean useBaseApi) {
    return initSearch(application, null, apiGet, appVersion, isDebug, useBaseApi);
  }

  /**** 比较特殊的 dataBase 初始化需要特定 dbContext, 而不是Application***/
  public SearchManager initSearch(Application application, Context dbContext, ApiGet apiGet,
      String appVersion, boolean isDebug, boolean useBaseApi) {
    LocalSearchManager.instance()
        .application(application)
        .dbContext(dbContext)
        .apiGet(apiGet)
        .type(new IType() {
          @Override
          public int getItemTypeFromType(String searchType) {
            return ItemType.getItemTypeFromType(searchType);
          }
        })
        .appVersion(appVersion)
        .debug(isDebug).init();
    this.useBaseApi = useBaseApi;
    this.application = application;
    this.isDebug = isDebug;
    if (apiGet != null) {
      this.apiGet = apiGet;
    }
    if (SearchUtil.isEmpty(appVersion)) {
      appVersion = "2.0.1";
    }

    initPersonLocalData(appVersion);
    if (needEnterprise){
      initEnterpriseLocalData(appVersion);
    }
    initSearchHints();
    registerItemConvert();
    return this;
  }

  public SearchManager initSearch(Application application, Context dbContext, ApiGet apiGet,
      String appVersion, boolean isDebug) {
    initSearch(application, dbContext, apiGet, appVersion, isDebug, false);
    return this;
  }

  public SearchManager isShenZhen(boolean isShenZhen) {
    this.isShenZhen = isShenZhen;
    return this;
  }

  public SearchManager matchPinyin(boolean matchPinyin) {
    LocalSearchManager.instance().matchPinyin(matchPinyin);
    this.matchPinyin = matchPinyin;
    return this;
  }

  /**
   * 个人版数据
   */
  public void initPersonLocalData(String appVersion) {
    String key = Table.Prefer.pre_type_key + "_" + Table.Value.EntranceLocation.person_type;
    key = SharePreUtil.getString(key, "1");
    if (SearchManager.instance().useBaseApi) {
      HomeBizBase.insertServiceLocalData(Table.Value.EntranceLocation.person_type, appVersion, key);
      if (needUnion){
        HomeBizBase.insertUnionLocalData();
      }

    } else {
      HomeBiz.insertServiceLocalData(Table.Value.EntranceLocation.person_type, appVersion, key);
      if (needUnion){
        HomeBiz.insertUnionLocalData();
      }

    }
  }

  /**
   * 企业版数据
   */
  public void initEnterpriseLocalData(String appVersion) {
    String key = Table.Prefer.pre_type_key + "_" + Table.Value.EntranceLocation.enterprise_type;
    key = SharePreUtil.getString(key, "1");
    if (SearchManager.instance().useBaseApi) {
      HomeBizBase.insertServiceLocalData(Table.Value.EntranceLocation.enterprise_type, appVersion,
          key);
    } else {
      HomeBiz.insertServiceLocalData(Table.Value.EntranceLocation.enterprise_type, appVersion, key);
    }
  }

  // 注册布局管理器
  void registerItemConvert() {
    ItemType.init();
  }

  void initSearchHints() {
    if (SearchManager.instance().isUseBaseApi()) {
      CommonBizBase.getAllSearchHint();
    } else {
      CommonBiz.getAllSearchHint();
    }
  }

  // 获取热门关键词
  public io.reactivex.Single<String> getSearchHint(String idType) {
    return SearchManager.instance().isUseBaseApi() ? CommonBizBase.getSearchHint(idType)
        : CommonBiz.getSearchHint(idType);
  }
}
