package com.pasc.business.search.home.net;

import com.pasc.business.search.home.model.local.ServiceResponse;
import com.pasc.business.search.home.model.local.UnionBean;
import com.pasc.business.search.home.model.param.AppStartParam;
import com.pasc.business.search.home.model.param.SearchParam;
import com.pasc.business.search.home.model.search.SearchResponse;
import com.pasc.lib.net.param.BaseParam;
import com.pasc.lib.net.resp.BaseV2Resp;
import com.pasc.lib.net.resp.VoidObject;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @author yangzijian
 * @date 2019/3/4
 * @des
 * @modify
 **/
public interface HomeApi {

    /*****客户端网络搜索--部门服务&办事指南&政策法规****/
    @FormUrlEncoded
    @POST("smtapp/search/appNetworkSearch.do")
    Single<BaseV2Resp<SearchResponse>> appNetworkSearch(@Field("jsonData") BaseParam<SearchParam> param);

    @FormUrlEncoded
    @POST("smtapp/appNetwork/multipleSearch")
    Single<BaseV2Resp<SearchResponse>> appNetworkSearchVoice(@Field("jsonData") BaseParam<SearchParam> param);

    @FormUrlEncoded
    @POST("smtapp/serviceIncrement/findServiceList.do")
    Single<BaseV2Resp<ServiceResponse>> serviceSearchCache(@Field("jsonData") BaseParam<AppStartParam> param);

    @FormUrlEncoded
    @POST("smtapp/serviceIncrement/findUnionList.do")
    Single<BaseV2Resp<List<UnionBean>>> unionSearchCache(@Field("jsonData") BaseParam<VoidObject> param);
}
