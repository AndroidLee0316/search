package com.pasc.business.search.home.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pasc.business.search.R;
import com.pasc.business.search.router.Table;
import com.pasc.lib.search.util.FragmentUtils;
import com.pasc.lib.search.util.StatusBarUtils;

/**
 * @author yangzijian
 * @date 2019/2/18
 * @des
 * @modify
 **/
@Route(path = Table.Path.path_search_home_router)
public class SearchHomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setStatusBarColor(this, true);
        setContentView(R.layout.pasc_search_content_activity);


    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.flContainer);
        if (fragment == null) {
            fragment = new SearchVoiceHomeFragment();
        }
        if (intent != null) {
            fragment.setArguments(intent.getExtras());
        }
        FragmentUtils.showTargetFragment(getSupportFragmentManager(), fragment, R.id.flContainer);
    }
}
