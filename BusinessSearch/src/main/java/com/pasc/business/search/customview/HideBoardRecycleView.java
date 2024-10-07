package com.pasc.business.search.customview;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.pasc.lib.search.util.KeyBoardUtils;

/**
 * @author yangzijian
 * @date 2019/4/8
 * @des
 * @modify
 **/
public class HideBoardRecycleView extends RecyclerView {
    private Activity activity;
    public HideBoardRecycleView(Context context) {
        this (context,null);
    }

    public HideBoardRecycleView(Context context, @Nullable AttributeSet attrs) {
        this (context, attrs,0);
    }

    public HideBoardRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super (context, attrs, defStyle);
        if (context instanceof Activity){
            this.activity= (Activity) context;

        }

        addOnScrollListener (new OnScrollListener () {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled (recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged (recyclerView, newState);
                if ( newState ==SCROLL_STATE_DRAGGING){
                    KeyBoardUtils.hideInput (activity);

                }
            }
        });
    }

}
