package com.pasc.business.search.util;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pasc.business.search.customview.DropDownMenu;


/**
 * 对DropDownMenu进行封装
 */
public class DropDownMenuHelper {

    public DropDownMenu fillDataIntoDropDownMenu(Context context, Object object, DropDownMenu dropDownMenu) {
        if (dropDownMenu == null || context == null || object == null) {
            return dropDownMenu;
        }
        fillEmptyView(dropDownMenu, context);
        return dropDownMenu;
    }

    /**
     * DropDownMenu需要添加一个内容显示区域的布局
     */
    private void fillEmptyView(DropDownMenu dropDownMenu, Context context) {
        TextView contentView = new TextView(context);
        contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
        contentView.setGravity(Gravity.CENTER);
        contentView.setAlpha(0);
        // TODO: 2019/2/28
//        dropDownMenu.setDropDownMenu();
    }
}
