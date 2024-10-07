package com.pasc.business.search.customview.gifview;

import android.view.View;
import android.widget.AdapterView;

public interface ItemListViewListener {

  public void onItemClick(AdapterView<?> parent, View view, int position, long id);

}
