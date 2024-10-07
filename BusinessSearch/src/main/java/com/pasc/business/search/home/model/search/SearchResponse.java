package com.pasc.business.search.home.model.search;

import com.google.gson.annotations.SerializedName;
import com.pasc.business.search.home.model.local.ServiceBean;
import com.pasc.business.search.home.model.local.UnionBean;
import com.pasc.business.search.more.model.policy.PolicyDataBean;
import com.pasc.business.search.more.model.task.ServiceGuideDataBean;

import java.util.List;

/**
 * @author yangzijian
 * @date 2019/3/4
 * @des
 * @modify
 **/
public class SearchResponse {


    @SerializedName("unionServiceData")
    public UnionServiceData unionServiceData;
    @SerializedName("serviceGuideData")
    public ServiceGuideData serviceGuideData;
    @SerializedName("policyData")
    public PolicyData policyData;

    @SerializedName ("serviceDepartmentData")
    public ServiceDepartmentData serviceDepartmentData;

    @SerializedName ("servicePoolData")
    public ServicePoolData servicePoolData;

    //证企号服务
    public static class UnionServiceData {

        @SerializedName("totalCount")
        public int totalCount;
        @SerializedName("list")
        public List<UnionServiceBean> list;


    }
    //办事指南
    public static class ServiceGuideData {

        @SerializedName("totalCount")
        public int totalCount;
        @SerializedName("list")
        public List<ServiceGuideDataBean> list;
    }
    //政策法规
    public static class PolicyData {

        @SerializedName("totalCount")
        public int totalCount;
        @SerializedName("list")
        public List<PolicyDataBean> list;
    }
    //办事部门
    public static class ServiceDepartmentData{
        @SerializedName("totalCount")
        public int totalCount;
        @SerializedName ("list")
        public List<UnionBean> list;
    }
    // 服务
    public static class ServicePoolData{
        @SerializedName("totalCount")
        public int totalCount;
        @SerializedName ("versionNo")
        public String versionNo;
        @SerializedName ("list")
        public List<ServiceBean> list;
    }
}
