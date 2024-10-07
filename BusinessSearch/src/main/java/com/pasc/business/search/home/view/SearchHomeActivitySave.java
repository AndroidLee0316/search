//package com.pasc.business.search.home.view;
//
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.inputmethod.EditorInfo;
//import android.widget.TextView;
//
//import com.alibaba.android.arouter.facade.annotation.Route;
//import com.chad.library.adapter.base.BaseQuickAdapter;
//import com.pasc.business.search.ItemType;
//import com.pasc.business.search.R;
//import com.pasc.business.search.SearchManager;
//import com.pasc.business.search.common.model.HotBean;
//import com.pasc.business.search.common.model.SearchThemeBean;
//import com.pasc.business.search.common.presenter.HistoryPresenter;
//import com.pasc.business.search.common.presenter.HotWordPresenter;
//import com.pasc.business.search.common.view.HistoryView;
//import com.pasc.business.search.common.view.HotView;
//import com.pasc.business.search.customview.ClearEditText;
//import com.pasc.business.search.customview.FooterViewControl;
//import com.pasc.business.search.customview.IReTryListener;
//import com.pasc.business.search.customview.MyOnEditorActionListener;
//import com.pasc.business.search.customview.SearchTagView;
//import com.pasc.business.search.customview.StatusView;
//import com.pasc.business.search.event.EventTable;
//import com.pasc.business.search.home.adapter.HomeDynamicAdapter;
//import com.pasc.business.search.home.presenter.SearchHomePresenter;
//import com.pasc.business.search.more.view.MoreSearchActivity;
//import com.pasc.business.search.router.Table;
//import com.pasc.business.voice.ActionView;
//import com.pasc.business.voice.VoiceControlManager;
//import com.pasc.business.voice.VoiceView;
//import com.pasc.lib.search.ISearchGroup;
//import com.pasc.lib.search.ISearchItem;
//import com.pasc.lib.search.SearchSourceGroup;
//import com.pasc.lib.search.base.BaseMvpActivity;
//import com.pasc.lib.search.base.MultiPresenter;
//import com.pasc.lib.search.common.IKeywordItem;
//import com.pasc.lib.search.db.history.HistoryBean;
//import com.pasc.lib.search.util.DimensUtils;
//import com.pasc.lib.search.util.KeyBoardUtils;
//import com.pasc.lib.search.util.SearchUtil;
//import com.pasc.lib.search.util.StatusBarUtils;
//import com.pasc.lib.search.util.ToastUtil;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author yangzijian
// * @date 2019/2/18
// * @des
// * @modify
// **/
//@Route(path = Table.Path.path_search_home_router)
//public class SearchHomeActivitySave extends BaseMvpActivity<MultiPresenter> implements SearchHomeView, HistoryView, HotView,
//        ClearEditText.EditTextChangeListener, MyOnEditorActionListener {
//    private SearchTagView searchView;
//    private RecyclerView recyclerView;
//    private HomeDynamicAdapter<ISearchGroup> homeAdapter;
//    private List<ISearchGroup> searchItems = new ArrayList<> ();
//    private List<ISearchGroup> searchNetItems = new ArrayList<> ();
//    private SearchHomePresenter homePresenter;
//    private HistoryPresenter historyPresenter;
//    private HotWordPresenter hotWordPresenter;
//    private final String homeType = "home";
//    private StatusView statusView;
//
//    //入口位置 1：个人版  2：企业版
//    private String entranceLocation;
//    private View contentView;
//    private FooterViewControl viewControl;
//
//    @Override
//    protected void setContViewBefore(Bundle savedInstanceState) {
//        StatusBarUtils.setStatusBarColor (this, true);
//    }
//
//    @Override
//    public MultiPresenter createPresenter() {
//        MultiPresenter multiPresenter = new MultiPresenter ();
//        homePresenter = new SearchHomePresenter ();
//        historyPresenter = new HistoryPresenter ();
//        hotWordPresenter = new HotWordPresenter ();
//        multiPresenter.requestPresenter (homePresenter, historyPresenter, hotWordPresenter);
//        return multiPresenter;
//    }
//
//    @Override
//    public int initLayout() {
//        return R.layout.pasc_search_home_activity;
//    }
//
//    @Override
//    protected void initView() {
//        contentView = LayoutInflater.from (this).inflate (R.layout.pasc_search_home_content, null);
//        recyclerView = contentView.findViewById (R.id.recyclerView);
//        statusView = contentView.findViewById (R.id.statusView);
//        statusView.setContentView (recyclerView);
//        searchView = findViewById (R.id.searchView);
//        searchView.addContentView (contentView)
//                .setBackShow (false)
//                .showCancle (true)
//                .setEditTextChangeListener (this)
//                .setOnEditorActionListener (this)
//                .setOnCancelClickListener (new View.OnClickListener () {
//                    @Override
//                    public void onClick(View v) {
//                        searchView.showKeyborad (false);
//                        finish ();
//                    }
//                })
//                .setOnDeleteHistoryClickListener (new View.OnClickListener () {
//                    @Override
//                    public void onClick(View v) {
//                        historyPresenter.clearHistory (historyType ());
//                    }
//                })
//                .setHistoryClickListener (new SearchTagView.ItemClickListener () {
//                    @Override
//                    public void itemClick(IKeywordItem iKeywordItem,boolean intercept) {
//                        saveKeyWord ();
//
//                        //首页历史搜索埋点
//                        Map<String, String> map = new HashMap<> ();
//                        map.put (EventTable.MoreLabel, iKeywordItem.keyword ());
//                        SearchManager.instance ().getApi ().onEvent (getActivity (), getHomeEventId ()
//                                , EventTable.HistoryLabel, map);
//                    }
//                })
//                .setAssignClickListener (new SearchTagView.ItemClickListener () {
//                    @Override
//                    public void itemClick(IKeywordItem iKeywordItem,boolean intercept) {
//
//                        String searchTypeName = ItemType.getGroupNameFromType (iKeywordItem.type ());
//                        //首页垂直入口埋点
//                        Map<String, String> map = new HashMap<> ();
//                        map.put (EventTable.MoreLabel, searchTypeName);
//                        SearchManager.instance ().getApi ().onEvent (getActivity (), getHomeEventId ()
//                                , EventTable.MoreLabel, map);
//
//                        Bundle bundle = new Bundle ();
//                        bundle.putString (Table.Key.key_search_type, iKeywordItem.type ());
//                        bundle.putString (Table.Key.key_entranceLocation, entranceLocation);
//                        bundle.putString (Table.Key.key_search_type_name, searchTypeName);
//                        gotoActivity (MoreSearchActivity.class, bundle);
//                    }
//                })
//                .setHotClickListener (new SearchTagView.ItemClickListener () {
//                    @Override
//                    public void itemClick(IKeywordItem iKeywordItem,boolean intercept) {
//                        saveKeyWord ();
//                        //首页热词埋点
//                        Map<String, String> map = new HashMap<> ();
//                        map.put (EventTable.MoreLabel, iKeywordItem.keyword ());
//                        SearchManager.instance ().getApi ().onEvent (getActivity (), getHomeEventId ()
//                                , EventTable.HotWordLabel, map);
//
//                    }
//                })
//                .setHistoryText ("历史搜索").setHotText ("热门搜索");
//
//        homeAdapter = new HomeDynamicAdapter (this, searchItems);
//        viewControl = new FooterViewControl (this);
//        homeAdapter.addFooterView (viewControl.footerView);
//        viewControl.showContent ("");
//        recyclerView.setLayoutManager (new LinearLayoutManager (this));
//        recyclerView.setAdapter (homeAdapter);
//        homeAdapter.setOnItemChildClickListener (new BaseQuickAdapter.OnItemChildClickListener () {
//            @Override
//            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//
//                KeyBoardUtils.hideInputForce (getActivity ());
//
//                if (view.getId ()==R.id.ll_title){
//                    return;
//                }
//
//                Object obj = homeAdapter.getData ().get (position);
//                if (obj instanceof ISearchGroup) {
//                    ISearchGroup itemGroup = (ISearchGroup) obj;
//
//                    if (ItemType.personal_server.equals (itemGroup.searchType ()) || ItemType.personal_zhengqi_number.equals (itemGroup.searchType ()) ){
//                        //本地搜索的点击
//                        saveKeyWord ();
//                    }
//
//                    List<ISearchItem> searchItems = itemGroup.data ();
//                    if (view.getId () == R.id.ll_one) {
//                        ISearchItem iSearchItem = searchItems.get (0);
//                        clickEvent (iSearchItem);
//                        SearchManager.instance ().getApi ().searchItemClick (getActivity (), iSearchItem);
//                    } else if (view.getId () == R.id.ll_two) {
//                        ISearchItem iSearchItem = searchItems.get (1);
//                        clickEvent (iSearchItem);
//                        SearchManager.instance ().getApi ().searchItemClick (getActivity (), iSearchItem);
//                    } else if (view.getId () == R.id.ll_three) {
//                        ISearchItem iSearchItem = searchItems.get (2);
//                        clickEvent (iSearchItem);
//                        SearchManager.instance ().getApi ().searchItemClick (getActivity (), iSearchItem);
//                    } else if (view.getId () == R.id.ll_more) {
//                        Bundle bundle = new Bundle ();
//                        bundle.putString (Table.Key.key_search_type, itemGroup.searchType ());
//                        bundle.putString (Table.Key.key_word, searchView.getKeyword ());
//                        bundle.putString (Table.Key.key_entranceLocation, entranceLocation);
//                        bundle.putString (Table.Key.key_search_type_name, itemGroup.groupName ());
//                        bundle.putBoolean (Table.Key.key_hide_keyboard_flag, true);
//                        gotoActivity (MoreSearchActivity.class, bundle);
//                    }
//                }
//            }
//        });
//
//        statusView.setTryListener (new IReTryListener () {
//            @Override
//            public void tryAgain() {
//                KeyBoardUtils.hideInputForce (getActivity ());
//                loadMore ();
//            }
//        });
//        viewControl.setTryListener (new IReTryListener () {
//            @Override
//            public void tryAgain() {
//                KeyBoardUtils.hideInputForce (getActivity ());
//                boolean intercept=interceptSearch ();
//                if (!intercept) {
//                    loadMore ();
//                }else {
//                    saveKeyWord ();
//                }
//            }
//        });
//        initVoice ();
//    }
//
//    private void initVoice(){
//        if (getIntent ()!=null && getIntent ().getExtras()!=null){
//            entranceLocation = getIntent ().getExtras().getString (Table.Key.key_entranceLocation,Table.Value.EntranceLocation.person_type);
//        }
//        VoiceView voiceView=findViewById (R.id.voiceView);
//        ActionView actionView=findViewById (R.id.iv_show_voice);
//        if (!Table.Value.EntranceLocation.person_type.equals (entranceLocation)){
//            actionView.setVisibility (View.GONE);
//            return;
//        }
//        actionView.setVisibilityListener (new ActionView.IVisibilityListener () {
//            @Override
//            public void visibility(boolean show) {
//                int paddingLeft=DimensUtils.dp2px (getActivity (),10);
//                int paddingRight=DimensUtils.dp2px (getActivity (),40);
//                searchView.getEtSearch ().setPadding (paddingLeft,0,show?paddingRight:paddingLeft,0);
//            }
//        });
//        actionView.setVisibility (View.VISIBLE);
//        VoiceControlManager.instance ().setVoiceView (voiceView);
//        VoiceControlManager.instance ().setActionView (actionView);
//        VoiceControlManager.instance ().initSpeechEngine ();
//        VoiceControlManager.instance ().initSpeech (this,true);
//        VoiceControlManager.instance ().initSpeakEngine ();
//        VoiceControlManager.instance ().initSpeak (this,true);
//
//        VoiceControlManager.instance ().setVoiceListener (new VoiceControlManager.VoiceListener () {
//            public void voiceCallback(String keyword) {
//                ToastUtil.showToast ("搜索: "+keyword);
//                //设置文本的时候不准触发本地搜索
//                searchView.setKeyword (keyword,true);
//                loadMore ();
//                Log.e ("speechTag", "voiceCallback: "+keyword );
//            }
//            public void startSpeech() {
//                searchView.getEtSearch ().clearFocus ();
//            }
//
//            public void stopSpeech() {
//
//            }
//        });
//        searchView.getEtSearch ().setShowClose (false);
//        searchView.getEtSearch ().setOnClickListener (new View.OnClickListener () {
//            @Override
//            public void onClick(View v) {
//                VoiceControlManager.instance ().stopSpeak ();
//                VoiceControlManager.instance ().stopSpeech ();
//                VoiceControlManager.instance ().showOrHideVoice (false);
//
//            }
//        });
//
//    }
//
//    void clickEvent(ISearchItem iSearchItem) {
//        String searchTypeName = ItemType.getGroupNameFromType (iSearchItem.searchType ());
//
//        //首页搜索结果点击埋点
//        Map<String, String> map = new HashMap<> ();
//        map.put (EventTable.WordLabel, searchView.getKeyword ());
//        map.put (EventTable.ResultLabel, iSearchItem.title ());
//        map.put (EventTable.SearchTypeKey, searchTypeName);
//        SearchManager.instance ().getApi ().onEvent (getActivity (), getHomeEventId ()
//                , EventTable.ResultLabel, map);
//    }
//
//
//    @Override
//    protected void initData(Bundle bundleData) {
//        entranceLocation = bundleData.getString (Table.Key.key_entranceLocation, Table.Value.EntranceLocation.person_type);
//        String idType = Table.Value.EntranceLocation.person_type.equals (entranceLocation) ? Table.Value.HomeType.personal_home_page : Table.Value.HomeType.business_home_page;
//        hotWordPresenter.searchTheme (entranceLocation);
//        hotWordPresenter.loadHotWord (idType);
//        hotWordPresenter.loadHintText (idType);
//        loadHistory ();
//        viewControl.getTvFooterTip ().setText (
//                Table.Value.EntranceLocation.person_type.equals (entranceLocation)?"支持所有部门服务、办事指南、政策法规":"支持所有办事指南"
//        );
//    }
//
//    @Override
//    public void afterChange(String keyword) {
//        homePresenter.searchLocal (keyword,entranceLocation);
//    }
//
//    @Override
//    public boolean onEditorAction(TextView v, int actionId, KeyEvent event,boolean intercept) {
//        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//            if (!intercept) {
//                loadMore ();
//            }else {
//                saveKeyWord ();
//            }
//            return false;
//        }
//        return false;
//    }
//
//
//    @Override
//    public void themeData(List<SearchThemeBean> beans) {
//        searchView.setAssignData (beans);
//    }
//
//    @Override
//    public void historyData(List<HistoryBean> beans) {
//        searchView.setHistoryData (beans);
//    }
//
//    @Override
//    public void hotData(List<HotBean> beans) {
//        searchView.setHotData (beans);
//    }
//
//    @Override
//    public void hintText(String text) {
//        searchView.setHint (text);
//    }
//
//    @Override
//    public void localData(List<SearchSourceGroup> searchListItemGroups) {
//        searchItems.clear ();
//        searchNetItems.clear ();
//        searchItems.addAll (searchListItemGroups);
//        homeAdapter.notifyDataSetChanged ();
//        statusView.showContent ();
//        viewControl.showContent (searchView.getKeyword ());
//    }
//
//    @Override
//    public void netData(List<SearchSourceGroup> searchListItemGroups) {
//        searchNetItems.addAll (searchListItemGroups);
//        searchItems.addAll (searchListItemGroups);
//        homeAdapter.setKeyword (searchView.getKeyword ());
//        homeAdapter.notifyDataSetChanged ();
//
//        if (searchListItemGroups.size () > 0) {
//            statusView.showContent ();
//            viewControl.showContent ("");
//        } else {
//            if (searchItems.size () > 0) {
//                statusView.showContent ();
//            } else {
//                statusView.showEmpty ();
//
//                //首页空结果埋点
//                Map<String, String> map = new HashMap<> ();
//                map.put (EventTable.WordLabel, searchView.getKeyword ());
//                SearchManager.instance ().getApi ().onEvent (getActivity (), getHomeEventId ()
//                        , EventTable.EmptyLabel, map);
//
//            }
//            viewControl.showEmpty ();
//
//        }
//
//
//    }
//
//    @Override
//    public void netError(String code, String msg) {
//        if (searchItems.size () == 0) {
//            statusView.showError ();
//        } else {
//            viewControl.showError ();
//        }
//    }
//
//    @Override
//    public void showContentView(boolean show) {
//        searchView.showContentView (show);
//    }
//
//    void loadMore() {
//        searchView.showContentView (true);
//        String keyword = searchView.getKeyword ();
//        saveKeyWord ();
//        //首页搜索词埋点
//        Map<String, String> map = new HashMap<> ();
//        map.put (EventTable.WordLabel, keyword);
//        SearchManager.instance ().getApi ().onEvent (getActivity (), getHomeEventId ()
//                , EventTable.WordLabel, map);
//
//        searchItems.removeAll (searchNetItems);
//        homeAdapter.notifyDataSetChanged ();
//        searchNetItems.clear ();
//        if (searchItems.size () == 0) {
//            statusView.showLoading ();
//        } else {
//            viewControl.showLoading ();
//        }
//        homePresenter.searchNet (keyword, entranceLocation);
//
//    }
//
//    private String getHomeEventId() {
//        return Table.Value.EntranceLocation.person_type.equals (entranceLocation) ? EventTable.HomePersonEventId : EventTable.HomeEnterpriseEventId;
//    }
//
//    /**
//     * 深圳特殊需求
//     * 输入以什么开头的时候拦截
//     * @return
//     */
//    boolean interceptSearch(){
//        return SearchManager.instance ().getApi ().interceptSearch (this,searchView.getKeyword ());
//    }
//
//    void saveKeyWord(){
//        String keyword=searchView.getKeyword ();
//        if (!SearchUtil.isEmpty (keyword)) {
//            historyPresenter.saveKeyword (keyword, historyType ());
//        }
//    }
//
//    void loadHistory(){
//        historyPresenter.loadHistory (historyType ());
//    }
//    String historyType(){
//        return homeType+"_"+entranceLocation;
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy ();
//        VoiceControlManager.instance ().destroyAll ();
//    }
//}
