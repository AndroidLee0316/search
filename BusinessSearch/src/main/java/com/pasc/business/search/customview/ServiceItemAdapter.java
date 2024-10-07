package com.pasc.business.search.customview;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pasc.business.search.R;
import com.pasc.lib.imageloader.PascImageLoader;
import com.pasc.lib.search.BaseItemConvert;
import com.pasc.lib.search.ISearchItem;
import com.pasc.lib.search.db.SearchSourceItem;
import com.pasc.lib.search.util.SearchUtil;

import java.util.List;

/**
 * Created by huangtebian535 on 2019/09/03.
 */

public class ServiceItemAdapter extends BaseAdapter {

  private List<ISearchItem> data;
  private LayoutInflater inflater;
  private String[] keyword;
  private Context mContext;

  public ServiceItemAdapter(Context context, List<ISearchItem> data, String[] keyword) {
    mContext = context;
    inflater = LayoutInflater.from(context);
    this.data = data;
    this.keyword = keyword;
  }

  @Override public int getCount() {
    return data == null ? 0 : data.size();
  }

  @Override public Object getItem(int position) {
    return data.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    View rootView = inflater.inflate(R.layout.pasc_search_service_item, null);
    ISearchItem item = data.get(position);

    if (item instanceof SearchSourceItem) {
      SearchSourceItem sourceItem= (SearchSourceItem) item;
      SpannableString spannableString=null;
      if (sourceItem.end>0){
        spannableString = BaseItemConvert.getSpannableString (mContext, sourceItem.name, sourceItem.start, sourceItem.end);
      }else {
        spannableString= BaseItemConvert.getSpannableString (mContext, sourceItem.name,keyword);
      }

      ((TextView) rootView.findViewById (R.id.tv_title)).setText (spannableString);
      ImageView imageView=rootView.findViewById (R.id.iv_icon);
      if (SearchUtil.isEmpty (sourceItem.icon ())) {
        imageView.setImageResource (R.drawable.search_icon_default);
      }else {
        PascImageLoader.getInstance ().loadImageUrl (sourceItem.icon (), imageView, R.drawable.search_icon_default, PascImageLoader.SCALE_DEFAULT);
      }
    }else{
        SpannableString spannableString = BaseItemConvert.getSpannableString(mContext, item.title(), keyword);
        ((TextView) rootView.findViewById(R.id.tv_title)).setText(spannableString);
        ImageView imageView = rootView.findViewById(R.id.iv_icon);
        if (SearchUtil.isEmpty(item.icon())) {
          imageView.setImageResource(R.drawable.search_icon_default);
        } else {
          PascImageLoader.getInstance().loadImageUrl(item.icon(), imageView, R.drawable.search_icon_default, PascImageLoader.SCALE_DEFAULT);
        }
    }

    return rootView;
  }
}
