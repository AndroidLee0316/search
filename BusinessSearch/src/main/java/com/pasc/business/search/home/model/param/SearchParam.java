package com.pasc.business.search.home.model.param;

import com.google.gson.annotations.SerializedName;
import com.pasc.business.search.ItemType;
import com.pasc.business.search.common.param.PageParam;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangzijian
 * @date 2019/3/4
 * @des
 * @modify
 **/
public class SearchParam {

    /**
     * 必须
     * keyWord : 关键字
     */
    @SerializedName("keyword")
    public String keyWord;

    /****
    搜索内容类型，是字符串数组，支持以下内容，不区分大小写
    All 所有
    unionService 政企号服务
    ServiceGuide 办事指南
    Policy            政策法规
     ***/
    @SerializedName ("searchContentType")
    public List<String> searchContentType;

    /***
     * 1.语音输入，2.手动输入
     */
    @SerializedName ("source")
    public String source= ItemType.ManualSearchType;

    /****必须
     1. 个人版，2企业版****/
    @SerializedName ("entranceLocation")
    public String entranceLocation;
    /*****非必须
     搜索条件**/
    @SerializedName ("searchQuery")
    public SearchQueryParam searchQuery;

    /***
     * 综合搜索
     * @param keyWord
     * @param entranceLocation
     * @return
     */
    public static SearchParam allParam(String source,String keyWord,String entranceLocation){
        SearchParam searchParam=new SearchParam ();
        searchParam.source=source;
        searchParam.keyWord=keyWord;
        searchParam.searchContentType=new ArrayList<> ();
        searchParam.searchContentType.add ("all");
        searchParam.entranceLocation=entranceLocation;
        return searchParam;
    }

    /***
     * 证企号服务搜索
     * @param keyWord  关键词
     * @param entranceLocation
     * @param unionId
     * @param pageNo
     * @param pageSize
     * @return
     */
    public static SearchParam unionServiceParam(String source,String keyWord,
                                              String entranceLocation,String unionId,int pageNo, int pageSize){
        SearchParam searchParam=new SearchParam ();
        searchParam.source=source;
        searchParam.keyWord=keyWord;
        searchParam.searchContentType=new ArrayList<> ();
        searchParam.searchContentType.add ("unionService");
        searchParam.entranceLocation=entranceLocation;
        searchParam.searchQuery=new SearchQueryParam ();
        searchParam.searchQuery.unionService=new UnionServiceParam (unionId,pageNo,pageSize);
        return searchParam;
    }

    /***
     *
     * @param keyWord
     * @param entranceLocation
     * @param areaCode
     * @param bureauCode
     * @param isOnlineApply
     * @param pageNo
     * @param pageSize
     * @return
     */
    public static SearchParam serviceGuideParam(String source,String keyWord,
                                              String entranceLocation,String areaCode,String bureauCode,boolean isOnlineApply,int pageNo, int pageSize){
        SearchParam searchParam=new SearchParam ();
        searchParam.source=source;
        searchParam.keyWord=keyWord;
        searchParam.searchContentType=new ArrayList<> ();
        searchParam.searchContentType.add ("ServiceGuide");
        searchParam.entranceLocation=entranceLocation;
        searchParam.searchQuery=new SearchQueryParam ();
        searchParam.searchQuery.serviceGuide=new ServiceGuideParam (areaCode,bureauCode,isOnlineApply,pageNo,pageSize);
        return searchParam;
    }


    /**
     *
     * @param keyWord
     * @param entranceLocation
     * @param issueUnit
     * @param pageNo
     * @param pageSize
     * @return
     */
    public static SearchParam policyParam(String source,String keyWord,
                                                String entranceLocation,String issueUnit,int pageNo, int pageSize){
        SearchParam searchParam=new SearchParam ();
        searchParam.source=source;
        searchParam.keyWord=keyWord;
        searchParam.searchContentType=new ArrayList<> ();
        searchParam.searchContentType.add ("Policy");
        searchParam.entranceLocation=entranceLocation;
        searchParam.searchQuery=new SearchQueryParam ();
        searchParam.searchQuery.policy=new PolicyParam (issueUnit,pageNo,pageSize);
        return searchParam;
    }

    public static SearchParam serviceDepartmentParam(String source,String keyWord,String entranceLocation,int pageNo, int pageSize){
        SearchParam searchParam=new SearchParam ();
        searchParam.source=source;
        searchParam.keyWord=keyWord;
        searchParam.searchContentType=new ArrayList<> ();
        searchParam.searchContentType.add ("serviceDepartment");
        searchParam.entranceLocation=entranceLocation;
        searchParam.searchQuery=new SearchQueryParam ();
        searchParam.searchQuery.serviceDepartment=new ServiceDepartmentParam (pageNo,pageSize);
        return searchParam;
    }

    public static SearchParam servicePoolParam(String source,String keyWord,String entranceLocation,int pageNo, int pageSize){
        SearchParam searchParam=new SearchParam ();
        searchParam.source=source;
        searchParam.keyWord=keyWord;
        searchParam.searchContentType=new ArrayList<> ();
        searchParam.searchContentType.add ("servicePool");
        searchParam.entranceLocation=entranceLocation;
        searchParam.searchQuery=new SearchQueryParam ();
        searchParam.searchQuery.servicePool=new ServicePoolParam (pageNo,pageSize);
        return searchParam;
    }

    /****搜索条件***/
    public static class SearchQueryParam{
        //办事服务
        @SerializedName ("unionService")
        public UnionServiceParam unionService;

        //办事指南
        @SerializedName ("serviceGuide")
        public ServiceGuideParam serviceGuide;

        //政策法规
        @SerializedName ("policy")
        public PolicyParam policy;

        //办事部门
        @SerializedName ("serviceDepartment")
        public ServiceDepartmentParam serviceDepartment;

        //服务池
        @SerializedName ("servicePool")
        public ServicePoolParam servicePool;
    }

    /****非必须
     政企号服务搜索条件***/
    public static class UnionServiceParam extends PageParam {
        /****政企号ID****/
        @SerializedName ("unionId")
        public String unionId;

        public UnionServiceParam(String unionId, int pageNo, int pageSize) {
            super(pageNo,pageSize);
            this.unionId = unionId;
        }
    }

    /****非必须
     办事指南搜索条件***/
    public static class ServiceGuideParam extends PageParam{
        /***
         *  非必须
         区域编码
         */
        @SerializedName ("areaCode")
        public String areaCode;

        @SerializedName ("bureauCode")
        public String bureauCode;

        @SerializedName ("isOnlineApply")
        public boolean isOnlineApply;

        public ServiceGuideParam(String areaCode, String bureauCode,
                                 boolean isOnlineApply, int pageNo, int pageSize) {
            super(pageNo,pageSize);
            this.areaCode = areaCode;
            this.bureauCode = bureauCode;
            this.isOnlineApply = isOnlineApply;
        }
    }
    /****非必须
     政策法规搜索条件***/
    public static class PolicyParam extends PageParam{

        @SerializedName ("issueUnit")
        public String issueUnit;

        public PolicyParam(String issueUnit, int pageNo, int pageSize) {
            super(pageNo,pageSize);
            this.issueUnit = issueUnit;
        }
    }

    /****办事部门****/
    public static class ServiceDepartmentParam extends PageParam{
        public ServiceDepartmentParam(int pageNo, int pageSize) {
            super (pageNo, pageSize);
        }
    }
    /***服务池***/
    public static class ServicePoolParam extends PageParam{
        //系统： 1：ios  2：android
        @SerializedName ("appType")
        public String appType="2";
        //app版本 ，非必须
        @SerializedName ("appVersion")
        public String appVersion;
        //版本号，非必须
        @SerializedName ("versionNo")
        public String versionNo;

        public ServicePoolParam(int pageNo, int pageSize) {
            super (pageNo, pageSize);
        }
    }



}
