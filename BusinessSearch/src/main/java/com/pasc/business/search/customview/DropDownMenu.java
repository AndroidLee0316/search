package com.pasc.business.search.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pasc.business.search.R;
import com.pasc.lib.search.util.DimensUtils;

import java.util.List;


/**
 * Created by dongjunkun on 2015/6/17.
 */
public class DropDownMenu extends LinearLayout {

    //顶部菜单布局
    private LinearLayout tabMenuView;
    //底部容器，包含popupMenuViews，maskView
    private FrameLayout containerView;
    //弹出菜单父布局
    private FrameLayout popupMenuViews;
    //遮罩半透明View，点击可关闭DropDownMenu
    private View maskView;
    //tabMenuView里面选中的tab位置，-1表示未选中
    private int current_tab_position = -1;

    //分割线颜色
    private int dividerColor = 0xffcccccc;
    //tab选中颜色
    private int textSelectedColor = 0x27a5f9;
    //tab未选中颜色
    private int textUnselectedColor = 0xff111111;
    //遮罩颜色
    private int maskColor = 0x88888888;
    //tab字体大小
    private int menuTextSize = 14;
    //禁止颜色
    private int disableColor= 0xff111111;

    //tab选中图标
    private int menuSelectedIcon=R.drawable.search_down_unselected;
    //tab未选中图标
    private int menuUnselectedIcon=R.drawable.search_down_selected;

    private int menuDisableIcon=R.drawable.search_down_selected;

    private float menuHeighPercent = 0.5f;


    public DropDownMenu(Context context) {
        super (context, null);
    }

    public DropDownMenu(Context context, AttributeSet attrs) {
        this (context, attrs, 0);
    }

    public DropDownMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);

        setOrientation (VERTICAL);

        //为DropDownMenu添加自定义属性
        int menuBackgroundColor = 0xffffffff;
        int underlineColor = 0xffcccccc;
        TypedArray a = context.obtainStyledAttributes (attrs, R.styleable.DropDownMenu);
        underlineColor = a.getColor (R.styleable.DropDownMenu_ddunderlineColor, underlineColor);
        dividerColor = a.getColor (R.styleable.DropDownMenu_dddividerColor, dividerColor);
        textSelectedColor = a.getColor (R.styleable.DropDownMenu_ddtextSelectedColor, textSelectedColor);
        disableColor= a.getColor (R.styleable.DropDownMenu_ddtextDisableColor, disableColor);
        textUnselectedColor = a.getColor (R.styleable.DropDownMenu_ddtextUnselectedColor, textUnselectedColor);
        menuBackgroundColor = a.getColor (R.styleable.DropDownMenu_ddmenuBackgroundColor, menuBackgroundColor);
        maskColor = a.getColor (R.styleable.DropDownMenu_ddmaskColor, maskColor);
        menuTextSize = a.getDimensionPixelSize (R.styleable.DropDownMenu_ddmenuTextSize, menuTextSize);
        menuSelectedIcon = a.getResourceId (R.styleable.DropDownMenu_ddmenuSelectedIcon, menuSelectedIcon);
        menuUnselectedIcon = a.getResourceId (R.styleable.DropDownMenu_ddmenuUnselectedIcon, menuUnselectedIcon);
        menuDisableIcon=a.getResourceId (R.styleable.DropDownMenu_ddmenuDisableIcon, menuDisableIcon);
        menuHeighPercent = a.getFloat (R.styleable.DropDownMenu_ddmenuMenuHeightPercent, menuHeighPercent);
        a.recycle ();
        //不需要分割线
        dividerColor=0xffffffff;
        //初始化tabMenuView并添加到tabMenuView
        tabMenuView = new LinearLayout (context);
        LayoutParams params = new LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, dpTpPx (45));
        tabMenuView.setOrientation (HORIZONTAL);
        tabMenuView.setBackgroundColor (menuBackgroundColor);
        tabMenuView.setLayoutParams (params);
        addView (tabMenuView, 0);

        //为tabMenuView添加下划线
        View underLine = new View (getContext ());
        underLine.setLayoutParams (new LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, 1));
        underLine.setBackgroundColor (underlineColor);
        addView (underLine, 1);

        //初始化containerView并将其添加到DropDownMenu
        containerView = new FrameLayout (context);
        // TODO: 2019/3/18 详情列表被containerView顶没有了
        containerView.setLayoutParams (new FrameLayout.LayoutParams (FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        addView (containerView, 2);

    }

    /**
     * 初始化DropDownMenu
     *
     * @param tabTexts
     * @param popupViews
     * @param contentView
     */
    public void setDropDownMenu(@NonNull List<String> tabTexts, @NonNull List<View> popupViews, @NonNull View contentView) {
        if (tabTexts.size () != popupViews.size ()) {
            throw new IllegalArgumentException ("params not match, tabTexts.size() should be equal popupViews.size()");
        }
        if (tabMenuView != null) {
            tabMenuView.removeAllViews ();
        }
        for (int i = 0; i < tabTexts.size (); i++) {
            addTab (tabTexts, i);
        }
        containerView.addView (contentView, 0);

        maskView = new View (getContext ());
        maskView.setLayoutParams (new FrameLayout.LayoutParams (FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        maskView.setBackgroundColor (maskColor);
        maskView.setOnClickListener (new OnClickListener () {
            @Override
            public void onClick(View v) {
                closeMenu ();
            }
        });
        containerView.addView (maskView, 1);
        maskView.setVisibility (GONE);
        if (containerView.getChildAt (2) != null) {
            containerView.removeViewAt (2);
        }

        popupMenuViews = new FrameLayout (getContext ());
        popupMenuViews.setLayoutParams (new FrameLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, (int) (DimensUtils.getScreenSize (getContext ()).y * menuHeighPercent)));
        popupMenuViews.setVisibility (GONE);
        containerView.addView (popupMenuViews, 2);

        for (int i = 0; i < popupViews.size (); i++) {
            popupViews.get (i).setLayoutParams (new ViewGroup.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            popupMenuViews.addView (popupViews.get (i), i);
        }

    }

    private void addTab(@NonNull List<String> tabTexts, int i) {
        //初始化LinearLayout
        final LinearLayout linearLayout = new LinearLayout (getContext ());
        linearLayout.setGravity (Gravity.CENTER);
        linearLayout.setOrientation (LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams (new LayoutParams (0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        linearLayout.setPadding (dpTpPx (5), dpTpPx (12), dpTpPx (5), dpTpPx (12));

        //初始化TextView
        final TextView tab = new TextView (getContext ());
        tab.setSingleLine ();
        tab.setEllipsize (TextUtils.TruncateAt.END);
        tab.setGravity (Gravity.CENTER);
        tab.setTextSize (TypedValue.COMPLEX_UNIT_PX, menuTextSize);
        tab.setLayoutParams (new LayoutParams (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tab.setTextColor (textUnselectedColor);
        tab.setText (tabTexts.get (i));


        //初始化ImageView
        ImageView imageView = new ImageView (getContext ());
        LayoutParams ivParams = new LayoutParams (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ivParams.setMargins (DimensUtils.dp2px (getContext (), 6), 0, 0, 0);
        imageView.setLayoutParams (ivParams);
        imageView.setImageResource (menuUnselectedIcon);

        //添加点击事件
        linearLayout.setOnClickListener (new OnClickListener () {
            @Override
            public void onClick(View v) {
                switchMenu (linearLayout);
            }
        });


        linearLayout.addView (tab);
        linearLayout.addView (imageView);
        tabMenuView.addView (linearLayout);

        //添加分割线
        if (i < tabTexts.size () - 1) {
            View view = new View (getContext ());
            view.setLayoutParams (new LayoutParams (dpTpPx (0.5f), ViewGroup.LayoutParams.MATCH_PARENT));
            view.setBackgroundColor (dividerColor);
            tabMenuView.addView (view);
        }
    }

    /**
     * 改变tab文字
     *
     * @param text
     */
    public void setTabText(String text) {
        if (current_tab_position != -1) {
            setTabText (text, current_tab_position, 5);
        }
    }

    public void setTabText(int len,String text) {
        if (current_tab_position != -1) {
            setTabText (text, current_tab_position, len);
        }
    }

    public void setTabText(String text, int pos) {
        setTabText (text, pos, 5);
    }

    public void setTabText(String text, int pos, int len) {
        try {
            if (text.length () > len) {
                text = text.substring (0, len) + "...";
            }
            LinearLayout linearLayout = (LinearLayout) tabMenuView.getChildAt (pos);
            ((TextView) linearLayout.getChildAt (0)).setText (text);
        } catch (Exception e) {

        }
    }

    public void setTabClickable(boolean clickable) {
        for (int i = 0; i < tabMenuView.getChildCount (); i = i + 2) {
            tabMenuView.getChildAt (i).setClickable (clickable);
        }
    }

    public void setTabClickable(boolean clickable,int index){
        try {
            LinearLayout linearLayout = (LinearLayout) tabMenuView.getChildAt (index);
            TextView textView = (TextView) linearLayout.getChildAt (0);
            textView.setTextColor (clickable?textUnselectedColor:disableColor);
            ImageView imageView = (ImageView) linearLayout.getChildAt (1);
            imageView.setImageResource (clickable?menuUnselectedIcon:menuDisableIcon);
            linearLayout.setClickable (clickable);
        }catch (Exception e){

        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        if (current_tab_position != -1) {
            LinearLayout linearLayout = (LinearLayout) tabMenuView.getChildAt (current_tab_position);
            TextView textView = (TextView) linearLayout.getChildAt (0);
            textView.setTextColor (textUnselectedColor);
            ImageView imageView = (ImageView) linearLayout.getChildAt (1);
            imageView.setImageResource (menuUnselectedIcon);
            popupMenuViews.setVisibility (View.GONE);
            popupMenuViews.setAnimation (AnimationUtils.loadAnimation (getContext (), R.anim.search_menu_out));
            maskView.setVisibility (GONE);
            maskView.setAnimation (AnimationUtils.loadAnimation (getContext (), R.anim.search_mask_out));
            current_tab_position = -1;
        }

    }

    /**
     * DropDownMenu是否处于可见状态
     *
     * @return
     */
    public boolean isShowing() {
        return current_tab_position != -1;
    }

    /**
     * 切换菜单
     *
     * @param target
     */
    private void switchMenu(View target) {
        System.out.println (current_tab_position);
        for (int i = 0; i < tabMenuView.getChildCount (); i = i + 2) {
            if (target == tabMenuView.getChildAt (i)) {
                if (current_tab_position == i) {
                    closeMenu ();
                } else {
                    if (current_tab_position == -1) {
                        popupMenuViews.setVisibility (View.VISIBLE);
                        popupMenuViews.setAnimation (AnimationUtils.loadAnimation (getContext (), R.anim.search_menu_in));
                        maskView.setVisibility (VISIBLE);
                        maskView.setAnimation (AnimationUtils.loadAnimation (getContext (), R.anim.search_mask_in));
                        popupMenuViews.getChildAt (i / 2).setVisibility (View.VISIBLE);
                    } else {
                        popupMenuViews.getChildAt (i / 2).setVisibility (View.VISIBLE);
                    }
                    current_tab_position = i;
                    LinearLayout linearLayout = (LinearLayout) tabMenuView.getChildAt (i);
                    boolean clickable= linearLayout.isClickable ();
                    TextView textView = (TextView) linearLayout.getChildAt (0);
                    textView.setTextColor (clickable?textSelectedColor:disableColor);

                    ImageView imageView = (ImageView) linearLayout.getChildAt (1);
                    imageView.setImageResource (clickable?menuSelectedIcon:menuDisableIcon);

                }
            } else {
                LinearLayout linearLayout = (LinearLayout) tabMenuView.getChildAt (i);
                boolean clickable= linearLayout.isClickable ();
                TextView textView = (TextView) linearLayout.getChildAt (0);
                textView.setTextColor (clickable?textUnselectedColor:disableColor);

                ImageView imageView = (ImageView) linearLayout.getChildAt (1);
                imageView.setImageResource (clickable?menuUnselectedIcon:menuDisableIcon);

                popupMenuViews.getChildAt (i / 2).setVisibility (View.GONE);
            }
        }
    }

    public int dpTpPx(float value) {
        DisplayMetrics dm = getResources ().getDisplayMetrics ();
        return (int) (TypedValue.applyDimension (TypedValue.COMPLEX_UNIT_DIP, value, dm) + 0.5);
    }
}
