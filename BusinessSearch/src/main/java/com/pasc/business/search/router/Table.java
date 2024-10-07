package com.pasc.business.search.router;

/**
 * @author yangzijian
 * @date 2019/3/5
 * @des
 * @modify
 **/
public interface Table {

    interface Path {
        String path_search_home_router = "/search/entrance/main";

        String path_search_more_router = "/search/theme/main";


    }

    interface Key {
        //入口类型 1：个人版  2：企业版
        // Value.EntranceLocation
        String key_entranceLocation = "entranceLocation";

        //入口ID
        String key_entranceId = "entranceId";

        String key_themeConfigId = "themeConfigId";

        String key_totalSize = "totalSize";

        // Value.HomeType   Value.MoreType
        String key_id_type = "key_id_type";

        String key_word = "key_word";
        String key_search_type = "key_search_type";
        String key_search_type_name = "key_search_type_name";

        String key_wang_ting_flag="key_wang_ting_flag";

        String key_hide_keyboard_flag="key_hide_keyboard_flag";

        String key_content_search_type = "key_content_search_type";

        //展示语音
        String key_show_voice_anim = "key_show_voice_anim";

        /**
         * 外部传入默认显示的搜索关键字，不从服务器获取
         */
        String key_search_hint = "key_search_hint";

    }

    interface Value {

        interface EntranceLocation {
            String person_type = "1";
            String enterprise_type = "2";
        }

        // 网络搜索修改通过入口id区分不同搜索
        interface EntranceId{
            String personal_home_test = "personal_home_page";
        }

        interface ThemeId{
            // 本地写死主题ID
            String local = "local_service_pool";
        }

        interface HomeType {
            //个人版首页
            String personal_home_page = "personal_home_page";
            //企业版首页
            String business_home_page = "business_home_page";
        }

        interface MoreType {
            //个人版政企号
            String personal_union_page = "personal_union_page";
            //个人版更多服务页
            String personal_more_service_page = "personal_more_service_page";

            //个人版办事指南页
            String personal_service_guide_page = "personal_service_guide_page";

            //个人版证企号服务
            String personal_zhengqi_server = "personal_zhengqi_server";
            //企业版办事指南页
            String business_service_guide_page = "business_service_guide_page";
            //网厅
            String personal_service_hall="personal_service_hall";

            //政策法规
            String personal_policy_page="personal_policy_page";
        }


    }

    interface Prefer{
        String pre_type_key="pre_type_key";


    }
}
