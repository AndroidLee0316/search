package com.pasc.lib.search.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * @author yangzijian
 * @date 2019/2/18
 * @des
 * @modify
 **/
public abstract class BaseActivity extends AppCompatActivity {

    private Bundle bundleDate;

    protected abstract int initLayout();

    protected abstract void initView();

    protected abstract void initData(Bundle bundleData);

    /**
     * 初始化其他
     */
    protected void setContViewBefore(Bundle savedInstanceState) {
    }

    /**
     * 初始化其他
     */
    protected void setContViewAfter(Bundle savedInstanceState) {
    }

    /**
     * needExtendLayout 返回true时使用  needExtendLayout 作为父布局  initLayout则作为子布局
     * 返回false 则默认用 initLayout
     *
     * @return
     */
    protected int getExtendLayout() {
        return -1;
    }

    /**
     * 拓展布局用 返回true 则 用 needExtendLayout initLayout作为 needExtendLayout, 反之用 initLayout
     *
     * @return
     */
    protected boolean needExtendLayout() {
        return false;
    }

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        setContViewBefore (savedInstanceState);
        super.onCreate (savedInstanceState);
        /*** 拓展布局用 返回true 则 用 needExtendLayout initLayout作为 needExtendLayout, 反之用 initLayout ****/
        @LayoutRes int layout = needExtendLayout () && getExtendLayout () > 0 ? getExtendLayout () : initLayout ();
        setContentView (layout);
        setContViewAfter (savedInstanceState);
        initView ();
        getBundleDate (savedInstanceState);
    }

    /**
     * singleTop  singleTask 或者 singleInstance
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent (intent);
        setIntent (intent);
        getBundleDate (null);
    }

    /**
     * 获取传递的bundle数据
     * bundle数据来源有三个
     * 1：界面正常跳转来的intent中
     * 2：onNewIntent中重新传来的intent中
     * 3：界面被后台回收后回到界面的savedInstanceState中
     */
    private void getBundleDate(Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            /****跳转携带过来的数据*****/
            if (getIntent () != null) {
                bundleDate = getIntent ().getExtras ();
            }
        } else {
            /****由于系统销毁而保存的数据*****/
            bundleDate = savedInstanceState;
        }

        if (bundleDate == null) {
            /**防止空指针 *****/
            bundleDate = new Bundle ();
        }
        initData (bundleDate);

    }


    /**
     * 调用时机
     * 1、当用户按下HOME键时。
     * 2、长按HOME键，选择运行其他的程序时。
     * 3、按下电源按键（关闭屏幕显示）时。
     * 4、从activity A中启动一个新的activity时。
     * 5、屏幕方向切换时，例如从竖屏切换到横屏时。
     * 总而言之，onSaveInstanceState的调用遵循一个重要原则，即当系统“未经你许可”时销毁了你的activity，
     * 则onSaveInstanceState会被系统调用，
     * 这是系统的责任，因为它必须要提供一个机会让你保存你的数据（当然你不保存那就随便你了）。
     *
     * @param savedInstanceState
     */
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null && bundleDate != null) {
            savedInstanceState.putAll (bundleDate);
        }
        super.onSaveInstanceState (savedInstanceState);
    }

    /**
     * 至于onRestoreInstanceState方法，需要注意的是，
     * onSaveInstanceState方法和onRestoreInstanceState方法“不一定”是成对的被调用的
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState (savedInstanceState);
        // 获取数据
    }

    public BaseActivity getActivity(){
        return this;
    }

    public void gotoActivity(Class<? extends Activity> targetClass) {
        gotoActivity (targetClass, null);
    }

    public void gotoActivity(Class<? extends Activity> targetClass, Bundle bundle) {
        gotoActivity (targetClass, bundle, -1);
    }

    public void gotoActivity(Class<? extends Activity> targetClass, int requestCode) {
        gotoActivity (targetClass, null, -1);

    }

    public void gotoActivity(Class<? extends Activity> targetClass, Bundle bundle, int requestCode) {
        Intent intent = new Intent (this, targetClass);
        if (bundle != null) {
            intent.putExtras (bundle);
        }
        startActivityForResult (intent, requestCode);
    }
}
