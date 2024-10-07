package com.pasc.lib.search.base;

import android.os.Bundle;

import java.lang.ref.WeakReference;

/**
 * @author yangzijian
 * @date 2019/2/18
 * @des
 * @modify
 **/
public class WrapperPresenter<V extends BaseView> implements Presenter<V> {


    private WeakReference<V> viewRef;

    protected V getView() {
        return viewRef.get ();
    }

    protected boolean isHeathView(){
       return viewRef!=null && viewRef.get ()!=null;
    }

    protected boolean isViewAttached() {
        return viewRef != null && viewRef.get () != null;
    }

    private void _attach(V view, Bundle savedInstanceState) {
        viewRef = new WeakReference<V> (view);
    }

    @Override
    public void onMvpAttachView(V view, Bundle savedInstanceState) {
        _attach (view, savedInstanceState);
    }

    @Override
    public void onMvpStart() {

    }

    @Override
    public void onMvpResume() {

    }

    @Override
    public void onMvpPause() {

    }

    @Override
    public void onMvpStop() {

    }

    @Override
    public void onMvpSaveInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onMvpDetachView(boolean retainInstance) {
        _detach (retainInstance);
    }
    private void _detach(boolean retainInstance) {
        if (viewRef != null) {
            viewRef.clear();
            viewRef = null;
        }
    }
    @Override
    public void onMvpDestroy() {

    }

    @Override
    public boolean isMainPresenter() {
        return false;
    }
}
