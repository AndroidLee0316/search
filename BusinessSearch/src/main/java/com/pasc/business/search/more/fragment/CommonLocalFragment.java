package com.pasc.business.search.more.fragment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.pasc.business.search.customview.FooterViewControl;
import com.pasc.business.search.customview.MyOnEditorActionListener;
import com.pasc.business.search.customview.SearchTagView;
import com.pasc.business.search.customview.StatusView;
import com.pasc.business.search.more.adapter.MoreSearchDynamicAdapter;
import com.pasc.business.search.more.presenter.MoreSearchPresenter;
import com.pasc.business.search.more.view.MoreSearchView;
import com.pasc.business.search.router.Table;
import com.pasc.lib.search.ISearchItem;
import com.pasc.lib.search.base.BaseMvpFragment;
import com.pasc.lib.search.base.MultiPresenter;
import com.pasc.lib.search.common.IKeywordItem;
import com.pasc.lib.search.db.history.HistoryBean;
import com.pasc.lib.search.util.KeyBoardUtils;
import com.pasc.lib.search.util.SearchUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangzijian
 * @date 2019/3/7
 * @des
 * @modify
 **/
public class CommonLocalFragment extends BaseMvpFragment<MultiPresenter> implements MoreSearchView, HotView, HistoryView, MyOnEditorActionListener, ClearEditText.EditTextChangeListener {
    SearchTagView searchView;
    HistoryPresenter historyPresenter;
    MoreSearchPresenter moreSearchPresenter;
    HotWordPresenter hotWordPresenter;

    private final String moreType = "more";
    MoreSearchDynamicAdapter searchAdapter;
    FooterViewControl viewControl;
    RecyclerView recyclerView;
    List<ISearchItem> listItems = new ArrayList<> ();
    String mSearchType;
    String mSearchTypeName;
    View contentView;
    TextView tvCountTip;
    StatusView statusView;
    //入口位置 1：个人版  2：企业版
    String entranceLocation;

    @Override
    public MultiPresenter createPresenter() {
        MultiPresenter multiPresenter = new MultiPresenter ();
        historyPresenter = new HistoryPresenter ();
        moreSearchPresenter = new MoreSearchPresenter ();
        hotWordPresenter = new HotWordPresenter ();
        multiPresenter.requestPresenter (historyPresenter, hotWordPresenter, moreSearchPresenter);
        return multiPresenter;
    }

    @Override
    protected int initLayout() {
        return R.layout.pasc_search_more_common_fragment;
    }

    @LayoutRes
    int contentLayout() {
        return R.layout.pasc_search_more_common_content;
    }

    @Override
    protected void initView() {
        searchView = rootView.findViewById (R.id.searchView);
        contentView = LayoutInflater.from (getActivity ()).inflate (contentLayout (), null);
        statusView = contentView.findViewById (R.id.statusView);
        recyclerView = contentView.findViewById (R.id.recyclerView);
        statusView.setContentView (recyclerView);
        tvCountTip = contentView.findViewById (R.id.tv_count_tip);
        searchView
                .addContentView (contentView)
                .setEditTextChangeListener (this)
                .setOnEditorActionListener (this)
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
                        saveKeyWord (true);
                    }
                })
                .setHotClickListener (new SearchTagView.ItemClickListener () {
                    @Override
                    public void itemClick(IKeywordItem iKeywordItem, boolean intercept) {
                        saveKeyWord (true);
                    }
                })
                .setHistoryText ("历史搜索").setHotText ("热门搜索");

        searchAdapter = new MoreSearchDynamicAdapter (getActivity (), listItems);
        viewControl = new FooterViewControl(getActivity());
        searchAdapter.addFooterView(viewControl.footerView);
        viewControl.showContent("");
        recyclerView.setLayoutManager (new LinearLayoutManager (getActivity ()));
        recyclerView.setAdapter (searchAdapter);
        searchAdapter.setOnItemChildClickListener (new BaseQuickAdapter.OnItemChildClickListener () {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId () == R.id.ll_search_item) {
                    saveKeyWord (true);
                    KeyBoardUtils.hideInputForce (getActivity ());
                    ISearchItem bean = listItems.get (position);
                    SearchManager.instance ().getApi ().searchItemClick (getActivity (), bean);
                }
            }
        });
    }

    @Override
    protected void initData(Bundle bundleData) {
        entranceLocation = bundleData.getString (Table.Key.key_entranceLocation, Table.Value.EntranceLocation.person_type);
        String mKeyword = bundleData.getString (Table.Key.key_word);
        mSearchType = bundleData.getString (Table.Key.key_search_type);
        mSearchTypeName = bundleData.getString (Table.Key.key_search_type_name);
        if (SearchUtil.isEmpty (mSearchTypeName)) {
            mSearchTypeName = ItemType.getGroupNameFromType (mSearchType);
        }
        searchView.showKeyborad (false);
        loadHistory ();
        if (!SearchUtil.isEmpty (mKeyword)) {
            searchView.setKeyword (mKeyword);
        }
        if (!bundleData.getBoolean (Table.Key.key_hide_keyboard_flag, false)) {
            KeyBoardUtils.openKeybord (searchView.getEtSearch (), getActivity ());
        }

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event, boolean intercept) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            saveKeyWord (false);
            return false;
        }
        return false;
    }

    @Override
    public void historyData(List<HistoryBean> beans) {
        searchView.setHistoryData (beans);
    }

    String searchCountTip() {
        return "个";
    }

    @Override
    public void localData(List<? extends ISearchItem> items) {
        listItems.clear ();
        listItems.addAll (items);
        searchAdapter.setKeyword (getKeywords(searchView.getKeyword ()));
        searchAdapter.notifyDataSetChanged ();
        int size = listItems.size ();
        if (size > 0) {
            statusView.showContent ();
        } else {
            statusView.showEmpty ();
        }
        tvCountTip.setVisibility (size > 0 ? View.VISIBLE : View.GONE);
        tvCountTip.setText ("找到" + size + searchCountTip () + mSearchTypeName);
        viewControl.showEmpty();
    }

    @Override
    public void netData(List<? extends ISearchItem> items, int totalCount) {

    }

    @Override
    public void showContentView(boolean show) {
        searchView.showContentView (show);
    }


    @Override
    public void onSearchError(String code, String msg) {

    }

    @Override
    public void setAnalyzers(List<String> analyzers) {

    }

    @Override
    public void afterChange(String keyword) {
        moreSearchPresenter.searchLocalByType (keyword, entranceLocation, mSearchType);

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

    void saveKeyWord(boolean needSave) {
        String keyword = searchView.getKeyword ();
        if (!SearchUtil.isEmpty (keyword)) {
            if (needSave) {
                historyPresenter.saveKeyword (keyword, historyType ());
            }
        }
    }

    void loadHistory() {
        historyPresenter.loadHistory (historyType ());
    }

    String historyType() {
        return moreType + "_" + mSearchType + "_" + entranceLocation;
    }
}
