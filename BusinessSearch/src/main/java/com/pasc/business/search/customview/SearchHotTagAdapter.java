package com.pasc.business.search.customview;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.pasc.business.search.R;
import com.pasc.lib.search.common.BaseHolder;
import com.pasc.lib.search.common.IKeywordItem;
import com.pasc.lib.search.common.MyBaseAdapter;

import java.util.List;

/**
 * @author yangzijian
 * @date 2019/2/19
 * @des
 * @modify
 **/
public class SearchHotTagAdapter extends MyBaseAdapter<IKeywordItem,SearchHotTagAdapter.HotHolder> {

    public SearchHotTagAdapter(Context context, List<IKeywordItem> datas) {
        super (context,datas);
        this.context = context;
    }
    @Override
    public int layout() {
        return R.layout.pasc_search_hot_item;
    }

    @Override
    public HotHolder createBaseHolder(View rootView) {
        return new HotHolder (rootView);
    }

    @Override
    public void setData(HotHolder holder,IKeywordItem hotBean) {
        String formatStr = hotBean.keyword ();
        if (formatStr.length ()>10){
            // 最多10个字符
            formatStr=formatStr.substring (0,10)+"...";
        }
        holder.tv_hot_title.setText (formatStr);
    }

    public static class HotHolder extends BaseHolder {
        TextView tv_hot_title;
        public HotHolder(View rootView){
            super(rootView);
            tv_hot_title=rootView.findViewById (R.id.tv_hot_title);
        }
    }
}
