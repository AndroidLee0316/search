package com.pasc.business.search.home.view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.pasc.business.search.ItemType;
import com.pasc.business.search.R;
import com.pasc.business.search.SearchManager;
import com.pasc.business.search.common.model.HotBean;
import com.pasc.business.search.common.model.SearchThemeBean;
import com.pasc.business.search.common.net.CommonBizBase;
import com.pasc.business.search.common.presenter.HistoryPresenter;
import com.pasc.business.search.common.presenter.HotWordPresenter;
import com.pasc.business.search.common.view.HistoryView;
import com.pasc.business.search.common.view.HotView;
import com.pasc.business.search.customview.ClearEditText;
import com.pasc.business.search.customview.FooterViewControl;
import com.pasc.business.search.customview.IReTryListener;
import com.pasc.business.search.customview.MyOnEditorActionListener;
import com.pasc.business.search.customview.SearchTagView;
import com.pasc.business.search.customview.StatusView;
import com.pasc.business.search.customview.gifview.ItemListViewListener;
import com.pasc.business.search.event.EventTable;
import com.pasc.business.search.event.EventTableImpl;
import com.pasc.business.search.home.adapter.HomeDynamicAdapter;
import com.pasc.business.search.home.presenter.SearchHomePresenter;
import com.pasc.business.search.more.view.MoreSearchActivity;
import com.pasc.business.search.router.Table;
import com.pasc.business.search.util.CommonUtil;
import com.pasc.lib.search.ISearchGroup;
import com.pasc.lib.search.ISearchItem;
import com.pasc.lib.search.SearchSourceGroup;
import com.pasc.lib.search.base.BaseMvpFragment;
import com.pasc.lib.search.base.MultiPresenter;
import com.pasc.lib.search.common.IKeywordItem;
import com.pasc.lib.search.db.history.HistoryBean;
import com.pasc.lib.search.util.KeyBoardUtils;
import com.pasc.lib.search.util.SearchUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @date 2019/6/6
 * @des
 * @modify
 **/
public class SearchHomeFragment extends BaseMvpFragment<MultiPresenter> implements SearchHomeView, HistoryView, HotView,
        ClearEditText.EditTextChangeListener, MyOnEditorActionListener,
        ClearEditText.IconDismissListener , ItemListViewListener {
    SearchTagView searchView;
    RecyclerView recyclerView;
    HomeDynamicAdapter<ISearchGroup> homeAdapter;
    List<ISearchGroup> searchItems = new ArrayList<>();
    List<ISearchGroup> searchNetItems = new ArrayList<>();
    SearchHomePresenter homePresenter;
    HistoryPresenter historyPresenter;
    HotWordPresenter hotWordPresenter;
    private final String homeType = "home";
    StatusView statusView;
    String source = ItemType.ManualSearchType;
    //入口位置 1：个人版  2：企业版
    String entranceLocation;
    String entranceId;
    View contentView;
    FooterViewControl viewControl;


    void resetSearchSource() {
        source = ItemType.ManualSearchType;
    }

    boolean needLocalSearch = true;
    private String hintText;

    @Override
    public MultiPresenter createPresenter() {
        MultiPresenter multiPresenter = new MultiPresenter();
        homePresenter = new SearchHomePresenter();
        historyPresenter = new HistoryPresenter();
        hotWordPresenter = new HotWordPresenter();
        multiPresenter.requestPresenter(homePresenter, historyPresenter, hotWordPresenter);
        return multiPresenter;
    }

    @Override
    protected int initLayout() {
        return R.layout.pasc_search_home_fragment;
    }

    @Override
    protected void initView() {
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.pasc_search_home_content, null);
        recyclerView = contentView.findViewById(R.id.recyclerView);
        statusView = contentView.findViewById(R.id.statusView);
        statusView.setContentView(recyclerView);
        searchView = findViewById(R.id.searchView);
        searchView.addContentView(contentView)
                .setBackShow(false)
                .showCancle(true)
                .setEditTextChangeListener(this)
                .setOnEditorActionListener(this)
                .setOnCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchView.showKeyborad(false);
                        getActivity().finish();
                        SearchManager.instance().getApi().onEvent(getActivity(), getHomeEventId()
                                , EventTable.BackLabel, null);
                    }
                })
                .setOnDeleteHistoryClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, String> map = new HashMap<>();
                        map.put(EventTableImpl.getInstance().getConfirmDialogAccordingly(), getString(R.string.search_confirm));
                        SearchManager.instance().getApi().onEvent(getActivity(), getHomeEventId()
                                , EventTable.ClearHistoryLabel, map);
                        historyPresenter.clearHistory(historyType());
                    }
                })
                .setOnDeleteHistoryCancelClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Map<String, String> map = new HashMap<>();
                        map.put(EventTableImpl.getInstance().getConfirmDialogAccordingly(), getString(R.string.search_cancel));
                        SearchManager.instance().getApi().onEvent(getActivity(), getHomeEventId()
                                , EventTable.ClearHistoryLabel, map);
                    }
                })
                .setHistoryClickListener(new SearchTagView.ItemClickListener() {
                    @Override
                    public void itemClick(IKeywordItem iKeywordItem, boolean intercept) {
                        saveKeyWord();

                        //首页历史搜索埋点
                        Map<String, String> map = new HashMap<>();
                        map.put("搜索词", iKeywordItem.keyword());
                        SearchManager.instance().getApi().onEvent(getActivity(),"综合搜索"
                                , "历史搜索词", map);
                    }
                })
                .setAssignClickListener(new SearchTagView.ItemClickListener() {
                    @Override
                    public void itemClick(IKeywordItem iKeywordItem, boolean intercept) {


                        String searchTypeName;
                        if (iKeywordItem instanceof SearchThemeBean) {
                            searchTypeName = ((SearchThemeBean) iKeywordItem).themeName;
                        } else {
                            searchTypeName = ItemType.getGroupNameFromType(iKeywordItem.type());
                        }


                        Bundle bundle = new Bundle();
                        bundle.putString(Table.Key.key_search_type, ItemType.personal_theme);
                        bundle.putString(Table.Key.key_entranceLocation, entranceLocation);
                        bundle.putString(Table.Key.key_themeConfigId, iKeywordItem.type());
                        bundle.putString(Table.Key.key_search_type_name, searchTypeName);

                        //首页垂直入口埋点
                        Map<String, String> map = new HashMap<>();
                        map.put("主题名称", searchTypeName);
                        SearchManager.instance().getApi().onEvent(getActivity(),"综合搜索"
                                , "主题搜索", map);
                        gotoActivity(MoreSearchActivity.class, bundle);
                    }
                })
                .setHotClickListener(new SearchTagView.ItemClickListener() {
                    @Override
                    public void itemClick(IKeywordItem iKeywordItem, boolean intercept) {
                        saveKeyWord();
                        //首页热词埋点
                        Map<String, String> map = new HashMap<>();
                        map.put("搜索词", iKeywordItem.keyword());
                        SearchManager.instance().getApi().onEvent(getActivity(),"综合搜索"
                                , "热词搜索", map);

                    }
                })
                .setOnIconDismissListener(this)
                .setHistoryText("历史搜索").setHotText("热门搜索");

        homeAdapter = new HomeDynamicAdapter(getActivity(),this, searchItems);
        viewControl = new FooterViewControl(getActivity());
        homeAdapter.addFooterView(viewControl.footerView);
        viewControl.showContent("");
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(homeAdapter);
        homeAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                KeyBoardUtils.hideInputForce(getActivity());

                if (view.getId() == R.id.ll_title) {
                    return;
                }

                Object obj = homeAdapter.getData().get(position);
                if (obj instanceof ISearchGroup) {
                    ISearchGroup itemGroup = (ISearchGroup) obj;

                    if (ItemType.personal_server.equals(itemGroup.searchType()) || ItemType.personal_zhengqi_number.equals(itemGroup.searchType())) {
                        //本地搜索的点击
                        saveKeyWord();
                    }

                    List<ISearchItem> searchItems = itemGroup.data();
                    if (view.getId() == R.id.ll_one) {
                        ISearchItem iSearchItem = searchItems.get(0);
                        clickEvent(iSearchItem);
                        SearchManager.instance().getApi().searchItemClick(getActivity(), iSearchItem);
                    } else if (view.getId() == R.id.ll_two) {
                        ISearchItem iSearchItem = searchItems.get(1);
                        clickEvent(iSearchItem);
                        SearchManager.instance().getApi().searchItemClick(getActivity(), iSearchItem);
                    } else if (view.getId() == R.id.ll_three) {
                        ISearchItem iSearchItem = searchItems.get(2);
                        clickEvent(iSearchItem);
                        SearchManager.instance().getApi().searchItemClick(getActivity(), iSearchItem);
                    } else if (view.getId() == R.id.ll_more) {
                        Bundle bundle = new Bundle();
                        bundle.putString(Table.Key.key_word, searchView.getKeyword());
                        bundle.putString(Table.Key.key_entranceLocation, entranceLocation);
                        bundle.putString(Table.Key.key_search_type, ItemType.personal_theme);
                        bundle.putString(Table.Key.key_search_type_name, itemGroup.groupName());
                        bundle.putBoolean(Table.Key.key_hide_keyboard_flag, true);
                        Map<String, String> map = itemGroup.extraData();
                        String searchContentTypeKey = map.get(ItemType.SearchContentTypeKey);
                        String themeConfigId = map.get(Table.Key.key_themeConfigId);
                        if (!TextUtils.isEmpty(searchContentTypeKey)) {
                            bundle.putString(Table.Key.key_content_search_type, searchContentTypeKey);
                        }
                        bundle.putString(Table.Key.key_themeConfigId, themeConfigId);
                        gotoActivity(MoreSearchActivity.class, bundle);
                    }
                }
            }
        });

        statusView.setTryListener(new IReTryListener() {
            @Override
            public void tryAgain() {
                KeyBoardUtils.hideInputForce(getActivity());
                loadMore();
            }
        });
        viewControl.setTryListener(new IReTryListener() {
            @Override
            public void tryAgain() {
                KeyBoardUtils.hideInputForce(getActivity());
                boolean intercept = interceptSearch();
                if (!intercept) {
                    loadMore();
                } else {
                    saveKeyWord();
                }
            }
        });
    }

    void clickEvent(ISearchItem iSearchItem) {
        String searchTypeName = ItemType.getGroupNameFromType(iSearchItem.searchType());

        //首页搜索结果点击埋点
        Map<String, String> map = new HashMap<>();
        map.put(EventTableImpl.getInstance().getWordLabelAccordingly(), searchView.getKeyword());
        map.put(EventTableImpl.getInstance().getResultLabelAccordingly(), iSearchItem.title());
        map.put(EventTableImpl.getInstance().getSearchTypeAccordingly(), searchTypeName);
        SearchManager.instance().getApi().onEvent(getActivity(), getHomeEventId()
                , EventTable.ResultLabel, map);
    }

    void clearAll() {
        searchItems.clear();
        searchNetItems.clear();
        homeAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initData(Bundle bundleData) {
        entranceLocation = bundleData.getString(Table.Key.key_entranceLocation, Table.Value.EntranceLocation.person_type);
        entranceId = bundleData.getString(Table.Key.key_entranceId, "");
        CommonBizBase.updateSearchHint(entranceLocation, entranceId);

        hotWordPresenter.searchTheme(entranceLocation, entranceId);
        hotWordPresenter.loadHotWord(entranceId);
        String searchHint = bundleData.getString(Table.Key.key_search_hint);
        //如果传入的默认显示搜索关键字不为空，则字节显示传入的关键字
        if (TextUtils.isEmpty(searchHint)) {
            hotWordPresenter.loadHintText(entranceId);
        } else {
            hintText(searchHint);
        }

        loadHistory();
        viewControl.getTvFooterTip().setText(
                Table.Value.EntranceLocation.person_type.equals(entranceLocation) ? "支持所有部门服务、办事指南、政策法规" : "支持所有办事指南"
        );

        Map<String, String> map = new HashMap<>();
        map.put("入口ID", entranceId);
        SearchManager.instance().getApi().onEvent(getActivity(),"综合搜索"
                , "搜索入口", map);
    }

    @Override
    public void afterChange(String keyword) {
        if(TextUtils.isEmpty(keyword)){
            homeAdapter.getData().clear();
            homeAdapter.notifyDataSetChanged();
            showContentView(false);
            return;
        }
        resetSearchSource();
        if (needLocalSearch) {
            homePresenter.searchLocal(keyword, entranceLocation);
        } else {
            loadMore(false, source);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event, boolean intercept) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (SearchManager.instance().isHideNetworkSearch()) {
                //首页搜索词埋点
                Map<String, String> map = new HashMap<>();
                map.put(EventTableImpl.getInstance().getWordLabelAccordingly(), searchView.getKeyword());
                SearchManager.instance().getApi().onEvent(getActivity(), getHomeEventId()
                        , EventTable.WordLabel, map);
                return false;
            }
            if (!intercept) {
                loadMore();
            } else {
                saveKeyWord();
            }
            return false;
        }
        return false;
    }


    @Override
    public void themeData(List<SearchThemeBean> beans) {
        List<SearchThemeBean> needShowBeans = new ArrayList<>();
        if (beans != null) {
            needLocalSearch = false;
            for (SearchThemeBean bean : beans) {
                if (Table.Value.ThemeId.local.equals(bean.id)) {
                    needLocalSearch = true;
                }
                if(bean.hasVerticalEntry == 1){
                    needShowBeans.add(bean);
                }
            }
        }

        if (needShowBeans.size() >= 8) {
            // 最多两行四列
            needShowBeans = needShowBeans.subList(0, 8);
        }

        searchView.setAssignData(needShowBeans);
    }

    @Override
    public void historyData(List<HistoryBean> beans) {
        searchView.setHistoryData(beans);
    }

    @Override
    public void hotData(List<HotBean> beans) {
        searchView.setHotData(beans);
    }

    @Override
    public void hintText(String text) {
        this.hintText = text;
        searchView.setHint(hintText);
    }

    @Override
    public void localData(List<SearchSourceGroup> searchListItemGroups) {
        searchItems.clear();
        searchNetItems.clear();
        searchItems.addAll(searchListItemGroups);
        homeAdapter.notifyDataSetChanged();
        if (SearchManager.instance().isHideNetworkSearch() && searchItems.isEmpty()) {
            statusView.showEmpty();
        } else {
            statusView.showContent();
            viewControl.showContent(searchView.getKeyword());
        }
        loadMore(false, source);
    }

    @Override
    public void netData(List<SearchSourceGroup> searchListItemGroups) {
        for (SearchSourceGroup searchSourceGroup : searchListItemGroups) {
            if (!searchSourceGroup.isLocalSearchGroup()) {
                //只添加网络的数据
                searchNetItems.add(searchSourceGroup);
                searchItems.add(searchSourceGroup);
            }
        }
        homeAdapter.setKeyword(getKeywords(searchView.getKeyword()));
        homeAdapter.notifyDataSetChanged();
        boolean isVoice = ItemType.VoiceSearchType.equals(source);
        if (isVoice) {
            //语音搜索结果埋点
            SearchManager.instance().getApi().onEvent(getActivity(), EventTable.HomeVoiceEventId
                    , EventTable.VoiceResult, new HashMap());
        }

        if (searchListItemGroups.size() > 0) {
            statusView.showContent();
            viewControl.showContent("");

            boolean isShowNoMore = true;
            for (SearchSourceGroup item : searchListItemGroups) {
                if (Boolean.valueOf(item.extraData().get("showMore").toString())) {
                    isShowNoMore = false;
                }
            }
            if (isShowNoMore) {
                viewControl.showEmpty();
            }

            //首页搜索结果埋点
            int totalSize = 0;
            for(SearchSourceGroup searchSourceGroup:searchListItemGroups){
                try {
                    int size = Integer.valueOf((String) searchSourceGroup.extraData.get(Table.Key.key_totalSize));
                    totalSize = totalSize + size;
                }catch (Exception e){

                }
            }
            Map<String, String> map = new HashMap<>();
            map.put(searchView.getKeyword(), totalSize+"");
            SearchManager.instance().getApi().onEvent(getActivity(),"综合搜索"
                    , "搜索结果", map);

        } else {
            if (searchItems.size() > 0) {
                statusView.showContent();
                viewControl.showEmpty();
            } else {
                statusView.showEmpty();
                if (!isVoice) {
                    //首页空结果埋点
                    Map<String, String> map = new HashMap<>();
                    map.put("搜索词", searchView.getKeyword());
                    SearchManager.instance().getApi().onEvent(getActivity(),"综合搜索"
                            , "搜索不到", map);
                }


            }

        }


    }

    @Override
    public void netError(String code, String msg) {
        if (searchItems.size() == 0) {
            statusView.showError();
        } else {
            viewControl.showError();
        }
    }

    @Override
    public void showContentView(boolean show) {
        searchView.showContentView(show);
    }

    @Override
    public void setAnalyzers(List<String> analyzers) {
        this.analyzers.clear();
        if(analyzers != null) {
            this.analyzers.addAll(analyzers);
        }
    }

    void loadMore() {
        loadMore(true, source);
    }

    /***
     * 1.语音输入，2.手动输入
     */
    void loadMore(boolean saveKeyWord, String source) {
        this.analyzers.clear();
        this.source = source;
        searchView.showContentView(true);
        String keyword = searchView.getKeyword();

        if (TextUtils.isEmpty(keyword)) {
            keyword = CommonUtil.getKeyWord(hintText);
            if(!TextUtils.isEmpty(keyword)) {
                searchView.getEtSearch().setText(keyword);
                searchView.getEtSearch().setSelection(searchView.getEtSearch().getText().length());
            }
        }
        if (SearchUtil.isEmpty (keyword)) {
            homeAdapter.getData().clear();
            homeAdapter.notifyDataSetChanged();
            showContentView(false);
            return;
        }

        if (saveKeyWord) {
            saveKeyWord();
        }
        //首页搜索词埋点
        Map<String, String> map = new HashMap<>();
        map.put("搜索词", keyword);
        SearchManager.instance().getApi().onEvent(getActivity(),"综合搜索"
                , "搜索词", map);

        searchItems.removeAll(searchNetItems);
        homeAdapter.notifyDataSetChanged();
        searchNetItems.clear();
        if (searchItems.size() == 0) {
            statusView.showLoading();
        } else {
            viewControl.showLoading();
        }
        homePresenter.searchNet(searchItems, keyword, entranceLocation, entranceId, source);
    }

    private String getHomeEventId() {
        return Table.Value.EntranceLocation.person_type.equals(entranceLocation) ? EventTable.HomePersonEventId : EventTable.HomeEnterpriseEventId;
    }

    /**
     * 深圳特殊需求
     * 输入以什么开头的时候拦截
     *
     * @return
     */
    boolean interceptSearch() {
        return SearchManager.instance().getApi().interceptSearch(getActivity(), searchView.getKeyword());
    }

    void saveKeyWord() {
        String keyword = searchView.getKeyword();
        if (!SearchUtil.isEmpty(keyword)) {
            historyPresenter.saveKeyword(keyword, historyType());
        }
    }

    void loadHistory() {
        historyPresenter.loadHistory(historyType());
    }

    String historyType() {
        return homeType + "_" + entranceLocation;
    }

    @Override
    public void onIconClick() {
        SearchManager.instance().getApi().onEvent(getActivity(), getHomeEventId()
                , EventTable.ClearLabel, null);
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ISearchItem iSearchItem = (ISearchItem) parent.getItemAtPosition(position);
        clickEvent(iSearchItem);
        SearchManager.instance().getApi().searchItemClick(getActivity(), iSearchItem);
        saveKeyWord();
    }


}
