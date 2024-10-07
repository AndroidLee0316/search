package com.pasc.business.search.more.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pasc.business.search.more.model.policy.UnitSearchBean;
import com.pasc.business.search.R;

import java.util.List;

public class PolicySearchAdapter extends BaseQuickAdapter<UnitSearchBean.DataBean, BaseViewHolder> {
    private int selectedPosition = 0;
    private Context context;

    public PolicySearchAdapter(Context context, @Nullable List<UnitSearchBean.DataBean> data) {
        super(R.layout.pasc_search_popup_item_pick_default, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, UnitSearchBean.DataBean bean) {
        helper.setText(R.id.content, bean.unitName);
        if (selectedPosition == helper.getAdapterPosition()) {
            helper.setTextColor(R.id.content, context.getResources().getColor(R.color.search_27A5F9));
        } else {
            helper.setTextColor(R.id.content, context.getResources().getColor(R.color.search_333333));
        }
    }

    public void selectItem(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }
}
