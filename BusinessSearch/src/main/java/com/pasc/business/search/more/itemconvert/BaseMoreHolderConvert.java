package com.pasc.business.search.more.itemconvert;

import android.content.Context;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.pasc.lib.search.BaseItemConvert;
import com.pasc.lib.search.ISearchItem;

/**
 * @author yangzijian
 * @date 2019/2/21
 * @des
 * @modify
 **/
public abstract class BaseMoreHolderConvert extends BaseItemConvert {
    private BaseViewHolder helper;
    @Override
    public void convert(BaseViewHolder helper, MultiItemEntity t,String[] keyword, Context context) {
        this.helper=helper;
        ISearchItem item = null;
        if (t instanceof ISearchItem && t != null) {
            item = (ISearchItem) t;
            setData (context, helper,keyword, item);
        }
    }


    public abstract void setData(Context context, BaseViewHolder helper,String[] keyword, ISearchItem item);
}
