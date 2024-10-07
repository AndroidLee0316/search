package com.pasc.lib.search;

import android.content.Context;
import android.support.annotation.LayoutRes;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * @author yangzijian
 * @date 2019/2/21
 * @des
 * @modify
 **/
public interface ItemConvert {
    @LayoutRes int itemLayout();
    void convert(BaseViewHolder helper, MultiItemEntity item,String[] keyword, Context context);
}
