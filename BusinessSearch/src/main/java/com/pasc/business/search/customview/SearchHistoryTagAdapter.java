package com.pasc.business.search.customview;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.pasc.business.search.R;
import com.pasc.business.search.customview.flowlayout.FlowLayout;
import com.pasc.business.search.customview.flowlayout.TagAdapter;
import com.pasc.lib.search.common.IKeywordItem;

import java.util.List;

/**
 * @author yangzijian
 * @date 2019/2/19
 * @des
 * @modify
 **/
public class SearchHistoryTagAdapter extends TagAdapter<IKeywordItem> {
    Context context;
    public SearchHistoryTagAdapter(Context context, List<IKeywordItem> datas) {
        super (datas);
        this.context=context;
    }

    @Override
    public View getView(FlowLayout flowLayout, int position, IKeywordItem data) {
        View tag = View.inflate (this.context, R.layout.pasc_search_keyword_item, null);
        String formatStr = data.keyword ().replaceAll ("\n", "");

        if (formatStr.length ()>10){
            formatStr=formatStr.substring (0,10)+"...";
        }
        ((TextView) tag.findViewById (R.id.rtv_tag)).setText (formatStr);
        return tag;
    }
}
