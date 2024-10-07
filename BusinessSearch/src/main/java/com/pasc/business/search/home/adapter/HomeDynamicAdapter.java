package com.pasc.business.search.home.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pasc.business.search.ItemType;
import com.pasc.business.search.R;
import com.pasc.business.search.customview.gifview.ItemListViewListener;
import com.pasc.business.search.home.itemconvert.BaseHomeHolderConvert;
import com.pasc.lib.search.ISearchGroup;
import com.pasc.lib.search.ItemConvert;
import com.pasc.lib.search.ItemManager;

import java.util.List;
import java.util.Map;

/**
 * @author yangzijian
 * @date 2019/2/20
 * @des
 * @modify
 **/

public class HomeDynamicAdapter<T extends ISearchGroup> extends BaseMultiItemQuickAdapter<T, BaseViewHolder> {
    private Context context;
    private ItemListViewListener itemListViewListener;
    public String[] keywords;

    public void setKeyword(String[] keywords) {
        this.keywords = keywords;
    }

    public HomeDynamicAdapter(Context context, ItemListViewListener itemListViewListener, List<T> data) {
        super (data);
        this.context = context;
        this.itemListViewListener = itemListViewListener;
        for (Map.Entry<String, ItemConvert> entry : ItemManager.instance (ItemType.HomeItemManager).getConvertMap ().entrySet ()) {
            int itemType = ItemType.getItemTypeFromType (entry.getKey ());
            addItemType (itemType, entry.getValue ().itemLayout ());
        }
        //注册一个 数据不对上的布局，模型数据对不上时，给个默认不显示布局
        addItemType (ItemType.defaultType, R.layout.pasc_search_none_group);
    }

    @Override
    protected void convert(BaseViewHolder helper, T item) {
        ItemConvert itemConvert = ItemManager.instance (ItemType.HomeItemManager).getItemConvert (item.searchType ());
        if (itemConvert != null) {
            itemConvert.convert (helper, item,keywords, context);
            if (itemConvert instanceof BaseHomeHolderConvert){
                ((BaseHomeHolderConvert) itemConvert).setItemListViewListener(itemListViewListener);
            }
        }
    }

}
