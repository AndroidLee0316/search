package com.pasc.business.search.more.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.pasc.business.search.common.model.HotBean;
import com.pasc.business.search.common.model.SearchThemeBean;
import com.pasc.business.search.common.presenter.HistoryPresenter;
import com.pasc.business.search.common.presenter.HotWordPresenter;
import com.pasc.business.search.customview.DropDownMenu;
import com.pasc.business.search.common.view.HistoryView;
import com.pasc.business.search.common.view.HotView;
import com.pasc.business.search.more.adapter.MoreSearchDynamicAdapter;
import com.pasc.business.search.more.adapter.PolicySearchAdapter;
import com.pasc.business.search.more.model.policy.UnitSearchBean;
import com.pasc.business.search.more.presenter.MoreSearchPresenter;
import com.pasc.business.search.more.presenter.PolicyPresenter;
import com.pasc.business.search.more.view.MoreSearchView;
import com.pasc.business.search.more.view.PolicyDetailSearchView;
import com.pasc.business.search.router.Table;
import com.pasc.lib.search.ISearchItem;
import com.pasc.business.search.ItemType;
import com.pasc.business.search.R;
import com.pasc.business.search.SearchManager;
import com.pasc.lib.search.base.BaseMvpFragment;
import com.pasc.lib.search.base.MultiPresenter;
import com.pasc.business.search.customview.ClearEditText;
import com.pasc.business.search.customview.CustomLoadMoreView;
import com.pasc.lib.search.common.IKeywordItem;
import com.pasc.business.search.customview.IReTryListener;
import com.pasc.business.search.customview.MyOnEditorActionListener;
import com.pasc.business.search.customview.SearchTagView;
import com.pasc.business.search.customview.StatusView;
import com.pasc.lib.search.db.history.HistoryBean;
import com.pasc.lib.search.util.DeviceUtil;
import com.pasc.lib.search.util.KeyBoardUtils;
import com.pasc.lib.search.util.SearchUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yangzijian
 * @date 2019/2/21
 * @des
 * @modify
 **/
public class PolicyFragment extends BaseMvpFragment<MultiPresenter> implements MoreSearchView, HistoryView, MyOnEditorActionListener, ClearEditText.EditTextChangeListener ,
        PolicyDetailSearchView,HotView {


    private SearchTagView searchView;
    private HistoryPresenter historyPresenter;
    private MoreSearchPresenter moreSearchPresenter;
    private final String moreType = "more";
    private MoreSearchDynamicAdapter searchAdapter;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private StatusView statusView;
    private List<ISearchItem> listItems = new ArrayList<> ();
    private String mSearchType;
    private String source;
    private String mSearchTypeName;
    private View contentView;
    private LinearLayout llContent;
    private TextView tvCountTip;
    private View line_policy;
    private int pageNo = 1;
    private final int pageSize = 20;
    private HotWordPresenter hotWordPresenter;
    private PolicyPresenter policyPresenter;
    private DropDownMenu dropDownMenu;
    private RecyclerView rvPolicyDatas;
    private PolicySearchAdapter policyAdapter;
    private String mCode = "";
    //入口位置 1：个人版  2：企业版
    private String entranceLocation;
    private CustomLoadMoreView loadMoreView;
    @Override
    public MultiPresenter createPresenter() {
        MultiPresenter multiPresenter = new MultiPresenter ();
        historyPresenter = new HistoryPresenter ();
        moreSearchPresenter = new MoreSearchPresenter ();
        policyPresenter = new PolicyPresenter ();
        hotWordPresenter=new HotWordPresenter ();
        multiPresenter.requestPresenter (historyPresenter,hotWordPresenter, moreSearchPresenter,policyPresenter);
        return multiPresenter;
    }


    @Override
    public int initLayout() {
        return R.layout.pasc_search_more_fragment;
    }


    @Override
    protected void initView() {
        searchView = rootView.findViewById (R.id.searchView);
        contentView = LayoutInflater.from (getActivity ()).inflate (R.layout.pasc_search_more_policy_content, null);
        llContent=contentView.findViewById (R.id.ll_content);
        refreshLayout=contentView.findViewById (R.id.refresh_layout);
        recyclerView = contentView.findViewById (R.id.recyclerView);
        statusView=contentView.findViewById (R.id.statusView);
        statusView.setContentView (refreshLayout);
        tvCountTip = contentView.findViewById (R.id.tv_count_tip);
        line_policy=contentView.findViewById (R.id.line_policy);
        dropDownMenu = contentView.findViewById (R.id.dropDownMenu);
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
                    public void itemClick(IKeywordItem iKeywordItem,boolean intercept) {
                        if (intercept){
                            saveKeyWord ();
                        }else {
                            pageNo=1;
                            loadMore (false);
                        }
                    }
                })
                .setHotClickListener (new SearchTagView.ItemClickListener () {
                    @Override
                    public void itemClick(IKeywordItem iKeywordItem,boolean intercept) {
                        if (intercept){
                            saveKeyWord ();
                        }else {
                            pageNo=1;
                            loadMore (false);
                        }
                    }
                })
                .setHistoryText ("历史搜索").setHotText ("热门搜索");

        searchAdapter = new MoreSearchDynamicAdapter (getActivity (), listItems);
        loadMoreView=new CustomLoadMoreView ();
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
                    SearchManager.instance ().getApi ().searchItemClick (getActivity (),bean);
                }
            }
        });

    }


    @Override
    protected void initData(Bundle bundleData) {
        entranceLocation=bundleData.getString (Table.Key.key_entranceLocation, Table.Value.EntranceLocation.person_type);
        source=bundleData.getString (Table.Key.key_content_search_type,ItemType.ManualSearchType);
        String mKeyword = bundleData.getString (Table.Key.key_word);
        mSearchType = bundleData.getString (Table.Key.key_search_type);
        mSearchTypeName = bundleData.getString (Table.Key.key_search_type_name);
        if (SearchUtil.isEmpty (mSearchTypeName)){
            mSearchTypeName= ItemType.getGroupNameFromType (mSearchType);
        }
        searchView.showKeyborad (false);
        hotWordPresenter.loadHintText (Table.Value.MoreType.personal_policy_page);
        hotWordPresenter.loadHotWord (Table.Value.MoreType.personal_policy_page);
        loadHistory ();
        initDropDownMenu ();
        policyUnitSearchFromNet ();
        if (!TextUtils.isEmpty (mKeyword)) {
            searchView.setKeyword (mKeyword);
            pageNo = 1;
            loadMore (false);
        }
        if (! bundleData.getBoolean (Table.Key.key_hide_keyboard_flag,false)){
            KeyBoardUtils.openKeybord (searchView.getEtSearch (),getActivity ());
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event,boolean intercept) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (!intercept) {
                pageNo = 1;
                loadMore (false);
            }else {
                saveKeyWord ();
            }
            return false;
        }
        return false;
    }

    @Override
    public void localData(List<? extends ISearchItem> items) {
    }



    @Override
    public void netData(List<? extends ISearchItem> items,int totalCount) {
        refreshLayout.setRefreshing (false);
        searchAdapter.loadMoreComplete ();
        searchAdapter.setKeyword (getKeywords(searchView.getKeyword ()));
        listItems.addAll (items);
        if (items.size () < pageSize) {
            //没有更多
            if (listItems.size() < pageSize) {
                //总共没那么多数据
                loadMoreView.setLoadEndViewVisible (true);
                searchAdapter.loadMoreEnd(false);
            } else {
                loadMoreView.setLoadEndViewVisible (false);
                searchAdapter.loadMoreEnd(false);
            }

        } else {
            loadMoreView.setLoadEndViewVisible (false);
            searchAdapter.loadMoreComplete();
        }


        searchAdapter.notifyDataSetChanged ();
        int visibility=(totalCount > 0 &&listItems.size ()>0) ? View.VISIBLE : View.GONE;
        line_policy.setVisibility (visibility);
        tvCountTip.setVisibility (visibility);
        tvCountTip.setText ("找到" + totalCount + "条" + mSearchTypeName);
        if (listItems.size () > 0) {
            statusView.showContent ();
        } else {
            statusView.showEmpty ();

        }
        //全部单位的时候没数据隐藏
        showDropDownMenu ( SearchUtil.isEmpty (mCode) && listItems.size ()==0);
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

    }

    @Override
    public void showContentView(boolean show) {
        searchView.showContentView (show);
    }

    private String headers[] = {"全部单位"};
    private List<View> popupViews = new ArrayList<> ();

    private void initDropDownMenu() {
        rvPolicyDatas = new RecyclerView (getActivity ());
        popupViews.add (rvPolicyDatas);
        TextView contentView = new TextView (getActivity ());
        contentView.setLayoutParams (new ViewGroup.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, 0));
        contentView.setAlpha (0);
        dropDownMenu.setDropDownMenu (Arrays.asList (headers), popupViews, contentView);
    }

    private void refreshPolicyAdapter(final UnitSearchBean resp) {
        if (resp == null || resp.list == null) {
            return;
        }
        if (policyAdapter == null) {
            policyAdapter = new PolicySearchAdapter (getActivity (), resp.list);
            rvPolicyDatas.setLayoutManager (new LinearLayoutManager (getActivity ()));
            rvPolicyDatas.setAdapter (policyAdapter);
            policyAdapter.setOnItemClickListener (new BaseQuickAdapter.OnItemClickListener () {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    policyAdapter.selectItem (position);
                    dropDownMenu.setTabText (15,policyAdapter.getData ().get (position).unitName);
                    dropDownMenu.closeMenu ();
                    detailSearchFromNet ( resp.list.get (position).code);
                }
            });
        } else {
            policyAdapter.setNewData (resp.list);
        }
    }

    @Override
    public void getPolicyUnitSearch(UnitSearchBean bean) {
        refreshPolicyAdapter (bean);
    }

    @Override
    public void onError(String code, String msg) {
//        policyAdapter.setNewData (new ArrayList<UnitSearchBean.DataBean> ());
    }


    /**
     * 发文单位搜索
     */
    private void policyUnitSearchFromNet() {
        policyPresenter.getPolicyUnitSearchFromNet ();
    }

    private void detailSearchFromNet( String code) {
        this.mCode = code;
        pageNo = 1;
        loadMore (false);

    }

    void loadMore(boolean isRefresh) {

        String keyword=searchView.getKeyword ();

        if (SearchUtil.isEmpty (keyword)){
            refreshLayout.setRefreshing (false);
            return;
        }
        saveKeyWord ();

        if (isRefresh){
            pageNo=1;
            if (listItems.size ()==0){
                statusView.showLoading ();
            }
            listItems.clear ();
            tvCountTip.setVisibility (View.GONE);
            searchAdapter.notifyDataSetChanged ();
        } else if (pageNo==1){
            listItems.clear ();
            searchAdapter.notifyDataSetChanged ();
            statusView.showLoading ();
        }
        int size=listItems.size ();
        int visibility=size > 0 ? View.VISIBLE : View.GONE;
        line_policy.setVisibility (visibility);
        tvCountTip.setVisibility (visibility);

        showDropDownMenu (  SearchUtil.isEmpty (mCode) && listItems.size ()==0);
        moreSearchPresenter.searchPolicy (source,keyword,entranceLocation,mCode,pageNo,pageSize);

    }

    void showDropDownMenu(boolean hide){
        dropDownMenu.setVisibility (hide?View.GONE:View.VISIBLE);
        llContent.setPadding (0,hide?0: DeviceUtil.dpTpPx (getActivity (),45),0,0);

    }

    @Override
    public void historyData(List<HistoryBean> beans) {
        searchView.setHistoryData (beans);
    }

    @Override
    public void afterChange(String s) {
        if (SearchUtil.isEmpty (s)){
            listItems.clear ();
            searchAdapter.notifyDataSetChanged ();
            showContentView (false);
            line_policy.setVisibility (View.GONE);
            tvCountTip.setVisibility (View.GONE);
            dropDownMenu.setVisibility ( ( SearchUtil.isEmpty (mCode) && listItems.size ()==0)  ? View.GONE:View.VISIBLE);
            dropDownMenu.closeMenu ();
        }

    }

    void saveKeyWord() {
        String keyword = searchView.getKeyword();
        if (!SearchUtil.isEmpty(keyword)) {
            historyPresenter.saveKeyword(keyword, historyType ());
        }
    }

    void loadHistory() {
        historyPresenter.loadHistory(historyType ());
    }
    String historyType(){
        return moreType + "_" + mSearchType+"_"+entranceLocation;
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
