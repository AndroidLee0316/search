package com.pasc.business.search.common.net;

import com.pasc.business.search.common.model.SearchHotBean;
import com.pasc.business.search.common.model.SearchThemeBean;
import com.pasc.business.search.common.param.EntranceParam;
import com.pasc.business.search.common.param.HotParam;
import com.pasc.lib.net.param.BaseParam;
import com.pasc.lib.net.resp.BaseV2Resp;
import com.pasc.lib.net.resp.VoidObject;
import com.pasc.lib.search.db.history.SearchInfoBean;

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
public interface CommonApi {


    /***
     *
     * 主键id  企业版首页：business_home_page  个人版首页：personal_home_page 个人版生活页：personal_life_page  个人版更多服务页：personal_more_service_page
     *
     */
    /****App查询搜索入口配置详情 , 返回 提示文案和 热门搜索词******/
    @FormUrlEncoded
    @POST("smtapp/searchEntrance/findByIdForApp.do")
    Single<BaseV2Resp<SearchHotBean>> searchHot(@Field("jsonData") BaseParam<HotParam> param);


    /****App查询搜索主题 ,返回指定搜索类型******/
    @FormUrlEncoded
    @POST("smtapp/searchTheme/findListByCnd.do")
    Single<BaseV2Resp<List<SearchThemeBean>>> searchTheme(@Field("jsonData") BaseParam<EntranceParam> param);

    /**
     * 热门提示词
     * @param param
     * @return
     */
    @FormUrlEncoded
    @POST("smtapp/searchEntrance/findListForApp.do")
    Single<BaseV2Resp<List<SearchInfoBean>>> searchHint(@Field("jsonData") BaseParam<VoidObject> param);
}
