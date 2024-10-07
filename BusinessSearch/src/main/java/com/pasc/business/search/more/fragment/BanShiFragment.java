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
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.pasc.business.search.event.EventTable;
import com.pasc.business.search.more.adapter.BanShiAdapter;
import com.pasc.business.search.more.adapter.DeptSearchAdapter;
import com.pasc.business.search.more.adapter.MoreSearchDynamicAdapter;
import com.pasc.business.search.more.model.task.AreaBean;
import com.pasc.business.search.more.model.task.DeptBean;
import com.pasc.business.search.more.model.task.IMultiPickBean;
import com.pasc.business.search.more.presenter.BanShiPresenter;
import com.pasc.business.search.more.presenter.MoreSearchPresenter;
import com.pasc.business.search.more.view.BanShiView;
import com.pasc.business.search.more.view.MoreSearchView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author yangzijian
 * @date 2019/2/21
 * @des 办事指南
 * @modify
 **/
public class BanShiFragment extends BaseMvpFragment<MultiPresenter> implements MoreSearchView, HistoryView, MyOnEditorActionListener, ClearEditText.EditTextChangeListener,
        BanShiView, HotView {

    private SearchTagView searchView;
    private HistoryPresenter historyPresenter;
    private MoreSearchPresenter moreSearchPresenter;
    private HotWordPresenter hotWordPresenter;

    private final String moreType = "more";
    private MoreSearchDynamicAdapter searchAdapter;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private LinearLayout llContent;
    private StatusView statusView;
    private List<ISearchItem> listItems = new ArrayList<> ();
    private String mSearchType;
    private String mSearchTypeName;
    public String source;
    private View contentView;
    private View banshiLine;
    private TextView tvCountTip;
    private View rlHeader;
    private BanShiPresenter multiPickPresenter;
    private DropDownMenu dropDownMenu;
    private final String allDept = "全部部门";
    private String[] headers = {"地区", allDept, ""};
    private List<View> popupViews = new ArrayList<> ();
    private View areaView;
    private RecyclerView rvDept;
    private DeptSearchAdapter deptSearchAdapter;
    private BanShiAdapter<AreaBean> areaAdapter;
    private BanShiAdapter<DeptBean> streetAdapter;
    private int pageNo = 1;
    private final int pageSize = 20;
    private String mCurrentAreaCode;
    private String mBureauCode;
    private String mAreaCode;
    private String entranceLocation;
    /***1:可在线办理; ""查出全部; 传入其他无效*/
    private boolean mIsOnline = false;
    /****是否为网厅 深圳特殊需求****/
    private boolean mIsWangTing = false;
    private CustomLoadMoreView loadMoreView;

    @Override
    public MultiPresenter createPresenter() {
        MultiPresenter multiPresenter = new MultiPresenter ();
        historyPresenter = new HistoryPresenter ();
        moreSearchPresenter = new MoreSearchPresenter ();
        multiPickPresenter = new BanShiPresenter ();
        hotWordPresenter = new HotWordPresenter ();
        multiPresenter.requestPresenter (historyPresenter, moreSearchPresenter, multiPickPresenter, hotWordPresenter);
        return multiPresenter;
    }


    @Override
    public int initLayout() {
        return R.layout.pasc_search_more_fragment;
    }

    @Override
    protected void initView() {
        searchView = rootView.findViewById (R.id.searchView);
        contentView = LayoutInflater.from (getActivity ()).inflate (R.layout.pasc_search_more_banshi_content, null);
        rlHeader = contentView.findViewById (R.id.rlHeader);
        llContent = contentView.findViewById (R.id.ll_content);
        banshiLine = contentView.findViewById (R.id.banshi_line);
        recyclerView = contentView.findViewById (R.id.recyclerView);
        tvCountTip = contentView.findViewById (R.id.tv_count_tip);
        refreshLayout = contentView.findViewById (R.id.refresh_layout);
        dropDownMenu = contentView.findViewById (R.id.multiPick_dropDownMenu);
        statusView = contentView.findViewById (R.id.statusView);
        statusView.setContentView (refreshLayout);
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
                        //首页历史搜索埋点
                        Map<String, String> map = new HashMap<> ();
                        map.put (EventTable.WordLabel, iKeywordItem.keyword ());
                        SearchManager.instance ().getApi ().onEvent (getActivity (), getBanShiEventId ()
                                , EventTable.HistoryLabel, map);
                        if (intercept){
                            saveKeyWord ();
                        }else {
                            pageNo = 1;
                            loadMore (false);
                        }
                    }
                })
                .setHotClickListener (new SearchTagView.ItemClickListener () {
                    @Override
                    public void itemClick(IKeywordItem iKeywordItem,boolean intercept) {
                        //首页热词搜索埋点
                        Map<String, String> map = new HashMap<> ();
                        map.put (EventTable.WordLabel, iKeywordItem.keyword ());
                        SearchManager.instance ().getApi ().onEvent (getActivity (), getBanShiEventId ()
                                , EventTable.HotWordLabel, map);
                        if (intercept){
                            saveKeyWord ();
                        }else {
                            pageNo = 1;
                            loadMore (false);
                        }
                    }
                })
                .setHistoryText ("历史搜索").setHotText ("热门搜索");

        searchAdapter = new MoreSearchDynamicAdapter (getActivity (), listItems);
        searchAdapter.setLoadMoreView (loadMoreView = new CustomLoadMoreView ());

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

        CheckBox checkOnline = contentView.findViewById (R.id.checkOnline);
        checkOnline.setOnCheckedChangeListener (new CompoundButton.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsOnline = isChecked;
                pageNo = 1;
                loadMore (false);
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
                    KeyBoardUtils.hideInputForce (getActivity ());
                    ISearchItem bean = listItems.get (position);
                    clickEvent (bean);
                    SearchManager.instance ().getApi ().searchItemClick (getActivity (), bean);
                }
            }
        });

    }


    void clickEvent(ISearchItem iSearchItem) {

        //办事指南搜索结果点击埋点
        Map<String, String> map = new HashMap<> ();
        map.put (EventTable.WordLabel, searchView.getKeyword ());
        map.put (EventTable.ResultLabel, iSearchItem.title ());
        SearchManager.instance ().getApi ().onEvent (getActivity (), getBanShiEventId ()
                , EventTable.ResultLabel, map);
    }

    @Override
    protected void initData(Bundle bundleData) {
        String mKeyword = bundleData.getString (Table.Key.key_word);
        source=bundleData.getString (Table.Key.key_content_search_type,ItemType.ManualSearchType);
        mIsWangTing = bundleData.getBoolean (Table.Key.key_wang_ting_flag, false);
        entranceLocation = bundleData.getString (Table.Key.key_entranceLocation, Table.Value.EntranceLocation.person_type);
        mSearchType = bundleData.getString (Table.Key.key_search_type);
        mSearchTypeName = bundleData.getString (Table.Key.key_search_type_name);
        if (SearchUtil.isEmpty (mSearchTypeName)) {
            mSearchTypeName = ItemType.getGroupNameFromType (mSearchType);
        }
        //产品说要改的
        mSearchTypeName = "办事事项";
        searchView.showKeyborad (false);
        loadHistory ();
        String idType = Table.Value.EntranceLocation.enterprise_type.equals (entranceLocation) ?
                Table.Value.MoreType.business_service_guide_page : Table.Value.MoreType.personal_service_guide_page;
        if (mIsWangTing) {
            //网厅
            idType = Table.Value.MoreType.personal_service_hall;
        }
        hotWordPresenter.loadHotWord (idType);
        hotWordPresenter.loadHintText (idType);

        if (!mIsWangTing) {
            initDropDownMenu ();
            getAreaList ();
            //网厅隐藏筛选
        }
        mIsOnline = mIsWangTing;
        rlHeader.setVisibility (mIsWangTing ? View.GONE : View.VISIBLE);
        llContent.setPadding (0, mIsWangTing ? 0 : DeviceUtil.dpTpPx (getActivity (), 45), 0, 0);
        if (!TextUtils.isEmpty (mKeyword)) {
            searchView.setKeyword (mKeyword);
            pageNo = 1;
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
            }else {
                saveKeyWord ();
            }
            return false;
        }
        return false;
    }

    @Override
    public void historyData(List<HistoryBean> beans) {
        searchView.setHistoryData (beans);
    }

    @Override
    public void localData(List<? extends ISearchItem> items) {
    }

    @Override
    public void netData(List<? extends ISearchItem> items, int totalCount) {
        boolean preIsEmpty = false;
        refreshLayout.setRefreshing (false);
        searchAdapter.loadMoreComplete ();
        preIsEmpty = listItems.size () == 0;
        listItems.addAll (items);
        searchAdapter.setKeyword (getKeywords(searchView.getKeyword ()));
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
        boolean show = (totalCount > 0 && listItems.size () > 0);
        tvCountTip.setVisibility (show ? View.VISIBLE : View.GONE);
        banshiLine.setVisibility (show ? View.VISIBLE : View.GONE);
        tvCountTip.setText ("找到" + totalCount + "项" + mSearchTypeName);
        if (listItems.size () > 0) {
            statusView.showContent ();
        } else {
            statusView.showEmpty ();

        }
        if (preIsEmpty && listItems.size () > 0) {
            recyclerView.scrollToPosition (0);
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

    }

    @Override
    public void showContentView(boolean show) {
        searchView.showContentView (show);
    }

    @Override
    public void afterChange(String s) {
        if (SearchUtil.isEmpty (s)) {
            listItems.clear ();
            searchAdapter.notifyDataSetChanged ();
            showContentView (false);
            dropDownMenu.closeMenu ();
        }
    }

    /**
     * 初始化条件筛选
     */
    private void initDropDownMenu() {
        //1. 初始化区域View
        areaView = LayoutInflater.from (getActivity ()).inflate (R.layout.pasc_search_area_popup_picker_multi, null);
        //2. 初始化部门View
        rvDept = new RecyclerView (getActivity ());
        //3. 初始化在线申办View

        popupViews.add (areaView);
        popupViews.add (rvDept);
        View noneView = View.inflate (getActivity (), R.layout.pasc_search_none_group, null);
        popupViews.add (noneView);
        View contentView = new View (getActivity ());
        contentView.setAlpha (0);
        contentView.setLayoutParams (new ViewGroup.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, 0));
        dropDownMenu.setDropDownMenu (Arrays.asList (headers), popupViews, contentView);
    }

    @SuppressWarnings("unchecked")
    private <T extends BanShiAdapter> T refreshAdapter(BanShiAdapter adapter, int adapterType, List<? extends IMultiPickBean> beans) {
        if (adapter == null) {
            RecyclerView recyclerView = null;
            if (adapterType == BanShiAdapter.TYPE_AREA) {
                recyclerView = areaView.findViewById (R.id.rvArea);
            } else if (adapterType == BanShiAdapter.TYPE_DEPT) {
                recyclerView = areaView.findViewById (R.id.rvStreet);
            }
            adapter = new BanShiAdapter<> (getActivity (), adapterType, beans);
            if (recyclerView == null) {
                return (T) adapter;
            }
            recyclerView.setLayoutManager (new LinearLayoutManager (getActivity ()));
            recyclerView.setAdapter (adapter);
        } else {
            adapter.setNewData (beans);
        }
        return (T) adapter;
    }

    /**
     * 刷新区域的Adapter
     */
    @SuppressWarnings("unchecked")
    private void initAreaAdapter(List<AreaBean> beans) {
        if (beans == null) {
            return;
        }

        if (beans.size () > 0) {
            mCurrentAreaCode = beans.get (0).areacode;

        }
        areaAdapter = refreshAdapter (areaAdapter, BanShiAdapter.TYPE_AREA, beans);
        if (areaAdapter != null) {
            areaAdapter.setOnItemClickListener (new BaseQuickAdapter.OnItemClickListener () {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    mCurrentAreaCode = areaAdapter.getItem (position).areacode;
                    areaAdapter.selectItem (position);
                    dropDownMenu.setTabText (areaAdapter.getData ().get (position).getAreaCodeText ());
                    getDeptByArea (mCurrentAreaCode);
                    getStreetByArea (mCurrentAreaCode);
                }
            });
        }
    }

    /**
     * 刷新区域的街道
     */
    private void initDeptStreetAdapter(List<DeptBean> beans) {
        if (beans == null) {
            return;
        }
        streetAdapter = refreshAdapter (streetAdapter, BanShiAdapter.TYPE_DEPT, beans);
        if (streetAdapter == null) {
        } else {
            streetAdapter.selectItem (0);
            streetAdapter.setNewData (beans);
        }
        if (streetAdapter.getOnItemClickListener () == null) {
            streetAdapter.setOnItemClickListener (new BaseQuickAdapter.OnItemClickListener () {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    streetAdapter.selectItem (position);
                    DeptBean dataBean = streetAdapter.getData ().get (position);
                    mAreaCode = dataBean.deptCode;
                    mBureauCode = null;
                    dropDownMenu.setTabText (dataBean.deptName);
                    dropDownMenu.closeMenu ();
                    pageNo = 1;
                    loadMore (false);

                    if (!TextUtils.isEmpty (mCurrentAreaCode) && mCurrentAreaCode.equals (mAreaCode)) {
                        // 区，开启部门可选
                        dropDownMenu.setTabClickable (true, 2);
                    } else {
                        // 街道 禁止部门可选
                        dropDownMenu.setTabClickable (false, 2);
                        // 文字变会全部部门
                        dropDownMenu.setTabText (allDept, 2);
                        deptSearchAdapter.selectItem (0);

                    }


                }
            });
        }

    }

    /**
     * 刷新区域部门筛选
     */
    private void initDeptAdapter(List<DeptBean> beans) {
        if (beans == null) {
            return;
        }
        if (deptSearchAdapter == null) {
            deptSearchAdapter = new DeptSearchAdapter (getActivity (), beans);
            rvDept.setLayoutManager (new LinearLayoutManager (getActivity ()));
            rvDept.setAdapter (deptSearchAdapter);
            deptSearchAdapter.setOnItemClickListener (new BaseQuickAdapter.OnItemClickListener () {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    DeptBean dataBean = deptSearchAdapter.getData ().get (position);
//                    mAreaCode = dataBean.areacode;
                    mAreaCode = null;
                    mBureauCode = dataBean.deptCode;
                    pageNo = 1;
                    loadMore (false);
                    deptSearchAdapter.selectItem (position);
                    dropDownMenu.setTabText (dataBean.deptName);
                    dropDownMenu.closeMenu ();
                }
            });
        } else {
            deptSearchAdapter.selectItem (0);
            deptSearchAdapter.setNewData (beans);
        }
    }


    @Override
    public void getAreaList(List<AreaBean> beans) {
        initAreaAdapter (beans);
        if (beans.size () > 0) {
            //获取默认部门 和街道
            dropDownMenu.setTabText (beans.get (0).areaCodeText, 0);
            getDeptByArea (beans.get (0).areacode);
            getStreetByArea (beans.get (0).areacode);

        }
    }

    @Override
    public void getDeptByAreaData(List<DeptBean> beans) {
        if (beans == null) {
            beans = new ArrayList<> ();
        }
        DeptBean deptBean = new DeptBean ();
        deptBean.deptName = allDept;
        deptBean.deptCode = "";
        deptBean.areacode = "";
        beans.add (0, deptBean);
        if (beans.size () > 0) {
            dropDownMenu.setTabText (beans.get (0).deptName, 2);

        }
        initDeptAdapter (beans);
    }

    @Override
    public void getStreetByAreaData(List<DeptBean> beans) {
        initDeptStreetAdapter (beans);
    }

    @Override
    public void onError(String code, String msg, int ConditionType) {
        if (ConditionType == BanShiPresenter.AreaType) {
            initAreaAdapter (new ArrayList<AreaBean> ());
        } else if (ConditionType == BanShiPresenter.StreetType) {
            initDeptStreetAdapter (new ArrayList<DeptBean> ());
        } else if (ConditionType == BanShiPresenter.DeptType) {
            initDeptAdapter (new ArrayList<DeptBean> ());
        }
    }

    /**
     * 获取区域
     */
    private void getAreaList() {
        multiPickPresenter.getAreaList ();
    }

    /**
     * 获取区域的部门
     */
    private void getDeptByArea(String areaCode) {
        multiPickPresenter.getDeptByArea (areaCode);
    }

    /**
     * 获取区域的街道
     */
    private void getStreetByArea(String areaCode) {
        multiPickPresenter.getStreetByArea (areaCode);
    }


    void loadMore(boolean isRefresh) {
        String keyword = searchView.getKeyword ();
        if (SearchUtil.isEmpty (keyword)) {
            refreshLayout.setRefreshing (false);
            return;
        }
        saveKeyWord ();
        //首页搜索词埋点
        Map<String, String> map = new HashMap<> ();
        map.put (EventTable.WordLabel, keyword);
        SearchManager.instance ().getApi ().onEvent (getActivity (), getBanShiEventId ()
                , EventTable.WordLabel, map);

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
        moreSearchPresenter.searchServiceGuide (source,keyword, entranceLocation, mAreaCode, mBureauCode, mIsOnline, pageNo, pageSize);

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

    private String getBanShiEventId() {
        return
                Table.Value.EntranceLocation.person_type.equals (entranceLocation) ? EventTable.BanShiPersonEventId : EventTable.BanShiEnterpriseEventId;
    }

    void saveKeyWord() {
        String keyword = searchView.getKeyword ();
        if (!SearchUtil.isEmpty (keyword)) {
            historyPresenter.saveKeyword (keyword, historyType ());
        }
    }

    void loadHistory() {
        historyPresenter.loadHistory (historyType ());
    }

    String historyType() {
        if (mIsWangTing) {
            return moreType + "_" + Table.Value.MoreType.personal_service_hall + "_" + entranceLocation;
        }
        return moreType + "_" + mSearchType + "_" + entranceLocation;
    }
}
