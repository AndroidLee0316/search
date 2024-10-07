package com.pasc.business.search.more.net;

import com.pasc.business.search.more.model.param.task.DeptParam;
import com.pasc.business.search.more.model.param.task.StreetParam;
import com.pasc.business.search.more.model.policy.UnitSearchBean;
import com.pasc.business.search.more.model.task.AreaBean;
import com.pasc.business.search.more.model.task.DeptBean;
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
public interface MoreApi {

    /*****************************办事指南*******************************************/

    /****行政区划列表--办事指南***/
    @FormUrlEncoded
    @POST("smtapp/gdzwfw/getAreaList.do")
    Single<BaseV2Resp<List<AreaBean>>> getAreaList(@Field("jsonData") BaseParam<VoidObject> param);

    /****对应区划的部门--办事指南*****/
    @FormUrlEncoded
    @POST("smtapp/gdzwfw/getDeptByArea.do")
    Single<BaseV2Resp<List<DeptBean>>> getDeptByArea(@Field("jsonData") BaseParam<DeptParam> param);


    /***获取部区下的街道***/
    @FormUrlEncoded
    @POST("smtapp/gdzwfw/getStreetByArea.do")
    Single<BaseV2Resp<List<DeptBean>>> getStreetByArea(@Field("jsonData") BaseParam<StreetParam> param);
    /*****************************办事指南*******************************************/


    /*****************************政策法规*******************************************/
    /*****发文单位搜索--政策法规***/
    @FormUrlEncoded
    @POST("smtapp/policy/unitSearch.do")
    Single<BaseV2Resp<UnitSearchBean>> unitSearch(@Field("jsonData") BaseParam<VoidObject> param);


    /*****************************政策法规*******************************************/

}
