package com.pasc.lib.search;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

/**
 * @author yangzijian
 * @date 2019/2/21
 * @des
 * @modify
 **/
public abstract class BaseItemConvert implements ItemConvert {
    public static final String BaseColorStr="#27A5F9";

    public static SpannableString getSpannableString(Context context, String name, String[] keywords) {
        if (TextUtils.isEmpty (name)) {
            return new SpannableString ("");
        } else {
            SpannableString spannableString = new SpannableString (name);
            String upperCaseName = name.toUpperCase();
            if(keywords != null){
                for(String keyword:keywords){
                    if (!TextUtils.isEmpty (keyword)) {

                        String upperCaseKeyword = keyword.toUpperCase();
                        for(int i=-1;(i=upperCaseName.indexOf (upperCaseKeyword,i+1)) !=-1;i++){
                            //spannableString.setSpan (new ForegroundColorSpan (Color.parseColor (BaseColorStr)), i, i + keyword.length (), 33);
                            spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.search_main_color)), i, i + keyword.length (), 33);
                        }

                    }
                }
            }

            return spannableString;
        }
    }
    public static SpannableString getSpannableString(Context context, String name, int start, int end) {
        if (TextUtils.isEmpty (name)) {
            return new SpannableString ("");
        } else {
            SpannableString spannableString = new SpannableString (name);
            //spannableString.setSpan (new ForegroundColorSpan (Color.parseColor (BaseColorStr)), start, end, 33);
            spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.search_main_color)), start, end,  33);
            return spannableString;
        }
    }



}
