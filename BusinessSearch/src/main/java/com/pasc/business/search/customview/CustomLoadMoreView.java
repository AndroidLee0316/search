package com.pasc.business.search.customview;

import android.view.View;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.pasc.business.search.R;

/**
 * @author yangzijian
 * @date 2019/3/9
 * @des
 * @modify
 **/
public class CustomLoadMoreView extends LoadMoreView {
    private boolean loadEndViewVisible;

    public CustomLoadMoreView() {
    }

    public int getLayoutId() {
        return R.layout.search_layout_load_more;
    }

    public boolean isLoadEndGone() {
        return true;
    }

    protected int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }

    protected int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

    protected int getLoadEndViewId() {
        return R.id.load_more_load_end_view;
    }

    public void convert(BaseViewHolder holder) {
        int loadEndViewId = this.getLoadEndViewId();
        if (loadEndViewId != 0) {
            View loadEndView = holder.getView(loadEndViewId);
            if (loadEndView != null) {
                loadEndView.setAlpha(this.loadEndViewVisible ? 0.0F : 1.0F);
            }
        }

        super.convert(holder);
    }

    public void setLoadEndViewVisible(boolean loadEndViewVisible) {
        this.loadEndViewVisible = loadEndViewVisible;
    }
}
