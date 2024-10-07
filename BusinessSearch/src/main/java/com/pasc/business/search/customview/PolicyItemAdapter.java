package com.pasc.business.search.customview;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pasc.business.search.R;
import com.pasc.lib.search.BaseItemConvert;
import com.pasc.lib.search.ISearchItem;

import java.util.List;

/**
 * Created by huangtebian535 on 2019/09/03.
 */

public class PolicyItemAdapter extends BaseAdapter {

  private List<ISearchItem> data;
  private LayoutInflater inflater;
  private String[] keyword;
  private Context mContext;

  public PolicyItemAdapter(Context context, List<ISearchItem> data, String[] keyword) {
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
    View rootView = inflater.inflate(R.layout.pasc_search_policy_item, null);

    SpannableString spannableString= BaseItemConvert.getSpannableString (mContext, data.get(position).title (),keyword);
    ((TextView) rootView.findViewById (R.id.tv_title)).setText (spannableString);
    TextView tv_content=rootView.findViewById (R.id.tv_department_name);
    SpannableString spannableString2 = BaseItemConvert.getSpannableString (mContext, data.get(position).content (), keyword);
    tv_content.setText (spannableString2);
    TextView tv_date=rootView.findViewById (R.id.tv_date);
    tv_date.setText (data.get(position).date ());

    return rootView;
  }
}
