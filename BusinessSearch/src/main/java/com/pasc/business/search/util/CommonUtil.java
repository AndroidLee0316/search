package com.pasc.business.search.util;

import android.text.TextUtils;

import com.pasc.lib.search.util.ToastUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

  /**
   * 如果提示语中，有""则取双引号中的内容，如果有冒号，则取冒号之后的内容
   * @param hintText
   * @return
   */
  public static String getKeyWord(String hintText){
    String keyword = "";
    if (!TextUtils.isEmpty(hintText)) {
      Pattern p = Pattern.compile("“(.*?)”");
      Matcher m = p.matcher(hintText);
      while (m.find()) {
        keyword = m.group();
      }
      if (TextUtils.isEmpty(keyword)) {
        if(hintText.indexOf("：") >= 0){
          keyword = hintText.substring(hintText.indexOf("：") + 1);
        }
        if(TextUtils.isEmpty(keyword)){
          ToastUtil.showToast ("请输入搜索内容");
        }
      } else {
        keyword = keyword.substring(1, keyword.length() - 1);
      }
    }
    return keyword;
  }
}
