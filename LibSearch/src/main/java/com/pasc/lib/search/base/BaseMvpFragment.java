package com.pasc.lib.search.base;

import android.os.Bundle;

import java.util.ArrayList;

/**
 * @author yangzijian
 * @date 2019/2/28
 * @des
 * @modify
 **/
public abstract class BaseMvpFragment<T extends WrapperPresenter>  extends BaseFragment implements BaseView {
    protected T mPresenter;

    protected  ArrayList<String> analyzers = new ArrayList<>();

    public abstract T createPresenter();

    @Override
    protected void setContViewAfter() {
        mPresenter=createPresenter ();
        if (mPresenter != null) {
            mPresenter.onMvpAttachView (this, getArguments ());
        }
    }

    @Override
    public void onStart() {
        super.onStart ();
        if (mPresenter != null) {
            mPresenter.onMvpStart ();
        }
    }

    @Override
    public void onResume() {
        super.onResume ();
        if (mPresenter != null) {
            mPresenter.onMvpResume ();
        }
    }

    @Override
    public void onPause() {
        super.onPause ();
        if (mPresenter != null) {
            mPresenter.onMvpPause ();
        }
    }

    @Override
    public void onStop() {
        super.onStop ();
        if (mPresenter != null) {
            mPresenter.onMvpStop ();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState (outState);
        if (mPresenter != null) {
            mPresenter.onMvpSaveInstanceState (outState);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy ();
        if (mPresenter != null) {
            mPresenter.onMvpDestroy ();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView ();
        if (mPresenter != null) {
            mPresenter.onMvpDetachView (false);
        }
    }

    // 获取分词后的关键字数组
    protected String[] getKeywords(String keyword){

        analyzers.add(keyword);

        int size = analyzers.size();

        String[] keywords = new String[size];

        for(int i=0;i<size;i++){
            keywords[i] = analyzers.get(i);
        }
        return  keywords;
    }
}
