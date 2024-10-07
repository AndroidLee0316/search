package com.pasc.lib.search;


import android.util.ArrayMap;

import com.pasc.lib.search.db.SearchSourceItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yangzijian
 * @date 2019/3/5
 * @des
 * @modify
 **/
public class SearchSourceGroup<T extends ISearchItem> implements ISearchGroup {
    public List<T> data;
    public String groupName;
    //排序
    public int priority;
    public String type;

    public ArrayList<String> analyzers;


    public Map<String, String> extraData = new HashMap<>(2);

    @Override
    public Map<String, String> extraData() {
        return extraData;
    }

    @Override
    public List<T> data() {
        return data;
    }

    @Override
    public String groupName() {
        return groupName;
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public String searchType() {
        return type;
    }

    @Override
    public int getItemType() {
        return LocalSearchManager.instance().getType().getItemTypeFromType(type);
    }

    public boolean isLocalSearchGroup() {
        return this.data != null && this.data.size() > 0  && this.data.get(0) instanceof SearchSourceItem;
    }

    public void optimizeData(int groups) {
        if (groups > 4) {
            groups = 4;
        }

        int maxNum = 12 / groups;
        this.setData(this.data.size(), maxNum);
    }


    private void setData(int size, int maxNum) {
        if (maxNum != 0) {
            if (!"true".equals(this.extraData.get("showMore"))) {
                if (size > maxNum) {
                    this.extraData.put("showMore", "true");
                } else {
                    this.extraData.put("showMore", "false");
                }
            }

            this.data = this.data.subList(0, this.data.size() > maxNum ? maxNum : this.data.size());
        }
    }
}
