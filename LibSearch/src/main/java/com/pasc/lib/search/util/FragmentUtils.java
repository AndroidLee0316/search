package com.pasc.lib.search.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.io.Serializable;


public class FragmentUtils {
    @SuppressLint({"CommitTransaction", "ResourceType"})
    public static void showTargetFragment(android.app.FragmentManager manager, android.app.Fragment curFragment, android.app.Fragment targetFragment, @IdRes int resId) {
        if (manager == null || targetFragment == null || resId < 0) {
            return;
        }
        if (curFragment != null) {
            if (targetFragment.isAdded()) {
                commit(manager.beginTransaction().show(targetFragment).hide(curFragment));
            } else {
                commit(manager.beginTransaction().add(resId, targetFragment).hide(curFragment));
            }
        } else {
            if (targetFragment.isAdded()) {
                commit(manager.beginTransaction().show(targetFragment));
            } else {
                commit(manager.beginTransaction().add(resId, targetFragment));
            }
        }
    }

    @SuppressLint({"CommitTransaction", "ResourceType"})
    public static void showTargetFragment(FragmentManager manager, Fragment curFragment, Fragment targetFragment, @IdRes int resId) {
        if (manager == null || targetFragment == null || resId < 0) {
            return;
        }
        if (curFragment != null && curFragment != targetFragment) {
            if (targetFragment.isAdded()) {
                commit(manager.beginTransaction().show(targetFragment).hide(curFragment));
            } else {
                commit(manager.beginTransaction().add(resId, targetFragment).hide(curFragment));
            }
        } else {
            if (targetFragment.isAdded()) {
                commit(manager.beginTransaction().show(targetFragment));
            } else {
                commit(manager.beginTransaction().add(resId, targetFragment));
            }
        }
    }

    /**
     * 这种方式与showTargetFragment的区别是这种方式显示targetFragment的同时从宿主Activity中移除当前Fragment
     */
    @SuppressLint({"CommitTransaction", "ResourceType"})
    public static void popTargetFragment(FragmentManager manager, Fragment curFragment, Fragment targetFragment, @IdRes int resId) {
        if (manager == null || targetFragment == null || resId < 0) {
            return;
        }
        if (curFragment != null && curFragment != targetFragment) {
            if (targetFragment.isAdded()) {
                commit(manager.beginTransaction().show(targetFragment).remove(curFragment));
            } else {
                commit(manager.beginTransaction().add(resId, targetFragment).remove(curFragment));
            }
        } else {
            if (targetFragment.isAdded()) {
                commit(manager.beginTransaction().show(targetFragment));
            } else {
                commit(manager.beginTransaction().add(resId, targetFragment));
            }
        }
    }

    @SuppressLint({"CommitTransaction", "ResourceType"})
    public static void showTargetFragment(FragmentManager manager, Fragment targetFragment, @IdRes int resId) {
        if (manager == null || targetFragment == null || resId < 0) {
            return;
        }
        if (targetFragment.isAdded()) {
            commit(manager.beginTransaction().show(targetFragment));
        } else {
            commit(manager.beginTransaction().add(resId, targetFragment));
        }
    }

    private static void commit(FragmentTransaction ft) {
        ft.commitAllowingStateLoss();
    }

    private static void commit(android.app.FragmentTransaction ft) {
        ft.commitAllowingStateLoss();
    }

    public static void showTargetFragment(FragmentActivity fragmentActivity, Fragment curFragment, Fragment targetFragment, @IdRes int resId) {
        if (fragmentActivity == null) {
            return;
        }
        showTargetFragment(fragmentActivity.getSupportFragmentManager(), curFragment, targetFragment, resId);
    }

    public static volatile FragmentUtils sInstance;
    private Intent mIntent;

    public static FragmentUtils getInstance() {
        if (sInstance == null) {
            synchronized (FragmentUtils.class) {
                if (sInstance == null) {
                    sInstance = new FragmentUtils();
                }
            }
        }
        return sInstance;
    }

    public FragmentUtils setActivity(Activity curActivity, Class<? extends Activity> targetActivity) {
        mIntent = new Intent(curActivity, targetActivity);
        return sInstance;
    }

    public <T> FragmentUtils putExtra(String key, T t) {
        if (mIntent != null) {
            if (t instanceof Boolean) {
                mIntent.putExtra(key, (Boolean) t);
            } else if (t instanceof Integer) {
                mIntent.putExtra(key, (Integer) t);
            } else if (t instanceof String) {
                mIntent.putExtra(key, (String) t);
            } else if (t instanceof Parcelable) {
                mIntent.putExtra(key, (Parcelable) t);
            } else if (t instanceof Serializable) {
                mIntent.putExtra(key, (Serializable) t);
            }
        }
        return sInstance;
    }


}
