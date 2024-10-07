package com.pasc.lib.search.base;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangzijian
 * @date 2019/2/18
 * @des
 * @modify
 **/
public class MultiPresenter<V extends BaseView> extends WrapperPresenter<V> {

    private List<Presenter> presenters = new ArrayList<> ();

    @SafeVarargs
    public final <Q extends Presenter<V>> void requestPresenter(Q... cls) {
        for (Q cl : cls) {
            presenters.add (cl);
        }
    }

    @Override
    public void onMvpAttachView(V view, Bundle savedInstanceState) {
        for (Presenter presenter : presenters) {
            presenter.onMvpAttachView (view,savedInstanceState);
        }
    }

    @Override
    public void onMvpStart() {
        for (Presenter presenter : presenters) {
            presenter.onMvpStart ();
        }
    }

    @Override
    public void onMvpResume() {
        for (Presenter presenter : presenters) {
            presenter.onMvpResume ();
        }
    }

    @Override
    public void onMvpPause() {
        for (Presenter presenter : presenters) {
            presenter.onMvpPause ();
        }
    }

    @Override
    public void onMvpStop() {
        for (Presenter presenter : presenters) {
            presenter.onMvpStop ();
        }
    }

    @Override
    public void onMvpSaveInstanceState(Bundle savedInstanceState) {
        for (Presenter presenter : presenters) {
            presenter.onMvpSaveInstanceState (savedInstanceState);
        }
    }

    @Override
    public void onMvpDetachView(boolean retainInstance) {
        for (Presenter presenter : presenters) {
            presenter.onMvpDetachView (retainInstance);
        }
    }

    @Override
    public void onMvpDestroy() {
        for (Presenter presenter : presenters) {
            presenter.onMvpDestroy ();
        }
    }
}

