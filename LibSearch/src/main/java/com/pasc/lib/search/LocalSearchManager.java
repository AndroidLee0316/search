package com.pasc.lib.search;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.LibSearchGeneratedDatabaseHolder;

/**
 * @date 2019/5/12
 * @des
 * @modify
 **/
public class LocalSearchManager {
    private static final class Single {
        private static LocalSearchManager instance = new LocalSearchManager ();
    }
    private LocalSearchManager() {
    }

    public static LocalSearchManager instance() {
        return Single.instance;
    }

    private @NonNull  Application application;
    private @NonNull  ApiGet apiGet = new DefaultApi ();
    private @NonNull  IType iType=new IType () {
        @Override
        public int getItemTypeFromType(String searchType) {
            return 0;
        }
    };
    private boolean isDebug=true;
    private boolean matchPinyin=false;
    private Context dbContext;
    private String appVersion;

    public IType getType() {
        return iType;
    }
    public boolean isDebug(){
        return isDebug;
    }
    public boolean isMatchPinyin() {
        return matchPinyin;
    }
    public ApiGet getApi() {
        return apiGet;
    }
    public Application getApp() {
        return application;
    }

    public LocalSearchManager application(Application application) {
        if (application!=null) {
            this.application = application;
        }
        return this;
    }

    public LocalSearchManager apiGet(ApiGet apiGet) {
        if (apiGet!=null) {
            this.apiGet = apiGet;
        }
        return this;

    }


    public LocalSearchManager debug(boolean debug) {
        isDebug = debug;
        return this;
    }

    public LocalSearchManager matchPinyin(boolean matchPinyin) {
        this.matchPinyin = matchPinyin;
        return this;
    }

    public LocalSearchManager type(IType iType) {
        if (iType!=null) {
            this.iType = iType;
        }
        return this;
    }

    public LocalSearchManager dbContext(Context dbContext){
        this.dbContext=dbContext;
        return this;

    }

    public LocalSearchManager appVersion(String appVersion){
        this.appVersion=appVersion;
        return this;
    }

    public void init(){
        Context tmpContext=dbContext;
        if (tmpContext==null){
            tmpContext=application;
        }
        //简化初始化DBFLOW，实际使用在app初始化，目前用于测试
        FlowConfig flowConfig = new FlowConfig.Builder (tmpContext)
                .addDatabaseHolder (LibSearchGeneratedDatabaseHolder.class)
                .build ();
        FlowManager.init (flowConfig);
    }
}
