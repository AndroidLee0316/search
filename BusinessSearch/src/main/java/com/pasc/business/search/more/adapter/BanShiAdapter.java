package com.pasc.business.search.more.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pasc.business.search.more.model.task.IMultiPickBean;
import com.pasc.business.search.R;

import java.util.List;

public class BanShiAdapter<ITEM extends IMultiPickBean> extends BaseQuickAdapter<ITEM, BaseViewHolder> {
    public static final int TYPE_AREA = 0;
    public static final int TYPE_DEPT = 1;
    public static final int TYPE_TASK = 2;
    private int type;
    private int selectedPosition = 0;
    private Context context;

    public BanShiAdapter(Context context, int type, @Nullable List<ITEM> data) {
        super(R.layout.pasc_search_popup_item_pick_default, data);
        this.type = type;
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, ITEM item) {
        if (type == TYPE_AREA) {
            helper.setText(R.id.content, item.getAreaCodeText());
            helper.setBackgroundColor(R.id.container, context.getResources().getColor(R.color.search_white));
        } else if (type == TYPE_DEPT) {
            helper.setText(R.id.content, item.getDeptName());
            helper.setBackgroundColor(R.id.container, context.getResources().getColor(R.color.search_f4f4f4));
        } else if (type == TYPE_TASK) {
            helper.setText(R.id.content, item.getTaskName());
            helper.setBackgroundColor(R.id.container, context.getResources().getColor(R.color.search_f4f4f4));
        }
        if (selectedPosition == helper.getAdapterPosition()) {
            helper.setTextColor(R.id.content, context.getResources().getColor(R.color.search_27A5F9));
            if (type == TYPE_AREA) {
                helper.setBackgroundColor(R.id.content, context.getResources().getColor(R.color.search_f4f4f4));
            }
        } else {
            helper.setTextColor(R.id.content, context.getResources().getColor(R.color.search_333333));
            if (type == TYPE_AREA) {
                helper.setBackgroundColor(R.id.content, context.getResources().getColor(R.color.search_white));
            }
        }
    }

    public void selectItem(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

}
