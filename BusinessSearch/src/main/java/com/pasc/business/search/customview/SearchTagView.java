package com.pasc.business.search.customview;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pasc.business.search.R;
import com.pasc.business.search.SearchManager;
import com.pasc.business.search.event.EventTable;
import com.pasc.lib.search.LocalSearchManager;
import com.pasc.business.search.customview.flowlayout.FlowLayout;
import com.pasc.business.search.customview.flowlayout.TagFlowLayout;
import com.pasc.lib.search.common.IKeywordItem;
import com.pasc.lib.search.util.KeyBoardUtils;
import com.pasc.lib.search.util.SearchUtil;
import com.pasc.lib.search.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangzijian
 * @date 2019/2/19
 * @des
 * @modify
 **/
public class SearchTagView extends FrameLayout {


    private ClearEditText etSearch;
    private View tvSearchCancel;
    private LinearLayout llHistoryTip;
    private View ivSearchHistoryDelete;
    private View.OnClickListener onDeleteHistoryClickListener;
    private View.OnClickListener onDeleteHistoryCancelClickListener;
    private TagFlowLayout searchHistoryFlow;
    private TextView tvHotSearch, tvHistory;
    private View llKeywordLayout;
    View rv_root_view;
    ViewGroup fl_content;
    View iv_back;
    View ll_assign;
    GridView gv_assign, gv_search_hot;
    private Activity activity;
    private List<IKeywordItem> historyBeans = new ArrayList<> ();
    private List<IKeywordItem> hotBeans = new ArrayList<> ();
    private List<IKeywordItem> assignBeans = new ArrayList<> ();

    private SearchHistoryTagAdapter historyTagAdapter;
    SearchHotTagAdapter hostTagAdapter;
    SearchAssignTagAdapter assignTagAdapter;
    private ItemClickListener historyClick, hotClick, assignClick;
    private Context context;
    private MyOnEditorActionListener onEditorActionListener;
    private ClearEditText.IconDismissListener iconDismissListener;

    public ClearEditText getEtSearch(){
        return etSearch;
    }

    private void initView() {
        iv_back = findViewById (R.id.iv_back);
        rv_root_view = findViewById (R.id.rv_root_view);
        fl_content = findViewById (R.id.fl_content);
        gv_assign = findViewById (R.id.gv_assign);
        gv_search_hot = findViewById (R.id.gv_search_hot);
        ll_assign = findViewById (R.id.ll_assign);
        etSearch = findViewById (R.id.et_search);
        tvSearchCancel = findViewById (R.id.tv_search_cancel);
        llHistoryTip = findViewById (R.id.ll_history_tip);
        ivSearchHistoryDelete = findViewById (R.id.iv_search_history_delete);
        searchHistoryFlow = findViewById (R.id.flow_history);
        tvHotSearch = findViewById (R.id.tv_hot_search);
        tvHistory = findViewById (R.id.tv_history);
        llKeywordLayout = findViewById (R.id.ll_keyword_layout);

        historyTagAdapter = new SearchHistoryTagAdapter (context, historyBeans);
        searchHistoryFlow.setAdapter (historyTagAdapter);
        searchHistoryFlow.setOnTagClickListener (new TagFlowLayout.OnTagClickListener () {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout flowLayout) {
                IKeywordItem iKeywordItem=  historyBeans.get (position);
                boolean intercept=intercept (iKeywordItem.keyword ());
                KeyBoardUtils.closeKeybord (etSearch,getContext ());
                setKeyword (iKeywordItem.keyword ());
                if (historyClick != null) {
                    historyClick.itemClick (iKeywordItem,intercept);
                }
                return false;
            }
        });

        hostTagAdapter = new SearchHotTagAdapter (context, hotBeans);
        gv_search_hot.setAdapter (hostTagAdapter);

        assignTagAdapter = new SearchAssignTagAdapter (context, assignBeans);
        gv_assign.setAdapter (assignTagAdapter);

        gv_assign.setOnItemClickListener (new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (assignClick != null) {
                    assignClick.itemClick (assignBeans.get (position),false);
                }
            }
        });

        gv_search_hot.setOnItemClickListener (new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IKeywordItem iKeywordItem=  hotBeans.get (position);
                boolean intercept=intercept (iKeywordItem.keyword ());
                KeyBoardUtils.closeKeybord (etSearch,getContext ());
                setKeyword (iKeywordItem.keyword ());
                if (hotClick != null) {
                    hotClick.itemClick (iKeywordItem,intercept);
                }
            }
        });

        hideKeyboard (rv_root_view);

        iv_back.setOnClickListener (new OnClickListener () {
            @Override
            public void onClick(View v) {
                showKeyborad (false);
                if (activity != null) {
                    activity.onBackPressed ();
                }
            }
        });

        ivSearchHistoryDelete.setOnClickListener (new OnClickListener () {
            @Override
            public void onClick(final View v) {
                showDeleteDialog (v);
            }
        });

        etSearch.setOnEditorActionListener (new TextView.OnEditorActionListener () {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    if(!SearchManager.instance().isHideNetworkSearch()) {
//                        if (SearchUtil.isEmpty(getKeyword())) {
//                            ToastUtil.showToast("请输入搜索内容");
//                            KeyBoardUtils.closeKeybord(etSearch, getContext());
//                            return true;
//                        }
//                    }
                    boolean intercept=interceptSearch ();
                    KeyBoardUtils.closeKeybord (etSearch,getContext ());
                    if (onEditorActionListener!=null){
                      return   onEditorActionListener.onEditorAction (v,actionId,event,intercept);
                    }
                }
                return false;
            }
        });
        etSearch.setIconDismissListener(new ClearEditText.IconDismissListener() {
            @Override public void onIconClick() {
                if(iconDismissListener!=null){
                  iconDismissListener.onIconClick();
                }
            }
        });

    }

    public Activity getActivity(){
        return (Activity) context;
    }
    /**
     * 深圳特殊需求
     * 输入以什么开头的时候拦截
     * @return
     */
    boolean interceptSearch(){
        return intercept (getKeyword ());
    }
    boolean intercept(String keyword){
        return  LocalSearchManager.instance ().getApi ().interceptSearch (getActivity (),keyword);
    }
    void showDeleteDialog(final View v) {
        new SelectDialog (context).setOnSelectedListener (new SelectDialog.OnSelectedListener () {
            @Override
            public void onSelected() {
                if (onDeleteHistoryClickListener != null) {
                    onDeleteHistoryClickListener.onClick (v);
                }
            }

            @Override
            public void onCancel() {
                if (onDeleteHistoryCancelClickListener != null) {
                    onDeleteHistoryCancelClickListener.onClick (v);
                }
            }
        }).show ();
    }

    void hideKeyboard(View rootView) {
        if (activity == null) {
            return;
        }
        if (rootView == null) {
            return;
        }
        rootView.setOnTouchListener (new View.OnTouchListener () {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction () == MotionEvent.ACTION_DOWN) {
                    if (activity.getCurrentFocus () != null && activity.getCurrentFocus ().getWindowToken () != null) {
                        KeyBoardUtils.hideInputForce (activity);
                    }
                }
                return false;
            }
        });
    }

    public SearchTagView(@NonNull Context context) {
        super (context, null);
    }

    public SearchTagView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this (context, attrs, 0);
    }

    public SearchTagView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);
        this.context = context;
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
        LayoutInflater.from (context).inflate (R.layout.pasc_search_action_view, this, true);
        initView ();
    }

    public <T extends IKeywordItem> void setHistoryData(List<T> beans) {
        historyBeans.clear ();
        historyBeans.addAll (beans);
        historyTagAdapter.notifyDataChanged ();
        int VISIBLE = historyBeans.size () > 0 ? View.VISIBLE : View.GONE;
        llHistoryTip.setVisibility (VISIBLE);
        searchHistoryFlow.setVisibility (VISIBLE);
    }

    public <T extends IKeywordItem> void setHotData(List<T> beans) {
        hotBeans.clear ();
        hotBeans.addAll (beans);
        hostTagAdapter.notifyDataSetChanged ();
        int VISIBLE = hotBeans.size () > 0 ? View.VISIBLE : View.GONE;
        tvHotSearch.setVisibility (VISIBLE);
        gv_search_hot.setVisibility (VISIBLE);
    }

    public <T extends IKeywordItem> void setAssignData(List<T> beans) {
        int size=beans.size ();
        if (size>0 && size<=4){
            gv_assign.setNumColumns (size);

        }else if (size>=5 && size<=6){
            gv_assign.setNumColumns (3);

        }else {
            gv_assign.setNumColumns (4);
        }

        assignBeans.clear ();
        assignBeans.addAll (beans);
        assignTagAdapter.notifyDataSetChanged ();
        int VISIBLE = assignBeans.size () > 0 ? View.VISIBLE : View.GONE;
        ll_assign.setVisibility (VISIBLE);
    }

    public void setKeyword(CharSequence keyword) {
        setKeyword (keyword,false);
    }

    /***
     *
     * @param keyword
     * @param noCallBack 不需要回调
     */
    public void setKeyword(CharSequence keyword,boolean noCallBack){
        etSearch.setChangeCallBack (noCallBack);
        etSearch.setText (keyword);
        int len = SearchUtil.isEmpty (keyword) ? 0 : keyword.length ();
        try {
            etSearch.setSelection (len);
        }catch (Exception e){

        }
    }

        public SearchTagView showCancle(boolean show){
        tvSearchCancel.setVisibility (show?VISIBLE:GONE);
        return this;
    }

    public SearchTagView setHistoryText(CharSequence text) {
        tvHistory.setText (text);
        return this;
    }

    public SearchTagView setHotText(CharSequence text) {
        tvHotSearch.setText (text);
        return this;
    }

    public SearchTagView setHotClickListener(ItemClickListener hotClick) {
        this.hotClick = hotClick;
        return this;
    }

    public SearchTagView setAssignClickListener(ItemClickListener assignClick) {
        this.assignClick = assignClick;
        return this;
    }

    public SearchTagView setHistoryClickListener(ItemClickListener historyClick) {
        this.historyClick = historyClick;
        return this;
    }

    public SearchTagView setEditTextChangeListener(ClearEditText.EditTextChangeListener editTextChangeListener) {
        this.etSearch.setEditTextChangeListener (editTextChangeListener);
        return this;
    }

    public SearchTagView setOnEditorActionListener(MyOnEditorActionListener onEditorActionListener) {
        this.onEditorActionListener=onEditorActionListener;
        return this;
    }

    public SearchTagView setHint(CharSequence hint) {
        this.etSearch.setHint (hint);
        return this;
    }

    public SearchTagView setBackShow(boolean show) {
        if (iv_back != null) {
            iv_back.setVisibility (show ? VISIBLE : GONE);
        }
        return this;
    }

    public SearchTagView setOnDeleteHistoryClickListener(OnClickListener onDeleteHistoryClickListener) {
        this.onDeleteHistoryClickListener = onDeleteHistoryClickListener;
        return this;
    }
    public SearchTagView setOnDeleteHistoryCancelClickListener(OnClickListener onDeleteHistoryCancelClickListener) {
        this.onDeleteHistoryCancelClickListener = onDeleteHistoryCancelClickListener;
        return this;
    }
    public SearchTagView setOnCancelClickListener(OnClickListener onCancelClickListener) {
        this.tvSearchCancel.setOnClickListener (onCancelClickListener);
        return this;
    }

    public SearchTagView addContentView(View contentView) {
        fl_content.removeAllViews ();
        fl_content.addView (contentView);
        return this;
    }

    public void showContentView(boolean show) {
        llKeywordLayout.setVisibility (show ? View.GONE : View.VISIBLE);
        fl_content.setVisibility (!show ? View.GONE : View.VISIBLE);
    }

    public String getKeyword() {
        return etSearch.getText ().toString ();
    }

    public interface ItemClickListener {
        void itemClick(IKeywordItem iKeywordItem,boolean intercept);
    }

    public void showKeyborad(boolean show) {
        if (show) {
            KeyBoardUtils.openKeybord (etSearch, getContext ());
        } else {
            etSearch.clearFocus ();
            KeyBoardUtils.closeKeybord (etSearch, getContext ());

        }

    }
    public SearchTagView setOnIconDismissListener(ClearEditText.IconDismissListener iconDismissListener){
        this.iconDismissListener=iconDismissListener;
        return this;
    }

}
