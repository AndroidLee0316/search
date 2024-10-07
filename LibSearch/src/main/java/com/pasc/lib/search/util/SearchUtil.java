package com.pasc.lib.search.util;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.github.promeg.pinyinhelper.Pinyin;
import com.pasc.lib.search.db.SearchSourceItem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yangzijian
 * @date 2019/2/15
 * @des
 * @modify
 **/
public final class SearchUtil {

    static final ArrayMap<Character, String> SURNAMES = new ArrayMap<> ();

    static {
        SURNAMES.put ('仇', "QIU");
        SURNAMES.put ('柏', "BO");
        SURNAMES.put ('牟', "MU");
        SURNAMES.put ('颉', "XIE");
        SURNAMES.put ('解', "XIE");
        SURNAMES.put ('尉', "YU");
        SURNAMES.put ('奇', "JI");
        SURNAMES.put ('单', "SHAN");
        SURNAMES.put ('谌', "SHEN");
        SURNAMES.put ('乐', "YUE");
        SURNAMES.put ('召', "SHAO");
        SURNAMES.put ('朴', "PIAO");
        SURNAMES.put ('区', "OU");
        SURNAMES.put ('查', "ZHA");
        SURNAMES.put ('曾', "ZENG");
        SURNAMES.put ('缪', "MIAO");
    }

    public static final char DEF_CHAR = '#';


    public static SearchSourceItem index(SearchSourceItem item, String keyword) {
        if (TextUtils.isEmpty (keyword)) return null;
        SearchSourceItem cnPinyin = matcherChinese (item, keyword);
        if (isContainChinese (keyword)) {//包含中文只匹配原字符
            return item;
        }
        if (!cnPinyin.hasIndex ()) {
            matcherFirst (cnPinyin, keyword);
            if (!cnPinyin.hasIndex ()) {
                matchersPinyins (cnPinyin, keyword);
            }
        }
        return item;
    }

    /**
     * 匹配中文
     *
     * @param item
     * @param keyword
     * @return
     */
    static SearchSourceItem matcherChinese(SearchSourceItem item, String keyword) {
        if (keyword.length () <= item.name.length ()) {
            Matcher matcher = Pattern.compile (keyword, Pattern.CASE_INSENSITIVE).matcher (item.name);
            if (matcher.find ()) {
                item.start = matcher.start ();
                item.end = matcher.end ();
            }
        }
        return item;
    }

    /**
     * 匹配首字母
     *
     * @param item
     * @param keyword
     * @return
     */
    static SearchSourceItem matcherFirst(SearchSourceItem item, String keyword) {
        if (keyword.length () <= item.pinyinArr ().length) {
            Matcher matcher = Pattern.compile (keyword, Pattern.CASE_INSENSITIVE).matcher (item.firstChars);
            if (matcher.find ()) {
                item.start = matcher.start ();
                item.end = matcher.end ();
            }
        }
        return item;
    }

    /**
     * 所有拼音匹配
     *
     * @param item
     * @param keyword
     * @return
     */
    static SearchSourceItem matchersPinyins(SearchSourceItem item, String keyword) {
        int start = -1;
        int end = -1;
        for (int i = 0; i < item.pinyinArr ().length; i++) {
            String pat = item.pinyinArr ()[i];
            if (pat.length () >= keyword.length ()) {//首个位置索引
                Matcher matcher = Pattern.compile (keyword, Pattern.CASE_INSENSITIVE).matcher (pat);
                if (matcher.find () && matcher.start () == 0) {
                    start = i;
                    end = i + 1;
                    break;
                }
            } else {
                Matcher matcher = Pattern.compile (pat, Pattern.CASE_INSENSITIVE).matcher (keyword);
                if (matcher.find () && matcher.start () == 0) {//全拼匹配第一个必须在0位置
                    start = i;
                    String left = matcher.replaceFirst ("");
                    end = end (item.pinyinArr (), left, ++i);
                    break;
                }
            }
        }
        if (start >= 0 && end >= start) {
            item.start = start;
            item.end = end;
        }
        return item;
    }

    /**
     * 根据匹配字符递归查找下一结束位置
     *
     * @param pinyinGroup
     * @param pattern
     * @param index
     * @return -1 匹配失败
     */
    private static int end(String[] pinyinGroup, String pattern, int index) {
        if (index < pinyinGroup.length) {
            String pinyin = pinyinGroup[index];
            if (pinyin.length () >= pattern.length ()) {//首个位置索引
                Matcher matcher = Pattern.compile (pattern, Pattern.CASE_INSENSITIVE).matcher (pinyin);
                if (matcher.find () && matcher.start () == 0) {
                    return index + 1;
                }
            } else {
                Matcher matcher = Pattern.compile (pinyin, Pattern.CASE_INSENSITIVE).matcher (pattern);
                if (matcher.find () && matcher.start () == 0) {//全拼匹配第一个必须在0位置
                    String left = matcher.replaceFirst ("");
                    return end (pinyinGroup, left, index + 1);
                }
            }
        }
        return -1;
    }

    // 逗号隔开
    public static SearchSourceItem createCNPinyin(SearchSourceItem t) {
        if (t == null || t.name == null) return t;
        String chinese = t.name.trim ();
        if (TextUtils.isEmpty (chinese)) return null;
        char[] chars = chinese.toCharArray ();
        int lenght = chars.length;

        StringBuilder pinyins = new StringBuilder ();
        StringBuilder pinyinsSpilt = new StringBuilder ();
        StringBuilder firstChars = new StringBuilder ();


        for (int i = 0; i < lenght; i++) {
            char c = chars[i];
            String pinyin = charToPinyin (chars[i], i);
            if (i == (lenght - 1)) {
                pinyinsSpilt.append (pinyin);
            } else {
                pinyinsSpilt.append (pinyin + ";");
            }
            pinyins.append (pinyin);
            if (pinyin.length () > 0) {
                firstChars.append (pinyin.charAt (0));
            } else {
                firstChars.append (c);
            }
        }
        t.pinyinsSpilt = pinyinsSpilt.toString ();
        t.pinyins = pinyins.toString ();
        t.firstChars = firstChars.toString ();
        return t;
    }

    /**
     * @param c
     * @param index
     * @return
     */
    private static String charToPinyin(char c, int index) {
        if (index == 0) {
            String pinyin = SURNAMES.get (c);
            if (pinyin != null) {
                return pinyin;
            }
        }
        String pinyin = Pinyin.toPinyin (c);
        if (pinyin == null) {
            pinyin = String.valueOf (c);
        }
        return pinyin;
    }

    /**
     * 拼音首个字母
     *
     * @param pinyins
     * @return
     */
    private static char getFirstChar(String[] pinyins) {
        if (pinyins != null && pinyins.length > 0) {
            String firstPinying = pinyins[0];
            if (firstPinying.length () > 0) {
                return charToUpperCase (firstPinying.charAt (0));
            }
        }
        return DEF_CHAR;
    }

    /**
     * 字符转大写
     *
     * @param c
     * @return
     */
    private static char charToUpperCase(char c) {
        if (c >= 'a' && c <= 'z') {
            c -= 32;
        }
        return c;
    }

    public static boolean isContainChinese(CharSequence str) {
        if (isEmpty (str)) {
            return false;
        }
        Pattern p = Pattern.compile ("[\u4e00-\u9fa5]");
        Matcher m = p.matcher (str);
        return m.find ();
    }

    public static boolean isEmpty(CharSequence sequence) {
        if (sequence == null || TextUtils.isEmpty (sequence.toString ().trim ())) {
            return true;
        }
        return false;
    }

}
