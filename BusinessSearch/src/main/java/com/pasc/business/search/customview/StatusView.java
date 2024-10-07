package com.pasc.business.search.customview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.pasc.business.search.R;

/**
 * @author yangzijian
 * @date 2019/3/5
 * @des
 * @modify
 **/
public class StatusView extends FrameLayout {
    private View statusLoading;
    private View statusEmpty;
    private View statusError;
    private View btnFooterRetry;

    public void setContentView(View contentView){
        this.contentView=contentView;
    }

    private View contentView;

    private IReTryListener tryListener;

    public void setTryListener(IReTryListener tryListener) {
        this.tryListener = tryListener;
    }

    private void assignViews() {
        statusLoading = findViewById (R.id.status_loading);
        statusEmpty =  findViewById (R.id.status_empty);
        statusError =  findViewById (R.id.status_error);
        btnFooterRetry =  findViewById (R.id.btn_footer_retry);

        btnFooterRetry.setOnClickListener (new OnClickListener () {
            @Override
            public void onClick(View v) {
                if (tryListener!=null){
                    tryListener.tryAgain ();
                }
            }
        });
    }

    public void showContent() {
        statusLoading.setVisibility (GONE);
        statusEmpty.setVisibility (GONE);
        statusError.setVisibility (GONE);
        if (contentView!=null){
            contentView.setVisibility (VISIBLE);
        }
    }

    public void showError() {
        statusLoading.setVisibility (GONE);
        statusEmpty.setVisibility (GONE);
        statusError.setVisibility (VISIBLE);
        if (contentView!=null){
            contentView.setVisibility (GONE);
        }
    }

    public void showLoading() {
        statusLoading.setVisibility (VISIBLE);
        statusEmpty.setVisibility (GONE);
        statusError.setVisibility (GONE);
        if (contentView!=null){
            contentView.setVisibility (GONE);
        }
    }

    public void showEmpty() {
        statusLoading.setVisibility (GONE);
        statusEmpty.setVisibility (VISIBLE);
        statusError.setVisibility (GONE);
        if (contentView!=null){
            contentView.setVisibility (GONE);
        }
    }

    public StatusView(@NonNull Context context) {
        this (context, null);
    }

    public StatusView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this (context, attrs, 0);
    }

    public StatusView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);
        LayoutInflater.from (context).inflate (R.layout.pasc_search_status_view, this, true);
        assignViews ();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure (widthMeasureSpec, heightMeasureSpec);
        findContentView ();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout (changed, left, top, right, bottom);

    }

    void findContentView(){
        if (contentView==null) {
            int count = getChildCount ();
            for (int i = 0; i < count; i++) {
                View child = getChildAt (i);
                if (child.getId () != R.id.status_root) {
                    contentView = child;
                    break;
                }
            }
            showContent ();
        }
    }
}
