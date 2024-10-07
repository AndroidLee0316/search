package com.pasc.business.search.home.itemconvert;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.pasc.business.search.R;
import com.pasc.business.search.SearchManager;
import com.pasc.business.search.customview.ItemListView;
import com.pasc.business.search.customview.gifview.ItemListViewListener;
import com.pasc.lib.search.BaseItemConvert;
import com.pasc.lib.search.ISearchGroup;
import com.pasc.lib.search.ISearchItem;

import java.util.List;

/**
 * @author yangzijian
 * @date 2019/2/21
 * @des
 * @modify
 **/
public abstract class BaseHomeHolderConvert extends BaseItemConvert {
  private ItemListView itemListView;
  private ItemListViewListener itemListViewListener;
  private int maxItemNum = 3;

  @Override
  public void convert(BaseViewHolder helper, MultiItemEntity item, String[] keyword,
                      Context context) {
    if (item instanceof ISearchGroup) {
      showTitleAndMore(context, helper, keyword, (ISearchGroup) item);
    }
  }

  public void showTitleAndMore(final Context context, BaseViewHolder helper, String[] keyword,
                               ISearchGroup itemGroup) {
    if (itemGroup == null) return;

    int originLength = itemGroup.data().size();
    List<ISearchItem> listItems;
//    if (!SearchManager.instance().isUseBaseApi()) {
      listItems = itemGroup.data();
//    } else {
//      listItems = originLength > maxItemNum ?
//          itemGroup.data().subList(0, maxItemNum)
//          : itemGroup.data();
//    }

    helper.addOnClickListener(R.id.ll_more)
        .addOnClickListener(R.id.ll_title)
        .setText(R.id.tv_type, itemGroup.groupName())
        .setGone(R.id.ll_more,  Boolean.valueOf(itemGroup.extraData().get("showMore").toString()));

    itemListView = helper.getView(R.id.list_item);

    itemListView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemListViewListener.onItemClick(parent, view, position,
                        id);
              }
            });

    setData(context, helper, itemGroup.searchType(), listItems, keyword,
        itemListView);
  }

  public void setItemListViewListener(ItemListViewListener itemListViewListener) {
    this.itemListViewListener = itemListViewListener;
  }

  public abstract void setData(Context context, BaseViewHolder helper, String type,
                               List<ISearchItem> list,
                               String[] keyword,
                               ItemListView itemListView);
}
