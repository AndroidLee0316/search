package com.pasc.lib.search.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

/**
 * Created by  yzj
 * on 2017/4/27.
 */

public abstract class BaseFragment extends Fragment {
    protected View rootView;
    /**
     * 结合needExtendLayout 返回true时使用  getExtendLayout子布局 作为父布局  initLayout则作为子布局
     * 返回false 则默认用 initLayout
     *
     * @return
     */
    protected int getExtendLayout() {
        return -1;
    }

    protected abstract int initLayout();

    protected abstract void initView();

    /**
     * 初始化其他
     */
    protected void setContViewAfter() {
    }

    protected abstract void initData(Bundle bundleData);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent ();
            if (parent != null) {
                parent.removeView (rootView);
            }
        } else {
            @LayoutRes int layout = needExtendLayout () && getExtendLayout () > 0 ? getExtendLayout () : initLayout ();
            rootView = inflater.inflate (layout, null);
        }
        initView ();
        setContViewAfter ();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated (savedInstanceState);
        getBundleDate (getArguments ());

    }

    protected void getBundleDate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            /**防止空指针 *****/
            savedInstanceState = new Bundle ();
        }
        initData (savedInstanceState);
    }

    public <T extends View> T  findViewById(int id) {
        if (rootView == null) {
            return null;
        }
        return (T)rootView.findViewById (id);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView ();
    }

    /**
     * 拓展布局用 返回true 则 用 needExtendLayout initLayout作为 getExtendLayout子布局, 反之用 initLayout
     *
     * @return
     */
    protected boolean needExtendLayout() {
        return false;
    }


    // http://stackoverflow.com/questions/15207305/getting-the-error-java-lang-illegalstateexception-activity-has-been-destroyed
    @Override
    public void onDetach() {
        super.onDetach ();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField ("mChildFragmentManager");
            childFragmentManager.setAccessible (true);
            childFragmentManager.set (this, null);
        } catch (Exception e) {
            e.printStackTrace ();
        }
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
        Intent intent = new Intent (getActivity (), targetClass);
        if (bundle != null) {
            intent.putExtras (bundle);
        }
        getActivity ().startActivityForResult (intent, requestCode);
    }
}
