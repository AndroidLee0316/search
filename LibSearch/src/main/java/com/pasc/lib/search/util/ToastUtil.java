package com.pasc.lib.search.util;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.pasc.lib.search.LocalSearchManager;

/**
 * @author yangzijian
 * @date 2019/3/28
 * @des
 * @modify
 **/
@SuppressLint("IncorrectToast")
public class ToastUtil {
    private static Toast toast;
    private final static Handler HANDLER=new Handler (Looper.getMainLooper ());
    private static void init() {
        if (toast == null) {
            toast = Toast.makeText (LocalSearchManager.instance ().getApp (), "", Toast.LENGTH_SHORT);
        }
    }

    public static void showToast(final String msg) {
        if (SearchUtil.isEmpty (msg)) {
            return;
        }
        if (Looper.getMainLooper () == Looper.myLooper ()) {
            innerShow (msg);
        } else {
            HANDLER.post (new Runnable () {
                @Override
                public void run() {
                    innerShow (msg);
                }
            });
        }
    }

    private static void innerShow(String msg) {

        init ();
        if (toast != null) {
            toast.setText (msg);
            toast.show ();
        }
    }

    public static void showToast(@StringRes int msg) {
        showToast (LocalSearchManager.instance ().getApp ().getString (msg));
    }

}
