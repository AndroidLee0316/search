package com.pasc.business.search.event;

/**
 * @author yangzijian
 * @date 2019/3/14
 * @des
 * @modify
 **/
public interface EventTable {


    String HomePersonEventId = "个人版首页-搜索-搜索明细";
    String HomeEnterpriseEventId="企业版首页-搜索-搜索明细";

    String BanShiPersonEventId="个人版办事指南-搜索-搜索明细";
    String BanShiEnterpriseEventId="企业版办事指南-搜索-搜索明细";


    String WordLabel = "搜索词";
    String HotWordLabel="热门搜索词";
    String MoreLabel="垂直搜索入口";
    String HistoryLabel="历史搜索词";
    String ResultLabel="搜索结果";
    String EmptyLabel="空结果";
    String SearchTypeKey="搜索类型";
    String ClearLabel="清空搜索词";
    String BackLabel="返回";
    String ClearHistoryLabel="清除搜索记录";

    String HomeVoiceEventId="搜索详情页-语音搜索";
    String ClickLabel="点击";
    String NoVoiceLabel="无语音输入";
    String HasLabel="有语音输入";
    String VoiceResult="普通搜索结果";
    String ConfirmDialog="确认弹窗";
    String ConfirmDialogEng="search_delete";
    String WordLabelEng="search_tag";
    String ResultLabelEng="search_result";
    String SearchTypeKeyEng="search_type";
    String MoreLabelEng="search_direct";

}
