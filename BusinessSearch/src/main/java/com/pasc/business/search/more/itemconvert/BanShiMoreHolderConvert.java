package com.pasc.business.search.more.itemconvert;

import android.content.Context;
import android.text.SpannableString;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pasc.business.search.R;
import com.pasc.business.search.more.model.task.ServiceGuideDataBean;
import com.pasc.lib.search.ISearchItem;
import com.pasc.lib.search.util.SearchUtil;

/**
 * @author yangzijian
 * @date 2019/2/21
 * @des
 * @modify
 **/
public class BanShiMoreHolderConvert extends BaseMoreHolderConvert {
    @Override
    public int itemLayout() {
        return R.layout.pasc_search_banshi_item;
    }



    @Override
    public void setData(Context context, BaseViewHolder helper,String[] keyword, ISearchItem item) {
        if (item instanceof ServiceGuideDataBean) {
            ServiceGuideDataBean guideDataBean= (ServiceGuideDataBean) item;
            String areacodeText = "";
            if (!SearchUtil.isEmpty (guideDataBean.areaCodeText)) {
                areacodeText = "【" + guideDataBean.areaCodeText + "】";
            }
            SpannableString spannableString = getSpannableString (context, areacodeText+item.title (), keyword);
            helper.setText (R.id.tv_title,spannableString);

        }else {
            SpannableString spannableString = getSpannableString (context, item.title (), keyword);
            helper.setText (R.id.tv_title,spannableString);

        }
        helper.addOnClickListener (R.id.ll_search_item);

    }
}
