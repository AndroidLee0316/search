package com.pasc.business.search.customview;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pasc.business.search.SearchManager;
import com.pasc.lib.search.BaseItemConvert;
import com.pasc.business.search.R;
import com.pasc.lib.search.util.SearchUtil;

/**
 * @author yangzijian
 * @date 2019/3/5
 * @des
 * @modify
 **/
public class FooterViewControl implements View.OnClickListener{

    public View footerView;
    private RelativeLayout footerDefault;
    private ImageView ivFooterIcon;
    private TextView tvFooterTitle;
    private TextView tvFooterTip;
    private LinearLayout footerLoading;
    private ProgressBar progressFooter;
    private LinearLayout footerError;
    private View btnFooterRetry;
    private LinearLayout footerNoMore;

    private IReTryListener tryListener;

    public TextView getTvFooterTip() {
        return tvFooterTip;
    }

    public void setTryListener(IReTryListener tryListener) {
        this.tryListener = tryListener;
    }

    private void initView() {
        footerDefault = (RelativeLayout) findViewById (R.id.footer_default);
        ivFooterIcon = (ImageView) findViewById (R.id.iv_footer_icon);
        tvFooterTitle = (TextView) findViewById (R.id.tv_footer_title);
        tvFooterTip = (TextView) findViewById (R.id.tv_footer_tip);
        footerLoading = (LinearLayout) findViewById (R.id.footer_loading);
        progressFooter = (ProgressBar) findViewById (R.id.progress_footer);
        footerError = (LinearLayout) findViewById (R.id.footer_error);
        btnFooterRetry =  findViewById (R.id.btn_footer_retry);
        footerNoMore = (LinearLayout) findViewById (R.id.footer_no_more);

        btnFooterRetry.setOnClickListener (this);
        footerDefault.setOnClickListener (this);

    }


    public void showContent(String keyword){
        if(SearchManager.instance().isHideNetworkSearch()){
            footerDefault.setVisibility (View.GONE);
            footerLoading.setVisibility (View.GONE);
            footerError.setVisibility (View.GONE);
            footerNoMore.setVisibility (View.GONE);
            return;
        }

        if (SearchUtil.isEmpty (keyword)){
            footerDefault.setVisibility (View.GONE);
        }else {

            String text="点击搜索<font color='"+ BaseItemConvert.BaseColorStr+"'>"+keyword+"</font>相关信息";
            tvFooterTitle.setText (Html.fromHtml (text));
            footerDefault.setVisibility (View.VISIBLE);
        }

        footerLoading.setVisibility (View.GONE);
        footerError.setVisibility (View.GONE);
        footerNoMore.setVisibility (View.GONE);
    }


    public void showError() {
        footerDefault.setVisibility (View.GONE);
        footerLoading.setVisibility (View.GONE);
        footerError.setVisibility (View.VISIBLE);
        footerNoMore.setVisibility (View.GONE);
    }

    public void showLoading() {
        footerDefault.setVisibility (View.GONE);
        footerLoading.setVisibility (View.VISIBLE);
        footerError.setVisibility (View.GONE);
        footerNoMore.setVisibility (View.GONE);
    }

    public void showEmpty() {
        footerDefault.setVisibility (View.GONE);
        footerLoading.setVisibility (View.GONE);
        footerError.setVisibility (View.GONE);
        footerNoMore.setVisibility (View.VISIBLE);
    }

    public FooterViewControl(Context context) {
        footerView = LayoutInflater.from (context).inflate (R.layout.pasc_search_footer_layout, null);
        initView ();
    }


    <T extends View> T findViewById(int id) {
        return footerView.findViewById (id);
    }

    @Override
    public void onClick(View v) {
        if (tryListener != null) {
            tryListener.tryAgain ();
        }
    }
}
