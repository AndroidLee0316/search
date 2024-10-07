package com.pasc.business.search.customview;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;

import com.pasc.business.search.R;
import com.pasc.lib.search.LocalSearchManager;
import com.pasc.lib.search.util.SearchUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lingchun147 on 2017/12/22.
 */

public class ClearEditText extends android.support.v7.widget.AppCompatEditText
        implements View.OnFocusChangeListener, TextWatcher {
    /**
     * 删除按钮的引用
     */
    public Drawable mClearDrawable;
    private Context context;
    private EditTextChangeListener editTextChangeListener;
    private int focusCount = 0;//焦点计数，大于1则表示重新获得焦点
    private boolean isNeedResetText = false;//是否需要重置
    private static final int LIMIT_LENGTH = 30;
    private boolean showClose=true;
    private boolean notCallBack=false;

    public void setChangeCallBack(boolean notCallBack) {
        this.notCallBack = notCallBack;
    }

    public void setShowClose(boolean showClose) {
        this.showClose = showClose;
    }

    /**
     * 控件是否有焦点
     */
    private boolean hasFocus;
    private int inputType;//输入类型
    private IconDismissListener iconDismissListener;
    private InnerFocusChangeListener innerFocusChangeListener = InnerFocusChangeListener.NONE;

    public interface InnerFocusChangeListener {
        InnerFocusChangeListener NONE = new InnerFocusChangeListener () {
            @Override
            public void onInnerFocusChange(View v, boolean hasFocus) {
            }
        };

        public void onInnerFocusChange(View v, boolean hasFocus);
    }

    public ClearEditText(Context context) {
        this (context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        this (context, attrs, android.R.attr.editTextStyle);
    }

    public void setInnerFocusChangeListener(InnerFocusChangeListener innerFocusChangeListener) {
        this.innerFocusChangeListener = innerFocusChangeListener;
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super (context, attrs, defStyle);
        this.context=context;
        init ();
    }

    private void init() {
        //获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        mClearDrawable = getCompoundDrawables ()[2];
        if (mClearDrawable == null) {
            mClearDrawable = getResources ().getDrawable (R.drawable.search_clear_icon);
        }
        mClearDrawable.setBounds (0, 0, mClearDrawable.getIntrinsicWidth (),
                mClearDrawable.getIntrinsicHeight ());
        //默认设置隐藏图标
        setClearIconVisible (false);
        //设置焦点改变的监听
        setOnFocusChangeListener (this);
        //设置输入框里面内容发生改变的监听
        addTextChangedListener (this);
        setEtFilter ();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!showClose){
            return super.onTouchEvent (event);
        }

        if (mClearDrawable != null && event.getAction () == MotionEvent.ACTION_UP) {
            //            int x = (int) event.getX();
            //            //判断触摸点是否在水平范围内
            //            boolean isInnerWidth = (x > (getWidth() - getTotalPaddingRight())) && (x < (getWidth()
            //                    - getPaddingRight()));
            //            //获取删除图标的边界，返回一个Rect对象
            //            Rect rect = mClearDrawable.getBounds();
            //            //获取删除图标的高度
            //            int height = rect.height();
            //            int y = (int) event.getY();
            //            //计算图标底部到控件底部的距离
            //            int distance = (getHeight() - height) / 2;
            //            //判断触摸点是否在竖直范围内(可能会有点误差)
            //            //触摸点的纵坐标在distance到（distance+图标自身的高度）之内，则视为点中删除图标
            //            boolean isInnerHeight = (y > distance) && (y < (distance + height));
            //            if (isInnerHeight && isInnerWidth) {
            //                this.setText("");
            //                if (iconDismissListener != null) {
            //                    iconDismissListener.onIconClick();
            //                }
            //            }

//            int eventX = (int) event.getRawX ();
//            int eventY = (int) event.getRawY ();
//            Rect rect = new Rect ();
//            getGlobalVisibleRect (rect);
//            rect.left = rect.right - 100;
//            if (rect.contains (eventX, eventY)) {
//                this.setText ("");
//                if (iconDismissListener != null) {
//                    iconDismissListener.onIconClick ();
//                }
//            }
            int eventX = (int) event.getRawX();
            Rect rect = new Rect();
            getGlobalVisibleRect(rect);
            rect.left = rect.right - 100;
            if (eventX<=rect.right&&eventX>=rect.left) {
                this.setText("");
                if (iconDismissListener != null) {
                    iconDismissListener.onIconClick();
                }
            }
        }
        return super.onTouchEvent (event);
    }

    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     */
    private void setClearIconVisible(boolean visible) {
        if (!showClose){
            return;
        }
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables (getCompoundDrawables ()[0], getCompoundDrawables ()[1], right,
                getCompoundDrawables ()[3]);
    }

    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFocus = hasFocus;
        if (hasFocus) {
            setClearIconVisible (getText ().length () > 0);
            focusCount++;
            inputType = getInputType ();
        } else {
            if (TextUtils.isEmpty (getText ())) {
                focusCount = 0;
            }
            setClearIconVisible (false);
        }
        innerFocusChangeListener.onInnerFocusChange (v, hasFocus);
    }

    public static String stringFilter(String str) {
        //只允许汉字
        if (!SearchUtil.isEmpty (str)) {
            try {
                // 只允许汉字和数字和英文
//                String regEx = "[^a-zA-Z0-9\u4E00-\u9FA5]";
                //过滤表情
                String regEx = "[^\\u0000-\\uFFFF]";
                Pattern p = Pattern.compile (regEx);
                Matcher m = p.matcher (str);
                return m.replaceAll ("").trim ();
            } catch (Exception e) {
                e.printStackTrace ();
            }
        } else {
            return "";
        }
        return str;
    }

    /**
     * 1.当输入框里面内容发生变化的时候回调的方法
     * 2.当输入的手机号符合要求就回调该方法
     */
    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore,
                              int lengthAfter) {
        if (hasFocus) {
            String strs = getText ().toString ();
//            String str = stringFilter (strs.toString ());
//            if (!strs.equals (str)) {
//                setText (str);
//            setSelection (strs.length ());
//            }
            setClearIconVisible (strs.trim ().length () > 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (focusCount > 1 && isPasswordInputType () && count > 0) {//重新获得焦点，输入类型为密码，点删除按钮
            focusCount = 1;
            setText ("");
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        String keyword=s.toString();
        int length = keyword.length();
        if (length > LIMIT_LENGTH && !LocalSearchManager.instance ().getApi ().ignoreLength ( keyword)) {
            s.delete(LIMIT_LENGTH, length);
            int tempSelection = s.length();
            setText(s);
            keyword=s.toString ();
            setSelection(tempSelection);//设置光标在最后
        }
        if (editTextChangeListener != null && !notCallBack) {
            editTextChangeListener.afterChange (SearchUtil.isEmpty (keyword) ? "" : keyword);
        }
        notCallBack=false;
    }


    /**
     * 设置晃动动画
     */
    public void setShakeAnimation() {
        this.startAnimation (shakeAnimation (5));
    }

    /**
     * 晃动动画
     *
     * @param counts 1秒钟晃动多少下
     */
    public static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation (0, 10, 0, 0);
        translateAnimation.setInterpolator (new CycleInterpolator (counts));
        translateAnimation.setDuration (1000);
        return translateAnimation;
    }

    public void setEditTextChangeListener(EditTextChangeListener editTextChangeListener) {
        this.editTextChangeListener = editTextChangeListener;
    }

    /**
     * 设置右边图标的点击事件
     */
    public void setIconDismissListener(IconDismissListener iconDismissListener) {
        this.iconDismissListener = iconDismissListener;
    }

    public interface IconDismissListener {
        void onIconClick();
    }

    public interface EditTextChangeListener {
        void afterChange(String s);
    }

    /**
     * 输入框输入类型是否为password
     */
    private boolean isPasswordInputType() {
        return inputType == EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD
                || inputType == (EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
                | InputType.TYPE_CLASS_TEXT)
                || inputType == EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                || inputType == EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD;
    }

    /**
     * 过滤掉常见特殊字符,常见的表情
     */
    public void setEtFilter() {
        //表情过滤器
        InputFilter emojiFilter = new InputFilter () {

            /**
             * @param source 输入的文字
             * @param start 输入-0，删除-0
             * @param end 输入-source文字的长度，删除-0
             * @param dest 原先显示的内容
             * @param dstart 输入-原光标位置，删除-光标删除结束位置
             * @param dend  输入-原光标位置，删除-光标删除开始位置
             * @return
             */
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                       int dstart, int dend) {

                try {
                    String regex = "(?:[\\uD83C\\uDF00-\\uD83D\\uDDFF]|[\\uD83E\\uDD00-\\uD83E\\uDDFF]|[\\uD83D\\uDE00-\\uD83D\\uDE4F]|[\\uD83D\\uDE80-\\uD83D\\uDEFF]|[\\u2600-\\u26FF]\\uFE0F?|[\\u2700-\\u27BF]\\uFE0F?|\\u24C2\\uFE0F?|[\\uD83C\\uDDE6-\\uD83C\\uDDFF]{1,2}|[\\uD83C\\uDD70\\uD83C\\uDD71\\uD83C\\uDD7E\\uD83C\\uDD7F\\uD83C\\uDD8E\\uD83C\\uDD91-\\uD83C\\uDD9A]\\uFE0F?|[\\u0023\\u002A\\u0030-\\u0039]\\uFE0F?\\u20E3|[\\u2194-\\u2199\\u21A9-\\u21AA]\\uFE0F?|[\\u2B05-\\u2B07\\u2B1B\\u2B1C\\u2B50\\u2B55]\\uFE0F?|[\\u2934\\u2935]\\uFE0F?|[\\u3030\\u303D]\\uFE0F?|[\\u3297\\u3299]\\uFE0F?|[\\uD83C\\uDE01\\uD83C\\uDE02\\uD83C\\uDE1A\\uD83C\\uDE2F\\uD83C\\uDE32-\\uD83C\\uDE3A\\uD83C\\uDE50\\uD83C\\uDE51]\\uFE0F?|[\\u203C\\u2049]\\uFE0F?|[\\u25AA\\u25AB\\u25B6\\u25C0\\u25FB-\\u25FE]\\uFE0F?|[\\u00A9\\u00AE]\\uFE0F?|[\\u2122\\u2139]\\uFE0F?|\\uD83C\\uDC04\\uFE0F?|\\uD83C\\uDCCF\\uFE0F?|[\\u231A\\u231B\\u2328\\u23CF\\u23E9-\\u23F3\\u23F8-\\u23FA]\\uFE0F?)";
                    Pattern emoji = Pattern.compile (
                            regex,
                            Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
                    Matcher emojiMatcher = emoji.matcher (source);
                    // 千万别用这句代码
//                    source = emojiMatcher.replaceAll ("");
                    if (emojiMatcher.find ()) {
                        return "";
                    }
                } catch (Exception e) {
                }


                return source;
            }
        };
        //特殊字符过滤器
//        InputFilter specialCharFilter = new InputFilter() {
//            @Override
//            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                String regexStr = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
//                Pattern pattern = Pattern.compile(regexStr);
//                Matcher matcher = pattern.matcher(source.toString());
//                if (matcher.matches()) {
//                    return "";
//                } else {
//                    return source;
//                }
//
//            }
//        };

        setFilters (new InputFilter[]{emojiFilter});
    }

    private boolean containsEmoji(String str) {
        int len = str.length ();
        for (int i = 0; i < len; i++) {
            if (isEmojiCharacter (str.charAt (i))) {
                return true;
            }
        }
        return false;
    }

    private boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }

}
