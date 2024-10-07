package com.pasc.business.search.more.itemconvert;

import android.content.Context;
import android.text.SpannableString;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pasc.business.search.R;
import com.pasc.lib.search.ISearchItem;

/**
 * @author yangzijian
 * @date 2019/2/21
 * @des  证企号服务
 * @modify
 **/
public class ZQHServiceMoreHolderConvert extends BaseMoreHolderConvert {
    @Override
    public int itemLayout() {
        return R.layout.pasc_search_zqhservice_item;
    }


    @Override
    public void setData(Context context, BaseViewHolder helper,String[] keyword, ISearchItem item) {
        SpannableString spannableString= getSpannableString (context, item.title (),keyword);
        helper.setText (R.id.tv_title,spannableString);
        SpannableString spannableString2 = getSpannableString (context, item.content (), keyword);
        helper.setText (R.id.tv_content,spannableString2);
        helper.addOnClickListener (R.id.ll_search_item);

    }
}
