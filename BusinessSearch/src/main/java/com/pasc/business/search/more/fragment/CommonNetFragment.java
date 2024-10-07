package com.pasc.business.search.more.fragment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.pasc.business.search.ItemType;
import com.pasc.business.search.R;
import com.pasc.business.search.SearchManager;
import com.pasc.business.search.common.model.HotBean;
import com.pasc.business.search.common.model.SearchThemeBean;
import com.pasc.business.search.common.presenter.HistoryPresenter;
import com.pasc.business.search.common.presenter.HotWordPresenter;
import com.pasc.business.search.common.view.HistoryView;
import com.pasc.business.search.common.view.HotView;
import com.pasc.business.search.customview.ClearEditText;
import com.pasc.business.search.customview.CustomLoadMoreView;
import com.pasc.business.search.customview.IReTryListener;
import com.pasc.business.search.customview.MyOnEditorActionListener;
import com.pasc.business.search.customview.SearchTagView;
import com.pasc.business.search.customview.StatusView;
import com.pasc.business.search.more.adapter.MoreSearchDynamicAdapter;
import com.pasc.business.search.more.presenter.MoreSearchPresenter;
import com.pasc.business.search.more.view.MoreSearchView;
import com.pasc.business.search.router.Table;
import com.pasc.business.search.util.CommonUtil;
import com.pasc.lib.search.ISearchItem;
import com.pasc.lib.search.base.BaseMvpFragment;
import com.pasc.lib.search.base.MultiPresenter;
import com.pasc.lib.search.common.IKeywordItem;
import com.pasc.lib.search.db.history.HistoryBean;
import com.pasc.lib.search.util.KeyBoardUtils;
import com.pasc.lib.search.util.SearchUtil;
import com.pasc.lib.search.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangzijian
 * @date 2019/2/21
 * @des 证企号服务
 * @modify
 **/
public class CommonNetFragment extends BaseMvpFragment<MultiPresenter> implements MoreSearchView, HistoryView, MyOnEditorActionListener, ClearEditText.EditTextChangeListener, HotView {

    private final String moreType = "more";
    SearchTagView searchView;
    HistoryPresenter historyPresenter;
    MoreSearchPresenter moreSearchPresenter;
    HotWordPresenter hotWordPresenter;
    MoreSearchDynamicAdapter searchAdapter;
    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    StatusView statusView;
    List<ISearchItem> listItems = new ArrayList<> ();
    String mSearchType;
    String source;
    String mSearchTypeName;
    View contentView;
    TextView tvCountTip;
    int pageNo = 1;
    final int pageSize = 20;
    //入口位置 1：个人版  2：企业版
    String entranceLocation;
    CustomLoadMoreView loadMoreView;
    String hintText;

    @Override
    public MultiPresenter createPresenter() {
        MultiPresenter multiPresenter = new MultiPresenter ();
        historyPresenter = new HistoryPresenter ();
        moreSearchPresenter = new MoreSearchPresenter ();
        hotWordPresenter = new HotWordPresenter ();
        multiPresenter.requestPresenter (hotWordPresenter, historyPresenter, moreSearchPresenter);
        return multiPresenter;
    }


    @Override
    public int initLayout() {
        return R.layout.pasc_search_more_fragment;
    }

    @LayoutRes int contentLayout(){
        return R.layout.pasc_search_more_net_content;
    }
    @Override
    protected void initView() {
        searchView = rootView.findViewById (R.id.searchView);
        contentView = LayoutInflater.from (getActivity ()).inflate (contentLayout (), null);
        refreshLayout = contentView.findViewById (R.id.refresh_layout);
        recyclerView = contentView.findViewById (R.id.recyclerView);
        statusView = contentView.findViewById (R.id.statusView);
        statusView.setContentView (refreshLayout);
        tvCountTip = contentView.findViewById (R.id.tv_count_tip);
        searchView
                .addContentView (contentView)
                .setOnCancelClickListener (new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {
                        searchView.showKeyborad (false);
                        getActivity ().finish ();
                    }
                })
                .setOnDeleteHistoryClickListener (new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {
                        historyPresenter.clearHistory (historyType ());
                    }
                })
                .setHistoryClickListener (new SearchTagView.ItemClickListener () {
                    @Override
                    public void itemClick(IKeywordItem iKeywordItem, boolean intercept) {
                        if (intercept) {
                            saveKeyWord ();
                        } else {
                            pageNo = 1;
                            loadMore (false);
                        }
                    }
                })
                .setHotClickListener (new SearchTagView.ItemClickListener () {
                    @Override
                    public void itemClick(IKeywordItem iKeywordItem, boolean intercept) {
                        if (intercept) {
                            saveKeyWord ();
                        } else {
                            pageNo = 1;
                            loadMore (false);
                        }
                    }
                })
                .setHistoryText ("历史搜索").setHotText ("热门搜索");

        searchAdapter = new MoreSearchDynamicAdapter (getActivity (), listItems);
        loadMoreView = new CustomLoadMoreView ();
        searchAdapter.setLoadMoreView (loadMoreView);
        statusView.setTryListener (new IReTryListener () {
            @Override
            public void tryAgain() {
                pageNo = 1;
                loadMore (false);
            }
        });
        refreshLayout.setOnRefreshListener (new SwipeRefreshLayout.OnRefreshListener () {
            @Override
            public void onRefresh() {
                pageNo = 1;
                loadMore (true);
            }
        });
        searchAdapter.setOnLoadMoreListener (new BaseQuickAdapter.RequestLoadMoreListener () {
            @Override
            public void onLoadMoreRequested() {
                if (refreshLayout.isRefreshing ()) {
                    searchAdapter.loadMoreComplete ();
                    return;
                }
                pageNo++;
                loadMore (false);
            }
        }, recyclerView);
        recyclerView.setLayoutManager (new LinearLayoutManager (getActivity ()));
        recyclerView.setAdapter (searchAdapter);
        searchAdapter.setOnItemChildClickListener (new BaseQuickAdapter.OnItemChildClickListener () {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId () == R.id.ll_search_item) {
                    ISearchItem bean = listItems.get (position);
                    KeyBoardUtils.hideInputForce (getActivity ());
                    SearchManager.instance ().getApi ().searchItemClick (getActivity (), bean);
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        searchView.setEditTextChangeListener (this);
        searchView.setOnEditorActionListener (this);
    }

    @Override
    public void onPause() {
        super.onPause();
        searchView.setEditTextChangeListener(null);
        searchView.setOnEditorActionListener (null);
    }

    @Override
    protected void initData(Bundle bundleData) {
        entranceLocation = bundleData.getString (Table.Key.key_entranceLocation, Table.Value.EntranceLocation.person_type);
        source = bundleData.getString (Table.Key.key_content_search_type, ItemType.ManualSearchType);
        String mKeyword = bundleData.getString (Table.Key.key_word);
        mSearchType = bundleData.getString (Table.Key.key_search_type);
        mSearchTypeName = bundleData.getString (Table.Key.key_search_type_name);
//        if (SearchUtil.isEmpty (mSearchTypeName)) {
//            mSearchTypeName = ItemType.getGroupNameFromType (mSearchType);
//        }
        searchView.showKeyborad (false);
        loadHistory ();

        if (!SearchUtil.isEmpty (mKeyword)) {
            searchView.setKeyword (mKeyword);
            loadMore (false);
        }
        if (!bundleData.getBoolean (Table.Key.key_hide_keyboard_flag, false)) {
            KeyBoardUtils.openKeybord (searchView.getEtSearch (), getActivity ());
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event, boolean intercept) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (!intercept) {
                pageNo = 1;
                loadMore (false);
            } else {
                saveKeyWord ();
            }
            return false;
        }
        return false;
    }


    @Override
    public void localData(List<? extends ISearchItem> items) {
    }

    String searchCountTip(){
        return "个";
    }
    @Override
    public void netData(List<? extends ISearchItem> items, int totalCount) {
        refreshLayout.setRefreshing (false);
        searchAdapter.loadMoreComplete ();
        searchAdapter.setKeyword (getKeywords(searchView.getKeyword ()));
        listItems.addAll (items);
        if (items.size () < pageSize) {
            //没有更多
            if (listItems.size () < pageSize) {
                //总共没那么多数据
                loadMoreView.setLoadEndViewVisible (true);
                searchAdapter.loadMoreEnd (false);
            } else {
                loadMoreView.setLoadEndViewVisible (false);
                searchAdapter.loadMoreEnd (false);
            }

        } else {
            loadMoreView.setLoadEndViewVisible (false);
            searchAdapter.loadMoreComplete ();
        }


        searchAdapter.notifyDataSetChanged ();
        tvCountTip.setVisibility ((totalCount > 0 && listItems.size () > 0) ? View.VISIBLE : View.GONE);
        tvCountTip.setText ("为您找到" + totalCount + searchCountTip () + "相关结果");
        if (listItems.size () > 0) {
            statusView.showContent ();
        } else {
            statusView.showEmpty ();

        }
    }

    @Override
    public void onSearchError(String code, String msg) {
        refreshLayout.setRefreshing (false);
        if (listItems.size () > 0) {
            searchAdapter.loadMoreFail ();
        } else {
            statusView.showError ();
        }
    }

    @Override
    public void setAnalyzers(List<String> analyzers) {
        this.analyzers.clear();
        if(analyzers != null) {
            this.analyzers.addAll(analyzers);
        }
    }

    @Override
    public void showContentView(boolean show) {
        searchView.showContentView (show);
    }

    void loadMore(boolean isRefresh){
        loadMore(isRefresh,true);
    }


    void loadMore(boolean isRefresh, boolean saveKeyWord) {
        this.analyzers.clear();
        String keyword = searchView.getKeyword ();
        if (TextUtils.isEmpty(keyword)) {
            keyword = CommonUtil.getKeyWord(hintText);
            if(!TextUtils.isEmpty(keyword)) {
                searchView.getEtSearch().setText(keyword);
                searchView.getEtSearch().setSelection(searchView.getEtSearch().getText().length());
            }
        }
        if (SearchUtil.isEmpty (keyword)) {
            refreshLayout.setRefreshing (false);
            return;
        }
        if(saveKeyWord) {
            saveKeyWord();
        }
        if (isRefresh) {
            pageNo = 1;
            if (listItems.size () == 0) {
                statusView.showLoading ();
            }
            listItems.clear ();
            searchAdapter.notifyDataSetChanged ();
        } else if (pageNo == 1) {
            listItems.clear ();
            searchAdapter.notifyDataSetChanged ();
            statusView.showLoading ();
        }
        int size = listItems.size ();
        tvCountTip.setVisibility (size > 0 ? View.VISIBLE : View.GONE);
//        tvCountTip.setText ("找到" + size + "个" + mSearchTypeName);
        searchNet ();
    }

    void searchNet() {

    }

    @Override
    public void historyData(List<HistoryBean> beans) {
        searchView.setHistoryData (beans);
    }

    @Override
    public void afterChange(String s) {
        if (SearchUtil.isEmpty (s)) {
            listItems.clear ();
            searchAdapter.notifyDataSetChanged ();
            showContentView (false);
        }else {
            loadMore(false, false);
        }
    }

    void saveKeyWord() {
        String keyword = searchView.getKeyword ();
        if (!SearchUtil.isEmpty (keyword)) {
            historyPresenter.saveKeyword (keyword, historyType ());
        } else {
            ToastUtil.showToast ("请输入搜索内容");

        }
    }

    void loadHistory() {
        historyPresenter.loadHistory (historyType ());
    }

    String historyType() {
        return moreType + "_" + mSearchType + "_" + entranceLocation;
    }

    @Override
    public void hotData(List<HotBean> beans) {
        searchView.setHotData (beans);

    }

    @Override
    public void hintText(String text) {
        searchView.setHint (text);
    }

    @Override
    public void themeData(List<SearchThemeBean> beans) {

    }
}
