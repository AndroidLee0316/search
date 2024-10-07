package com.pasc.business.search.more.itemconvert;

import android.content.Context;
import android.text.SpannableString;
import android.widget.ImageView;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pasc.business.search.R;
import com.pasc.lib.imageloader.PascImageLoader;
import com.pasc.lib.search.ISearchItem;
import com.pasc.lib.search.util.SearchUtil;

/**
 * @author yangzijian
 * @date 2019/2/21
 * @des
 * @modify
 **/
public class ServiceMoreHolderConvert extends BaseMoreHolderConvert {
    @Override
    public int itemLayout() {
        return R.layout.pasc_search_service_item;
    }


    @Override
    public void setData(Context context, BaseViewHolder helper,String[] keyword, ISearchItem item) {
//        if (item instanceof SearchSourceItem) {
//            SearchSourceItem sourceItem= (SearchSourceItem) item;
//            SpannableString spannableString=null;
//            if (sourceItem.end>0){
//                spannableString = getSpannableString (sourceItem.name, sourceItem.start, sourceItem.end);
//            }else {
//                spannableString=
//            }
            SpannableString spannableString=getSpannableString (context, item.title(),keyword);;

            helper.setText (R.id.tv_title,spannableString);
            ImageView imageView=helper.getView (R.id.iv_icon);
            if (SearchUtil.isEmpty (item.icon ())) {
                imageView.setImageResource (R.drawable.search_icon_default);
            }else {
                PascImageLoader.getInstance ().loadImageUrl (item.icon (), imageView, R.drawable.search_icon_default, PascImageLoader.SCALE_DEFAULT);
            }
//        }
        helper.addOnClickListener (R.id.ll_search_item);


    }
}
