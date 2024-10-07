package com.search.demo;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import android.widget.Toast;
import com.google.gson.Gson;
import com.pasc.business.search.SearchManager;
import com.pasc.business.search.home.model.local.ServiceBean;
import com.pasc.business.search.home.model.local.UnionBean;
import com.pasc.lib.search.ApiGet;
import com.pasc.lib.search.ISearchItem;
import com.pasc.lib.search.util.LogUtil;

import com.pasc.lib.search.util.ToastUtil;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @date 2019/6/4
 * @des
 * @modify
 **/
public class ApiGetImpl extends ApiGet {
    private List<InterceptorResponse.MatchBean> matchBeans=new ArrayList<> ();

    private static final String DEFAULT_OPEN_URL_KEY = "::openurl";
    private static final String GS_OPEN_URL_KEY = "::gs"; //政企号服务
    private static final String GS_URL = "https://isz-web.sz.gov.cn/sz/app/feature/service-entry-page/#/"; //政企号服务固定URL
    @Override
    public String getPhone() {
        return null;
    }

    @Override
    public boolean interceptSearch(Activity activity,String keyWord) {

        if (keyWord.trim ().startsWith(DEFAULT_OPEN_URL_KEY)) {
//                        BrowserBranchUtils.browserBranch(this, keyWord.replaceAll(DEFAULT_OPEN_URL_KEY, ""), null);
            LogUtil.log (DEFAULT_OPEN_URL_KEY);
            return true;
        }
        if(keyWord.trim ().startsWith(GS_OPEN_URL_KEY)){
//                        BrowserBranchUtils.browserBranch(this, GS_URL, null);
            LogUtil.log (GS_OPEN_URL_KEY);

            return true;
        }

        return false;
    }

    @Override
    public boolean ignoreLength(String keyWord) {
        if (keyWord.trim ().startsWith(DEFAULT_OPEN_URL_KEY)) {
            return true;
        }
        if(keyWord.trim ().startsWith(GS_OPEN_URL_KEY)){
            return true;
        }
        return false;
    }

    @Override
    public String getToken() {
        return null;
    }

    @Override
    public void searchItemClick(Activity activity,ISearchItem item) {
        LogUtil.log ("点击 item " + item.title ());

        if ("personal_service".equals (item.searchType ())) {
            ServiceBean serviceBean = new Gson ().fromJson (item.jsonContent (), ServiceBean.class);
            ToastUtil.showToast("点击内容："+item.jsonContent ());

        }
        // 根据  item.searchType (); 获取  item.jsonContent (); 解析跳转到具体界面
        //服务
        // "personal_service"; json 结构为 com.pasc.business.search.home.model.local.ServiceBean

        //政企号 / 办事服务
        // "personal_union"; json 结构为 com.pasc.business.search.home.model.local.UnionBean

        //政企号服务 / 部门服务
        // "personal_zhengqi_server"; ,json结构为 com.pasc.business.search.home.model.search.UnionServiceBean

        //办事指南
        //"personal_work_guide"; json 结构为 com.pasc.business.search.more.model.task.ServiceGuideDataBean

        //政策法规
        //"personal_policy_rule"; json 结构为 com.pasc.business.search.more.model.policy.PolicyDataBean
    }
    @Override
    public void onEvent(Context context, String eventId, String label, Map map) {
        LogUtil.log ("埋点 eventId：" + eventId+ ", label："+label);

    }

    @Override
    public Single<Boolean> voiceInterceptorSearch(final Context context, final String keyword) {
        return   Single.create (new SingleOnSubscribe<List<InterceptorResponse.MatchBean>> () {
            @Override
            public void subscribe(SingleEmitter<List<InterceptorResponse.MatchBean>> emitter) throws Exception {
                if (matchBeans.size ()>0){
                    emitter.onSuccess (matchBeans);
                    return;
                }
                setServiceData ();
                emitter.onSuccess (matchBeans);
            }
        }).subscribeOn (Schedulers.io ())
                .map (new Function<List<InterceptorResponse.MatchBean>, DirectBean> () {
                    @Override
                    public DirectBean apply(List<InterceptorResponse.MatchBean> serviceBeans) throws Exception {
                        DirectBean directBean=new DirectBean (false,null);
                        if (serviceBeans!=null && serviceBeans.size ()>0){
                            int index=0;
                            InterceptorResponse.MatchBean target=null;
                            for (InterceptorResponse.MatchBean bean:serviceBeans){
                                if (!TextUtils.isEmpty (bean.keyword)){
                                    String key[]=bean.keyword.split (",");
                                    boolean hasContain=false;
                                    for (String kk:key){
                                        if (keyword.contains (kk)){
                                            hasContain=true;
                                            break;
                                        }
                                    }
                                    if(hasContain){
                                        target=bean;
                                        index++;
                                        if(index>=2){
                                            return directBean;
                                        }
                                    }

                                }
                            }
                            if (target!=null){
                                directBean.intercept=true;
                                directBean.searchItem=target.searchItem;
                                return directBean;
                            }
                            return directBean;
                        }else {
                            return directBean;
                        }

                    }
                }).observeOn (AndroidSchedulers.mainThread ()).map (new Function<DirectBean, Boolean> () {
                    @Override
                    public Boolean apply(DirectBean directBean) throws Exception {
                        if (directBean.intercept && directBean.searchItem!=null){
                            if (context instanceof Activity){
                                searchItemClick ((Activity) context,directBean.searchItem);
                            }
                        }
                        return directBean.intercept;
                    }
                });
    }

    private void setServiceData(){
        InputStream is=null;
        try {
            is= SearchManager.instance ().getApp ().getAssets ().open ("smt.sz.sxi.interceptor.json");
            BufferedReader reader = new BufferedReader (new InputStreamReader (is));
            StringBuilder builder=new StringBuilder ();
            String line=null;
            while ((line=reader.readLine ())!=null){
                builder.append (line);
            }
            InterceptorResponse response=  new Gson ().fromJson (builder.toString (),InterceptorResponse.class);
            for (ServiceBean serviceBean:response.items){
                InterceptorResponse.MatchBean matchBean=new InterceptorResponse.MatchBean (serviceBean.convert ("1"),serviceBean.keyWord);
                matchBeans.add (matchBean);
            }
        }catch (Exception e){
            e.printStackTrace ();
        }finally {
            try {
                if (is!=null){
                    is.close ();
                }
            }catch (Exception e){
            }

        }
    }
    @Override
    public Single<Map<String, String>> voiceTip() {
        return Single.create (new SingleOnSubscribe<Map<String, String>> () {
            @Override
            public void subscribe(SingleEmitter<Map<String, String>> emitter) throws Exception {
//                if (serviceBeans.size ()>0){
//                }else {
//                    setServiceData ();
//                }
//
                Map<String,String> map=new HashMap<> ();
//                int len=serviceBeans.size ();
//                if (len>0){
//                    int index= (int) (Math.random () *len);
//                    ServiceBean serviceBean=  serviceBeans.get (index);
//                  String showTip = "您可以这样说";
////                String showMsg = serviceBean.keyWord;
                    String showTip = "您可以这样说";
                    String showMsg = "公积金查询";
                    map.put ("voicePath","search/search_tts.wav");
                    map.put ("showTip",showTip);
                    map.put ("showMsg",showMsg);

//                }
                emitter.onSuccess (map);


            }
        }).subscribeOn (Schedulers.io ()).observeOn (AndroidSchedulers.mainThread ());
    }
    private static class DirectBean{
        public boolean intercept;
        public ISearchItem searchItem;

        public DirectBean(boolean intercept, ISearchItem searchItem) {
            this.intercept = intercept;
            this.searchItem = searchItem;
        }
    }
}
