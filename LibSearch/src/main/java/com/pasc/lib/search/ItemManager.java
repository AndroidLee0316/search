package com.pasc.lib.search;

import com.pasc.lib.search.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yangzijian
 * @date 2019/2/21
 * @des   动态Item 布局管理器
 * @modify
 **/
public class ItemManager {
    private static final Map<String, ItemManager> MANAGER_MAP = new HashMap<> ();
    private ItemManager() {
    }

    public static ItemManager instance(String key) {
        ItemManager itemManager = MANAGER_MAP.get (key);
        if (itemManager == null) {
            itemManager = new ItemManager ();
            MANAGER_MAP.put (key, itemManager);
        }
        return itemManager;
    }

    private Map<String, ItemConvert> convertMap = new HashMap<> ();

    public ItemManager registerItemConvert(String type, ItemConvert itemConvert) {
        if (itemConvert != null) {
            if (convertMap.containsKey (type)) {
                //
                LogUtil.log ("has contain ItemConvert");
            }
            convertMap.put (type, itemConvert);
        }
        return this;
    }

    public void unRegisterItemConvert(String type) {
        convertMap.remove (type);
    }

    public ItemConvert getItemConvert(String type) {
        return convertMap.get (type);
    }

    public Map<String, ItemConvert> getConvertMap() {
        return convertMap;
    }

}
