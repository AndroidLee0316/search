package com.pasc.business.search.more.net;

import com.pasc.business.search.more.model.param.task.DeptParam;
import com.pasc.business.search.more.model.param.task.StreetParam;
import com.pasc.business.search.more.model.policy.UnitSearchBean;
import com.pasc.business.search.more.model.task.AreaBean;
import com.pasc.business.search.more.model.task.DeptBean;
import com.pasc.lib.net.resp.BaseV2Resp;
import com.pasc.lib.net.resp.VoidObject;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author yangzijian
 * @date 2019/3/4
 * @des
 * @modify
 **/
public interface MoreApiBase {

  /*****************************办事指南*******************************************/

  /****行政区划列表--办事指南***/
  @POST("api/app/gdzwfw/getAreaList.do")
  Single<BaseV2Resp<List<AreaBean>>> getAreaList(@Body VoidObject voidObject);

  @POST("api/app/gdzwfw/getDeptByArea.do")
  Single<BaseV2Resp<List<DeptBean>>> getDeptByArea(@Body DeptParam param);

  /***获取部区下的街道***/
  @POST("api/app/gdzwfw/getStreetByArea.do")
  Single<BaseV2Resp<List<DeptBean>>> getStreetByArea(
      @Body StreetParam param);
  /*****************************办事指南*******************************************/

  /*****************************政策法规*******************************************/
  /*****发文单位搜索--政策法规***/
  @POST("api/app/policy/unitSearch.do")
  Single<BaseV2Resp<UnitSearchBean>> unitSearch();

  /*****************************政策法规*******************************************/

}
