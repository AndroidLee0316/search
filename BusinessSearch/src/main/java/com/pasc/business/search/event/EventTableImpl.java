package com.pasc.business.search.event;

import com.pasc.business.search.SearchManager;

/**
 * Created by zhangxu678 on 2019-09-18.
 */
public class EventTableImpl implements EventTable {
  private static EventTableImpl eventTable=new EventTableImpl();

  private EventTableImpl() {
  }
  public static EventTableImpl getInstance(){
    return eventTable;
  }
  public String getWordLabelAccordingly() {
    return SearchManager.instance().isEnglishParams() ? WordLabelEng : WordLabel;
  }

  public String getResultLabelAccordingly() {
    return SearchManager.instance().isEnglishParams() ? ResultLabelEng : ResultLabel;
  }

  public String getConfirmDialogAccordingly() {
    return SearchManager.instance().isEnglishParams() ? ConfirmDialogEng : ConfirmDialog;
  }
  public String getSearchTypeAccordingly() {
    return SearchManager.instance().isEnglishParams() ? SearchTypeKeyEng : SearchTypeKey;
  }
  public String getMoreLabelAccordingly(){
    return SearchManager.instance().isEnglishParams() ? MoreLabelEng : MoreLabel;
  }
}
