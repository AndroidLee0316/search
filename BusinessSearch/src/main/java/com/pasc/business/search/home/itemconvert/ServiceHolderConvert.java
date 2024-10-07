package com.pasc.business.search.home.itemconvert;

import android.content.Context;

import com.chad.library.adapter.base.BaseViewHolder;
import com.pasc.business.search.ItemType;
import com.pasc.business.search.R;
import com.pasc.business.search.customview.ItemListView;
import com.pasc.business.search.customview.ServiceItemAdapter;
import com.pasc.lib.search.ISearchItem;

import java.util.List;

/**
 * @author yangzijian
 * @date 2019/2/21
 * @des  服务
 * @modify
 **/
public class ServiceHolderConvert extends BaseHomeHolderConvert {
    @Override
    public int itemLayout() {
        return R.layout.pasc_search_service_group;
    }

    @Override
    public void setData(Context context, BaseViewHolder helper, String type, List<ISearchItem> list,
                        String[] keyword, ItemListView itemListView) {
        if (!type.equals(ItemType.personal_server)) return;
        ServiceItemAdapter itemAdapter = new ServiceItemAdapter(context, list,keyword);
        itemListView.setAdapter(itemAdapter);

    }
}
