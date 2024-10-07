package com.pasc.business.search.customview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.pasc.business.search.R;

/**
 * @author yangzijian
 * @date 2019/3/13
 * @des
 * @modify
 **/
public class MySwipeRefreshLayout extends SwipeRefreshLayout {
    public MySwipeRefreshLayout(@NonNull Context context) {
        this (context,null);
    }

    public MySwipeRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super (context, attrs);
        int color=getResources ().getColor (R.color.search_27A5F9);
        setColorSchemeColors (color,color,color);
    }
}
