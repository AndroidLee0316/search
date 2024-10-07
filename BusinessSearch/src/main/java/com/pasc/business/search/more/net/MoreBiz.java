package com.pasc.business.search.more.net;

import com.pasc.business.search.home.model.local.ServiceBean;
import com.pasc.business.search.home.model.local.UnionBean;
import com.pasc.business.search.home.model.param.SearchParam;
import com.pasc.business.search.home.model.search.SearchResponse;
import com.pasc.business.search.home.model.search.UnionServiceBean;
import com.pasc.business.search.home.net.HomeApi;
import com.pasc.business.search.more.model.TotalCountResponse;
import com.pasc.business.search.more.model.param.task.DeptParam;
import com.pasc.business.search.more.model.param.task.StreetParam;
import com.pasc.business.search.more.model.policy.PolicyDataBean;
import com.pasc.business.search.more.model.policy.UnitSearchBean;
import com.pasc.business.search.more.model.task.AreaBean;
import com.pasc.business.search.more.model.task.DeptBean;
import com.pasc.business.search.more.model.task.ServiceGuideDataBean;
import com.pasc.business.search.router.Table;
import com.pasc.lib.net.ApiGenerator;
import com.pasc.lib.net.param.BaseParam;
import com.pasc.lib.net.resp.BaseV2Resp;
import com.pasc.lib.net.resp.VoidObject;
import com.pasc.lib.net.transform.RespV2Transformer;
import com.pasc.lib.search.ISearchItem;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author yangzijian
 * @date 2019/2/21
 * @des
 * @modify
 **/
public class MoreBiz {

    /****行政区划列表--办事指南***/
    public static Single<List<AreaBean>> getAreaList() {
        return ApiGenerator.createApi(MoreApi.class).getAreaList(new BaseParam<VoidObject>(VoidObject.getInstance()))
                .subscribeOn(Schedulers.io())
                .compose(RespV2Transformer.<List<AreaBean>>newInstance())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /****对应区划的部门--办事指南*****/
    public static Single<List<DeptBean>> getDeptByArea(DeptParam param) {
        return ApiGenerator.createApi(MoreApi.class).getDeptByArea(new BaseParam<DeptParam>(param))
                .subscribeOn(Schedulers.io())
                .compose(RespV2Transformer.<List<DeptBean>>newInstance())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /****对应区划的街道--办事指南*****/
    public static Single<List<DeptBean>> getStreetByArea(StreetParam param) {
        return ApiGenerator.createApi(MoreApi.class).getStreetByArea(new BaseParam<StreetParam>(param))
                .subscribeOn(Schedulers.io())
                .compose(RespV2Transformer.<List<DeptBean>>newInstance())
                .observeOn(AndroidSchedulers.mainThread());
    }



    /*****政策法规下的部门--政策法规***/
    public static Single<UnitSearchBean> policyUnitSearch() {
        return ApiGenerator.createApi(MoreApi.class).unitSearch(new BaseParam<>(VoidObject.getInstance()))
                .subscribeOn(Schedulers.io())
                .compose(RespV2Transformer.<UnitSearchBean>newInstance())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /***
     * 垂直搜索入口
     * @param searchParam
     * @return
     */
    public static Single<TotalCountResponse> searchByCondition(final SearchParam searchParam){
        Single<BaseV2Resp<SearchResponse>> single=null;
//        if (ItemType.VoiceSearchType.equals (searchParam.source)) {
//            single=  ApiGenerator.createApi (HomeApi.class).appNetworkSearchVoice (new BaseParam<SearchParam> (searchParam));
//        } else {
            single=  ApiGenerator.createApi (HomeApi.class).appNetworkSearch (new BaseParam<SearchParam> (searchParam));
//        }
        return single
                .subscribeOn(Schedulers.io())
                .compose(RespV2Transformer.<SearchResponse>newInstance())
                .observeOn(Schedulers.io ())
                .map (new Function<SearchResponse, TotalCountResponse> () {
                    @Override
                    public TotalCountResponse apply(SearchResponse searchResponse) throws Exception {
                        TotalCountResponse totalCountResponse=new TotalCountResponse ();
                        List<ISearchItem> searchItems=new ArrayList<> ();
                        totalCountResponse.iSearchItems=searchItems;
                        //服务
                        if (searchResponse.servicePoolData!=null){
                            List<ServiceBean> list = searchResponse.servicePoolData.list;
                            if (list!=null && list.size ()>0){
                                for (ServiceBean bean:list){
                                    searchItems.add (bean.convert (Table.Value.EntranceLocation.person_type));
                                }
                            }
                            totalCountResponse.totalCount+=searchResponse.servicePoolData.totalCount;

                        }

                        //服务部门 / 证企号
                        if (searchResponse.serviceDepartmentData!=null){
                            List<UnionBean> list = searchResponse.serviceDepartmentData.list;
                            if (list!=null && list.size ()>0){
                                for (UnionBean unionBean:list){
                                    searchItems.add (unionBean.convert ());
                                }
                            }
                            totalCountResponse.totalCount+=searchResponse.serviceDepartmentData.totalCount;

                        }



                        if (searchResponse.unionServiceData!=null){
                            List<UnionServiceBean> list=searchResponse.unionServiceData.list;
                            if (list!=null && list.size ()>0) {
                                searchItems.addAll (list);
                            }
                            totalCountResponse.totalCount+=searchResponse.unionServiceData.totalCount;
                        }
                        //办事指南
                        if (searchResponse.serviceGuideData!=null){
                            List<ServiceGuideDataBean> list=searchResponse.serviceGuideData.list;
                            if (list!=null && list.size ()>0){
                                searchItems.addAll (list);

                            }
                            totalCountResponse.totalCount+=searchResponse.serviceGuideData.totalCount;

                        }
                        //政策法规
                        if (searchResponse.policyData!=null){
                            List<PolicyDataBean> list=searchResponse.policyData.list;
                            if (list!=null && list.size ()>0){
                                searchItems.addAll (list);
                            }
                            totalCountResponse.totalCount+=searchResponse.policyData.totalCount;

                        }

                        return totalCountResponse;

                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }
}
