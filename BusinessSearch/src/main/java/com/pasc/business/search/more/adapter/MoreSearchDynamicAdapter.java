package com.pasc.business.search.more.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pasc.lib.search.ISearchItem;
import com.pasc.lib.search.ItemConvert;
import com.pasc.lib.search.ItemManager;
import com.pasc.business.search.ItemType;
import com.pasc.business.search.R;

import java.util.List;
import java.util.Map;

/**
 * @author yangzijian
 * @date 2019/2/21
 * @des
 * @modify
 **/
public class MoreSearchDynamicAdapter<T extends ISearchItem> extends BaseMultiItemQuickAdapter<T, BaseViewHolder> {
    private Context context;
    private String[] keyword;

    public void setKeyword(String[] keyword) {
        this.keyword = keyword;
    }

    public MoreSearchDynamicAdapter(Context context, List<T> data) {
        super (data);
        this.context = context;
        for (Map.Entry<String, ItemConvert> entry : ItemManager.instance (ItemType.MoreItemManager).getConvertMap ().entrySet ()) {
            addItemType (ItemType.getItemTypeFromType (entry.getKey ()), entry.getValue ().itemLayout ());
        }
        //注册一个 数据不对上的布局，模型数据对不上时，给个默认不显示布局
        addItemType (ItemType.defaultType, R.layout.pasc_search_none_group);
    }

    @Override
    protected void convert(BaseViewHolder helper, T item) {
        ItemConvert itemConvert = ItemManager.instance (ItemType.MoreItemManager).getItemConvert (item.searchType ());
        if (itemConvert != null) {
            itemConvert.convert (helper, item, keyword, context);
        }
    }
}
