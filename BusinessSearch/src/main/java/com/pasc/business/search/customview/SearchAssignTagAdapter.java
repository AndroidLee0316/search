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
public class SearchAssignTagAdapter extends MyBaseAdapter<IKeywordItem,SearchAssignTagAdapter.AssignHolder> {

    public SearchAssignTagAdapter(Context context, List<IKeywordItem> datas) {
        super (context, datas);
    }

    @Override
    public int layout() {
        return R.layout.pasc_search_assign_item;
    }

    @Override
    public AssignHolder createBaseHolder(View rootView) {
        return new AssignHolder (rootView);
    }

    @Override
    public void setData(AssignHolder holder, IKeywordItem data) {
        String formatStr = data.keyword ();
        if (formatStr!=null && formatStr.length ()>7){
            // 最多四个字符
            formatStr=formatStr.substring (0,7)+"...";
        }
        holder.tv_assign_title.setText (formatStr);
    }

    public static class AssignHolder extends BaseHolder {
        TextView tv_assign_title;
        public AssignHolder(View rootView){
            super(rootView);
            tv_assign_title=rootView.findViewById (R.id.tv_assign_title);
        }
    }


}
