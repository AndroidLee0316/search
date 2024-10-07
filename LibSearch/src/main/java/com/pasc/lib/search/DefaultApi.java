package com.pasc.lib.search;

import android.app.Activity;
import android.content.Context;

import java.util.Map;

/**
 * @author yangzijian
 * @date 2019/3/14
 * @des
 * @modify
 **/
 public class DefaultApi extends ApiGet{
    @Override
    public String getPhone() {
        return null;
    }

    @Override
    public String getToken() {
        return null;
    }

    @Override
    public void searchItemClick(Activity activity, ISearchItem item) {

    }

    @Override
    public void onEvent(Context context, String eventId, String label, Map map) {

    }
}
