package com.pasc.business.search.home.net;

import com.pasc.business.search.home.model.local.ServiceResponse;
import com.pasc.business.search.home.model.local.UnionBean;
import com.pasc.business.search.home.model.param.AppStartParam;
import com.pasc.business.search.home.model.param.NetQueryParam;
import com.pasc.business.search.home.model.param.SearchParam;
import com.pasc.business.search.home.model.search.NetQueryResponse;
import com.pasc.business.search.home.model.search.SearchResponse;
import com.pasc.lib.net.resp.BaseV2Resp;
import com.pasc.lib.net.resp.VoidObject;
import io.reactivex.Single;
import java.util.List;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author yangzijian
 * @date 2019/3/4
 * @des
 * @modify
 **/
public interface HomeApiBase {

  /*****客户端网络搜索--部门服务&办事指南&政策法规****/
  @POST("api/app/search/appNetworkSearch.do")
  Single<BaseV2Resp<SearchResponse>> appNetworkSearch(
      @Body SearchParam param);

  @POST("api/app/appNetwork/multipleSearch")
  Single<BaseV2Resp<SearchResponse>> appNetworkSearchVoice(
      @Body SearchParam param);

  @POST("api/app/serviceIncrement/findServiceList.do")
  Single<BaseV2Resp<ServiceResponse>> serviceSearchCache(
      @Body AppStartParam param);

  @POST("api/app/serviceIncrement/findUnionList.do")
  Single<BaseV2Resp<List<UnionBean>>> unionSearchCache(@Body VoidObject object);

  /**
   * 新版网络搜索，统一API
   * @param param
   * @return
   */
  @POST("api/app/search/netQuery")
  Single<BaseV2Resp<NetQueryResponse>> appNetQuery(
          @Body NetQueryParam param);
}
