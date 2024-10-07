package com.pasc.business.search.customview;

import android.view.KeyEvent;
import android.widget.TextView;

/**
 * @author yangzijian
 * @date 2019/4/9
 * @des
 * @modify
 **/
public interface MyOnEditorActionListener {

    boolean onEditorAction(TextView v, int actionId, KeyEvent event,boolean intercept);

}
