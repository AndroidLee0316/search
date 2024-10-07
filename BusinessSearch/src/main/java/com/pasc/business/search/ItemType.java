package com.pasc.business.search;

import com.pasc.business.search.home.itemconvert.PolicyHolderConvert;
import com.pasc.business.search.home.itemconvert.ServiceHolderConvert;
import com.pasc.business.search.more.itemconvert.BanShiMoreHolderConvert;
import com.pasc.business.search.more.itemconvert.PolicyMoreHolderConvert;
import com.pasc.business.search.more.itemconvert.ServiceMoreHolderConvert;
import com.pasc.business.search.more.itemconvert.ZQHMoreHolderConvert;
import com.pasc.business.search.more.itemconvert.ZQHServiceMoreHolderConvert;
import com.pasc.lib.search.ItemManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yangzijian
 * @date 2019/2/20
 * @des
 * @modify
 **/
public class ItemType {
    /***搜索类型***/
    // 语音搜索
    public static final String VoiceSearchType="1";
    //手动搜索
    public static final String ManualSearchType="2";
    public static final String SearchContentTypeKey="SearchContentTypeKey";


    public static final String HomeItemManager = "HomeItemManager";
    public static final String MoreItemManager = "MoreItemManager";

    /******个人版 类型******/

    //服务
    public static final String personal_server = "personal_service";
    //政企号 / 办事服务
    public static final String personal_zhengqi_number = "personal_union";

    //政企号服务 / 部门服务
    public static final String personal_zhengqi_server = "personal_union_service";

    //办事指南
    public static final String personal_work_guide = "personal_work_guide";
    //政策法规
    public static final String personal_policy_rule = "personal_policy_rule";

    //主题搜索
    public static final String personal_theme = "personal_theme";

    public final static int defaultType = 1000;


    private final static Map<String, ItemInnerBean> itemTypeMap = new HashMap<> ();

    static class ItemInnerBean {
        public int priority;
        public String groupName;
        public int itemType;

        public ItemInnerBean(  int itemType,String groupName,int priority) {
            this.itemType = itemType;
            this.groupName = groupName;
            this.priority = priority;

        }
    }

    public static void init() {

    }

    /***
     *
     * @param type  搜索类型
     * @param itemType  布局type
     * @param groupName 组名称
     * @param priority  组排序优先级
     */
    public static void registerItemType(String type, int itemType, String groupName, int priority) {
        itemTypeMap.put (type, new ItemInnerBean (itemType, groupName, priority));
    }

    /***内置类型**/
    static {
        registerItemType (personal_server, 0, "服务", 0);
        registerItemType (personal_zhengqi_number, 1, "办事部门", 1);
        registerItemType (personal_zhengqi_server, 2, "部门服务", 2);
        registerItemType (personal_work_guide, 3, "办事指南", 3);
        registerItemType (personal_policy_rule, 4, "政策法规", 4);

        ItemManager.instance (ItemType.HomeItemManager)
                .registerItemConvert (ItemType.personal_server, new ServiceHolderConvert ())
                .registerItemConvert (ItemType.personal_policy_rule, new PolicyHolderConvert ());

        ItemManager.instance (ItemType.MoreItemManager)
                .registerItemConvert (ItemType.personal_server, new ServiceMoreHolderConvert ())
                .registerItemConvert (ItemType.personal_zhengqi_number, new ZQHMoreHolderConvert ())
                .registerItemConvert (ItemType.personal_zhengqi_server, new ZQHServiceMoreHolderConvert ())
                .registerItemConvert (ItemType.personal_work_guide, new BanShiMoreHolderConvert ())
                .registerItemConvert (ItemType.personal_policy_rule, new PolicyMoreHolderConvert ());
    }

    /***
     * 获取 布局item   type
     * @param type
     * @return
     */
    public static int getItemTypeFromType(String type) {
        ItemInnerBean innerBean = itemTypeMap.get (type);
        if (innerBean == null) {
            return defaultType;
        }
        return innerBean.itemType;
    }

    /***
     *获取组排序 优先级
     * @param type
     * @return
     */
    public static int getPriorityFromType(String type) {
        ItemInnerBean innerBean = itemTypeMap.get (type);
        if (innerBean == null) {
            return defaultType;
        }
        return innerBean.priority;
    }

    /***
     * 获取组名称
     * @param type
     * @return
     */
    public static String getGroupNameFromType(String type) {
        ItemInnerBean innerBean = itemTypeMap.get (type);
        if (innerBean == null) {
            return "";
        }
        return innerBean.groupName;
    }
}
