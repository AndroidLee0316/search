package com.pasc.lib.search.base;

import android.os.Bundle;

/**
 * @author yangzijian
 * @date 2019/2/18
 * @des
 * @modify
 **/
public interface Presenter<V extends BaseView> {

    void onMvpAttachView(V view, Bundle savedInstanceState);

    void onMvpStart();

    void onMvpResume();

    void onMvpPause();

    void onMvpStop();

    void onMvpSaveInstanceState(Bundle savedInstanceState);

    void onMvpDetachView(boolean retainInstance);

    void onMvpDestroy();

    boolean isMainPresenter();

}
