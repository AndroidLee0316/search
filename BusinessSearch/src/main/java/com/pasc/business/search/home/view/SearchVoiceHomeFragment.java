package com.pasc.business.search.home.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;

import com.pasc.business.search.ItemType;
import com.pasc.business.search.R;
import com.pasc.business.search.SearchManager;
import com.pasc.business.search.event.EventTable;
import com.pasc.business.search.permission.RxPermissions;
import com.pasc.business.search.router.Table;
import com.pasc.business.voice.ActionView;
import com.pasc.business.voice.VoiceControlManager;
import com.pasc.business.voice.VoiceView;
import com.pasc.lib.search.util.DimensUtils;
import com.pasc.lib.search.util.KeyBoardUtils;
import com.pasc.lib.search.util.LogUtil;
import com.pasc.lib.search.util.SharePreUtil;
import com.pasc.lib.search.util.ToastUtil;
import com.pasc.lib.voice.IVoiceInitListener;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * @date 2019/6/6
 * @des
 * @modify
 **/
public class SearchVoiceHomeFragment extends SearchHomeFragment {
    final String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO};
    final String permissionTip = "语音助手需要录音权限，是否前往设置界面开启权限?";
    RxPermissions rxPermissions;
    Disposable disposable;
    private final String isVoiceFirstKey="isVoiceFirstKey";
    private boolean showAnim=false;
    private boolean needResumeInit=false;
    private Handler handler=new Handler (Looper.getMainLooper ());
    boolean isDebug(){
        return SearchManager.instance ().isDebug ();
    }
    @Override
    protected int initLayout() {
        return R.layout.pasc_search_voice_home_fragment;
    }

    @Override
    protected void initView() {
        rxPermissions = new RxPermissions (this);
        super.initView ();
        initVoice ();
    }
    boolean isPerson(){
        return Table.Value.EntranceLocation.person_type.equals (entranceLocation);
    }
    boolean isShowVoice(){
        return SearchManager.instance().isShowVoice();
    }
    private void initVoice() {
        if (getArguments () != null && getArguments () != null) {
            entranceLocation = getArguments ().getString (Table.Key.key_entranceLocation, Table.Value.EntranceLocation.person_type);
            showAnim=getArguments ().getBoolean (Table.Key.key_show_voice_anim);
        }
        VoiceView voiceView = findViewById (R.id.voiceView);
        ActionView actionView = findViewById (R.id.iv_show_voice);
        if(!isPerson()){
            SearchManager.instance().setShowVoice(false);
        }
        if (!isShowVoice ()) {
            actionView.setVisibility (View.GONE);
            return;
        }
        actionView.setVisibilityListener (new ActionView.IVisibilityListener () {
            @Override
            public void visibility(boolean show) {
                int paddingLeft = DimensUtils.dp2px (SearchManager.instance ().getApp (), 10);
                int paddingRight = DimensUtils.dp2px (SearchManager.instance ().getApp (), 40);
                searchView.getEtSearch ().setPadding (paddingLeft, 0, show ? paddingRight : paddingLeft, 0);
            }
        });
        actionView.setVisibility (View.VISIBLE);
        VoiceControlManager.instance ().setVoiceView (voiceView);
        VoiceControlManager.instance ().setActionView (actionView);
        VoiceControlManager.instance ().initSpeechEngine ();
        VoiceControlManager.instance ().initSpeakEngine ();
        VoiceControlManager.instance ().setVoiceListener (new VoiceControlManager.VoiceListener () {
            public void voiceCallback(String keyword) {
//                ToastUtil.showToast ("搜索: "+keyword);
                //设置文本的时候不准触发本地搜索
                SearchManager.instance ().getApi ().onEvent (getActivity (), EventTable.HomeVoiceEventId
                        , EventTable.HasLabel, new HashMap ());
                KeyBoardUtils.hideInputForce (getActivity ());
                searchView.setKeyword (keyword, true);
                clearAll ();
                loadMore (true,ItemType.VoiceSearchType);
                LogUtil.log ("voiceCallback: " + keyword);
            }

            public void startSpeech() {
                searchView.getEtSearch ().clearFocus ();
            }

            public void stopSpeech() {

            }
        });
        searchView.getEtSearch ().setShowClose (false);
        searchView.getEtSearch ().setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                VoiceControlManager.instance ().stopSpeak ();
                VoiceControlManager.instance ().stopSpeech ();
                VoiceControlManager.instance ().showOrHideVoice (false);

            }
        });

        actionView.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                SearchManager.instance ().getApi ().onEvent (getActivity (), EventTable.HomeVoiceEventId
                        , EventTable.ClickLabel, new HashMap ());
                if (rxPermissions.isGranted (Manifest.permission.RECORD_AUDIO)) {
                    if (!isNetworkAvailable (SearchManager.instance ().getApp ())){
                        ToastUtil.showToast ("当前网络不佳，请稍后重试");
                        return;
                    }
                    VoiceControlManager.instance ().showStartSpeech ();
                } else {
                    showPermissionsDialog (true);
                }
            }
        });
        showPermissionsDialog (false);

    }

    @Override
    public void onResume() {

        if (isShowVoice ()){
            if (needResumeInit&&rxPermissions.isGranted (Manifest.permission.RECORD_AUDIO) && !VoiceControlManager.instance ().isSpeechInit () ){
                needResumeInit=false;
                initEngine (false);
            }
        }
        super.onResume ();

    }
    Runnable runnable=new Runnable () {
        @Override
        public void run() {
            if (!isNetworkAvailable (SearchManager.instance ().getApp ())){
                ToastUtil.showToast ("当前网络不佳，请稍后重试");
                return;
            }
            VoiceControlManager.instance ().showStartSpeech ();

        }
    };
    void initEngine(final boolean needStart){
        final boolean isFirst= !SharePreUtil.getBoolean (isVoiceFirstKey,false);
        VoiceControlManager.instance ().initSpeech (getActivity (), new IVoiceInitListener () {
            @Override
            public void onInitSDKState(boolean success, Object data) {
                if (needStart || isFirst || showAnim){
                    handler.postDelayed (runnable,500 );
                }
                showAnim=false;
            }
        },isDebug ());
        SharePreUtil.setBoolean (isVoiceFirstKey,true);
    }

    void showPermissionsDialog(final boolean needStart) {
        disposable = rxPermissions.request (permissions).subscribeOn (Schedulers.io ())
                .observeOn (AndroidSchedulers.mainThread ())
                .subscribe (new Consumer<Boolean> () {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            initEngine (needStart);
                        } else {
                            showMessageOKCancel ();
                        }
                    }
                }, new Consumer<Throwable> () {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        showMessageOKCancel ();
                    }
                });

    }



    @Override
    public void onStop() {
        super.onStop ();
        if (isShowVoice ()){
            //隐藏动画
            VoiceControlManager.instance ().showOrHideVoice (false);
            VoiceControlManager.instance ().stopSpeech ();
            VoiceControlManager.instance ().stopSpeak ();
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView ();
        if (isShowVoice ()){
            handler.removeCallbacks (runnable);
        }
    }

    @Override
    public void onDestroy() {
        if (isShowVoice ()){
            VoiceControlManager.instance ().destroyAll ();
            if (disposable != null && !disposable.isDisposed ()) {
                disposable.dispose ();
            }
            disposable = null;
        }

        super.onDestroy ();
    }

    private void showMessageOKCancel( ) {
        needResumeInit=true;
        new AlertDialog.Builder (getActivity (),R.style.paPermissionStyle)
                .setMessage (permissionTip)
                .setPositiveButton ("确定", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goSettingActivity (getActivity ());
                    }
                })
                .setNegativeButton("取消", null)
                .setCancelable (false)
                .create ()
                .show ();
    }
    private void goSettingActivity(Activity activity) {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        try {
            Intent intent = new Intent ();
            intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction (Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts ("package", activity.getPackageName (), null);
            intent.setData (uri);
            activity.startActivity (intent);
        } catch (Exception e) {
            e.printStackTrace ();
        }

    }
    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
